#ifndef NDCRASH_SIGNAL_UTILS_H
#define NDCRASH_SIGNAL_UTILS_H
#include <stdbool.h>
#include <signal.h>

/**
 * Retrieves a flag whether signal has si_addr parameter in siginfo.
 * @param si_signo Signal number from signal handler.
 * @param si_code Corresponding field from siginfo struct.
 * @return Flag value.
 */
bool ndcrash_signal_has_si_addr(int si_signo, int si_code);

/**
 * Retrieves signal name string by its integer value.
 * @param sig Signal number value.
 * @return Signal name string. Statically allocated.
 */
const char *ndcrash_get_signame(int sig);

/**
 * Retrieves signal code name string by its integer value.
 * @param sig Signal number value.
 * @param code Signal code value, si_code field from siginfo struct.
 * @return Signal name string. Statically allocated.
 */
const char *ndcrash_get_sigcode(int signo, int code);

/// Type for signal handling function pointer. Should be the same as declared in sigaction struct.
typedef void (* ndcrash_signal_handler_function) (int, struct siginfo *, void *);

/**
 * Registers a passed signal handler saving old handlers to passed array.
 *
 * @param handler A new signal handler. Used for all signals.
 * @param old_handlers Array where to save old handlers. Index is signal numbers, saved only handlers
 * for signals that were successfully registered.
 * @return Flag if registration of all signal handlers is successful.
 */
bool ndcrash_register_signal_handler(ndcrash_signal_handler_function handler, struct sigaction old_handlers[NSIG]);

/**
 * Unregisters previously set signal handlers restoring old taken from passed array.
 * @param old_handlers Previously set signal handlers array.
 */
void ndcrash_unregister_signal_handler(struct sigaction old_handlers[NSIG]);

#endif //NDCRASH_SIGNAL_UTILS_H
