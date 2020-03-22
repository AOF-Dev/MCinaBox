#include "ndcrash_dump.h"
#include "ndcrash_log.h"
#include "ndcrash_signal_utils.h"
#include "sizeofa.h"
#include <ucontext.h>
#include <unistd.h>
#include <stdio.h>
#include <fcntl.h>
#include <android/log.h>
#include <inttypes.h>
#include <string.h>
#include <errno.h>
#include <sys/system_properties.h>
#include <sys/ptrace.h>
#include <linux/elf.h>

#if __LP64__
#define PRIPTR "016" PRIxPTR
#else
#define PRIPTR "08" PRIxPTR
#endif

/**
 * Reads a file contents from passed filename to output buffer with specified size. Appends '\0'
 * character after file data that has been read.
 * @param filename File name to read.
 * @param outbuffer Buffer where to read a file
 * @param buffersize Size of buffer in bytes.
 * @return Count of read bytes not including terminating '\0' character or -1 or error.
 */
static inline ssize_t ndcrash_read_file(const char *filename, char *outbuffer, size_t buffersize) {
    const int fd = open(filename, O_RDONLY);
    if (fd < 0) return -1;
    ssize_t bytes_read;
    ssize_t overall_read = 0;
    while ((bytes_read = read(fd, outbuffer + overall_read, buffersize - overall_read - 1)) > 0) {
        overall_read += bytes_read;
        if (overall_read >= buffersize - 1) break;
    }
    if (bytes_read < 0) {
        close(fd);
        return -1;
    }
    outbuffer[overall_read] = '\0';
    close(fd);
    return overall_read;
}

int ndcrash_dump_create_file(const char *path) {
    const int result = open(path, O_WRONLY | O_CREAT | O_TRUNC); //, S_IRUSR | S_IWUSR);
    if (result < 0) {
        NDCRASHLOG(
                ERROR,
                "Error creating dump file %s: %s (%d)",
                path,
                strerror(errno),
                errno);
    }
    return result;
}

#ifndef NDCRASH_LOG_BUFFER_SIZE
#define NDCRASH_LOG_BUFFER_SIZE 256
#endif

void ndcrash_dump_write_line(int fd, const char *format, ...) {
    char buffer[NDCRASH_LOG_BUFFER_SIZE];

    // First writing to a log as is.
    {
        va_list args;
        va_start(args, format);
        __android_log_vprint(ANDROID_LOG_ERROR, NDCRASH_LOG_TAG, format, args);
        va_end(args);
    }

    // Writing file to log may be disabled.
    if (fd <= 0) return;

    // Writing to a buffer.
    int printed;
    {
        va_list args;
        va_start(args, format);
        printed = vsnprintf(buffer, NDCRASH_LOG_BUFFER_SIZE, format, args);
        va_end(args);
    }

    // printed contains the number of characters that would have been written if n had been sufficiently
    // large, not counting the terminating null character.
    if (printed > 0) {
        if (printed >= NDCRASH_LOG_BUFFER_SIZE) {
            printed = NDCRASH_LOG_BUFFER_SIZE - 1;
        }
        // Replacing last buffer character with new line.
        buffer[printed] = '\n';

        // Writing to a file including \n character.
        write(fd, buffer, (size_t) printed + 1);
    }
}

/**
 * Writes "backtrace:" line and a new line before it.
 * @param outfile Output file descriptor for a crash report.
 */
static inline void ndcrash_write_backtrace_title(int outfile) {
    ndcrash_dump_write_line(outfile, " ");
    ndcrash_dump_write_line(outfile, "backtrace:");
}

/**
 * Writes a line with an information about process and thread. Reads process and thread names
 * and writes them to a crash dump. Example:
 * "pid: 26823, tid: 26828, name: Jit thread pool  >>> ru.ivanarh.ndcrashdemo <<<"
 * @param outfile Output file descriptor for a crash report.
 * @param pid Process identifier.
 * @param tid Thread identifier.
 * @param process_name_buffer A buffer where process name (/proc/pid/cmdline content) is read. Passing
 * as an argument to reduce a stack usage.
 * @param process_name_buffer_size A size of passed process_name_buffer in bytes.
 */
