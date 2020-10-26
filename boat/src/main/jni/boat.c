#include "boat.h"
#include "boat_activity.h"
#include <string.h>
#include <stdlib.h>

void (*current_event_processor)();
BoatInputEvent current_event;

EGLNativeWindowType boatGetNativeWindow() {
    return (EGLNativeWindowType) boat.window;
}

EGLNativeDisplayType boatGetNativeDisplay() {
    return EGL_DEFAULT_DISPLAY;
}

void boatGetCurrentEvent(BoatInputEvent *event) {
    memcpy(event, &current_event, sizeof(BoatInputEvent));
}

void boatSetCurrentEventProcessor(void (*processor)()) {
    current_event_processor = processor;
}

void boatSetCursorMode(int mode) {
    JNIEnv *env;

    jint result = (*boat.boatActivity->vm)->AttachCurrentThread(boat.boatActivity->vm, &env, 0);
    if (result != JNI_OK) {
        BOAT_LOGE("Failed to attach thread to JavaVM.");
        abort();
    }

    jmethodID setCursorModeId = (*env)->GetStaticMethodID(env, boat.boatInputClass, "setCursorMode", "(I)V");
    if (setCursorModeId == NULL) {
        BOAT_LOGE("Failed to get static method BoatInput::setCursorMode");
        abort();
    }
    (*env)->CallStaticVoidMethod(env, boat.boatInputClass, setCursorModeId, mode);

    (*boat.boatActivity->vm)->DetachCurrentThread(boat.boatActivity->vm);
}

JNIEXPORT jintArray JNICALL
Java_cosine_boat_BoatInput_get(JNIEnv *env, jclass clazz) {
    jintArray ja = (*env)->NewIntArray(env, 2);
    int arr[2] = {current_event.x, current_event.y};
    (*env)->SetIntArrayRegion(env, ja, 0, 2, arr);
    return ja;
}

JNIEXPORT void JNICALL
Java_cosine_boat_BoatInput_send(JNIEnv *env, jclass clazz, jlong time, jint type, jint p1, jint p2) {
    current_event.time = time;
    current_event.type = type;

    if (type == ButtonPress || type == ButtonRelease) {
        current_event.mouse_button = p1;
    } else if (type == KeyPress || type == KeyRelease) {
        current_event.keycode = p1;
        current_event.keychar = p2;
    } else if (type == MotionNotify) {
        current_event.x = p1;
        current_event.y = p2;
    }

    if (current_event_processor != NULL) {
        current_event_processor();
    }
}
