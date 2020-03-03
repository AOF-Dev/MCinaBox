/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Backtracing functions for x86.
 */

#define LOG_TAG "Corkscrew"
//#define LOG_NDEBUG 0

#include <corkscrew/backtrace-arch.h>
#include "../backtrace-helper.h"
#include <corkscrew/ptrace-arch.h>
#include <corkscrew/ptrace.h>
#include "dwarf.h"

#include <stdlib.h>
#include <signal.h>
#include <stdbool.h>
#include <limits.h>
#include <errno.h>
#include <string.h>
#include <sys/ptrace.h>
#include <cutils/log.h>

#if defined(__BIONIC__)

#if defined(__BIONIC_HAVE_UCONTEXT_T)

// Bionic offers the Linux kernel headers.
#include <asm/sigcontext.h>
#include <ucontext.h>
typedef struct ucontext ucontext_t;

#else /* __BIONIC_HAVE_UCONTEXT_T */

/* Old versions of the Android <signal.h> didn't define ucontext_t. */

typedef struct {
  uint32_t  gregs[32];
  void*     fpregs;
  uint32_t  oldmask;
  uint32_t  cr2;
} mcontext_t;

enum {
  REG_GS = 0, REG_FS, REG_ES, REG_DS,
  REG_EDI, REG_ESI, REG_EBP, REG_ESP,
  REG_EBX, REG_EDX, REG_ECX, REG_EAX,
  REG_TRAPNO, REG_ERR, REG_EIP, REG_CS,
  REG_EFL, REG_UESP, REG_SS
};

/* Machine context at the time a signal was raised. */
typedef struct ucontext {
    uint32_t uc_flags;
    struct ucontext* uc_link;
    stack_t uc_stack;
    mcontext_t uc_mcontext;
    uint32_t uc_sigmask;
} ucontext_t;

#endif /* __BIONIC_HAVE_UCONTEXT_T */

#elif defined(__APPLE__)

#define _XOPEN_SOURCE
#include <ucontext.h>

#else

// glibc has its own renaming of the Linux kernel's structures.
#define __USE_GNU // For REG_EBP, REG_ESP, and REG_EIP.
#include <ucontext.h>

#endif

/* Unwind state. */
typedef struct {
    uint32_t reg[DWARF_REGISTERS];
} unwind_state_t;

typedef struct {
    backtrace_frame_t* backtrace;
    size_t ignore_depth;
    size_t max_depth;
    size_t ignored_frames;
    size_t returned_frames;
    memory_t memory;
} backtrace_state_t;

uintptr_t rewind_pc_arch(const memory_t* memory __attribute__((unused)), uintptr_t pc) {
    /* TODO: x86 instructions are 1-16 bytes, to define exact size of previous instruction
       we have to disassemble from the function entry point up to pc.
       Returning pc-1 is probably enough for now, the only drawback is that
       it points somewhere between the first byte of instruction we are looking for and
       the first byte of the next instruction. */

    return pc-1;
    /* TODO: We should adjust that for the signal frames and return pc for them instead of pc-1.
       To recognize signal frames we should read cie_info property. */
}

/* Read byte through 4 byte cache. Usually we read byte by byte and updating cursor. */
static bool try_get_byte(const memory_t* memory, uintptr_t ptr, uint8_t* out_value, uint32_t* cursor) {
    static uintptr_t lastptr;
    static uint32_t buf;

    ptr += *cursor;

    if (ptr < lastptr || lastptr + 3 < ptr) {
        lastptr = (ptr >> 2) << 2;
        if (!try_get_word(memory, lastptr, &buf)) {
            return false;
        }
    }
    *out_value = (uint8_t)((buf >> ((ptr & 3) * 8)) & 0xff);
    ++*cursor;
    return true;
}

/* Getting X bytes. 4 is maximum for now. */
static bool try_get_xbytes(const memory_t* memory, uintptr_t ptr, uint32_t* out_value, uint8_t bytes, uint32_t* cursor) {
    uint32_t data = 0;
    if (bytes > 4) {
        ALOGE("can't read more than 4 bytes, trying to read %d", bytes);
        return false;
    }
    for (int i = 0; i < bytes; i++) {
        uint8_t buf;
        if (!try_get_byte(memory, ptr, &buf, cursor)) {
            return false;
        }
        data |= (uint32_t)buf << (i * 8);
    }
    *out_value = data;
    return true;
}

