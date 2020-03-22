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

#include <corkscrew/ptrace-arch.h>

#include <stddef.h>
#include <linux/elf.h>
#include <cutils/log.h>

static void load_eh_frame_hdr(pid_t pid, map_info_t* mi, uintptr_t *eh_frame_hdr) {
    uint32_t elf_phoff;
    uint32_t elf_phentsize_ehsize;
    uint32_t elf_shentsize_phnum;
    if (try_get_word_ptrace(pid, mi->start + offsetof(Elf32_Ehdr, e_phoff), &elf_phoff)
            && try_get_word_ptrace(pid, mi->start + offsetof(Elf32_Ehdr, e_ehsize),
                    &elf_phentsize_ehsize)
            && try_get_word_ptrace(pid, mi->start + offsetof(Elf32_Ehdr, e_phnum),
                    &elf_shentsize_phnum)) {
        uint32_t elf_phentsize = elf_phentsize_ehsize >> 16;
        uint32_t elf_phnum = elf_shentsize_phnum & 0xffff;
        for (uint32_t i = 0; i < elf_phnum; i++) {
            uintptr_t elf_phdr = mi->start + elf_phoff + i * elf_phentsize;
            uint32_t elf_phdr_type;
            if (!try_get_word_ptrace(pid, elf_phdr + offsetof(Elf32_Phdr, p_type), &elf_phdr_type)) {
                break;
            }
            if (elf_phdr_type == PT_GNU_EH_FRAME) {
                uint32_t elf_phdr_offset;
                if (!try_get_word_ptrace(pid, elf_phdr + offsetof(Elf32_Phdr, p_offset),
                        &elf_phdr_offset)) {
                    break;
                }
                *eh_frame_hdr = mi->start + elf_phdr_offset;
                ALOGV("Parsed .eh_frame_hdr info for %s: start=0x%08x", mi->name, *eh_frame_hdr);
                return;
            }
        }
    }
    *eh_frame_hdr = 0;
}

void load_ptrace_map_info_data_arch(pid_t pid, map_info_t* mi, map_info_data_t* data) {
    load_eh_frame_hdr(pid, mi, &data->eh_frame_hdr);
}

void free_ptrace_map_info_data_arch(map_info_t* mi __attribute__((unused)),
                                    map_info_data_t* data __attribute__((unused))) {
}
