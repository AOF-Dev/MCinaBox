#include "ndcrash_memory_map.h"
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>

void ndcrash_parse_memory_map(pid_t pid, ndcrash_memory_map_entry_callback callback, void *data) {

    // Reading buffer. Should have enough space for 2 pointers, '-' separator and terminating '\0'.
    char buffer[34];

    // Opening input file.
    snprintf(buffer, sizeof(buffer), "/proc/%d/maps", (int) pid);
    const int fd = open(buffer, O_RDONLY);
    if (fd < 0) return;

    // How many bytes are read on each cycle iteration.
    ssize_t bytes_read;

    // An offset within buffer where a data is saved on reading.
    size_t read_offset = 0;

    // Flag if a buffer starts from a new line. Assuming the first line is new.
    bool new_line = true;

    // The last buffer character is reserved for terminating '\0'.
    while ((bytes_read = read(fd, buffer + read_offset, sizeof(buffer) - read_offset - 1)) != 0) {
        if (bytes_read < 0) {
            goto func_end;
        }
        buffer[bytes_read + read_offset] = '\0';
        if (new_line) {
            // Obtaining start and end addresses
            unsigned long int start, end;
            if (sscanf(buffer, "%lx-%lx", &start, &end) != 2) {
                goto func_end;
            }
            bool stop = false;
            callback(start, end, data, &stop);
            if (stop) {
                goto func_end;
            }
        }
        // Searching for a new line.
        ssize_t newline_pos = 0;
        for (; newline_pos != bytes_read; ++newline_pos) {
            if (buffer[newline_pos] == '\n') break;
        }
        if (newline_pos != bytes_read) {
            // How many bytes was read AFTER newline pos.
            read_offset = (size_t) (bytes_read - newline_pos - 1);
            // Moving memory, a byte after new line should be at the beginning of buffer.
            memmove(buffer, buffer + newline_pos + 1, read_offset);
            new_line = true;
        } else {
            read_offset = 0;
            new_line = false;
        }
    }

    func_end:
    close(fd);
}