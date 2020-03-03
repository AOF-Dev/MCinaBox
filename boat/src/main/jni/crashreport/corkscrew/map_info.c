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

#include <corkscrew/map_info.h>

#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>
#include <pthread.h>
#include <unistd.h>
#include <cutils/log.h>
#include <sys/time.h>

#if defined(__APPLE__)

// Mac OS vmmap(1) output:
// __TEXT                 0009f000-000a1000 [    8K     8K] r-x/rwx SM=COW  /Volumes/android/dalvik-dev/out/host/darwin-x86/bin/libcorkscrew_test\n
// 012345678901234567890123456789012345678901234567890123456789
// 0         1         2         3         4         5
static map_info_t* parse_vmmap_line(const char* line) {
    unsigned long int start;
    unsigned long int end;
    char permissions[4];
    int name_pos;
    if (sscanf(line, "%*21c %lx-%lx [%*13c] %3c/%*3c SM=%*3c  %n",
               &start, &end, permissions, &name_pos) != 3) {
        return NULL;
    }

    const char* name = line + name_pos;
    size_t name_len = strlen(name);

    map_info_t* mi = calloc(1, sizeof(map_info_t) + name_len);
    if (mi != NULL) {
        mi->start = start;
        mi->end = end;
        mi->is_readable = permissions[0] == 'r';
        mi->is_writable = permissions[1] == 'w';
        mi->is_executable = permissions[2] == 'x';
        mi->data = NULL;
        memcpy(mi->name, name, name_len);
        mi->name[name_len - 1] = '\0';
        ALOGV("Parsed map: start=0x%08x, end=0x%08x, "
              "is_readable=%d, is_writable=%d is_executable=%d, name=%s",
              mi->start, mi->end,
              mi->is_readable, mi->is_writable, mi->is_executable, mi->name);
    }
    return mi;
}

map_info_t* load_map_info_list(pid_t pid) {
    char cmd[1024];
    snprintf(cmd, sizeof(cmd), "vmmap -w -resident -submap -allSplitLibs -interleaved %d", pid);
    FILE* fp = popen(cmd, "r");
    if (fp == NULL) {
        return NULL;
    }

    char line[1024];
    map_info_t* milist = NULL;
    while (fgets(line, sizeof(line), fp) != NULL) {
        map_info_t* mi = parse_vmmap_line(line);
        if (mi != NULL) {
            mi->next = milist;
            milist = mi;
        }
    }
    pclose(fp);
    return milist;
}

#else

// Linux /proc/<pid>/maps lines:
// 6f000000-6f01e000 rwxp 00000000 00:0c 16389419   /system/lib/libcomposer.so\n
// 012345678901234567890123456789012345678901234567890123456789
// 0         1         2         3         4         5
static map_info_t* parse_maps_line(const char* line)
{
    unsigned long int start;
    unsigned long int end;
    char permissions[5];
    int name_pos;
    if (sscanf(line, "%lx-%lx %4s %*x %*x:%*x %*d%n", &start, &end,
            permissions, &name_pos) != 3) {
        return NULL;
    }

    while (isspace(line[name_pos])) {
        name_pos += 1;
    }
    const char* name = line + name_pos;
    size_t name_len = strlen(name);
    if (name_len && name[name_len - 1] == '\n') {
        name_len -= 1;
    }

    map_info_t* mi = calloc(1, sizeof(map_info_t) + name_len + 1);
    if (mi) {
        mi->start = start;
        mi->end = end;
        mi->is_readable = strlen(permissions) == 4 && permissions[0] == 'r';
        mi->is_writable = strlen(permissions) == 4 && permissions[1] == 'w';
        mi->is_executable = strlen(permissions) == 4 && permissions[2] == 'x';
        mi->data = NULL;
        memcpy(mi->name, name, name_len);
        mi->name[name_len] = '\0';
        ALOGV("Parsed map: start=0x%08x, end=0x%08x, "
              "is_readable=%d, is_writable=%d, is_executable=%d, name=%s",
              mi->start, mi->end,
              mi->is_readable, mi->is_writable, mi->is_executable, mi->name);
    }
    return mi;
}

map_info_t* load_map_info_list(pid_t tid) {
    char line[1024];
    FILE* fp;
    map_info_t* milist = NULL;

    snprintf(line, sizeof(line), "/proc/%d/maps", tid);
    fp = fopen(line, "r");
    if (fp) {
        while(fgets(line, sizeof(line), fp)) {
            map_info_t* mi = parse_maps_line(line);
            if (mi) {
                mi->next = milist;
                milist = mi;
            }
        }
        fclose(fp);
    }
    return milist;
}

