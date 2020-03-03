#ifndef NDCRASH_MEMORY_MAP_H
#define NDCRASH_MEMORY_MAP_H
#include <stdint.h>
#include <stdbool.h>
#include <sys/types.h>

/**
 * Callback type for a memory map parser. Note that now only getting start and end addresses is
 * supported.
 * @param start Start address of region, inclusive.
 * @param end End address of region, exclusive.
 * @param data Auxiliary data passed from parsing function.
 * @param stop Pointer to a flag which allows us to stop memory maps parsing.
 */
typedef void (*ndcrash_memory_map_entry_callback)(uintptr_t start, uintptr_t end, void *data, bool *stop);

/**
 * Parses memory map for specified pid. Calls callback for each line of map providing values to it.
 * @param pid Process id which memory map to parse.
 * @param callback Callback which is called for each line during parsing.
 * @param data Auxiliary data passed to callback.
 */
void ndcrash_parse_memory_map(pid_t pid, ndcrash_memory_map_entry_callback callback, void *data);

#endif //NDCRASH_MEMORY_MAP_H