static void ndcrash_write_process_and_thread_info(
        int outfile,
        pid_t pid,
        pid_t tid,
        char *process_name_buffer,
        size_t process_name_buffer_size) {

    // Buffer used for file path formatting. Max theoretical value is "/proc/2147483647/cmdline"
    // 25 characters with terminating characters.
    char proc_file_path[25];

    // Thread name. Strings longer than TASK_COMM_LEN (16) characters are silently truncated.
    char proc_comm_content[16];

    // Setting first characters for a case when reading is failed.
    process_name_buffer[0] = proc_file_path[0] = '\0';

    // Reading a process name.
    if (snprintf(proc_file_path, sizeofa(proc_file_path), "/proc/%d/cmdline", pid) >= 0) {
        ndcrash_read_file(proc_file_path, process_name_buffer, process_name_buffer_size);
    }

    // Reading a thread name.
    if (snprintf(proc_file_path, sizeofa(proc_file_path), "/proc/%d/comm", tid) >= 0) {
        const ssize_t bytes_read = ndcrash_read_file(proc_file_path, proc_comm_content,
                                                     sizeofa(proc_comm_content));
        // comm usually contains newline character on the end. We don't need it.
        if (bytes_read > 0 && proc_comm_content[bytes_read - 1] == '\n') {
            proc_comm_content[bytes_read - 1] = '\0';
        }
    }

    // Writing to a log and to a file.
    ndcrash_dump_write_line(
            outfile,
            "pid: %d, tid: %d, name: %s  >>> %s <<<",
            pid,
            tid,
            proc_comm_content,
            process_name_buffer);
}

/**
 * Writes a signal information line to a crash report.
 * @param outfile Output file descriptor for a crash report.
 * @param signo Number of signal that was caught on crash.
 * @param si_code Code of signal that was caught on crash (from siginfo structure).
 * @param faultaddr Optional fault address (from siginfo structure).
 * @param str_buffer A buffer where a fault address is written. Passing as an argument to reduce a stack usage.
 * @param str_buffer_size A size of passed process_name_buffer in bytes.
 */
static inline void ndcrash_dump_signal_info(
        int outfile,
        int signo,
        int si_code,
        void *faultaddr,
        char *str_buffer,
        size_t str_buffer_size) {
    if (ndcrash_signal_has_si_addr(signo, si_code)) {
        snprintf(str_buffer, str_buffer_size, "%p", faultaddr);
    } else {
        snprintf(str_buffer, str_buffer_size, "--------");
    }
    ndcrash_dump_write_line(
            outfile,
            "signal %d (%s), code %d (%s), fault addr %s",
            signo,
            ndcrash_get_signame(signo),
            si_code,
            ndcrash_get_sigcode(signo, si_code),
            str_buffer);
}

