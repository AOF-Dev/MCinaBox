#include "ndcrash.h"
#include "ndcrash_unwinders.h"
#include "ndcrash_dump.h"
#include "ndcrash_private.h"
#include "ndcrash_log.h"
#include "ndcrash_utils.h"
#include "ndcrash_fd_utils.h"
#include <malloc.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/socket.h>
#include <android/log.h>
#include <linux/un.h>
#include <sys/param.h>
#include <sys/ptrace.h>
#include <sys/wait.h>
#include <string.h>
#include <errno.h>

#ifdef ENABLE_OUTOFPROCESS

struct ndcrash_out_daemon_context {

    /// Pointer to unwinder initialization function.
    ndcrash_out_unwinder_init_func_ptr unwinder_init;

    /// Pointer to unwinder de-initialization function.
    ndcrash_out_unwinder_deinit_func_ptr unwinder_deinit;

    /// Pointer to unwinding function.
    ndcrash_out_unwind_func_ptr unwind_function;

    /// Path to a log file. Null if not set.
    char *log_file;

    /// Pipes that we use to stop a daemon.
    int interruptor[2];

    /// Daemon thread.
    pthread_t daemon_thread;

    /// Daemon lifecycle callbacks. See docs for ndcrash_out_start_daemon.
    ndcrash_daemon_start_stop_callback start_callback;
    ndcrash_daemon_crash_callback crash_callback;
    ndcrash_daemon_start_stop_callback stop_callback;

    /// Argument for daemon lifecycle callbacks. Passed to initialization function.
    void *callback_arg;

    /// Socket address that is used to communicate with debugger.
    struct sockaddr_un socket_address;

};

/// Global instance of out-of-process daemon context.
struct ndcrash_out_daemon_context *ndcrash_out_daemon_context_instance = NULL;

/// Constant for listening socket backlog argument.
static const int SOCKET_BACKLOG = 1;


/**
 * Attaches to specified thread by ptrace. Writes a message to log on error.
 * @return Flag whether an attaching is successful.
 */
static bool ndcrash_out_ptrace_attach(pid_t tid) {
    // Attaching.
    if (ptrace(PTRACE_ATTACH, tid, NULL, NULL) == -1) {
        NDCRASHLOG(INFO, "Ptrace attach failed to tid: %d errno: %d (%s)", (int) tid, errno, strerror(errno));
        return false;
    }
    return true;
}

/**
 * Detaches from specified thread by ptrace. All errors are ignored.
 */
static void ndcrash_out_ptrace_detach(pid_t tid) {
    ptrace(PTRACE_DETACH, tid, NULL, NULL);
}

/**
 * Creates and fills a new crash dump.
 * @param message A message received from a signal handler.
 * @return Flag if output file for report has been created successfully.
 */
