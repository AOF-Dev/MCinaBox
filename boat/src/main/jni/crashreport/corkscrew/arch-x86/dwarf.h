/*
 * Copyright (C) 2013 The Android Open Source Project
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
 * Dwarf2 data encoding flags.
 */

#define DW_EH_PE_absptr         0x00
#define DW_EH_PE_omit           0xff
#define DW_EH_PE_uleb128        0x01
#define DW_EH_PE_udata2         0x02
#define DW_EH_PE_udata4         0x03
#define DW_EH_PE_udata8         0x04
#define DW_EH_PE_sleb128        0x09
#define DW_EH_PE_sdata2         0x0A
#define DW_EH_PE_sdata4         0x0B
#define DW_EH_PE_sdata8         0x0C
#define DW_EH_PE_signed         0x08
#define DW_EH_PE_pcrel          0x10
#define DW_EH_PE_textrel        0x20
#define DW_EH_PE_datarel        0x30
#define DW_EH_PE_funcrel        0x40
#define DW_EH_PE_aligned        0x50
#define DW_EH_PE_indirect       0x80

/*
 * Dwarf2 call frame instructions.
 */

typedef enum {
    DW_CFA_advance_loc = 0x40,
    DW_CFA_offset = 0x80,
    DW_CFA_restore = 0xc0,
    DW_CFA_nop = 0x00,
    DW_CFA_set_loc = 0x01,
    DW_CFA_advance_loc1 = 0x02,
    DW_CFA_advance_loc2 = 0x03,
    DW_CFA_advance_loc4 = 0x04,
    DW_CFA_offset_extended = 0x05,
    DW_CFA_restore_extended = 0x06,
    DW_CFA_undefined = 0x07,
    DW_CFA_same_value = 0x08,
    DW_CFA_register = 0x09,
    DW_CFA_remember_state = 0x0a,
    DW_CFA_restore_state = 0x0b,
    DW_CFA_def_cfa = 0x0c,
    DW_CFA_def_cfa_register = 0x0d,
    DW_CFA_def_cfa_offset = 0x0e
} dwarf_CFA;

/*
 * eh_frame_hdr information.
*/

typedef struct {
      uint8_t version;
      uint8_t eh_frame_ptr_enc;
      uint8_t fde_count_enc;
      uint8_t fde_table_enc;
      uintptr_t eh_frame_ptr;
      uint32_t fde_count;
} eh_frame_hdr_info_t;

/*
 * CIE information.
*/

typedef struct {
      uint8_t version;
      uint32_t code_align;
      uint32_t data_align;
      uint32_t reg;
      uint32_t aug_z;
      uint8_t aug_L;
      uint8_t aug_R;
      uint8_t aug_S;
      uint32_t aug_P;
} cie_info_t;

/*
 * FDE information.
*/

typedef struct {
      uint32_t start;
      uint32_t length; // number of instructions covered by FDE
      uint32_t aug_z;
      uint32_t aug_L;
} fde_info_t;

/*
 * Dwarf state.
*/

/* Stack of states: required for DW_CFA_remember_state/DW_CFA_restore_state
   30 should be enough */
#define DWARF_STATES_STACK 30

typedef struct {
    char rule;         // rule: o - offset(value); r - register(value)
    uint32_t value;    // value
} reg_rule_t;

/* Dwarf preserved number of registers for x86. */

#define DWARF_REGISTERS 17

typedef struct {
    uintptr_t loc;     // location (ip)
    uint8_t cfa_reg;   // index of register where CFA location stored
    intptr_t cfa_off;  // offset
    reg_rule_t regs[DWARF_REGISTERS]; // dwarf preserved registers for x86
} dwarf_state_t;

/* DWARF registers we are caring about. */

#define DWARF_EAX     0
#define DWARF_ECX     1
#define DWARF_EDX     2
#define DWARF_EBX     3
#define DWARF_ESP     4
#define DWARF_EBP     5
#define DWARF_ESI     6
#define DWARF_EDI     7
#define DWARF_EIP     8


