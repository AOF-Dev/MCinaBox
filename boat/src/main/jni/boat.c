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

    if (!boat.isLoaded)
        return;

    jint result = (*boat.vm)->AttachCurrentThread(boat.vm, &env, 0);
    if (result != JNI_OK) {
        BOAT_LOGE("Failed to attach thread to JavaVM.");
        abort();
    }

    (*env)->CallVoidMethod(env, boat.boatActivity, boat.setCursorModeId, mode);

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
Java_cosine_boat_BoatInput_send(JNIEnv *env, jclass thiz, jlong time, jint type, jint param_1,
                                jint param_2) {
    current_event.time = time;
    current_event.type = type;

    if (type == ButtonPress || type == ButtonRelease) {
        current_event.mouse_button = param_1;
    } else if (type == KeyPress || type == KeyRelease) {
        current_event.keycode = param_1;
        current_event.keychar = param_2;
    } else if (type == MotionNotify) {
        current_event.x = param_1;
        current_event.y = param_2;
    }

    if (current_event_processor != NULL) {
        current_event_processor();
    }
}
