LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := client
LOCAL_SRC_FILES := Client.cpp \
					Main.cpp
LOCAL_LDLIBS    := -llog -ldl -landroid -lEGL -lGLESv1_CM
TARGET_NO_UNDEFINED_LDFLAGS :=
LOCAL_CFLAGS += -O2
LOCAL_CPPFLAGS += -O2
include $(BUILD_SHARED_LIBRARY)
