/*
 * Copyright (C) 2012 The Android Open Source Project
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
 * Backtracing functions for mips
 */

#define LOG_TAG "Corkscrew"
//#define LOG_NDEBUG 0

#include <corkscrew/backtrace-arch.h>
#include "../backtrace-helper.h"
#include <corkscrew/ptrace.h>

#include <stdlib.h>
#include <signal.h>
#include <stdbool.h>
#include <limits.h>
#include <errno.h>
#include <sys/ptrace.h>
#include <sys/exec_elf.h>
#include <cutils/log.h>

/* For PTRACE_GETREGS */
typedef struct {
    /* FIXME: check this definition */
    uint64_t regs[32];
    uint64_t lo;
    uint64_t hi;
    uint64_t epc;
    uint64_t badvaddr;
    uint64_t status;
    uint64_t cause;
} user_regs_struct;

/* Machine context at the time a signal was raised. */
typedef struct ucontext {
    /* FIXME: use correct definition */
    uint32_t sp;
    uint32_t ra;
    uint32_t pc;
} ucontext_t;

/* Unwind state. */
typedef struct {
    uint32_t sp;
    uint32_t ra;
    uint32_t pc;
} unwind_state_t;

uintptr_t rewind_pc_arch(const memory_t* memory, uintptr_t pc) {
    if (pc == 0)
        return pc;
    if ((pc & 1) == 0)
        return pc-8;            /* jal/bal/jalr + branch delay slot */
    return pc;
}

static ssize_t unwind_backtrace_common(const memory_t* memory,
        const map_info_t* map_info_list,
        unwind_state_t* state, backtrace_frame_t* backtrace,
        size_t ignore_depth, size_t max_depth) {
    size_t ignored_frames = 0;
    size_t returned_frames = 0;

    for (size_t index = 0; returned_frames < max_depth; index++) {
        uintptr_t pc = index ? rewind_pc_arch(memory, state->pc) : state->pc;
        backtrace_frame_t* frame;
        uintptr_t addr;
        int maxcheck = 1024;
        int stack_size = 0, ra_offset = 0;
        bool found_start = false;

        frame = add_backtrace_entry(pc, backtrace, ignore_depth,
                                    max_depth, &ignored_frames, &returned_frames);

        if (frame)
            frame->stack_top = state->sp;

        ALOGV("#%d: frame=%p pc=%08x sp=%08x\n",
              index, frame, frame->absolute_pc, frame->stack_top);

        for (addr = state->pc; maxcheck-- > 0 && !found_start; addr -= 4) {
            uint32_t op;
            if (!try_get_word(memory, addr, &op))
                break;

            // ALOGV("@0x%08x: 0x%08x\n", addr, op);
            switch (op & 0xffff0000) {
            case 0x27bd0000: // addiu sp, imm
                {
                    // looking for stack being decremented
                    int32_t immediate = ((((int)op) << 16) >> 16);
                    if (immediate < 0) {
                        stack_size = -immediate;
                        found_start = true;
                        ALOGV("@0x%08x: found stack adjustment=%d\n", addr, stack_size);
                    }
                }
                break;
            case 0xafbf0000: // sw ra, imm(sp)
                ra_offset = ((((int)op) << 16) >> 16);
                ALOGV("@0x%08x: found ra offset=%d\n", addr, ra_offset);
                break;
            case 0x3c1c0000: // lui gp
                ALOGV("@0x%08x: found function boundary\n", addr);
                found_start = true;
                break;
            default:
                break;
            }
        }

        if (ra_offset) {
            uint32_t next_ra;
            if (!try_get_word(memory, state->sp + ra_offset, &next_ra))
                break;
            state->ra = next_ra;
            ALOGV("New ra: 0x%08x\n", state->ra);
        }

        if (stack_size) {
            if (frame)
                frame->stack_size = stack_size;
            state->sp += stack_size;
            ALOGV("New sp: 0x%08x\n", state->sp);
        }

        if (state->pc == state->ra && stack_size == 0)
            break;

        if (state->ra == 0)
            break;

        state->pc = state->ra;
    }

    ALOGV("returning %d frames\n", returned_frames);

    return returned_frames;
}

ssize_t unwind_backtrace_signal_arch(siginfo_t* siginfo, void* sigcontext,
        const map_info_t* map_info_list,
        backtrace_frame_t* backtrace, size_t ignore_depth, size_t max_depth) {
    const ucontext_t* uc = (const ucontext_t*)sigcontext;

    unwind_state_t state;
    state.sp = uc->sp;
    state.pc = uc->pc;
    state.ra = uc->ra;

    ALOGV("unwind_backtrace_signal_arch: "
          "ignore_depth=%d max_depth=%d pc=0x%08x sp=0x%08x ra=0x%08x\n",
          ignore_depth, max_depth, state.pc, state.sp, state.ra);

    memory_t memory;
    init_memory(&memory, map_info_list);
    return unwind_backtrace_common(&memory, map_info_list,
            &state, backtrace, ignore_depth, max_depth);
}

ssize_t unwind_backtrace_ptrace_arch(pid_t tid, const ptrace_context_t* context,
        backtrace_frame_t* backtrace, size_t ignore_depth, size_t max_depth) {

    user_regs_struct regs;
    if (ptrace(PTRACE_GETREGS, tid, 0, &regs)) {
        return -1;
    }

    unwind_state_t state;
    state.sp = regs.regs[29];
    state.ra = regs.regs[31];
    state.pc = regs.epc;

    ALOGV("unwind_backtrace_ptrace_arch: "
          "ignore_depth=%d max_depth=%d pc=0x%08x sp=0x%08x ra=0x%08x\n",
          ignore_depth, max_depth, state.pc, state.sp, state.ra);

    memory_t memory;
    init_memory_ptrace(&memory, tid);
    return unwind_backtrace_common(&memory, context->map_info_list,
            &state, backtrace, ignore_depth, max_depth);
}

ssize_t unwind_backtrace_ptrace_context_arch(pid_t tid, void *sigcontext,
        const ptrace_context_t* context, backtrace_frame_t* backtrace, size_t ignore_depth,
        size_t max_depth)
{
    const ucontext_t* uc = (const ucontext_t*)sigcontext;

    unwind_state_t state;
    state.sp = uc->sp;
    state.pc = uc->pc;
    state.ra = uc->ra;

    memory_t memory;
    init_memory_ptrace(&memory, tid);
    return unwind_backtrace_common(&memory, context->map_info_list,
                                   &state, backtrace, ignore_depth, max_depth);
}