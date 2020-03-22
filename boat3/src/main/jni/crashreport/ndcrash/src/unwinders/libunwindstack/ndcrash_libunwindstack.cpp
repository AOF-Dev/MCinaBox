#include "ndcrash_unwinders.h"
#include "ndcrash_log.h"
#include "ndcrash_dump.h"
#include "ndcrash_private.h"
#include <android/log.h>
#include <unwindstack/Elf.h>
#include <unwindstack/MapInfo.h>
#include <unwindstack/Maps.h>
#include <unwindstack/Memory.h>
#include <unwindstack/Regs.h>


extern "C" {

using namespace unwindstack;

#if defined(ENABLE_INPROCESS) || defined(ENABLE_OUTOFPROCESS)

/**
 * Common unwinding method for in-process and out-of-process.
 * @param outfile Output file descriptor for a crash dump.
 * @param context Processor context to unwind a stack.
 * @param maps Parsed libunwindstack memory maps instance.
 * @param memory libunwindstack Memory instance.
 * @param withDebugData Flag whether to use GNU debug symbols data on unwinding.
 */
static inline void ndcrash_common_unwind_libunwindstack(
        int outfile,
        const std::unique_ptr<Regs> &regs,
        Maps &maps,
        const std::shared_ptr<Memory> &memory,
        bool withDebugData) {
    // String for function name.
    std::string unw_function_name;

    for (size_t frame_num = 0; frame_num < NDCRASH_MAX_FRAMES; frame_num++) {
        // Looking for a map info item for pc on this unwinding step.
        MapInfo * const map_info = maps.Find(regs->pc());
        if (!map_info) {
            ndcrash_dump_backtrace_line(
                    outfile,
                    (int)frame_num,
                    (intptr_t)regs->pc(),
                    NULL,
                    NULL,
                    0);
            break;
        }

        // Loading data from ELF
        Elf * const elf = map_info->GetElf(memory, withDebugData);
        if (!elf) {
            ndcrash_dump_backtrace_line(
                    outfile,
                    (int)frame_num,
                    (intptr_t)regs->pc(),
                    map_info->name.c_str(),
                    NULL,
                    0);
            break;
        }

        // Getting value of program counter relative module where a function is located.
        const uint64_t rel_pc = elf->GetRelPc(regs->pc(), map_info);
        uint64_t adjusted_rel_pc = rel_pc;
        if (frame_num != 0) {
            // If it's not a first frame we need to rewind program counter value to previous instruction.
            // For the first frame pc from ucontext points exactly to a failed instruction, for other
            // frames rel_pc will contain return address after function call instruction.
            adjusted_rel_pc -= regs->GetPcAdjustment(rel_pc, elf);
        }

        // Getting function name and writing value to a log.
        uint64_t func_offset = 0;
        if (elf->GetFunctionName(rel_pc, &unw_function_name, &func_offset)) {
            ndcrash_dump_backtrace_line(
                    outfile,
                    (int)frame_num,
                    (intptr_t)rel_pc,
                    map_info->name.c_str(),
                    unw_function_name.c_str(),
                    (intptr_t) func_offset);
        } else {
            unw_function_name.clear();
            ndcrash_dump_backtrace_line(
                    outfile,
                    (int)frame_num,
                    (intptr_t)rel_pc,
                    map_info->name.c_str(),
                    NULL,
                    0);
        }

        // Trying to switch to a next frame.
        bool finished = false;
        if (!elf->Step(rel_pc, adjusted_rel_pc, map_info->elf_offset, regs.get(), memory.get(), &finished)) {
            break;
        }
    }
}

#endif //defined(ENABLE_INPROCESS) || defined(ENABLE_OUTOFPROCESS)

#ifdef ENABLE_INPROCESS

void ndcrash_in_unwind_libunwindstack(int outfile, struct ucontext *context) {
    // Initializing /proc/self/maps cache.
    LocalMaps maps;
    if (!maps.Parse()) {
        NDCRASHLOG(ERROR, "libunwindstack: failed to parse local /proc/pid/maps.");
        return;
    }
    // Unwinding stack.
    const std::shared_ptr<Memory> memory(new MemoryLocal);
    // GNU debug symbols usage is disabled, it's quite expensive and unwinding may fail because
    // in signal handler we have a very limited stack size.
    ndcrash_common_unwind_libunwindstack(
            outfile,
            std::unique_ptr<Regs>(Regs::CreateFromUcontext(Regs::CurrentArch(), context)),
            maps,
            memory,
            false);
}

#endif //ENABLE_INPROCESS

#ifdef ENABLE_OUTOFPROCESS

void * ndcrash_out_init_libunwindstack(pid_t pid) {
    // Using RemoteMaps instance as an opaque data.
    RemoteMaps * const maps = new RemoteMaps(pid);
    if (!maps->Parse()) {
        NDCRASHLOG(ERROR, "libunwindstack: failed to parse remote /proc/pid/maps.");
    }
    return maps;
}

void ndcrash_out_deinit_libunwindstack(void *data) {
    delete static_cast<RemoteMaps *>(data);
}

void ndcrash_out_unwind_libunwindstack(int outfile, pid_t tid, struct ucontext *context, void *data) {
    RemoteMaps * const maps = static_cast<RemoteMaps *>(data);
    const std::shared_ptr<Memory> memory(new MemoryRemote(tid));
    std::unique_ptr<Regs> regs;
    if (context) {
        regs.reset(Regs::CreateFromUcontext(Regs::CurrentArch(), context));
    } else {
        regs.reset(Regs::RemoteGet(tid));
        if (!regs) {
            NDCRASHLOG(ERROR, "libunwindstack: Couldn't get registers by ptrace for tid: %d", (int) tid);
            return;
        }
    }
    ndcrash_common_unwind_libunwindstack(outfile, regs, *maps, memory, true);
}

#endif //ENABLE_OUTOFPROCESS

}