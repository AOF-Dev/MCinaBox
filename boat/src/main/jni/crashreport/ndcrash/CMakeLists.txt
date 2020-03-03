cmake_minimum_required(VERSION 3.4.1)
project(ndcrash)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Werror=implicit-function-declaration -Werror=incompatible-function-pointer-types")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11") #For libunwindstack only.

set(NDCRASH_SOURCE_ROOT ${CMAKE_CURRENT_SOURCE_DIR}/src)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include ${NDCRASH_SOURCE_ROOT})
file(GLOB NDCRASH_SOURCES ${NDCRASH_SOURCE_ROOT}/*.c)

if (${ENABLE_INPROCESS})
    add_definitions(-DENABLE_INPROCESS)
endif()

if (${ENABLE_OUTOFPROCESS})
    add_definitions(-DENABLE_OUTOFPROCESS)
endif()

if (${ENABLE_OUTOFPROCESS_ALL_THREADS})
    add_definitions(-DENABLE_OUTOFPROCESS_ALL_THREADS)
endif()

if (${ENABLE_LIBCORKSCREW})
    if (${CMAKE_SYSTEM_PROCESSOR} MATCHES arm OR ${CMAKE_SYSTEM_PROCESSOR} MATCHES i686)
        message(STATUS "Unwinder enabled: libcorkscrew")
        add_definitions(-DENABLE_LIBCORKSCREW)
        file(GLOB NDCRASH_UNWINDER_SOURCES ${NDCRASH_SOURCE_ROOT}/unwinders/libcorkscrew/*.c)
        list(APPEND NDCRASH_SOURCES ${NDCRASH_UNWINDER_SOURCES})

        include_directories(${CMAKE_CURRENT_SOURCE_DIR}/external/libcorkscrew-ndk/include)
        add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/external/libcorkscrew-ndk/cmake)
        list(APPEND LINK_LIBRARIES corkscrew)
    else()
        message(WARNING "libcorkscrew isn't supported on this architecture: ${CMAKE_SYSTEM_PROCESSOR}")
    endif()
else()
    message(STATUS "Unwinder disabled: libcorkscrew")
endif()

if (${ENABLE_LIBUNWIND})
    message(STATUS "Unwinder enabled: libunwind")
    add_definitions(-DENABLE_LIBUNWIND)

    #Creating object library in order to pass additional defines for its sources.
    file(GLOB NDCRASH_UNWINDER_SOURCES ${NDCRASH_SOURCE_ROOT}/unwinders/libunwind/*.c)
    add_library(libunwindobjects OBJECT ${NDCRASH_UNWINDER_SOURCES})
    list(APPEND LIBUNWIND_OBJECTS_DEFINES
        HAVE_CONFIG_H
        _GNU_SOURCE
    )
    set_target_properties(libunwindobjects
        PROPERTIES
        COMPILE_DEFINITIONS "${LIBUNWIND_OBJECTS_DEFINES}"
    )
    list(APPEND NDCRASH_SOURCES $<TARGET_OBJECTS:libunwindobjects>)

    include_directories(
        ${CMAKE_CURRENT_SOURCE_DIR}/external/libunwind-ndk/include
        ${CMAKE_CURRENT_SOURCE_DIR}/external/libunwind-ndk/include/tdep
        ${CMAKE_CURRENT_SOURCE_DIR}/external/libunwind-ndk/src
    )
    add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/external/libunwind-ndk/cmake)
    list(APPEND LINK_LIBRARIES unwind)
else()
    message(STATUS "Unwinder disabled: libunwind")
endif()

if (${ENABLE_LIBUNWINDSTACK})
    message(STATUS "Unwinder enabled: libunwindstack")
    add_definitions(-DENABLE_LIBUNWINDSTACK)
    file(GLOB NDCRASH_UNWINDER_SOURCES ${NDCRASH_SOURCE_ROOT}/unwinders/libunwindstack/*.cpp)
    list(APPEND NDCRASH_SOURCES ${NDCRASH_UNWINDER_SOURCES})

    include_directories(${CMAKE_CURRENT_SOURCE_DIR}/external/libunwindstack-ndk/include)
    add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/external/libunwindstack-ndk/cmake)
    list(APPEND LINK_LIBRARIES unwindstack)
else()
    message(STATUS "Unwinder disabled: libunwindstack")
endif()

if (${ENABLE_CXXABI})
    message(STATUS "Unwinder enabled: cxxabi")
    add_definitions(-DENABLE_CXXABI)
    file(GLOB NDCRASH_UNWINDER_SOURCES ${NDCRASH_SOURCE_ROOT}/unwinders/cxxabi/*.c)
    list(APPEND NDCRASH_SOURCES ${NDCRASH_UNWINDER_SOURCES})
else()
    message(STATUS "Unwinder disabled: cxxabi")
endif()

if (${ENABLE_STACKSCAN})
    message(STATUS "Unwinder enabled: stackscan")
    add_definitions(-DENABLE_STACKSCAN)
    file(GLOB NDCRASH_UNWINDER_SOURCES ${NDCRASH_SOURCE_ROOT}/unwinders/stackscan/*.c)
    list(APPEND NDCRASH_SOURCES ${NDCRASH_UNWINDER_SOURCES})
else()
    message(STATUS "Unwinder disabled: stackscan")
endif()

add_library(ndcrash STATIC ${NDCRASH_SOURCES})
target_link_libraries(ndcrash ${LINK_LIBRARIES})