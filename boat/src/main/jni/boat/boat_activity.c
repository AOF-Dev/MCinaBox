#include "boat.h"
#include <pthread.h>
#include <stdbool.h>
#include <android/log.h>

#define TAG "boat_activity"

static bool isLoop = false;
static pthread_t loopID;

void *looper(void *args);

//ANativeActivity callbacks
void onStart(ANativeActivity *activity) {

}

void onResume(ANativeActivity *activity) {

}

void *onSaveInstanceState(ANativeActivity *activity, size_t *outSize) {
    return NULL;
}

void onPause(ANativeActivity *activity) {

}

void onStop(ANativeActivity *activity) {

}

void onDestroy(ANativeActivity *activity) {

}

void onWindowFocusChanged(ANativeActivity *activity, int hasFocus) {

}

void onNativeWindowCreated(ANativeActivity *activity, ANativeWindow *win) {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "onNativeWindowCreated : %p", win);

    mBoat.window = win;
    mBoat.display = 0;
}

void onNativeWindowDestroyed(ANativeActivity *activity, ANativeWindow *win) {

}

void onNativeWindowRedrawNeeded(ANativeActivity *activity, ANativeWindow *win) {

}

void onNativeWindowResized(ANativeActivity *activity, ANativeWindow *win) {

}

void onInputQueueCreated(ANativeActivity *activity, AInputQueue *queue) {
    isLoop = true;
    activity->instance = (void *) queue;
    pthread_create(&loopID, NULL, looper, activity);
}

void onInputQueueDestroyed(ANativeActivity *activity, AInputQueue *queue) {

}

void onConfigurationChanged(ANativeActivity *activity) {

}

void onLowMemory(ANativeActivity *activity) {

}

void ANativeActivity_onCreate(ANativeActivity *activity, void *savedState, size_t savedStateSize) {
    activity->callbacks->onStart = onStart;
    activity->callbacks->onResume = onResume;
    activity->callbacks->onSaveInstanceState = onSaveInstanceState;
    activity->callbacks->onPause = onPause;
    activity->callbacks->onStop = onStop;
    activity->callbacks->onDestroy = onDestroy;
    activity->callbacks->onWindowFocusChanged = onWindowFocusChanged;
    activity->callbacks->onNativeWindowCreated = onNativeWindowCreated;
    activity->callbacks->onNativeWindowDestroyed = onNativeWindowDestroyed;
    activity->callbacks->onInputQueueCreated = onInputQueueCreated;
    activity->callbacks->onInputQueueDestroyed = onInputQueueDestroyed;
    activity->callbacks->onConfigurationChanged = onConfigurationChanged;
    activity->callbacks->onLowMemory = onLowMemory;
}

void *looper(void *args) {
    ANativeActivity *activity = (ANativeActivity *) args;
    AInputQueue *queue = (AInputQueue *) activity->instance;
    AInputEvent *event = NULL;

    while (isLoop) {
        while (!AInputQueue_hasEvents(queue)) {}
        AInputQueue_getEvent(queue, &event);
        sendKeyEvent(event);
        AInputQueue_finishEvent(queue, event, 1);
    }

    return args;
}

