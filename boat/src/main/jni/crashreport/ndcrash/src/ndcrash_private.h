#ifndef NDCRASH_PRIVATE_H
#define NDCRASH_PRIVATE_H
#include "sizeofa.h"
#include <signal.h>
#include <ucontext.h>

/// Array of constants with signal numbers to catch.
static const int SIGNALS_TO_CATCH[] = {
        SIGABRT,
        SIGBUS,
        SIGFPE,
        SIGSEGV,
        SIGILL,
#if defined(SIGSTKFLT)
        SIGSTKFLT,
#endif
        SIGTRAP,
};

/// Count of signals to catch
static const int NUM_SIGNALS_TO_CATCH = sizeofa(SIGNALS_TO_CATCH);

/// Struct for message that is sent from signal handler to daemon in out-of-process architecture.
struct ndcrash_out_message
{
    /// Identifier of crashed process (Linux thread group id)
    pid_t pid;

    /// Identifier of crashed thread.
    pid_t tid;

    /// Number of signal that was received on crash.
    int signo;

    /// si_code value from siginfo structure which is passed to signal handler.
    int si_code;

    /// si_addr field from siginfo structure which is passed to signal handler.
    void *faultaddr;

    /// Processor context value in moment of crash. 3rd argument of a signal handler.
    struct ucontext context;
};

/**
 * Type of pointer to unwinding function for in-process unwinding.
 * @param outfile Output file descriptor for a crash dump.
 * @param context processor state at a moment of crash.
 */
typedef void (*ndcrash_in_unwind_func_ptr)(int outfile, struct ucontext *context);

/**
 * Type of pointer to unwinder initialization function for out-of-process unwinding. Does some
 * platform specific set up required before unwinding for all threads is started, for example,
 * parses a memory map.
 * @param outfile Output file descriptor for a crash dump.
 * @param pid Crashed process identifier. It's an id of a main thread (thread group id).
 * @param context A processor context (all register values) where to start unwinding. If null
 * a context is obtained by ptrace. Typically it's non-null for a main thread and null for all
 * other threads.
 * @return pointer to opaque unwinder-specific data. Theoretically may be null if an unwinder
 * doesn't need any preliminary setup.
 */
typedef void * (*ndcrash_out_unwinder_init_func_ptr)(pid_t pid);

/**
 * Type of pointer to unwinding function for out-of-process unwinding.
 * @param outfile Output file descriptor for a crash dump.
 * @param tid Thread id being unwound.
 * @param context A processor context (all register values) where to start unwinding. If null
 * a context is obtained by ptrace. Typically it's non-null for a main thread and null for all
 * other threads.
 * @param data A result of initialization function. Theoretically may be null.
 */
typedef void (*ndcrash_out_unwind_func_ptr)(int outfile, pid_t tid, struct ucontext *context, void *data);

/**
 * Type of pointer to unwinder de-initialization function. Should free resources allocated by
 * unwinder initialization function.
 * @param data A result of initialization function. Theoretically may be null.
 */
typedef void (*ndcrash_out_unwinder_deinit_func_ptr)(void *data);

/// This macro allows us to configure maximum lines count in backtrace.
#ifndef NDCRASH_MAX_FRAMES
#define NDCRASH_MAX_FRAMES 128
#endif

/// This macro allows us to configure maximum function name length. Used for buffer size.
#ifndef NDCRASH_MAX_FUNCTION_NAME_LENGTH
#define NDCRASH_MAX_FUNCTION_NAME_LENGTH 128
#endif

#endif //NDCRASH_PRIVATE_H
