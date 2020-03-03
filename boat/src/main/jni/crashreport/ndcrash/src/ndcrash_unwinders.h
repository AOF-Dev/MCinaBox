#ifndef NDCRASH_UNWINDERS_H
#define NDCRASH_UNWINDERS_H
#include "ndcrash_private.h"

#ifdef __cplusplus
extern "C" {
#endif

struct ucontext;

// See ndcrash_in_unwind_func_ptr for arguments description.
void ndcrash_in_unwind_libcorkscrew(int outfile, struct ucontext *context);
void ndcrash_in_unwind_libunwind(int outfile, struct ucontext *context);
void ndcrash_in_unwind_libunwindstack(int outfile, struct ucontext *context);
void ndcrash_in_unwind_cxxabi(int outfile, struct ucontext *context);
void ndcrash_in_unwind_stackscan(int outfile, struct ucontext *context);

// Unwinder initialization functions. See ndcrash_out_unwinder_init_func_ptr typedef.
void * ndcrash_out_init_libcorkscrew(pid_t pid);
void * ndcrash_out_init_libunwind(pid_t pid);
void * ndcrash_out_init_libunwindstack(pid_t pid);

// Unwinder de-initialization functions. See ndcrash_out_unwinder_deinit_func_ptr typedef.
void ndcrash_out_deinit_libcorkscrew(void *data);
void ndcrash_out_deinit_libunwind(void *data);
void ndcrash_out_deinit_libunwindstack(void *data);

// See ndcrash_out_unwind_func_ptr for arguments description.
void ndcrash_out_unwind_libcorkscrew(int outfile, pid_t tid, struct ucontext *context, void *data);
void ndcrash_out_unwind_libunwind(int outfile, pid_t tid, struct ucontext *context, void *data);
void ndcrash_out_unwind_libunwindstack(int outfile, pid_t tid, struct ucontext *context, void *data);

#ifdef __cplusplus
}
#endif


#endif //NDCRASH_UNWINDERS_H
