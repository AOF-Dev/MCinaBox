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

#include <corkscrew/symbol_table.h>

#include <stdbool.h>
#include <stdlib.h>
#include <fcntl.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/mman.h>
#include <cutils/log.h>
#include <unistd.h>

#if defined(__APPLE__)
#else

#include <elf.h>

static bool is_elf(Elf32_Ehdr* e) {
    return (e->e_ident[EI_MAG0] == ELFMAG0 &&
            e->e_ident[EI_MAG1] == ELFMAG1 &&
            e->e_ident[EI_MAG2] == ELFMAG2 &&
            e->e_ident[EI_MAG3] == ELFMAG3);
}

#endif

// Compare function for qsort
static int qcompar(const void *a, const void *b) {
    const symbol_t* asym = (const symbol_t*)a;
    const symbol_t* bsym = (const symbol_t*)b;
    if (asym->start > bsym->start) return 1;
    if (asym->start < bsym->start) return -1;
    return 0;
}

// Compare function for bsearch
static int bcompar(const void *key, const void *element) {
    uintptr_t addr = *(const uintptr_t*)key;
    const symbol_t* symbol = (const symbol_t*)element;
    if (addr < symbol->start) return -1;
    if (addr >= symbol->end) return 1;
    return 0;
}

symbol_table_t* load_symbol_table(const char *filename) {
    symbol_table_t* table = NULL;
#if !defined(__APPLE__)
    ALOGV("Loading symbol table from '%s'.", filename);

    int fd = open(filename, O_RDONLY);
    if (fd < 0) {
        goto out;
    }

    struct stat sb;
    if (fstat(fd, &sb)) {
        goto out_close;
    }

    size_t length = sb.st_size;
    char* base = mmap(NULL, length, PROT_READ, MAP_PRIVATE, fd, 0);
    if (base == MAP_FAILED) {
        goto out_close;
    }

    // Parse the file header
    Elf32_Ehdr *hdr = (Elf32_Ehdr*)base;
    if (!is_elf(hdr)) {
        goto out_close;
    }
    Elf32_Shdr *shdr = (Elf32_Shdr*)(base + hdr->e_shoff);

    // Search for the dynamic symbols section
    int sym_idx = -1;
    int dynsym_idx = -1;
    for (Elf32_Half i = 0; i < hdr->e_shnum; i++) {
        if (shdr[i].sh_type == SHT_SYMTAB) {
            sym_idx = i;
        }
        if (shdr[i].sh_type == SHT_DYNSYM) {
            dynsym_idx = i;
        }
    }
    if (dynsym_idx == -1 && sym_idx == -1) {
        goto out_unmap;
    }

    table = malloc(sizeof(symbol_table_t));
    if(!table) {
        goto out_unmap;
    }
    table->num_symbols = 0;

    Elf32_Sym *dynsyms = NULL;
    int dynnumsyms = 0;
    char *dynstr = NULL;
    if (dynsym_idx != -1) {
        dynsyms = (Elf32_Sym*)(base + shdr[dynsym_idx].sh_offset);
        dynnumsyms = shdr[dynsym_idx].sh_size / shdr[dynsym_idx].sh_entsize;
        int dynstr_idx = shdr[dynsym_idx].sh_link;
        dynstr = base + shdr[dynstr_idx].sh_offset;
    }

    Elf32_Sym *syms = NULL;
    int numsyms = 0;
    char *str = NULL;
    if (sym_idx != -1) {
        syms = (Elf32_Sym*)(base + shdr[sym_idx].sh_offset);
        numsyms = shdr[sym_idx].sh_size / shdr[sym_idx].sh_entsize;
        int str_idx = shdr[sym_idx].sh_link;
        str = base + shdr[str_idx].sh_offset;
    }

    int dynsymbol_count = 0;
    if (dynsym_idx != -1) {
        // Iterate through the dynamic symbol table, and count how many symbols
        // are actually defined
        for (int i = 0; i < dynnumsyms; i++) {
            if (dynsyms[i].st_shndx != SHN_UNDEF) {
                dynsymbol_count++;
            }
        }
    }

    size_t symbol_count = 0;
    if (sym_idx != -1) {
        // Iterate through the symbol table, and count how many symbols
        // are actually defined
        for (int i = 0; i < numsyms; i++) {
            if (syms[i].st_shndx != SHN_UNDEF
                    && str[syms[i].st_name]
                    && syms[i].st_value
                    && syms[i].st_size) {
                symbol_count++;
            }
        }
    }

    // Now, create an entry in our symbol table structure for each symbol...
    table->num_symbols += symbol_count + dynsymbol_count;
    table->symbols = malloc(table->num_symbols * sizeof(symbol_t));
    if (!table->symbols) {
        free(table);
        table = NULL;
        goto out_unmap;
    }

    size_t symbol_index = 0;
    if (dynsym_idx != -1) {
        // ...and populate them
        for (int i = 0; i < dynnumsyms; i++) {
            if (dynsyms[i].st_shndx != SHN_UNDEF) {
                table->symbols[symbol_index].name = strdup(dynstr + dynsyms[i].st_name);
                table->symbols[symbol_index].start = dynsyms[i].st_value;
                table->symbols[symbol_index].end = dynsyms[i].st_value + dynsyms[i].st_size;
                ALOGV("  [%d] '%s' 0x%08x-0x%08x (DYNAMIC)",
                        symbol_index, table->symbols[symbol_index].name,
                        table->symbols[symbol_index].start, table->symbols[symbol_index].end);
                symbol_index += 1;
            }
        }
    }

    if (sym_idx != -1) {
        // ...and populate them
        for (int i = 0; i < numsyms; i++) {
            if (syms[i].st_shndx != SHN_UNDEF
                    && str[syms[i].st_name]
                    && syms[i].st_value
                    && syms[i].st_size) {
                table->symbols[symbol_index].name = strdup(str + syms[i].st_name);
                table->symbols[symbol_index].start = syms[i].st_value;
                table->symbols[symbol_index].end = syms[i].st_value + syms[i].st_size;
                ALOGV("  [%d] '%s' 0x%08x-0x%08x",
                        symbol_index, table->symbols[symbol_index].name,
                        table->symbols[symbol_index].start, table->symbols[symbol_index].end);
                symbol_index += 1;
            }
        }
    }

    // Sort the symbol table entries, so they can be bsearched later
    qsort(table->symbols, table->num_symbols, sizeof(symbol_t), qcompar);

out_unmap:
    munmap(base, length);

out_close:
    close(fd);
#endif

out:
    return table;
}

void free_symbol_table(symbol_table_t* table) {
    if (table) {
        for (size_t i = 0; i < table->num_symbols; i++) {
            free(table->symbols[i].name);
        }
        free(table->symbols);
        free(table);
    }
}

const symbol_t* find_symbol(const symbol_table_t* table, uintptr_t addr) {
    if (!table) return NULL;
    return (const symbol_t*)bsearch(&addr, table->symbols, table->num_symbols,
            sizeof(symbol_t), bcompar);
}
