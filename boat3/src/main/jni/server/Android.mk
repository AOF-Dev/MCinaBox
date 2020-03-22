LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := server
LOCAL_SRC_FILES := Server.cpp
LOCAL_LDLIBS    := -llog -ldl -L$(LOCAL_PATH)/ -lclient
TARGET_NO_UNDEFINED_LDFLAGS :=
LOCAL_CFLAGS += -O2 
LOCAL_CPPFLAGS += -O2
include $(BUILD_SHARED_LIBRARY)
