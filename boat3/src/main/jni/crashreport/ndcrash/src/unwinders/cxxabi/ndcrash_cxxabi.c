#include "ndcrash_unwinders.h"
#include "ndcrash_dump.h"
#include "ndcrash_private.h"
#include <dlfcn.h>
#include <string.h>
#define _GNU_SOURCE
#include <unwind.h>

#ifdef ENABLE_INPROCESS

/**
 * We use this struct as a callback for unwinding function.
 */
typedef struct {

    /// Output file descriptor for a crash dump.
    int outfile;

    /// Real frame number, incremented when each frame have been unwound.
    int real_frame_no;

    /// A frame number to add to log. Incremented when we add a frame to backtrace.
    int log_frame_no;

} ndcrash_cxxabi_unwind_data;

static _Unwind_Reason_Code ndcrash_in_cxxabi_callback(struct _Unwind_Context *context, void *data) {
    ndcrash_cxxabi_unwind_data * const ud = (ndcrash_cxxabi_unwind_data *) data;
    // We always skip first 2 frames because they are always ndcrash functions:
    // ndcrash_in_signal_handler and ndcrash_in_unwind_cxxabi.
    if (ud->real_frame_no > 2) {
        const uintptr_t pc = _Unwind_GetIP(context);
        Dl_info info;
        if (pc && dladdr((void *) pc, &info)) {

            // Writing a line to backtrace.
            ndcrash_dump_backtrace_line(
                    ud->outfile,
                    ud->log_frame_no,
                    (intptr_t) pc - (intptr_t) info.dli_fbase,
                    info.dli_fname,
                    info.dli_sname,
                    (intptr_t) pc - (intptr_t) info.dli_saddr
            );
        } else {
            ndcrash_dump_backtrace_line(ud->outfile, ud->log_frame_no, pc, NULL, NULL, 0);
        }
        ++ud->log_frame_no;
    }

    ++ud->real_frame_no;
    return ud->log_frame_no >= NDCRASH_MAX_FRAMES ? _URC_END_OF_STACK : _URC_NO_REASON;
}

void ndcrash_in_unwind_cxxabi(int outfile, struct ucontext *context) {
    ndcrash_cxxabi_unwind_data unwdata;
    unwdata.real_frame_no = unwdata.log_frame_no = 0;
    unwdata.outfile = outfile;
    _Unwind_Backtrace(ndcrash_in_cxxabi_callback, &unwdata);
}

#endif //ENABLE_INPROCESS
