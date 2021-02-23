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

// TODO: Should be boatSetGrabCursor
void boatSetCursorMode(int mode) {
    JNIEnv *env;

    if (!boat.isLoaded)
        return;

    jint result = (*boat.vm)->AttachCurrentThread(boat.vm, &env, 0);
    if (result != JNI_OK) {
        BOAT_LOGE("Failed to attach thread to JavaVM.");
        abort();
    }

    (*env)->CallVoidMethod(env, boat.boatActivity, boat.setGrabCursorId, mode == CursorDisabled ? JNI_TRUE : JNI_FALSE);

    (*boat.vm)->DetachCurrentThread(boat.vm);
}

JNIEXPORT jintArray JNICALL
Java_cosine_boat_BoatInput_getPointer(JNIEnv *env, jclass thiz) {
    jintArray ja = (*env)->NewIntArray(env, 2);
    int arr[2] = {current_event.x, current_event.y};
    (*env)->SetIntArrayRegion(env, ja, 0, 2, arr);
    return ja;
}

JNIEXPORT void JNICALL
Java_cosine_boat_BoatInput_setMouseButton(JNIEnv *env, jclass clazz, jlong time, jint button,
                                          jboolean is_pressed) {
    current_event.time = time;
    current_event.mouse_button = button;
    current_event.type = is_pressed == JNI_TRUE ? ButtonPress : ButtonRelease;
    if (current_event_processor != NULL) {
        current_event_processor();
    }
}

JNIEXPORT void JNICALL
Java_cosine_boat_BoatInput_setPointer(JNIEnv *env, jclass clazz, jlong time, jint x, jint y) {
    current_event.time = time;
    current_event.x = x;
    current_event.y = y;
    current_event.type = MotionNotify;
    if (current_event_processor != NULL) {
        current_event_processor();
    }
}

JNIEXPORT void JNICALL
Java_cosine_boat_BoatInput_setKey(JNIEnv *env, jclass clazz, jlong time, jboolean is_pressed,
                                  jint key_code, jint key_char) {
    current_event.time = time;
    current_event.keycode = key_code;
    current_event.keychar = key_char;
    current_event.type = is_pressed == JNI_TRUE ? KeyPress : KeyRelease;
    if (current_event_processor != NULL) {
        current_event_processor();
    }
}