/* Reads signed/unsigned LEB128 encoded data. From 1 to 4 bytes. */
static bool try_get_leb128(const memory_t* memory, uintptr_t ptr, uint32_t* out_value, uint32_t* cursor, bool sign_extend) {
    uint8_t buf = 0;
    uint32_t val = 0;
    uint8_t c = 0;
    do {
       if (!try_get_byte(memory, ptr, &buf, cursor)) {
           return false;
       }
       val |= ((uint32_t)buf & 0x7f) << (c * 7);
       c++;
    } while (buf & 0x80 && (c * 7) <= 32);
    if (c * 7 > 32) {
       ALOGE("%s: data exceeds expected 4 bytes maximum", __FUNCTION__);
       return false;
    }
    if (sign_extend) {
        if (buf & 0x40) {
            val |= ((uint32_t)-1 << (c * 7));
        }
    }
    *out_value = val;
    return true;
}

/* Reads signed LEB128 encoded data. From 1 to 4 bytes. */
static bool try_get_sleb128(const memory_t* memory, uintptr_t ptr, uint32_t* out_value, uint32_t* cursor) {
  return try_get_leb128(memory, ptr, out_value, cursor, true);
}

/* Reads unsigned LEB128 encoded data. From 1 to 4 bytes. */
static bool try_get_uleb128(const memory_t* memory, uintptr_t ptr, uint32_t* out_value, uint32_t* cursor) {
  return try_get_leb128(memory, ptr, out_value, cursor, false);
}

/* Getting data encoded by dwarf encodings. */
static bool read_dwarf(const memory_t* memory, uintptr_t ptr, uint32_t* out_value, uint8_t encoding, uint32_t* cursor) {
    uint32_t data = 0;
    bool issigned = true;
    uintptr_t addr = ptr + *cursor;
    /* Lower 4 bits is data type/size */
    /* TODO: add more encodings if it becomes necessary */
    switch (encoding & 0xf) {
        case DW_EH_PE_absptr:
            if (!try_get_xbytes(memory, ptr, &data, 4, cursor)) {
                return false;
            }
            *out_value = data;
            return true;
        case DW_EH_PE_udata4:
            issigned = false;
        case DW_EH_PE_sdata4:
            if (!try_get_xbytes(memory, ptr, &data, 4, cursor)) {
                return false;
            }
            break;
        default:
            ALOGE("unrecognized dwarf lower part encoding: 0x%x", encoding);
            return false;
    }
    /* Higher 4 bits is modifier */
    /* TODO: add more encodings if it becomes necessary */
    switch (encoding & 0xf0) {
        case 0:
            *out_value = data;
            break;
        case DW_EH_PE_pcrel:
            if (issigned) {
                *out_value = addr + (int32_t)data;
            } else {
                *out_value = addr + data;
            }
            break;
        /* Assuming ptr is correct base to calculate datarel */
        case DW_EH_PE_datarel:
            if (issigned) {
                *out_value = ptr + (int32_t)data;
            } else {
                *out_value = ptr + data;
            }
            break;
        default:
            ALOGE("unrecognized dwarf higher part encoding: 0x%x", encoding);
            return false;
    }
    return true;
}