static bool ndcrash_out_daemon_create_report(struct ndcrash_out_message *message) {
    // Attaching to a crashed thread. If errors has occurred it's fatal, aborting crash report creation.
    if (!ndcrash_out_ptrace_attach(message->tid)) return false;

    // Getting not crashed threads list and attaching to all of them.
#ifdef ENABLE_OUTOFPROCESS_ALL_THREADS
    pid_t tids[64];
    const size_t tids_size = ndcrash_get_threads(message->pid, tids, sizeofa(tids));
    for (pid_t *it = tids, *end = tids + tids_size; it != end; ++it) {
        // Attaching errors for background threads are not fatal, just skipping such threads by
        // setting tid to 0.
        if (!ndcrash_out_ptrace_attach(*it)) {
            *it = 0;
        }
    }
#endif //ENABLE_OUTOFPROCESS_ALL_THREADS

    //Opening output file
    int outfile = -1;
    if (ndcrash_out_daemon_context_instance->log_file) {
        outfile = ndcrash_dump_create_file(ndcrash_out_daemon_context_instance->log_file);
    }

    // Writing a crash dump header
    ndcrash_dump_header(
            outfile,
            message->pid,
            message->tid,
            message->signo,
            message->si_code,
            message->faultaddr,
            &message->context);

    // Unwinder initialization, should be done before any thread unwinding.
    void * const unwinder_data = ndcrash_out_daemon_context_instance->unwinder_init(message->tid);

    // Stack unwinding for a main thread.
    ndcrash_out_daemon_context_instance->unwind_function(outfile, message->tid, &message->context, unwinder_data);

#ifdef ENABLE_OUTOFPROCESS_ALL_THREADS
    // Processing other threads: printing a header and stack trace.
    for (pid_t *it = tids, *end = tids + tids_size; it != end; ++it) {

        // Skipping threads failed to attach.
        if (!*it) continue;

        /// Writing other thread header.
        ndcrash_dump_other_thread_header(outfile, message->pid, *it);

        // Stack unwinding for a secondary thread.
        ndcrash_out_daemon_context_instance->unwind_function(outfile, *it, NULL, unwinder_data);
    }
#endif //ENABLE_OUTOFPROCESS_ALL_THREADS

    // Unwinder de-initialization.
    ndcrash_out_daemon_context_instance->unwinder_deinit(unwinder_data);

    // Final line of crash dump.
    ndcrash_dump_write_line(outfile, " ");

    // Closing output file.
    if (outfile >= 0) {
        //Closing file
        close(outfile);
    }

    // Detaching from all threads.
    ndcrash_out_ptrace_detach(message->tid);
#ifdef ENABLE_OUTOFPROCESS_ALL_THREADS
    for (pid_t *it = tids, *end = tids + tids_size; it != end; ++it) {
        if (!*it) continue;
        ndcrash_out_ptrace_detach(*it);
    }
#endif //ENABLE_OUTOFPROCESS_ALL_THREADS

    // Returning a if output file for report has been created successfully.
    // Note that outfile is currently closed, we use it only to check if file was created.
    return outfile >= 0;
}

/**
 * Processes a client request: receives a data from client and creates a crash report.
 * @param clientsock A socket to communicate with a client.
 */
 ////////////
 static inline int MAX(int a,int b){
	 if (a > b){
		 return a;
	 }
	 else{
		 return b;
	 }
 }
 
 
static void ndcrash_out_daemon_process_client(int clientsock) {
    struct ndcrash_out_message message = {0, 0};
    ssize_t overall_read = 0;
    do {
        fd_set fdset;
        FD_ZERO(&fdset);
        FD_SET(clientsock, &fdset);
        FD_SET(ndcrash_out_daemon_context_instance->interruptor[0], &fdset);
        const int select_result = select(
                MAX(clientsock, ndcrash_out_daemon_context_instance->interruptor[0]) + 1, &fdset,
                NULL, NULL, NULL);
        if (select_result < 0) {
            NDCRASHLOG(ERROR, "Select on recv error: %s (%d)", strerror(errno), errno);
            close(clientsock);
            return;
        }
        if (FD_ISSET(ndcrash_out_daemon_context_instance->interruptor[0], &fdset)) {
            // Interrupting by pipe.
            close(clientsock);
            return;
        }
        const ssize_t bytes_read = recv(
                clientsock,
                (char *) &message + overall_read,
                sizeof(struct ndcrash_out_message) - overall_read,
                MSG_NOSIGNAL);
        if (bytes_read < 0) {
            NDCRASHLOG(ERROR, "Recv error: %s (%d)", strerror(errno), errno);
            close(clientsock);
            return;
        }
        overall_read += bytes_read;
    } while (overall_read < sizeof(struct ndcrash_out_message));

    NDCRASHLOG(INFO, "Client info received, pid: %d tid: %d", message.pid, message.tid);

    // Creating a report.
    const bool report_file_created = ndcrash_out_daemon_create_report(&message);

    //Write 1 byte as a response.
    write(clientsock, "\0", 1);

    // Closing a connection.
    close(clientsock);

    // Running successful unwinding callback if it's set. We do it after detaching and disconnecting
    // from crashing process because at this point it can terminate. A callback may perform some
    // long operation, for example, synchronous networking and we shouldn't allow any bad UX
    // with a hang of application. In modern Android service has "a window of several minutes in which
    // it is still allowed to create and use services" so it won't be a problem.
    if (report_file_created && ndcrash_out_daemon_context_instance->crash_callback) {
        ndcrash_out_daemon_context_instance->crash_callback(
                ndcrash_out_daemon_context_instance->log_file,
                ndcrash_out_daemon_context_instance->callback_arg
        );
    }
}