#endif

void free_map_info_list(map_info_t* milist) {
    while (milist) {
        map_info_t* next = milist->next;
        free(milist);
        milist = next;
    }
}

const map_info_t* find_map_info(const map_info_t* milist, uintptr_t addr) {
    const map_info_t* mi = milist;
    while (mi && !(addr >= mi->start && addr < mi->end)) {
        mi = mi->next;
    }
    return mi;
}

bool is_readable_map(const map_info_t* milist, uintptr_t addr) {
    const map_info_t* mi = find_map_info(milist, addr);
    return mi && mi->is_readable;
}

bool is_writable_map(const map_info_t* milist, uintptr_t addr) {
    const map_info_t* mi = find_map_info(milist, addr);
    return mi && mi->is_writable;
}

bool is_executable_map(const map_info_t* milist, uintptr_t addr) {
    const map_info_t* mi = find_map_info(milist, addr);
    return mi && mi->is_executable;
}

static pthread_mutex_t g_my_map_info_list_mutex = PTHREAD_MUTEX_INITIALIZER;
static map_info_t* g_my_map_info_list = NULL;

static const int64_t MAX_CACHE_AGE = 5 * 1000 * 1000000LL;

typedef struct {
    uint32_t refs;
    int64_t timestamp;
} my_map_info_data_t;

static int64_t now_ns() {
#if defined(HAVE_POSIX_CLOCKS)
    struct timespec t;
    t.tv_sec = t.tv_nsec = 0;
    clock_gettime(CLOCK_MONOTONIC, &t);
    return t.tv_sec * 1000000000LL + t.tv_nsec;
#else
    struct timeval t;
    gettimeofday(&t, NULL);
    return t.tv_sec * 1000000000LL + t.tv_usec * 1000LL;
#endif
}

static void dec_ref(map_info_t* milist, my_map_info_data_t* data) {
    if (!--data->refs) {
        ALOGV("Freed my_map_info_list %p.", milist);
        free(data);
        free_map_info_list(milist);
    }
}

map_info_t* acquire_my_map_info_list() {
    pthread_mutex_lock(&g_my_map_info_list_mutex);

    int64_t time = now_ns();
    if (g_my_map_info_list != NULL) {
        my_map_info_data_t* data = (my_map_info_data_t*)g_my_map_info_list->data;
        int64_t age = time - data->timestamp;
        if (age >= MAX_CACHE_AGE) {
            ALOGV("Invalidated my_map_info_list %p, age=%lld.", g_my_map_info_list, age);
            dec_ref(g_my_map_info_list, data);
            g_my_map_info_list = NULL;
        } else {
            ALOGV("Reusing my_map_info_list %p, age=%lld.", g_my_map_info_list, age);
        }
    }

    if (g_my_map_info_list == NULL) {
        my_map_info_data_t* data = (my_map_info_data_t*)malloc(sizeof(my_map_info_data_t));
        g_my_map_info_list = load_map_info_list(getpid());
        if (g_my_map_info_list != NULL) {
            ALOGV("Loaded my_map_info_list %p.", g_my_map_info_list);
            g_my_map_info_list->data = data;
            data->refs = 1;
            data->timestamp = time;
        } else {
            free(data);
        }
    }

    map_info_t* milist = g_my_map_info_list;
    if (milist) {
        my_map_info_data_t* data = (my_map_info_data_t*)g_my_map_info_list->data;
        data->refs += 1;
    }

    pthread_mutex_unlock(&g_my_map_info_list_mutex);
    return milist;
}

void release_my_map_info_list(map_info_t* milist) {
    if (milist) {
        pthread_mutex_lock(&g_my_map_info_list_mutex);

        my_map_info_data_t* data = (my_map_info_data_t*)milist->data;
        dec_ref(milist, data);

        pthread_mutex_unlock(&g_my_map_info_list_mutex);
    }
}

void flush_my_map_info_list() {
    pthread_mutex_lock(&g_my_map_info_list_mutex);

    if (g_my_map_info_list != NULL) {
        my_map_info_data_t* data = (my_map_info_data_t*) g_my_map_info_list->data;
        dec_ref(g_my_map_info_list, data);
        g_my_map_info_list = NULL;
    }

    pthread_mutex_unlock(&g_my_map_info_list_mutex);
}
