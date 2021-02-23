#ifndef BOAT_ACTIVITY_H
#define BOAT_ACTIVITY_H

#include <jni.h>
#include <android/native_window_jni.h>
#include <android/log.h>

#define __FILENAME__ (__builtin_strrchr(__FILE__, '/') ? __builtin_strrchr(__FILE__, '/') + 1 : __FILE__)
#define BOAT_LOGE(fmt, ...) __android_log_print(ANDROID_LOG_ERROR, __FILENAME__, "%s: " fmt, __func__, ##__VA_ARGS__)
#define BOAT_LOGD(fmt, ...) __android_log_print(ANDROID_LOG_DEBUG, __FILENAME__, "%s: " fmt, __func__, ##__VA_ARGS__)

typedef struct {
    uint8_t isLoaded;
    JavaVM *vm;
    jobject boatActivity;
    jclass boatActivityClass;
    jmethodID setGrabCursorId;
    ANativeWindow *window;
} Boat_t;

Boat_t boat;

#endif