/**
 * An entry point to a daemon. This function is passed to pthread as a thread main function.
 */
static void *ndcrash_out_daemon_function(void *arg) {
    // Creating socket
    const int listensock = socket(PF_LOCAL, SOCK_STREAM, 0);
    if (listensock < 0) {
        NDCRASHLOG(ERROR, "Couldn't create socket, error: %s (%d)", strerror(errno), errno);
        return NULL;
    }

    // Setting options
    {
        int n = 1;
        setsockopt(listensock, SOL_SOCKET, SO_REUSEADDR, &n, sizeof(n));
    }

    // Binding to an address.
    if (bind(listensock, (struct sockaddr *) &ndcrash_out_daemon_context_instance->socket_address,
             sizeof(struct sockaddr_un)) < 0) {
        NDCRASHLOG(ERROR, "Couldn't bind socket, error: %s (%d)", strerror(errno), errno);
        return NULL;
    }

    // Listening
    if (listen(listensock, SOCKET_BACKLOG) < 0) {
        NDCRASHLOG(ERROR, "Couldn't listen socket, error: %s (%d)", strerror(errno), errno);
        return NULL;
    }

    NDCRASHLOG(INFO, "Daemon is successfuly started, accepting connections...");

    if (ndcrash_out_daemon_context_instance->start_callback) {
        ndcrash_out_daemon_context_instance->start_callback(
                ndcrash_out_daemon_context_instance->callback_arg);
    }

    // Accepting connections in a cycle.
    for (;;) {
        fd_set fdset;
        FD_ZERO(&fdset);
        FD_SET(listensock, &fdset);
        FD_SET(ndcrash_out_daemon_context_instance->interruptor[0], &fdset);
        const int select_result = select(
                MAX(listensock, ndcrash_out_daemon_context_instance->interruptor[0]) + 1, &fdset,
                NULL, NULL, NULL);
        if (select_result < 0) {
            NDCRASHLOG(ERROR, "Select on accept error: %s (%d)", strerror(errno), errno);
            break;
        }
        if (FD_ISSET(ndcrash_out_daemon_context_instance->interruptor[0], &fdset)) {
            // Interrupting by pipe.
            break;
        }

        struct sockaddr_storage ss;
        struct sockaddr *addrp = (struct sockaddr *) &ss;
        socklen_t alen = sizeof(ss);
        int clientsock = accept(listensock, addrp, &alen);
        if (clientsock == -1) {
            NDCRASHLOG(ERROR, "Accept failed, error: %s (%d)", strerror(errno), errno);
            continue;
        }

        NDCRASHLOG(INFO, "Client connected, socket: %d", clientsock);
        ndcrash_out_daemon_process_client(clientsock);
    }

    close(listensock);

    if (ndcrash_out_daemon_context_instance->stop_callback) {
        ndcrash_out_daemon_context_instance->stop_callback(
                ndcrash_out_daemon_context_instance->callback_arg);
    }

    return NULL;
}

