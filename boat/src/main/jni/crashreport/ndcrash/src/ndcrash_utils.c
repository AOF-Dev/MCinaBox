#include "ndcrash_utils.h"
#include <string.h>
#include <sys/socket.h>
#include <dirent.h>
#include <stdio.h>
#include <stdlib.h>

void ndcrash_out_fill_sockaddr(const char *socket_name, struct sockaddr_un *out_addr) {
    size_t socket_name_length = strlen(socket_name);
    // Discarding exceeding characters.
    if (socket_name_length > UNIX_PATH_MAX - 1) {
        socket_name_length = UNIX_PATH_MAX - 1;
    }
    memset(out_addr, 0, sizeof(struct sockaddr_un));
    out_addr->sun_family = PF_UNIX;
    // The socket is abstract, the first byte should be 0. See "man 7 unix" for details.
    out_addr->sun_path[0] = '\0';
    memcpy(out_addr->sun_path + 1, socket_name, socket_name_length);
}

size_t ndcrash_get_threads(pid_t pid, pid_t *out, size_t size) {

    // Should have sufficient space to save "/proc/2147483647/task" including \0.
    char path[22];
    snprintf(path, sizeof(path), "/proc/%d/task", (int) pid);

    // Opening a directory for iteration.
    DIR *dir = opendir(path);
    if (!dir) return 0;

    // Iterating.
    pid_t *it = out;
    struct dirent *entry;
    while (it - out < size && (entry = readdir(dir))) {
        const pid_t tid = (pid_t) atoi(entry->d_name);
        if (!tid || tid == pid) continue;
        *it = tid;
        ++it;
    }

    // Closing.
    closedir(dir);

    return it - out;
}