# NDCrash #

**NDCrash** is a powerful crash reporting library for Android NDK applications. The author was inspired by PLCrashReporter and Google Breakpad. Note that this library is new and has a an experimental status.

## Key features ##

* Written in C99 so it may be used in plain C projects.
* On-device stack unwinding.
* [ndk-stack](https://developer.android.com/ndk/guides/ndk-stack.html) compatible human-readable report format. This tool can be easilly used to access line numbers.
* Supports 2 crash handling modes: *in-process* and *out-of-process*. 
* Supports 5 different stack unwinders. 
* *out-of-process* mode supports stack traces collection of all application threads.
* 32 and 64 bit architectures support (depends on unwinder).
* Easy-to-use Java wrapper https://github.com/ivanarh/jndcrash
* Minimum tested Android version is 4.0.3 but theoretically it may work even on Android 2.3.

## Roadmap ##

These features are not currently implemented but they are in plans. They are likely to be implemented only for out-of-process mode due to in-process mode restrictions.

* Stack dump.
* Dump of memory around addresses stored in registers.
* Memory map dump.

## Crash handling modes ##

NDCrash supports 2 crash handling modes. Mode is a way how and where a crash report data is collected. The same concept is used by Google Breakpad, see its [documentation](https://chromium.googlesource.com/breakpad/breakpad/+show/master/docs/exception_handling.md)
Both of these modes are supported in the library at once but only one of them needs to be activated at run-time (useful for A/B testing). Also any of them can be disabled by compilation flags for optimization purposes.

### In-process mode ###

In-process means crash report is created within crashing process, it happens in a signal handler. In most cases this approach works but there are 2 major problems when you use this mode: *signal safety* and *stack restrictions*. It means you should prefer *out-of-process* mode if it's possible.

#### Signal safety ####

In general signal handler requires its code to be *signal safe*. It's safe to run only a very limited set of functions, see [this man page](http://man7.org/linux/man-pages/man7/signal-safety.7.html). More details could be read in [glibc documentation](http://www.gnu.org/software/libc/manual/html_node/Defining-Handlers.html).
A lot of functions habitual for each and every developer are not signal safe, for example heap memory allocation by malloc/free. The worst case that may happen if your handler isn't signal safe is a deadlock during signal handler execution, in such event a crash report won't be created and user will have to destroy your application explicitly.
But it doesn't mean that these stuff couldn't be used in signal handler for crash reporting, because an application process will be terminated anyway after signal handler execution and all we need is to create a report. Therefore, a good idea is to minimize unsafe stuff usage in order to make a crash reporter work properly for most part of crashes.

#### Stack restrictions ####

Starting from Android 4.4 bionic uses an alternative stack for signal handlers. See [bionic source](https://android.googlesource.com/platform/bionic/+/kitkat-dev/libc/bionic/pthread_create.cpp) and [sigaltstack documentation](http://man7.org/linux/man-pages/man2/sigaltstack.2.html). This stack has fixed size, by default SIGSTKSZ constant value is used (8 kilobytes on 32-bit ARM Platform).
It's very useful feature when a crash due to stack overflow happens. However, this stack size could be insufficient because heap allocations are not safe and you are forced to allocate a memory on a stack. For example, libunwind's unw_cursor_t has a huge size (4 kilobytes) and it's a very big trade-off where to allocate a memory for it. Of course, some static buffer may be used but it's not thread safe, signal handlers may execute concurrently for different threads. Libunwind provides a special "memory pool" mechanism for this case.
A workaround for this problem is possible: you can allocate a stack of any size and set it by sigaltstack function. But it should be done for every thread of your application, so some wrapper around pthread is required.

### Out-of-process mode ###

Out-of-process means crash report is generated in a proces other than crashing. This is possible due to [ptrace](http://man7.org/linux/man-pages/man2/ptrace.2.html) system call that allows some process to inspect a state of another process. Originally out-of-process mode is used by Android system debugger (debuggerd). 

When **NDCrash** works in out-of-process mode it has 2 different parts:

* **Crash reporting daemon**. It's a special service with **android:process** attribute in its manifest definition, see [service tag documentation](https://developer.android.com/guide/topics/manifest/service-element.html#proc). It will make it run in a separate process with own PID and address space. This daemon plays a role of debugger, so later "debugger" word will be used with the same meaning.
* **Signal handler**. It's run in the main application process. It's a very simple and lightweight: all it should done is to communicate with debugger and wait until crash report is generated.

#### Out-of-process mode flow ####

Details how out-of-process mode works are described below:

* When a daemon is started it opens a listening UNIX domain socket which allows crashing process to communicate with. It remains in sleeping state until crash happens or explicit stop is requested.
* When a crash happens a signal handler within crashing process is executed. Which in turn connects to a listening UNIX domain socket previously opened by daemon. Then a handler sends some data about a crash (pid, tid, register values) to debugger. This data is necessary to generate a crash report. After that handler sleeps by blocking "recv" operation (waits for a response from daemon).
* Daemon receives data from crashing app and attaches to it by ptrace mechanism. At this point daemon has access to a state of crashing process.
* Daemon generates a crash report, by default it's saved to a file and written to logcat. Crash report generation includes **stack unwinding** operation, see information below.
* After a crash report is generated daemon sends one byte response to a socket, closes it (disconnects) and starts listening for another connection.
* A crashing process receives this byte (recv operation wakes), restores a previous signal handler (that was set by bionic library) and re-raises a signal.

A restoration of previous signal handler is necessary to preserve operating status of standard Android debugger (debuggerd), the bionic library registers this handler in order to initiate crash report generation by debuggerd. This is because we can't obtain registers state for stack unwinding by ptrace (we send it by a socket). To do this we would install a default signal handler (SIG_DFL) and re-raise a signal. This is exactly how google breakpad behaves and broken debuggerd is one of big disadvantages of this crash reporting library.

## Stack unwinding ##

The most interesting information in a report is, of course, a **backtrace**, also known as **stack trace**. At the same time, obtaining of this data is the most difficult task during crash report generation. To obtain a backtrace we need to analyze a stack data by walking through all **stack frames**. This process is called **stack unwinding**. 
There are several ways how to perform stack unwinding, also different third party code may be used for this purpose. This led to support of different **stack unwinders** in NDCrash library. Each unwinder is a module within the library that provides a code that unwinds a stack and writes a backtrace to a crash report. All unwinders are supported by library at one but only one of them should be selected in the moment of library initialization. This may be useful for A/B testing.

### Ways to unwind a stack ###

Stack may be unwound using different algorithms. The main challenge for them in crash reporting is to analyze stack data, determine bounds of every stack frame and extract all return addresses from it. Stack data may be easily accessed by reading memory at an address from "stack pointer" register.

- *Full stack scanning.* This is the most inaccurate unwinding algorithm: taking every stack element (machine word) and searching a function with this address. If it's found, adding it to a backtrace. Used by [Bugsnag SDK](https://github.com/bugsnag/bugsnag-android-ndk/blob/989f3410f87c4d578bbfb264bab8510995038216/ndk/src/main/jni/bugsnag_unwind.c).
- *DWARF call frame information data.* Located in ELF section **.eh_frame** or .debug_frame, used on most processor architectures for C++ exceptions handling and by debuggers, such as gdb and lldb. See [documentation, chapter 6.4](http://www.dwarfstd.org/doc/DWARF4.pdf).
- *ARM Exception Tables.* This is a replacement of DWARF call frame information data for 32-bit ARM architecture, locates in ELF section **.ARM.extab**. The same binary may contain both .ARM.extab and .debug_frame sections but the second only used by debuggers and doesn't used during C++ exceptions handling, it's also stripped on release builds. See [documentation](http://infocenter.arm.com/help/topic/com.arm.doc.ihi0038b/IHI0038B_ehabi.pdf) about this tables.

On some architectures that not currently supported by NDK (such as MIPS) proper stack unwinding is possible without information in additional sections. These architectures are not supported by NDCrash and it doesn't make sense to mention them.

Details about each unwinder supported in NDCrash are described below.

### "cxxabi" unwinder ###

It uses standard C++ library facilities to unwind a stack (the same functionality is used during C++ exception handling). Specifically, it uses *_Unwind_Backtrace* and *_Unwind_GetIP* functions to unwind a stack plus POSIX *dladdr* function to obtain an information about module and function. 

**Supported processor architectures:** All.

**Ways to unwind a stack:** ARM Exception Tables on 32-bit ARM, DWARF .eh_frame on another architectures.

**Supported modes:** In-process only.

**Advantages:** Simple, doesn't require any 3rd party code. 

**Disadvantages:** It's impossible to set an initial processor state to unwind a stack. On Android <= 5.0 on ARM architecture it can't unwind a stack properly due to bug in bionic, see [this commit](https://android.googlesource.com/platform/bionic/+/5054e1a121fc5aca814815625fe230c4a8abd5a5). On newer versions of Android it will lead to surplus lines in backtrace that are not related to crash itself.

### "libcorkscrew" unwinder ###

It uses obsolete [libcorkscrew](https://android.googlesource.com/platform/system/core/+/kitkat-dev/libcorkscrew/) library from Android sources that was used by debuggerd on Android 4.1 - 4.4 versions. To make it work on any Android version it's linked statically. A special [fork](https://github.com/ivanarh/libcorkscrew-ndk) with patches to build with NDK toolchain is used.

**Language:** C89

**Supported processor architectures:** ARM, x86

**Ways to unwind a stack:** ARM Exception Tables on ARM, DWARF .eh_frame on x86.

**Supported modes:** In-process & Out-of-process.

**Advantages:** A very tiny size.

**Disadvantages:** Obsolete. Lack of 64-bit architectures support.

### "libunwind" unwinder ###

Uses [android fork](https://android.googlesource.com/platform/external/libunwind/) of [libunwind](https://www.nongnu.org/libunwind/) library that was used as a replacement for libcorkscrew since Android 5.0. Like for libcorkscrew, some patches has been applied to make build with standard NDK toolchain possible, fork with patches is [here](https://github.com/ivanarh/libunwind-ndk). 

**Supported processor architectures:** All supported by NDK plus some extra.

**Language:** C89

**Ways to unwind a stack:** DWARF on all architectures, ARM Exception Tables on ARM.

**Supported modes:** In-process & Out-of-process

**Advantages:** Powerful, a lot of supported architectures, stable.

**Disadvantages:** It's going to be obsolete in newer Android versions because new **libunwindstack** library is actively developed.

### "libunwind" unwinder ###

This library is being actively developed especially for Android, you can obtain a source code [here](https://android.googlesource.com/platform/system/core/+/master/libunwindstack/). This is going to be a replacement for libunwnd in modern Android versions.

**Supported processor architectures:** All supported by NDK.

**Language:** C++11

**Ways to unwind a stack:** DWARF on all architectures, ARM Exception Tables on ARM.

**Supported modes:** In-process & Out-of-process

**Advantages:** Powerful, modern, actively developed.

**Disadvantages:** Unstable. Requires massive C++11 standard library, so it's not a good solution for plain C projects.

### "stackscan" unwinder ###

The most simple unwinder. Not recommended to use in general cases.

**Supported processor architectures:** All supported by NDK.

**Ways to unwind a stack:** Full stack scanning.

**Supported modes:** In-process only. 

**Advantages:** Doesn't require additional sections such as .ARM.extab or .eh_frame, so they can be stripped.

**Disadvantages:** Inaccurate results, not optimal.

## Integration ##

For easier integration you can use [java wrapper](https://github.com/ivanarh/jndcrash)

Current build system uses CMake and builds **NDCrash** as a static library. It's a recommended way to use. A good idea is to add its source code as a submodule. Assuming NDCrash submodule is checked out to *libndcrash* subdirectory in the same directory where CMakeLists file is located, you need to add a subdirectory with **NDCrash** library, add its "include" directory to include paths and use this static library. Please add these lines to your CMakeLists.txt:

```
add_subdirectory(libndcrash)
include_directories(${CMAKE_SOURCE_DIR}/libndcrash/include)
...
find_library(LOG_LIB log)
target_link_libraries(myproject ndcrash ${LOG_LIB})
```
Please note that **log** library linkage is required.

## Customization ##

For optimization purposes you can turn on or turn off unused modules or library usage mode (in-process or out-of-process). The following CMake variables are used, all of these are boolean-typed. Absense of variable is treated as false.

- **ENABLE_INPROCESS** Enables in-process mode for a library.
- **ENABLE_OUTOFPROCESS** Enables in-process mode for a library.
- **ENABLE_OUTOFPROCESS_ALL_THREADS** Enables all threads unwinding for in-process mode. Ignored if out-process-mode is disabled.
- **ENABLE_LIBCORKSCREW** Enables "libcorkscrew" unwinder.
- **ENABLE_LIBUNWIND** Enables "libunwind" unwinder.
- **ENABLE_LIBUNWINDSTACK** Enables "libunwindstack" unwinder.
- **ENABLE_CXXABI** Enables "cxxabi" unwinder.
- **ENABLE_STACKSCAN** Enables "stackscan" unwinder.

Note that it's possible to build a library with all flags set to "false", in this case it would return error on initialization.

A good place to set this variables is *build.gradle* file of a module where NDK library is built:
```
        externalNativeBuild {
            cmake {
                arguments "-DANDROID_STL=c++_static",
                        "-DCMAKE_VERBOSE_MAKEFILE:BOOL=ON",
                        "-DENABLE_LIBCORKSCREW:BOOL=ON",
                        "-DENABLE_LIBUNWIND:BOOL=ON",
                        "-DENABLE_LIBUNWINDSTACK:BOOL=ON",
                        "-DENABLE_CXXABI:BOOL=ON",
                        "-DENABLE_STACKSCAN:BOOL=ON",
                        "-DENABLE_INPROCESS:BOOL=ON",
                        "-DENABLE_OUTOFPROCESS:BOOL=ON"
                        "-DEENABLE_OUTOFPROCESS_ALL_THREADS:BOOL=ON"
            }
        }
        ndk {
            abiFilters "x86", "armeabi-v7a", "x86_64", "arm64-v8a"
        }
```
Also in this place you can customize processor architectures for what a library should be built. **NDCrash** supports 32-bit and 64-bit ARM and x86 architectures.

## Usage ##

All public API of this library is located in a single header file, to use it please add to your sources: `include <ndcrash.h>`
To start using library you need to **initialize** it. **NDCrash** requires 2 parameters to be set in both modes:
- Unwinder. **NDCrash** library may be built with multiple unwinders simultaneously and an application code should determine what unwinder it will use. For example, different unwinders may be used for different operating system versions or processor architectures. Also A/B testing is possible in order to determine what unwinder could produce a better result. If a specified unwinder is disabled on compile time an initialization function will return an error.
- Crash report output file full path. **NDCrash** doesn't take care what file name to use to write a crash report when a crash happens. It may be either a same file for different launches or random-generated file name. If this file exists it will be overwritten. A path should follow these requirements:
  - A directory where this file is going to be created should exist. NDCrash won't create a directory in this case.
  - An application should have permissions to create and write this file.

It's an application developer responsibility to process this file in some way: send it to a server, offer a user to write a letter to developers, etc. Typically it may be done on a next application launch after a crash (a report existence may be an indicator whether a last launch has finished by crash). But out-of-process mode supports a special callback that could be executed straight after crash report generation, it's done in background service process and allows to send a report to a server immediately (but don't forget that in this case user interaction abilities are very limited).

Please note that **NDCrash** may be initialized only once, a repeated initialization will return an error. An initialization is done for a whole process and this operation is *not reentrant*. A good place for it is before any initialization code of application, for example `Application.onCreate` Java method or `JNI_OnLoad` function, before any background thread is created.

For examples you can take a look at [java wrapper source code](https://github.com/ivanarh/jndcrash).

### In-process ###

For this mode you should only provide an unwinder enum value and full path to a crash reprot. For example, initialization with "libunwind" unwinder:

```
    const char *crashReportPath = ...
    const enum ndcrash_error error = ndcrash_in_init(ndcrash_unwinder_libunwind, crashReportPath);
    if (error == ndcrash_ok) {
    	// Initialization is successful.
    } else {
		// Initialization failed, check error value.
    }
```
It will set up a signal handler that generates a crash report. In case when an old signal handler should be restored please use `ndcrash_in_deinit()` function, it doesn't have any arguments.

### Out-of-process ###

An initialization in this mode is quite more difficult: we should initialize 2 components that are run in different processes:
- Main process signal handler.
- Background process crash reporting daemon.

An UNIX domain socket is used by crashing process to communicate with daemon. To identify this socket **NDCrash** requires to specify one parameter yet: a special string called *socket name*. It should be unique for all applications that are installed on a device, so it's a good idea is to include an application package name to a socket name.

#### Main process signal handler initialization ####

As a signal handler in out-of-process mode doesn't generate a crash report, an unwinder and crash report path are not used for initialization. All you need is to specify a socket name:
```
    const char *socket_name = ...
    const enum ndcrash_error error = ndcrash_out_init(socket_name);
    if (error == ndcrash_ok) {
    	// Initialization is successful.
    } else {
		// Initialization failed, check error value.
    }
```

#### Background process crash reporting daemon initialization ####

For this mode you should only provide a crash report path, an unwinder enum value and full path to a crash reprot. For example, initialization with "libunwind" unwinder:

```
    const char *socket_name = ...
    const char *crashReportPath = ...
    const enum ndcrash_error error = ndcrash_in_init(
    		socket_name,
    		ndcrash_unwinder_libunwind,
    		crashReportPath,
    		NULL,
    		NULL,
    		NULL,
    		NULL);
    if (error == ndcrash_ok) {
    	// Initialization is successful.
    } else {
		// Initialization failed, check error value.
    }
```

The last 4 parameters of `ndcrash_in_init` that have NULL values are optional, this is daemon lifecycle callbacks that may be used to run a special code when crash happens. For example, we can send a report to server in a crash callback, it's called immediately after crash happens. For details please take a look at `jndcrash.c` source file.
All callbacks are run in background thread of crash reporting service process. The following callbacks can be set:

- Daemon start callback. It's run when a daemon is successfully started, when listening socket is bound before waiting for a new client is started. It may be used to attach this background thread to JNI in order to make JNI calls possible from crash callback.
- Crash callback. Called when a report is generated. A crash report path is passed to this callback as an argument.
- Daemon stop callback. Useful to detach a background thread from JNI.

Also 4th argument may be set: it's an auxiliary argument that is saved inside a library and passed to all callbacks. This argument can be obtained at any time by `ndcrash_out_get_daemon_callbacks_arg()` function.