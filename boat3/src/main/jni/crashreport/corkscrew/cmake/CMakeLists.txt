cmake_minimum_required(VERSION 2.8)
set(CORKSCREW_ROOT ${CMAKE_CURRENT_SOURCE_DIR}/..)

list(APPEND CORKSCREW_INCLUDES
	${CORKSCREW_ROOT}/include
	${CORKSCREW_ROOT}
)

list(APPEND CORKSCREW_SOURCES
	${CORKSCREW_ROOT}/backtrace.c
	${CORKSCREW_ROOT}/backtrace-helper.c
	${CORKSCREW_ROOT}/demangle.c
	${CORKSCREW_ROOT}/map_info.c
	${CORKSCREW_ROOT}/ptrace.c
	${CORKSCREW_ROOT}/symbol_table.c
)

if (${CMAKE_SYSTEM_PROCESSOR} MATCHES ".*arm.*")
	list(APPEND CORKSCREW_SOURCES
		${CORKSCREW_ROOT}/arch-arm/backtrace-arm.c
		${CORKSCREW_ROOT}/arch-arm/ptrace-arm.c
	)
elseif (${CMAKE_SYSTEM_PROCESSOR} MATCHES ".*i.86.*")
	list(APPEND CORKSCREW_SOURCES
		${CORKSCREW_ROOT}/arch-x86/backtrace-x86.c
		${CORKSCREW_ROOT}/arch-x86/ptrace-x86.c
	)
elseif (${CMAKE_SYSTEM_PROCESSOR} MATCHES ".*mips.*")
	list(APPEND CORKSCREW_SOURCES
		${CORKSCREW_ROOT}/arch-mips/backtrace-mips.c
		${CORKSCREW_ROOT}/arch-mips/ptrace-mips.c
	)
else()
	message(FATAL_ERROR "Architecture isn't supported: ${CMAKE_SYSTEM_PROCESSOR}")
endif()

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=gnu99")
include_directories(${CORKSCREW_INCLUDES})
add_library(corkscrew STATIC ${CORKSCREW_SOURCES})
list(APPEND CORKSCREW_DEFINES
	CORKSCREW_HAVE_ARCH
)
set_target_properties(corkscrew
	PROPERTIES
	COMPILE_DEFINITIONS "${CORKSCREW_DEFINES}"
)