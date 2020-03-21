LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := client
LOCAL_SRC_FILES_RAW := $(shell find $(LOCAL_PATH) -name '*.cpp') $(shell find $(LOCAL_PATH) -name '*.c')
LOCAL_SRC_FILES := $(LOCAL_SRC_FILES_RAW:$(LOCAL_PATH)/%=%)
LOCAL_LDLIBS    := -llog -ldl -landroid -lEGL -lGLESv1_CM
TARGET_NO_UNDEFINED_LDFLAGS :=
LOCAL_CFLAGS += -O2
LOCAL_CPPFLAGS += -O2
include $(BUILD_SHARED_LIBRARY)
