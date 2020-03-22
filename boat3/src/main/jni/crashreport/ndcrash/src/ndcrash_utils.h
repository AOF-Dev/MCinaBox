#ifndef NDCRASH_UTILS_H
#define NDCRASH_UTILS_H
#include <stdbool.h>
#include <linux/socket.h>  //////////
#include <linux/un.h>
#include <sys/types.h>

#ifdef __cplusplus
extern "C" {
#endif

/**
 * Fills in sockaddr_un struct with passed socket name.
 * @param socket_name Null-terminated socket name string.
 * @param out_addr Pointer to sockaddr_un structure to fill.
 */
void ndcrash_out_fill_sockaddr(const char *socket_name, struct sockaddr_un *out_addr);

/**
 * Gets all identifiers of threads of passed process excluding main thread.
 *
 * @param pid A process identifier which thread identifiers to get.
 * @param out A buffer where to put identifiers. They are put to beginning of a buffer.
 * @param size A size of out buffer, maximum thread identifiers count.
 * @return Count of thread identifiers.
 */
size_t ndcrash_get_threads(pid_t pid, pid_t *out, size_t size);

#ifdef __cplusplus
}
#endif

#endif //NDCRASH_UTILS_H
