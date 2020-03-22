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

/* Architecture dependent functions. */

#ifndef _CORKSCREW_PTRACE_ARCH_H
#define _CORKSCREW_PTRACE_ARCH_H

#include <corkscrew/ptrace.h>
#include <corkscrew/map_info.h>
#include <corkscrew/symbol_table.h>

#ifdef __cplusplus
extern "C" {
#endif

/* Custom extra data we stuff into map_info_t structures as part
 * of our ptrace_context_t. */
typedef struct {
#ifdef __arm__
    uintptr_t exidx_start;
    size_t exidx_size;
#elif __i386__
    uintptr_t eh_frame_hdr;
#endif
    symbol_table_t* symbol_table;
} map_info_data_t;

void load_ptrace_map_info_data_arch(pid_t pid, map_info_t* mi, map_info_data_t* data);
void free_ptrace_map_info_data_arch(map_info_t* mi, map_info_data_t* data);

#ifdef __cplusplus
}
#endif

#endif // _CORKSCREW_PTRACE_ARCH_H