/* Having PC find corresponding FDE by reading .eh_frame_hdr section data. */
static uintptr_t find_fde(const memory_t* memory,
                          const map_info_t* map_info_list, uintptr_t pc) {
    if (!pc) {
        ALOGV("find_fde: pc is zero, no eh_frame");
        return 0;
    }
    const map_info_t* mi = find_map_info(map_info_list, pc);
    if (!mi) {
        ALOGV("find_fde: no map info for pc:0x%x", pc);
        return 0;
    }
    const map_info_data_t* midata = mi->data;
    if (!midata) {
        ALOGV("find_fde: no eh_frame_hdr for map: start=0x%x, end=0x%x", mi->start, mi->end);
        return 0;
    }

    eh_frame_hdr_info_t eh_hdr_info;
    memset(&eh_hdr_info, 0, sizeof(eh_frame_hdr_info_t));

    /* Getting the first word of eh_frame_hdr:
        1st byte is version;
        2nd byte is encoding of pointer to eh_frames;
        3rd byte is encoding of count of FDEs in lookup table;
        4th byte is encoding of lookup table entries.
    */
    uintptr_t eh_frame_hdr = midata->eh_frame_hdr;
    uint32_t c = 0;
    if (!try_get_byte(memory, eh_frame_hdr, &eh_hdr_info.version, &c)) return 0;
    if (!try_get_byte(memory, eh_frame_hdr, &eh_hdr_info.eh_frame_ptr_enc, &c)) return 0;
    if (!try_get_byte(memory, eh_frame_hdr, &eh_hdr_info.fde_count_enc, &c)) return 0;
    if (!try_get_byte(memory, eh_frame_hdr, &eh_hdr_info.fde_table_enc, &c)) return 0;

    /* TODO: 3rd byte can be DW_EH_PE_omit, that means no lookup table available and we should
       try to parse eh_frame instead. Not sure how often it may occur, skipping now.
    */
    if (eh_hdr_info.version != 1) {
        ALOGV("find_fde: eh_frame_hdr version %d is not supported", eh_hdr_info.version);
        return 0;
    }
    /* Getting the data:
        2nd word is eh_frame pointer (normally not used, because lookup table has all we need);
        3rd word is count of FDEs in the lookup table;
        starting from 4 word there is FDE lookup table (pairs of PC and FDE pointer) sorted by PC;
    */
    if (!read_dwarf(memory, eh_frame_hdr, &eh_hdr_info.eh_frame_ptr, eh_hdr_info.eh_frame_ptr_enc, &c)) return 0;
    if (!read_dwarf(memory, eh_frame_hdr, &eh_hdr_info.fde_count, eh_hdr_info.fde_count_enc, &c)) return 0;
    ALOGV("find_fde: found %d FDEs", eh_hdr_info.fde_count);

    int32_t low = 0;
    int32_t high = eh_hdr_info.fde_count;
    uintptr_t start = 0;
    uintptr_t fde = 0;
    /* eh_frame_hdr + c points to lookup table at this point. */
    while (low <= high) {
        uint32_t mid = (high + low)/2;
        uint32_t entry = c + mid * 8;
        if (!read_dwarf(memory, eh_frame_hdr, &start, eh_hdr_info.fde_table_enc, &entry)) return 0;
        if (pc <= start) {
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }
    /* Value found is at high. */
    if (high < 0) {
        ALOGV("find_fde: pc %x is out of FDE bounds: %x", pc, start);
        return 0;
    }
    c += high * 8;
    if (!read_dwarf(memory, eh_frame_hdr, &start, eh_hdr_info.fde_table_enc, &c)) return 0;
    if (!read_dwarf(memory, eh_frame_hdr, &fde, eh_hdr_info.fde_table_enc, &c)) return 0;
    ALOGV("pc 0x%x, ENTRY %d: start=0x%x, fde=0x%x", pc, high, start, fde);
    return fde;
}

/* Execute single dwarf instruction and update dwarf state accordingly. */
static bool execute_dwarf(const memory_t* memory, uintptr_t ptr, cie_info_t* cie_info,
                          dwarf_state_t* dstate, uint32_t* cursor,
                          dwarf_state_t* stack, uint8_t* stack_ptr) {
    uint8_t inst;
    uint8_t op = 0;

    if (!try_get_byte(memory, ptr, &inst, cursor)) {
        return false;
    }
    ALOGV("DW_CFA inst: 0x%x", inst);

    /* For some instructions upper 2 bits is opcode and lower 6 bits is operand. See dwarf-2.0 7.23. */
    if (inst & 0xc0) {
        op = inst & 0x3f;
        inst &= 0xc0;
    }

    switch ((dwarf_CFA)inst) {
        uint32_t reg = 0;
        uint32_t offset = 0;
        case DW_CFA_advance_loc:
            dstate->loc += op * cie_info->code_align;
            ALOGV("DW_CFA_advance_loc: %d to 0x%x", op, dstate->loc);
            break;
        case DW_CFA_offset:
            if (!try_get_uleb128(memory, ptr, &offset, cursor)) return false;
            dstate->regs[op].rule = 'o';
            dstate->regs[op].value = offset * cie_info->data_align;
            ALOGV("DW_CFA_offset: r%d = o(%d)", op, dstate->regs[op].value);
            break;
        case DW_CFA_restore:
            dstate->regs[op].rule = stack->regs[op].rule;
            dstate->regs[op].value = stack->regs[op].value;
            ALOGV("DW_CFA_restore: r%d = %c(%d)", op, dstate->regs[op].rule, dstate->regs[op].value);
            break;
        case DW_CFA_nop:
            break;
        case DW_CFA_set_loc: // probably we don't have it on x86.
            if (!try_get_xbytes(memory, ptr, &offset, 4, cursor)) return false;
            if (offset < dstate->loc) {
                ALOGE("DW_CFA_set_loc: attempt to move location backward");
                return false;
            }
            dstate->loc = offset * cie_info->code_align;
            ALOGV("DW_CFA_set_loc: %d to 0x%x", offset * cie_info->code_align, dstate->loc);
            break;
        case DW_CFA_advance_loc1:
            if (!try_get_byte(memory, ptr, (uint8_t*)&offset, cursor)) return false;
            dstate->loc += (uint8_t)offset * cie_info->code_align;
            ALOGV("DW_CFA_advance_loc1: %d to 0x%x", (uint8_t)offset * cie_info->code_align, dstate->loc);
            break;
        case DW_CFA_advance_loc2:
            if (!try_get_xbytes(memory, ptr, &offset, 2, cursor)) return false;
            dstate->loc += (uint16_t)offset * cie_info->code_align;
            ALOGV("DW_CFA_advance_loc2: %d to 0x%x", (uint16_t)offset * cie_info->code_align, dstate->loc);
            break;
        case DW_CFA_advance_loc4:
            if (!try_get_xbytes(memory, ptr, &offset, 4, cursor)) return false;
            dstate->loc += offset * cie_info->code_align;
            ALOGV("DW_CFA_advance_loc4: %d to 0x%x", offset * cie_info->code_align, dstate->loc);
            break;
        case DW_CFA_offset_extended: // probably we don't have it on x86.
            if (!try_get_uleb128(memory, ptr, &reg, cursor)) return false;
            if (!try_get_uleb128(memory, ptr, &offset, cursor)) return false;
            if (reg > DWARF_REGISTERS) {
                ALOGE("DW_CFA_offset_extended: r%d exceeds supported number of registers (%d)", reg, DWARF_REGISTERS);
                return false;
            }
            dstate->regs[reg].rule = 'o';
            dstate->regs[reg].value = offset * cie_info->data_align;
            ALOGV("DW_CFA_offset_extended: r%d = o(%d)", reg, dstate->regs[reg].value);
            break;
        case DW_CFA_restore_extended: // probably we don't have it on x86.
            if (!try_get_uleb128(memory, ptr, &reg, cursor)) return false;
            dstate->regs[reg].rule = stack->regs[reg].rule;
            dstate->regs[reg].value = stack->regs[reg].value;
            if (reg > DWARF_REGISTERS) {
                ALOGE("DW_CFA_restore_extended: r%d exceeds supported number of registers (%d)", reg, DWARF_REGISTERS);
                return false;
            }
            ALOGV("DW_CFA_restore: r%d = %c(%d)", reg, dstate->regs[reg].rule, dstate->regs[reg].value);
            break;
        case DW_CFA_undefined: // probably we don't have it on x86.
            if (!try_get_uleb128(memory, ptr, &reg, cursor)) return false;
            dstate->regs[reg].rule = 'u';
            dstate->regs[reg].value = 0;
            if (reg > DWARF_REGISTERS) {
                ALOGE("DW_CFA_undefined: r%d exceeds supported number of registers (%d)", reg, DWARF_REGISTERS);
                return false;
            }
            ALOGV("DW_CFA_undefined: r%d", reg);
            break;
        case DW_CFA_same_value: // probably we don't have it on x86.
            if (!try_get_uleb128(memory, ptr, &reg, cursor)) return false;
            dstate->regs[reg].rule = 's';
            dstate->regs[reg].value = 0;
            if (reg > DWARF_REGISTERS) {
                ALOGE("DW_CFA_undefined: r%d exceeds supported number of registers (%d)", reg, DWARF_REGISTERS);
                return false;
            }
            ALOGV("DW_CFA_same_value: r%d", reg);
            break;
        case DW_CFA_register: // probably we don't have it on x86.
            if (!try_get_uleb128(memory, ptr, &reg, cursor)) return false;
            /* that's new register actually, not offset */
            if (!try_get_uleb128(memory, ptr, &offset, cursor)) return false;
            if (reg > DWARF_REGISTERS || offset > DWARF_REGISTERS) {
                ALOGE("DW_CFA_register: r%d or r%d exceeds supported number of registers (%d)", reg, offset, DWARF_REGISTERS);
                return false;
            }
            dstate->regs[reg].rule = 'r';
            dstate->regs[reg].value = offset;
            ALOGV("DW_CFA_register: r%d = r(%d)", reg, dstate->regs[reg].value);
            break;
        case DW_CFA_remember_state:
            if (*stack_ptr == DWARF_STATES_STACK) {
                ALOGE("DW_CFA_remember_state: states stack overflow %d", *stack_ptr);
                return false;
            }
            stack[(*stack_ptr)++] = *dstate;
            ALOGV("DW_CFA_remember_state: stacktop moves to %d", *stack_ptr);
            break;
        case DW_CFA_restore_state:
            /* We have CIE state saved at 0 position. It's not supposed to be taken
               by DW_CFA_restore_state. */
            if (*stack_ptr == 1) {
                ALOGE("DW_CFA_restore_state: states stack is empty");
                return false;
            }
            /* Don't touch location on restore. */
            uintptr_t saveloc = dstate->loc;
            *dstate = stack[--*stack_ptr];
            dstate->loc = saveloc;
            ALOGV("DW_CFA_restore_state: stacktop moves to %d", *stack_ptr);
            break;
        case DW_CFA_def_cfa:
            if (!try_get_uleb128(memory, ptr, &reg, cursor)) return false;
            if (!try_get_uleb128(memory, ptr, &offset, cursor)) return false;
            dstate->cfa_reg = reg;
            dstate->cfa_off = offset;
            ALOGV("DW_CFA_def_cfa: %x(r%d)", offset, reg);
            break;
        case DW_CFA_def_cfa_register:
            if (!try_get_uleb128(memory, ptr, &reg, cursor)) {
                return false;
            }
            dstate->cfa_reg = reg;
            ALOGV("DW_CFA_def_cfa_register: r%d", reg);
            break;
        case DW_CFA_def_cfa_offset:
            if (!try_get_uleb128(memory, ptr, &offset, cursor)) {
                return false;
            }
            dstate->cfa_off = offset;
            ALOGV("DW_CFA_def_cfa_offset: %x", offset);
            break;
        default:
            ALOGE("unrecognized DW_CFA_* instruction: 0x%x", inst);
            return false;
    }
    return true;
}

/* Restoring particular register value based on dwarf state. */
static bool get_old_register_value(const memory_t* memory, uint32_t cfa,
                                   dwarf_state_t* dstate, uint8_t reg,
                                   unwind_state_t* state, unwind_state_t* newstate) {
    uint32_t addr;
    switch (dstate->regs[reg].rule) {
        case 0:
            /* We don't have dstate updated for this register, so assuming value kept the same.
               Normally we should look into state and return current value as the old one
               but we don't have all registers in state to handle this properly */
            ALOGV("get_old_register_value: value of r%d is the same", reg);
            // for ESP if it's not updated by dwarf rule we assume it's equal to CFA
            if (reg == DWARF_ESP) {
                ALOGV("get_old_register_value: adjusting esp to CFA: 0x%x", cfa);
                newstate->reg[reg] = cfa;
            } else {
                newstate->reg[reg] = state->reg[reg];
            }
            break;
        case 'o':
            addr = cfa + (int32_t)dstate->regs[reg].value;
            if (!try_get_word(memory, addr, &newstate->reg[reg])) {
                ALOGE("get_old_register_value: can't read from 0x%x", addr);
                return false;
            }
            ALOGV("get_old_register_value: r%d at 0x%x is 0x%x", reg, addr, newstate->reg[reg]);
            break;
        case 'r':
            /* We don't have all registers in state so don't even try to look at 'r' */
            ALOGE("get_old_register_value: register lookup not implemented yet");
            break;
        default:
            ALOGE("get_old_register_value: unexpected rule:%c value:%d for register %d",
                   dstate->regs[reg].rule, (int32_t)dstate->regs[reg].value, reg);
            return false;
    }
    return true;
}

/* Updaing state based on dwarf state. */
static bool update_state(const memory_t* memory, unwind_state_t* state,
                         dwarf_state_t* dstate, cie_info_t* cie_info) {
    unwind_state_t newstate;
    /* We can restore more registers here if we need them. Meanwile doing minimal work here. */
    /* Getting CFA. */
    uintptr_t cfa = 0;
    if (dstate->cfa_reg == DWARF_ESP) {
        cfa = state->reg[DWARF_ESP] + dstate->cfa_off;
    } else if (dstate->cfa_reg == DWARF_EBP) {
        cfa = state->reg[DWARF_EBP] + dstate->cfa_off;
    } else {
        ALOGE("update_state: unexpected CFA register: %d", dstate->cfa_reg);
        return false;
    }
    ALOGV("update_state: new CFA: 0x%x", cfa);
    /* Getting EIP. */
    if (!get_old_register_value(memory, cfa, dstate, DWARF_EIP, state, &newstate)) return false;
    /* Getting EBP. */
    if (!get_old_register_value(memory, cfa, dstate, DWARF_EBP, state, &newstate)) return false;
    /* Getting ESP. */
    if (!get_old_register_value(memory, cfa, dstate, DWARF_ESP, state, &newstate)) return false;

    ALOGV("update_state: IP:  0x%x; restore IP:  0x%x", state->reg[DWARF_EIP], newstate.reg[DWARF_EIP]);
    ALOGV("update_state: EBP: 0x%x; restore EBP: 0x%x", state->reg[DWARF_EBP], newstate.reg[DWARF_EBP]);
    ALOGV("update_state: ESP: 0x%x; restore ESP: 0x%x", state->reg[DWARF_ESP], newstate.reg[DWARF_ESP]);
    *state = newstate;
    return true;
}

/* Execute CIE and FDE instructions for FDE found with find_fde. */
static bool execute_fde(const memory_t* memory,
                        const map_info_t* map_info_list,
                        uintptr_t fde,
                        unwind_state_t* state) {
    uint32_t fde_length = 0;
    uint32_t cie_length = 0;
    uintptr_t cie = 0;
    uintptr_t cie_offset = 0;
    cie_info_t cie_i;
    cie_info_t* cie_info = &cie_i;
    fde_info_t fde_i;
    fde_info_t* fde_info = &fde_i;
    dwarf_state_t dwarf_state;
    dwarf_state_t* dstate = &dwarf_state;
    dwarf_state_t stack[DWARF_STATES_STACK];
    uint8_t stack_ptr = 0;

    memset(dstate, 0, sizeof(dwarf_state_t));
    memset(cie_info, 0, sizeof(cie_info_t));
    memset(fde_info, 0, sizeof(fde_info_t));

    /* Read common CIE or FDE area:
        1st word is length;
        2nd word is ID: 0 for CIE, CIE pointer for FDE.
    */
    if (!try_get_word(memory, fde, &fde_length)) {
        return false;
    }
    if ((int32_t)fde_length == -1) {
        ALOGV("execute_fde: 64-bit dwarf detected, not implemented yet");
        return false;
    }
    if (!try_get_word(memory, fde + 4, &cie_offset)) {
        return false;
    }
    if (cie_offset == 0) {
        /* This is CIE. We shouldn't be here normally. */
        cie = fde;
        cie_length = fde_length;
    } else {
        /* Find CIE. */
        /* Positive cie_offset goes backward from current field. */
        cie = fde + 4 - cie_offset;
        if (!try_get_word(memory, cie, &cie_length)) {
           return false;
        }
        if ((int32_t)cie_length == -1) {
           ALOGV("execute_fde: 64-bit dwarf detected, not implemented yet");
           return false;
        }
        if (!try_get_word(memory, cie + 4, &cie_offset)) {
           return false;
        }
        if (cie_offset != 0) {
           ALOGV("execute_fde: can't find CIE");
           return false;
        }
    }
    ALOGV("execute_fde: FDE length: %d", fde_length);
    ALOGV("execute_fde: CIE pointer: %x", cie);
    ALOGV("execute_fde: CIE length: %d", cie_length);

    /* Read CIE:
       Augmentation independent:
        1st byte is version;
        next x bytes is /0 terminated augmentation string;
        next x bytes is unsigned LEB128 encoded code alignment factor;
        next x bytes is signed LEB128 encoded data alignment factor;
        next 1 (CIE version 1) or x (CIE version 3 unsigned LEB128) bytes is return register column;
       Augmentation dependent:
        if 'z' next x bytes is unsigned LEB128 encoded augmentation data size;
        if 'L' next 1 byte is LSDA encoding;
        if 'R' next 1 byte is FDE encoding;
        if 'S' CIE represents signal handler stack frame;
        if 'P' next 1 byte is personality encoding folowed by personality function pointer;
       Next x bytes is CIE program.
    */

    uint32_t c = 8;
    if (!try_get_byte(memory, cie, &cie_info->version, &c)) {
       return false;
    }
    ALOGV("execute_fde: CIE version: %d", cie_info->version);
    uint8_t ch;
    do {
        if (!try_get_byte(memory, cie, &ch, &c)) {
           return false;
        }
        switch (ch) {
           case '\0': break;
           case 'z': cie_info->aug_z = 1; break;
           case 'L': cie_info->aug_L = 1; break;
           case 'R': cie_info->aug_R = 1; break;
           case 'S': cie_info->aug_S = 1; break;
           case 'P': cie_info->aug_P = 1; break;
           default:
              ALOGV("execute_fde: Unrecognized CIE augmentation char: '%c'", ch);
              return false;
              break;
        }
    } while (ch);
    if (!try_get_uleb128(memory, cie, &cie_info->code_align, &c)) {
        return false;
    }
    if (!try_get_sleb128(memory, cie, &cie_info->data_align, &c)) {
        return false;
    }
    if (cie_info->version >= 3) {
        if (!try_get_uleb128(memory, cie, &cie_info->reg, &c)) {
            return false;
        }
    } else {
        if (!try_get_byte(memory, cie, (uint8_t*)&cie_info->reg, &c)) {
            return false;
        }
    }
    ALOGV("execute_fde: CIE code alignment factor: %d", cie_info->code_align);
    ALOGV("execute_fde: CIE data alignment factor: %d", cie_info->data_align);
    if (cie_info->aug_z) {
        if (!try_get_uleb128(memory, cie, &cie_info->aug_z, &c)) {
            return false;
        }
    }
    if (cie_info->aug_L) {
        if (!try_get_byte(memory, cie, &cie_info->aug_L, &c)) {
            return false;
        }
    } else {
        /* Default encoding. */
        cie_info->aug_L = DW_EH_PE_absptr;
    }
    if (cie_info->aug_R) {
        if (!try_get_byte(memory, cie, &cie_info->aug_R, &c)) {
            return false;
        }
    } else {
        /* Default encoding. */
        cie_info->aug_R = DW_EH_PE_absptr;
    }
    if (cie_info->aug_P) {
        /* Get encoding of personality routine pointer. We don't use it now. */
        if (!try_get_byte(memory, cie, (uint8_t*)&cie_info->aug_P, &c)) {
            return false;
        }
        /* Get routine pointer. */
        if (!read_dwarf(memory, cie, &cie_info->aug_P, (uint8_t)cie_info->aug_P, &c)) {
            return false;
        }
    }
    /* CIE program. */
    /* Length field itself (4 bytes) is not included into length. */
    stack[0] = *dstate;
    stack_ptr = 1;
    while (c < cie_length + 4) {
        if (!execute_dwarf(memory, cie, cie_info, dstate, &c, stack, &stack_ptr)) {
           return false;
        }
    }

    /* We went directly to CIE. Normally it shouldn't occur. */
    if (cie == fde) return true;

    /* Go back to FDE. */
    c = 8;
    /* Read FDE:
       Augmentation independent:
        next x bytes (encoded as specified in CIE) is FDE starting address;
        next x bytes (encoded as specified in CIE) is FDE number of instructions covered;
       Augmentation dependent:
        if 'z' next x bytes is unsigned LEB128 encoded augmentation data size;
        if 'L' next x bytes is LSDA pointer (encoded as specified in CIE);
       Next x bytes is FDE program.
     */
    if (!read_dwarf(memory, fde, &fde_info->start, (uint8_t)cie_info->aug_R, &c)) {
        return false;
    }
    dstate->loc = fde_info->start;
    ALOGV("execute_fde: FDE start: %x", dstate->loc);
    if (!read_dwarf(memory, fde, &fde_info->length, 0, &c)) {
        return false;
    }
    ALOGV("execute_fde: FDE length: %x", fde_info->length);
    if (cie_info->aug_z) {
        if (!try_get_uleb128(memory, fde, &fde_info->aug_z, &c)) {
            return false;
        }
    }
    if (cie_info->aug_L && cie_info->aug_L != DW_EH_PE_omit) {
        if (!read_dwarf(memory, fde, &fde_info->aug_L, cie_info->aug_L, &c)) {
            return false;
        }
    }
    /* FDE program. */
    /* Length field itself (4 bytes) is not included into length. */
    /* Save CIE state as 0 element of stack. Used by DW_CFA_restore. */
    stack[0] = *dstate;
    stack_ptr = 1;
    while (c < fde_length + 4 && state->reg[DWARF_EIP] >= dstate->loc) {
        if (!execute_dwarf(memory, fde, cie_info, dstate, &c, stack, &stack_ptr)) {
           return false;
        }
        ALOGV("IP: %x, LOC: %x", state->reg[DWARF_EIP], dstate->loc);
    }

    return update_state(memory, state, dstate, cie_info);
}

static ssize_t unwind_backtrace_common(const memory_t* memory,
        const map_info_t* map_info_list,
        unwind_state_t* state, backtrace_frame_t* backtrace,
        size_t ignore_depth, size_t max_depth) {

    size_t ignored_frames = 0;
    size_t returned_frames = 0;

    ALOGV("Unwinding tid: %d", memory->tid);
    ALOGV("IP: %x", state->reg[DWARF_EIP]);
    ALOGV("BP: %x", state->reg[DWARF_EBP]);
    ALOGV("SP: %x", state->reg[DWARF_ESP]);

    for (size_t index = 0; returned_frames < max_depth; index++) {
        uintptr_t fde = find_fde(memory, map_info_list, state->reg[DWARF_EIP]);
        /* FDE is not found, it may happen if stack is corrupted or calling wrong adress.
           Getting return address from stack.
        */
        if (!fde) {
            uint32_t ip;
            ALOGV("trying to restore registers from stack");
            if (!try_get_word(memory, state->reg[DWARF_EBP] + 4, &ip) ||
                ip == state->reg[DWARF_EIP]) {
                ALOGV("can't get IP from stack");
                break;
            }
            /* We've been able to get IP from stack so recording the frame before continue. */
            backtrace_frame_t* frame = add_backtrace_entry(
                    index ? rewind_pc_arch(memory, state->reg[DWARF_EIP]) : state->reg[DWARF_EIP],
                    backtrace, ignore_depth, max_depth,
                    &ignored_frames, &returned_frames);
            state->reg[DWARF_EIP] = ip;
            state->reg[DWARF_ESP] = state->reg[DWARF_EBP] + 8;
            if (!try_get_word(memory, state->reg[DWARF_EBP], &state->reg[DWARF_EBP])) {
                ALOGV("can't get EBP from stack");
                break;
            }
            ALOGV("restore IP: %x", state->reg[DWARF_EIP]);
            ALOGV("restore BP: %x", state->reg[DWARF_EBP]);
            ALOGV("restore SP: %x", state->reg[DWARF_ESP]);
            continue;
        }
        backtrace_frame_t* frame = add_backtrace_entry(
                index ? rewind_pc_arch(memory, state->reg[DWARF_EIP]) : state->reg[DWARF_EIP],
                backtrace, ignore_depth, max_depth,
                &ignored_frames, &returned_frames);

        uint32_t stack_top = state->reg[DWARF_ESP];

        if (!execute_fde(memory, map_info_list, fde, state)) break;

        if (frame) {
            frame->stack_top = stack_top;
            if (stack_top < state->reg[DWARF_ESP]) {
                frame->stack_size = state->reg[DWARF_ESP] - stack_top;
            }
        }
        ALOGV("Stack: 0x%x ... 0x%x - %d bytes", frame->stack_top, state->reg[DWARF_ESP], frame->stack_size);
    }
    return returned_frames;
}

ssize_t unwind_backtrace_signal_arch(siginfo_t* siginfo __attribute__((unused)), void* sigcontext,
        const map_info_t* map_info_list,
        backtrace_frame_t* backtrace, size_t ignore_depth, size_t max_depth) {
    const ucontext_t* uc = (const ucontext_t*)sigcontext;

    unwind_state_t state;
#if defined(__APPLE__)
    state.reg[DWARF_EBP] = uc->uc_mcontext->__ss.__ebp;
    state.reg[DWARF_ESP] = uc->uc_mcontext->__ss.__esp;
    state.reg[DWARF_EIP] = uc->uc_mcontext->__ss.__eip;
#else
    state.reg[DWARF_EBP] = uc->uc_mcontext.gregs[REG_EBP];
    state.reg[DWARF_ESP] = uc->uc_mcontext.gregs[REG_ESP];
    state.reg[DWARF_EIP] = uc->uc_mcontext.gregs[REG_EIP];
#endif

    memory_t memory;
    init_memory(&memory, map_info_list);
    return unwind_backtrace_common(&memory, map_info_list,
            &state, backtrace, ignore_depth, max_depth);
}

ssize_t unwind_backtrace_ptrace_arch(pid_t tid, const ptrace_context_t* context,
        backtrace_frame_t* backtrace, size_t ignore_depth, size_t max_depth) {
#if defined(__APPLE__)
    return -1;
#else
    pt_regs_x86_t regs;
    if (ptrace(PTRACE_GETREGS, tid, 0, &regs)) {
        return -1;
    }

    unwind_state_t state;
    state.reg[DWARF_EBP] = regs.ebp;
    state.reg[DWARF_EIP] = regs.eip;
    state.reg[DWARF_ESP] = regs.esp;

    memory_t memory;
    init_memory_ptrace(&memory, tid);
    return unwind_backtrace_common(&memory, context->map_info_list,
            &state, backtrace, ignore_depth, max_depth);
#endif
}

ssize_t unwind_backtrace_ptrace_context_arch(pid_t tid, void *sigcontext,
        const ptrace_context_t* context, backtrace_frame_t* backtrace, size_t ignore_depth,
        size_t max_depth) {
#if defined(__APPLE__)
    return -1;
#else
    const ucontext_t* uc = (const ucontext_t*)sigcontext;

    unwind_state_t state;
#if defined(__APPLE__)
    state.reg[DWARF_EBP] = uc->uc_mcontext->__ss.__ebp;
    state.reg[DWARF_ESP] = uc->uc_mcontext->__ss.__esp;
    state.reg[DWARF_EIP] = uc->uc_mcontext->__ss.__eip;
#else
    state.reg[DWARF_EBP] = uc->uc_mcontext.gregs[REG_EBP];
    state.reg[DWARF_ESP] = uc->uc_mcontext.gregs[REG_ESP];
    state.reg[DWARF_EIP] = uc->uc_mcontext.gregs[REG_EIP];
#endif

    memory_t memory;
    init_memory_ptrace(&memory, tid);
    return unwind_backtrace_common(&memory, context->map_info_list,
                                   &state, backtrace, ignore_depth, max_depth);
#endif
}