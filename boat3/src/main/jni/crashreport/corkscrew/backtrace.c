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

#define LOG_TAG "Corkscrew"
//#define LOG_NDEBUG 0

#include <corkscrew/backtrace-arch.h>
#include "backtrace-helper.h"
#include <corkscrew/ptrace-arch.h>
#include <corkscrew/map_info.h>
#include <corkscrew/symbol_table.h>
#include <corkscrew/ptrace.h>
#include <corkscrew/demangle.h>

#include <unistd.h>
#include <signal.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <pthread.h>
#include <unwind.h>
#include <cutils/log.h>

#define __USE_GNU // For dladdr(3) in glibc.
#include <dlfcn.h>

#if defined(__BIONIC__)

// Bionic implements and exports gettid but only implements tgkill.
extern int tgkill(int tgid, int tid, int sig);

#elif defined(__APPLE__)

#include <sys/syscall.h>

// Mac OS >= 10.6 has a system call equivalent to Linux's gettid().
static pid_t gettid() {
  return syscall(SYS_thread_selfid);
}

#else

// glibc doesn't implement or export either gettid or tgkill.

#include <unistd.h>
#include <sys/syscall.h>

static pid_t gettid() {
  return syscall(__NR_gettid);
}

static int tgkill(int tgid, int tid, int sig) {
  return syscall(__NR_tgkill, tgid, tid, sig);
}

#endif

typedef struct {
    backtrace_frame_t* backtrace;
    size_t ignore_depth;
    size_t max_depth;
    size_t ignored_frames;
    size_t returned_frames;
    memory_t memory;
} backtrace_state_t;

static _Unwind_Reason_Code unwind_backtrace_callback(struct _Unwind_Context* context, void* arg) {
    backtrace_state_t* state = (backtrace_state_t*)arg;
    uintptr_t pc = _Unwind_GetIP(context);
    if (pc) {
        // TODO: Get information about the stack layout from the _Unwind_Context.
        //       This will require a new architecture-specific function to query
        //       the appropriate registers.  Current callers of unwind_backtrace
        //       don't need this information, so we won't bother collecting it just yet.
        add_backtrace_entry(rewind_pc_arch(&state->memory, pc), state->backtrace,
                state->ignore_depth, state->max_depth,
                &state->ignored_frames, &state->returned_frames);
    }
    return state->returned_frames < state->max_depth ? _URC_NO_REASON : _URC_END_OF_STACK;
}

ssize_t unwind_backtrace(backtrace_frame_t* backtrace, size_t ignore_depth, size_t max_depth) {
    ALOGV("Unwinding current thread %d.", gettid());

    map_info_t* milist = acquire_my_map_info_list();

    backtrace_state_t state;
    state.backtrace = backtrace;
    state.ignore_depth = ignore_depth;
    state.max_depth = max_depth;
    state.ignored_frames = 0;
    state.returned_frames = 0;
    init_memory(&state.memory, milist);

    _Unwind_Reason_Code rc = _Unwind_Backtrace(unwind_backtrace_callback, &state);

    release_my_map_info_list(milist);

    if (state.returned_frames) {
        return state.returned_frames;
    }
    return rc == _URC_END_OF_STACK ? 0 : -1;
}

ssize_t unwind_backtrace_ptrace(pid_t tid, const ptrace_context_t* context,
        backtrace_frame_t* backtrace, size_t ignore_depth, size_t max_depth) {
#ifdef CORKSCREW_HAVE_ARCH
    return unwind_backtrace_ptrace_arch(tid, context, backtrace, ignore_depth, max_depth);
#else
    return -1;
#endif
}

static void init_backtrace_symbol(backtrace_symbol_t* symbol, uintptr_t pc) {
    symbol->relative_pc = pc;
    symbol->relative_symbol_addr = 0;
    symbol->map_name = NULL;
    symbol->symbol_name = NULL;
//    symbol->demangled_name = NULL;
}

