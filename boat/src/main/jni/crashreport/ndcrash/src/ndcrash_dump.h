#ifndef NDCRASH_DUMP_H
#define NDCRASH_DUMP_H
#include <sys/types.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

struct siginfo;
struct ucontext;

/**
 * Creates an output file for a crash report. Wrapper around open() system call.
 * @param path Path to an output file.
 * @return Result of open() system call (file descriptor on success), see its documentation.
 */
int ndcrash_dump_create_file(const char *path);

/**
 * Write an arbitrary line to a crash dump.
 * @param fd Crash dump file descriptor.
 * @param format Dump line format.
 * @param ... Format arguments.
 */
void ndcrash_dump_write_line(int fd, const char *format, ...);

/**
 * Write a crash report header to a file and to log. Contains an information about crashed thread.
 * @param outfile Output file descriptor for a crash report.
 * @param pid Crashed process identifier.
 * @param tid Crashed thread identifier.
 * @param signo Number of signal that was caught on crash.
 * @param si_code Code of signal that was caught on crash (from siginfo structure).
 * @param faultaddr Optional fault address (from siginfo structure).
 * @param context Execution context a moment of crash.
 */
void ndcrash_dump_header(int outfile, pid_t pid, pid_t tid, int signo, int si_code, void *faultaddr,
                         struct ucontext *context);

/**
 * Write an other thread info (which is not crashed) to a file and to a log.
 * @param outfile Output file descriptor for a crash report.
 * @param pid Process identifier.
 * @param tid Thread identifier.
 */
void ndcrash_dump_other_thread_header(int outfile, pid_t pid, pid_t tid);

/**
 * Write a full line of backtrace to a crash report. Full means that we have all data including
 * function name and instruction offset within a function.
 * @param outfile Output file descriptor for a crash report.
 * @param counter Number of backtrace element.
 * @param pc Program counter value (address of instruction). Relative.
 * @param map_name Name of memory map entry containing this function.
 * @param func_name Name of function. If NULL not printed.
 * @param func_offset Offset of instruction from function start, in bytes. Ignored if func_name is NULL.
 */
void ndcrash_dump_backtrace_line(
        int outfile,
        int counter,
        intptr_t pc,
        const char *map_name,
        const char *func_name,
        intptr_t func_offset);

#ifdef __cplusplus
}
#endif

#endif //NDCRASH_DUMP_H