enum ndcrash_error ndcrash_out_start_daemon(
        const char *socket_name,
        const enum ndcrash_unwinder unwinder,
        const char *log_file,
        ndcrash_daemon_start_stop_callback start_callback,
        ndcrash_daemon_crash_callback crash_callback,
        ndcrash_daemon_start_stop_callback stop_callback,
        void *callback_arg) {

    if (ndcrash_out_daemon_context_instance) {
        return ndcrash_error_already_initialized;
    }

    // Socket name can't be null or empty.
    if (!socket_name || !*socket_name) {
        return ndcrash_error_socket_name;
    }

    // Creating a new struct instance.
    ndcrash_out_daemon_context_instance = (struct ndcrash_out_daemon_context *) malloc(
            sizeof(struct ndcrash_out_daemon_context));
    memset(ndcrash_out_daemon_context_instance, 0, sizeof(struct ndcrash_out_daemon_context));
    ndcrash_out_daemon_context_instance->start_callback = start_callback;
    ndcrash_out_daemon_context_instance->crash_callback = crash_callback;
    ndcrash_out_daemon_context_instance->stop_callback = stop_callback;
    ndcrash_out_daemon_context_instance->callback_arg = callback_arg;

    // Filling in socket address.
    ndcrash_out_fill_sockaddr(socket_name, &ndcrash_out_daemon_context_instance->socket_address);

    // Checking if unwinder is supported. Setting unwind function.
    switch (unwinder) {
#ifdef ENABLE_LIBCORKSCREW
        case ndcrash_unwinder_libcorkscrew:
            ndcrash_out_daemon_context_instance->unwinder_init = &ndcrash_out_init_libcorkscrew;
            ndcrash_out_daemon_context_instance->unwinder_deinit = &ndcrash_out_deinit_libcorkscrew;
            ndcrash_out_daemon_context_instance->unwind_function = &ndcrash_out_unwind_libcorkscrew;
            break;
#endif
#ifdef ENABLE_LIBUNWIND
        case ndcrash_unwinder_libunwind:
            ndcrash_out_daemon_context_instance->unwinder_init = &ndcrash_out_init_libunwind;
            ndcrash_out_daemon_context_instance->unwinder_deinit = &ndcrash_out_deinit_libunwind;
            ndcrash_out_daemon_context_instance->unwind_function = &ndcrash_out_unwind_libunwind;
            break;
#endif
#ifdef ENABLE_LIBUNWINDSTACK
        case ndcrash_unwinder_libunwindstack:
            ndcrash_out_daemon_context_instance->unwinder_init = &ndcrash_out_init_libunwindstack;
            ndcrash_out_daemon_context_instance->unwinder_deinit = &ndcrash_out_deinit_libunwindstack;
            ndcrash_out_daemon_context_instance->unwind_function = &ndcrash_out_unwind_libunwindstack;
            break;
#endif
        default: // To suppress a warning.
            break;
    }
    if (!ndcrash_out_daemon_context_instance->unwind_function) {
        ndcrash_out_deinit();
        return ndcrash_error_not_supported;
    }


    // Copying log file path if set.
    if (log_file) {
        size_t log_file_size = strlen(log_file);
        if (log_file_size) {
            ndcrash_out_daemon_context_instance->log_file = malloc(++log_file_size);
            memcpy(ndcrash_out_daemon_context_instance->log_file, log_file, log_file_size);
        }
    }

    // Creating interruption pipes.
    if (pipe(ndcrash_out_daemon_context_instance->interruptor) < 0 ||
        !ndcrash_set_nonblock(ndcrash_out_daemon_context_instance->interruptor[0] ||
                              !ndcrash_set_nonblock(
                                      ndcrash_out_daemon_context_instance->interruptor[1]))) {
        ndcrash_out_stop_daemon();
        return ndcrash_error_pipe;
    }

    // Creating a daemon thread.
    const int res = pthread_create(&ndcrash_out_daemon_context_instance->daemon_thread, NULL,
                                   ndcrash_out_daemon_function, NULL);
    if (res) {
        return ndcrash_error_thread;
    }

    return ndcrash_ok;
}

bool ndcrash_out_stop_daemon() {
    if (!ndcrash_out_daemon_context_instance) return false;
    if (ndcrash_out_daemon_context_instance->daemon_thread) {
        // Writing to pipe in order to interrupt select.
        if (write(ndcrash_out_daemon_context_instance->interruptor[1], (void *) "\0", 1) < 0) {
            return false;
        }
        pthread_join(ndcrash_out_daemon_context_instance->daemon_thread, NULL);
        close(ndcrash_out_daemon_context_instance->interruptor[0]);
        close(ndcrash_out_daemon_context_instance->interruptor[1]);
    }
    if (ndcrash_out_daemon_context_instance->log_file) {
        free(ndcrash_out_daemon_context_instance->log_file);
    }
    free(ndcrash_out_daemon_context_instance);
    ndcrash_out_daemon_context_instance = NULL;
    return true;
}

void *ndcrash_out_get_daemon_callbacks_arg() {
    if (!ndcrash_out_daemon_context_instance) return NULL;
    return ndcrash_out_daemon_context_instance->callback_arg;
}

#endif //ENABLE_OUTOFPROCESS