void get_backtrace_symbols(const backtrace_frame_t* backtrace, size_t frames,
        backtrace_symbol_t* backtrace_symbols) {
    map_info_t* milist = acquire_my_map_info_list();
    for (size_t i = 0; i < frames; i++) {
        const backtrace_frame_t* frame = &backtrace[i];
        backtrace_symbol_t* symbol = &backtrace_symbols[i];
        init_backtrace_symbol(symbol, frame->absolute_pc);

        const map_info_t* mi = find_map_info(milist, frame->absolute_pc);
        if (mi) {
            symbol->relative_pc = frame->absolute_pc - mi->start;
            if (mi->name[0]) {
                symbol->map_name = strdup(mi->name);
            }
            Dl_info info;
            if (dladdr((const void*)frame->absolute_pc, &info) && info.dli_sname) {
                symbol->relative_symbol_addr = (uintptr_t)info.dli_saddr
                        - (uintptr_t)info.dli_fbase;
                symbol->symbol_name = strdup(info.dli_sname);
//                symbol->demangled_name = demangle_symbol_name(symbol->symbol_name);
            }
        }
    }
    release_my_map_info_list(milist);
}

void get_backtrace_symbols_ptrace(const ptrace_context_t* context,
        const backtrace_frame_t* backtrace, size_t frames,
        backtrace_symbol_t* backtrace_symbols) {
    for (size_t i = 0; i < frames; i++) {
        const backtrace_frame_t* frame = &backtrace[i];
        backtrace_symbol_t* symbol = &backtrace_symbols[i];
        init_backtrace_symbol(symbol, frame->absolute_pc);

        const map_info_t* mi;
        const symbol_t* s;
        find_symbol_ptrace(context, frame->absolute_pc, &mi, &s);
        if (mi) {
            symbol->relative_pc = frame->absolute_pc - mi->start;
            if (mi->name[0]) {
                symbol->map_name = strdup(mi->name);
            }
        }
        if (s) {
            symbol->relative_symbol_addr = s->start;
            symbol->symbol_name = strdup(s->name);
//            symbol->demangled_name = demangle_symbol_name(symbol->symbol_name);
        }
    }
}

void free_backtrace_symbols(backtrace_symbol_t* backtrace_symbols, size_t frames) {
    for (size_t i = 0; i < frames; i++) {
        backtrace_symbol_t* symbol = &backtrace_symbols[i];
        free(symbol->map_name);
        free(symbol->symbol_name);
//        free(symbol->demangled_name);
        init_backtrace_symbol(symbol, 0);
    }
}

void format_backtrace_line(unsigned frameNumber, const backtrace_frame_t* frame __attribute__((unused)),
        const backtrace_symbol_t* symbol, char* buffer, size_t bufferSize) {
    const char* mapName = symbol->map_name ? symbol->map_name : "<unknown>";
    const char* symbolName = /*symbol->demangled_name ? symbol->demangled_name :*/ symbol->symbol_name;
    int fieldWidth = (bufferSize - 80) / 2;
    if (symbolName) {
        uint32_t pc_offset = symbol->relative_pc - symbol->relative_symbol_addr;
        if (pc_offset) {
            snprintf(buffer, bufferSize, "#%02u  pc %08x  %.*s (%.*s+%u)",
                    frameNumber, (unsigned int) symbol->relative_pc,
                    fieldWidth, mapName, fieldWidth, symbolName, pc_offset);
        } else {
            snprintf(buffer, bufferSize, "#%02u  pc %08x  %.*s (%.*s)",
                    frameNumber, (unsigned int) symbol->relative_pc,
                    fieldWidth, mapName, fieldWidth, symbolName);
        }
    } else {
        snprintf(buffer, bufferSize, "#%02u  pc %08x  %.*s",
                frameNumber, (unsigned int) symbol->relative_pc,
                fieldWidth, mapName);
    }
}