void ndcrash_dump_header(int outfile, pid_t pid, pid_t tid, int signo, int si_code, void *faultaddr,
                         struct ucontext *context) {
    // A special marker of crash report beginning.
    ndcrash_dump_write_line(outfile, "*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***");

    // This buffer we use to read data from system properties and to read other data from files.
    char str_buffer[PROP_VALUE_MAX];

    {
        // Getting system properties and writing them to report.
        __system_property_get("ro.build.fingerprint", str_buffer);
        ndcrash_dump_write_line(outfile, "Build fingerprint: %s", str_buffer);
        __system_property_get("ro.revision", str_buffer);
        ndcrash_dump_write_line(outfile, "Revision: '0'");
    }

    // Writing processor architecture.
#ifdef __arm__
    ndcrash_dump_write_line(outfile, "ABI: 'arm'");
#elif defined(__aarch64__)
    ndcrash_dump_write_line(outfile, "ABI: 'arm64'");
#elif defined(__i386__)
    ndcrash_dump_write_line(outfile, "ABI: 'x86'");
#elif defined(__x86_64__)
    ndcrash_dump_write_line(outfile, "ABI: 'x86_64'");
#endif

    // Writing a line about process and thread. Re-using str_buffer for a process name.
    ndcrash_write_process_and_thread_info(outfile, pid, tid, str_buffer, sizeofa(str_buffer));

    // Writing an information about signal.
    ndcrash_dump_signal_info(outfile, signo, si_code, faultaddr, str_buffer, sizeofa(str_buffer));

    // Writing registers to a report.
    const mcontext_t *const ctx = &context->uc_mcontext;
#if defined(__arm__)
    ndcrash_dump_write_line(outfile, "    r0 %08x  r1 %08x  r2 %08x  r3 %08x",
                            ctx->arm_r0, ctx->arm_r1, ctx->arm_r2, ctx->arm_r3);
    ndcrash_dump_write_line(outfile, "    r4 %08x  r5 %08x  r6 %08x  r7 %08x",
                            ctx->arm_r4, ctx->arm_r5, ctx->arm_r6, ctx->arm_r7);
    ndcrash_dump_write_line(outfile, "    r8 %08x  r9 %08x  sl %08x  fp %08x",
                            ctx->arm_r8, ctx->arm_r9, ctx->arm_r10, ctx->arm_fp);
    ndcrash_dump_write_line(outfile, "    ip %08x  sp %08x  lr %08x  pc %08x  cpsr %08x",
                            ctx->arm_ip, ctx->arm_sp, ctx->arm_lr, ctx->arm_pc, ctx->arm_cpsr);
#elif defined(__aarch64__)
    for (int i = 0; i < 28; i += 4) {
        ndcrash_dump_write_line(
                outfile,
                "    x%-2d  %016llx  x%-2d  %016llx  x%-2d  %016llx  x%-2d  %016llx",
                i, ctx->regs[i],
                i+1, ctx->regs[i+1],
                i+2, ctx->regs[i+2],
                i+3, ctx->regs[i+3]);
    }
    ndcrash_dump_write_line(
            outfile,
            "    x28  %016llx  x29  %016llx  x30  %016llx",
            ctx->regs[28],
            ctx->regs[29],
            ctx->regs[30]);
    ndcrash_dump_write_line(
            outfile,
            "    sp   %016llx  pc   %016llx  pstate %016llx",
            ctx->sp,
            ctx->pc,
            ctx->pstate);
#elif defined(__i386__)
    ndcrash_dump_write_line(outfile, "    eax %08lx  ebx %08lx  ecx %08lx  edx %08lx",
            ctx->gregs[REG_EAX], ctx->gregs[REG_EBX], ctx->gregs[REG_ECX], ctx->gregs[REG_EDX]);
    ndcrash_dump_write_line(outfile, "    esi %08lx  edi %08lx",
            ctx->gregs[REG_ESI], ctx->gregs[REG_EDI]);
    ndcrash_dump_write_line(outfile, "    xcs %08x  xds %08x  xes %08x  xfs %08x  xss %08x",
            ctx->gregs[REG_CS], ctx->gregs[REG_DS], ctx->gregs[REG_ES], ctx->gregs[REG_FS], ctx->gregs[REG_SS]);
    ndcrash_dump_write_line(outfile, "    eip %08lx  ebp %08lx  esp %08lx  flags %08lx",
            ctx->gregs[REG_EIP], ctx->gregs[REG_EBP], ctx->gregs[REG_ESP], ctx->gregs[REG_EFL]);
#elif defined(__x86_64__)
    ndcrash_dump_write_line(
            outfile, "    rax %016lx  rbx %016lx  rcx %016lx  rdx %016lx",
            ctx->gregs[REG_RAX], ctx->gregs[REG_RBX], ctx->gregs[REG_RCX], ctx->gregs[REG_RDX]);
    ndcrash_dump_write_line(
            outfile, "    rsi %016lx  rdi %016lx",
            ctx->gregs[REG_RSI], ctx->gregs[REG_RDI]);
    ndcrash_dump_write_line(
            outfile, "    r8  %016lx  r9  %016lx  r10 %016lx  r11 %016lx",
            ctx->gregs[REG_R8], ctx->gregs[REG_R9], ctx->gregs[REG_R10], ctx->gregs[REG_R11]);
    ndcrash_dump_write_line(
            outfile, "    r12 %016lx  r13 %016lx  r14 %016lx  r15 %016lx",
            ctx->gregs[REG_R12], ctx->gregs[REG_R13], ctx->gregs[REG_R14], ctx->gregs[REG_R15]);
    ndcrash_dump_write_line(
            outfile, "    cs  %016lx"/*  ss  %016lx"*/,
            ctx->gregs[REG_CSGSFS]/*, ctx->gregs[REG_SS]*/);
    ndcrash_dump_write_line(
            outfile, "    rip %016lx  rbp %016lx  rsp %016lx  eflags %016lx",
            ctx->gregs[REG_RIP], ctx->gregs[REG_RBP], ctx->gregs[REG_RSP], ctx->gregs[REG_EFL]);
#endif

    // Writing "backtrace:"
    ndcrash_write_backtrace_title(outfile);
}

