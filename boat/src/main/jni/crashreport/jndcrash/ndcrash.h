#ifndef NDCRASHDEMO_NDCRASH_H
#define NDCRASHDEMO_NDCRASH_H
#include <stdint.h>
#include <stdbool.h>

/**
 * Enum representing supported unwinders for stack unwinding.
 */
enum ndcrash_unwinder {                      // Supported modes
    ndcrash_unwinder_libcorkscrew,           // Both
    ndcrash_unwinder_libunwind,              // Both
    ndcrash_unwinder_libunwindstack,         // Both
    ndcrash_unwinder_cxxabi,                 // In-process only
    ndcrash_unwinder_stackscan,              // In-process only
};

/**
 * Represents a result of ndcrash initialization.
 */
enum ndcrash_error {

    /// No error, everything is ok.
    ndcrash_ok,

    /// NDCrash has already been initialized.
    ndcrash_error_already_initialized,

    /// A selected working mode or unwinder is not supported.
    ndcrash_error_not_supported,

    /// Error during registering a signal handler.
    ndcrash_error_signal,

    /// Error during pipe creation. Pipes are used internally in out-of-process mode.
    ndcrash_error_pipe,

    /// Error during thread creation for out-of-process daemon.
    ndcrash_error_thread,

    /// Wrong socket name error.
    ndcrash_error_socket_name,

    /// Wrong process error. Happens if we try to initialize an out-of-process daemon from a main process.
    ndcrash_error_wrong_process,

    /// A background out-of-process service has failed to start.
    ndcrash_error_service_start_failed,
};

/**
 * Initializes crash reporting library in in-process mode.
 *
 * @param unwinder What unwinder to use for unwinding.
 * @param log_file Path to crash report file where to write it.
 * @return Initialization result.
 */
enum ndcrash_error ndcrash_in_init(const enum ndcrash_unwinder unwinder, const char *log_file);

/**
 * De-initialize crash reporting library in in-process mode. This call will restore previous signal
 * handlers used for crash reporting.
 * @return Flag whether de-initialization is successful.
 */
bool ndcrash_in_deinit();

/**
 * Initializes crash reporting library in out-of-process mode. This method should be called from
 * the main process of an application.
 * @param socket_name Name used to identify UNIX domain socket that is used to communicate with
 * crash reporting daemon. Should be the same string as passed to ndcrash_out_start_daemon function.
 * Shouldn't be null or empty string.
 */
enum ndcrash_error ndcrash_out_init(const char *socket_name);

/**
 * De-initialize crash reporting library in out-of-process mode. This call will restore previous signal
 * handlers used for crash reporting.
 * @return Flag whether de-initialization is successful.
 */
bool ndcrash_out_deinit();

/**
 * Type for start and stop daemon callback.
 *
 * @param arg Argument value that was previously passed to ndcrash_out_start_daemon function.
 */
typedef void (*ndcrash_daemon_start_stop_callback)(void *arg);

/**
 * Type for a crash callback.
 *
 * @param report_file Path to a crash report file that has just been generated.
 * @param arg Argument value that was previously passed to ndcrash_out_start_daemon function.
 */
typedef void (*ndcrash_daemon_crash_callback)(const char *report_file, void *arg);

/**
 * Start an unwinding daemon for out-of-process crash reporting. In out-of-process architecture
 * a daemon performs stack crash report creation, does stack unwinding using ptrace mechanism and
 * saves a crash report to file. This method should be called from a separate process, for example,
 * background service that has android:process attribute.
 *
 * @param socket_name Name used to identify UNIX domain socket that is used to communicate with
 * crash reporting daemon. Should be the same string as passed to ndcrash_out_init function.
 * Shouldn't be null or empty string.
 * @param unwinder What unwinder to use for unwinding.
 * @param log_file Path to crash report file where to write it.
 * @param start_callback Callback executed on successful daemon start from its background thread. NULL if not needed.
 * @param crash_callback Callback executed on successful crash report creation. NULL if not needed.
 * @param stop_callback Callback executed on successful daemon stop from its background thread.  NULL if not needed.
 * @param callback_arg Argument for callbacks. NULL if not needed.
 * @return Initialization result.
 */
enum ndcrash_error ndcrash_out_start_daemon(
        const char *socket_name,
        const enum ndcrash_unwinder unwinder,
        const char *report_file,
        ndcrash_daemon_start_stop_callback start_callback,
        ndcrash_daemon_crash_callback crash_callback,
        ndcrash_daemon_start_stop_callback stop_callback,
        void *callback_arg);

/**
 * Stops an unwinding daemon for out-of-process crash reporting.
 *
 * @return Flag whether a daemon has been successfully stopped.
 */
bool ndcrash_out_stop_daemon();

/**
 * Retrieves argument for callbacks that was previously passed to ndcrash_out_start_daemon function.
 * Should be called before ndcrash_out_stop_daemon.
 * @return Argument value.
 */
void * ndcrash_out_get_daemon_callbacks_arg();

#endif //NDCRASHDEMO_NDCRASH_H
