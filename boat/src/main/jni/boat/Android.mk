
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := boat
LOCAL_SRC_FILES := boat.c
LOCAL_LDLIBS    := -llog -ldl
TARGET_NO_UNDEFINED_LDFLAGS := 
LOCAL_SHARED_LIBRARIES :=
LOCAL_CFLAGS += -mthumb -O2 -std=gnu99
include $(BUILD_SHARED_LIBRARY)