/**
 * Obtains other thread registers by ptrace and dumps them to a report.
 * @param outfile Output file for a report.
 * @param tid Thread identifier which registers to dump.
 */
static inline void dump_other_thread_registers_by_ptrace(int outfile, pid_t tid) {
#if defined(__aarch64__)
    // For arm64 modern PTRACE_GETREGSET request should be executed.
    struct user_pt_regs r;
    struct iovec io;
    io.iov_base = &r;
    io.iov_len = sizeof(r);
    if (ptrace(PTRACE_GETREGSET, tid, (void *)NT_PRSTATUS, &io) == -1) {
        goto error;
    }
#else
    // For other architectures PTRACE_GETREGS is sufficient.
#if defined(__x86_64__)
    struct user_regs_struct r;
#else
    struct pt_regs r;
#endif
    if (ptrace(PTRACE_GETREGS, tid, 0, &r) == -1) {
        goto error;
    }
#endif

#if defined(__arm__)
    ndcrash_dump_write_line(outfile, "    r0 %08x  r1 %08x  r2 %08x  r3 %08x",
            (uint32_t)r.ARM_r0, (uint32_t)r.ARM_r1, (uint32_t)r.ARM_r2, (uint32_t)r.ARM_r3);
    ndcrash_dump_write_line(outfile, "    r4 %08x  r5 %08x  r6 %08x  r7 %08x",
            (uint32_t)r.ARM_r4, (uint32_t)r.ARM_r5, (uint32_t)r.ARM_r6, (uint32_t)r.ARM_r7);
    ndcrash_dump_write_line(outfile, "    r8 %08x  r9 %08x  sl %08x  fp %08x",
            (uint32_t)r.ARM_r8, (uint32_t)r.ARM_r9, (uint32_t)r.ARM_r10, (uint32_t)r.ARM_fp);
    ndcrash_dump_write_line(outfile, "    ip %08x  sp %08x  lr %08x  pc %08x  cpsr %08x",
            (uint32_t)r.ARM_ip, (uint32_t)r.ARM_sp, (uint32_t)r.ARM_lr, (uint32_t)r.ARM_pc, (uint32_t)r.ARM_cpsr);
#elif defined(__aarch64__)
    for (int i = 0; i < 28; i += 4) {
        ndcrash_dump_write_line(
                outfile,
                "    x%-2d  %016llx  x%-2d  %016llx  x%-2d  %016llx  x%-2d  %016llx",
                i, r.regs[i],
                i+1, r.regs[i+1],
                i+2, r.regs[i+2],
                i+3, r.regs[i+3]);
    }
    ndcrash_dump_write_line(
            outfile,
            "    x28  %016llx  x29  %016llx  x30  %016llx",
            r.regs[28],
            r.regs[29],
            r.regs[30]);
    ndcrash_dump_write_line(
            outfile,
            "    sp   %016llx  pc   %016llx  pstate %016llx",
            r.sp,
            r.pc,
            r.pstate);
#elif defined(__i386__)
    ndcrash_dump_write_line(outfile, "    eax %08lx  ebx %08lx  ecx %08lx  edx %08lx",
            r.eax, r.ebx, r.ecx, r.edx);
    ndcrash_dump_write_line(outfile, "    esi %08lx  edi %08lx",
            r.esi, r.edi);
    ndcrash_dump_write_line(outfile, "    xcs %08x  xds %08x  xes %08x  xfs %08x  xss %08x",
            r.xcs, r.xds, r.xes, r.xfs, r.xss);
    ndcrash_dump_write_line(outfile, "    eip %08lx  ebp %08lx  esp %08lx  flags %08lx",
            r.eip, r.ebp, r.esp, r.eflags);
#elif defined(__x86_64__)
    ndcrash_dump_write_line(
            outfile, "    rax %016lx  rbx %016lx  rcx %016lx  rdx %016lx",
            r.rax, r.rbx, r.rcx, r.rdx);
    ndcrash_dump_write_line(
            outfile, "    rsi %016lx  rdi %016lx",
            r.rsi, r.rdi);
    ndcrash_dump_write_line(
            outfile, "    r8  %016lx  r9  %016lx  r10 %016lx  r11 %016lx",
            r.r8, r.r9, r.r10, r.r11);
    ndcrash_dump_write_line(
            outfile, "    r12 %016lx  r13 %016lx  r14 %016lx  r15 %016lx",
            r.r12, r.r13, r.r14, r.r15);
    ndcrash_dump_write_line(
            outfile, "    cs  %016lx  ss  %016lx",
            r.cs, r.ss);
    ndcrash_dump_write_line(
            outfile, "    rip %016lx  rbp %016lx  rsp %016lx  eflags %016lx",
            r.rip, r.rbp, r.rsp, r.eflags);
#endif
    return;
    // C-style error processing.
error:
    NDCRASHLOG(ERROR, "Couldn't get registers by ptrace: %s (%d)", strerror(errno), errno);
}

