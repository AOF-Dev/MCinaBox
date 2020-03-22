LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := ndcrash
# LOCAL_SRC_FILES_RAW := $(shell find $(LOCAL_PATH) -name '*.c')
# LOCAL_SRC_FILES := $(LOCAL_SRC_FILES_RAW:$(LOCAL_PATH)/%=%)
LOCAL_SRC_FILES := src/ndcrash_dump.c src/ndcrash_fd_utils.c src/ndcrash_in.c src/ndcrash_memory_map.c src/ndcrash_out.c src/ndcrash_out_daemon.c src/ndcrash_signal_utils.c src/ndcrash_utils.c
LOCAL_SRC_FILES += src/unwinders/libcorkscrew/ndcrash_libcorkscrew.c
LOCAL_LDLIBS    := -llog -ldl 
TARGET_NO_UNDEFINED_LDFLAGS :=
LOCAL_CFLAGS += -mthumb -std=c11 -DENABLE_LIBCORKSCREW:BOOL=ON -DENABLE_OUTOFPROCESS:BOOL=ON -DEENABLE_OUTOFPROCESS_ALL_THREADS:BOOL=ON -D_GNU_SOURCE:BOOL=ON
LOCAL_SHARED_LIBRARIES += libcorkscrew_unwinder
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include/ $(LOCAL_PATH)/src/
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include/
include $(BUILD_SHARED_LIBRARY)



