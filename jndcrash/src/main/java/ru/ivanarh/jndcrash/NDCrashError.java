package ru.ivanarh.jndcrash;

/**
 * Error status. Matches ndcrash_error enum values in ndcrash.h.
 */
public enum NDCrashError {
    /// No error, everything is ok.
    ok,

    /// NDCrash has already been initialized.
    error_already_initialized,

    /// A selected working mode or unwinder is not supported.
    error_not_supported,

    /// Error during registering a signal handler.
    error_signal,

    /// Error during pipe creation. Pipes are used internally in out-of-process mode.
    error_pipe,

    /// Error during thread creation for out-of-process daemon.
    error_thread,

    /// Wrong socket name error.
    error_socket_name,

    /// Wron process error. Happens if we try to initialize an out-of-process daemon from a main process.
    error_wrong_process,

    /// A background out-of-process service has failed to start.
    error_service_start_failed,
}