void ndcrash_dump_other_thread_header(int outfile, pid_t pid, pid_t tid) {
    // A special marker about next (not crashed) thread data beginning.
    ndcrash_dump_write_line(outfile, "--- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---");

    // Assuming 64 bytes is sufficient for a process name.
    char process_name_buffer[64];

    // Writing a line about process and thread.
    ndcrash_write_process_and_thread_info(outfile, pid, tid, process_name_buffer, sizeofa(process_name_buffer));

    // Getting signal info by ptrace and writing to a dump.
    siginfo_t si;
    memset(&si, 0, sizeof(si));
    if (ptrace(PTRACE_GETSIGINFO, tid, 0, &si) == -1) {
        NDCRASHLOG(ERROR, "Couldn't get signal info by ptrace: %s (%d)", strerror(errno), errno);
        return;
    }
    ndcrash_dump_signal_info(outfile, si.si_signo, si.si_code, si.si_addr, process_name_buffer, sizeofa(process_name_buffer));

    // Dumping registers information.
    dump_other_thread_registers_by_ptrace(outfile, tid);

    // Writing "backtrace:"
    ndcrash_write_backtrace_title(outfile);
}

void ndcrash_dump_backtrace_line(
        int outfile,
        int counter,
        intptr_t pc,
        const char *map_name,
        const char *func_name,
        intptr_t func_offset) {
    if (!map_name) {
        map_name = "<unknown>";
    } else if (!*map_name) {
        map_name = "<anonymous>";
    }
    if (!func_name) {
        ndcrash_dump_write_line(
                outfile,
                "    #%02d pc %"PRIPTR"  %s",
                counter,
                pc,
                map_name);
    } else {
        ndcrash_dump_write_line(
                outfile,
                "    #%02d pc %"PRIPTR"  %s (%s+%d)",
                counter,
                pc,
                map_name,
                func_name,
                (int)func_offset
        );

    }
}
