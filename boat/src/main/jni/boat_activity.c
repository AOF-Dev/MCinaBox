#include "boat_activity.h"
#include <pthread.h>
#include <stdlib.h>

static uint8_t shouldLoop;
static pthread_t inputQueueThread;

static void *inputQueueThreadFunc(void *arg) {
    AInputQueue *queue = (AInputQueue *) arg;
    JNIEnv *env;

    jint res = (*boat.boatActivity->vm)->AttachCurrentThread(boat.boatActivity->vm, &env, NULL);
    if (res != JNI_OK) {
        BOAT_LOGE("Failed to attach thread to JavaVM.");
        goto exit;
    }

    jmethodID onInputEventId = (*env)->GetMethodID(env, boat.boatActivityClass,
            "onInputEvent", "(JJIIIIIIII)V");
    if (onInputEventId == NULL) {
        BOAT_LOGE("onInputEvent method could not be found. Exiting.");
        goto exit_detach;
    }

    while (shouldLoop) {
        if (AInputQueue_hasEvents(queue)) {
            AInputEvent *event;
            AInputQueue_getEvent(queue, &event);
            (*env)->CallVoidMethod(env, boat.boatActivity->clazz, onInputEventId,
                                   AKeyEvent_getDownTime(event), AKeyEvent_getEventTime(event),
                                   AKeyEvent_getAction(event), AKeyEvent_getKeyCode(event),
                                   AKeyEvent_getRepeatCount(event), AKeyEvent_getMetaState(event),
                                   AInputEvent_getDeviceId(event), AKeyEvent_getScanCode(event),
                                   AKeyEvent_getFlags(event), AInputEvent_getSource(event));
            AInputQueue_finishEvent(queue, event, 1);
        }
    }

    exit_detach:
    (*boat.boatActivity->vm)->DetachCurrentThread(boat.boatActivity->vm);
    exit:
    return NULL;
}

static void onNativeWindowCreated(ANativeActivity *activity, ANativeWindow *window) {
    boat.window = window;
}

static void onInputQueueCreated(ANativeActivity *activity, AInputQueue *queue) {
    shouldLoop = 1;
    pthread_create(&inputQueueThread, NULL, inputQueueThreadFunc, queue);
}

static void onInputQueueDestroyed(ANativeActivity *activity, AInputQueue *queue) {
    shouldLoop = 0;
    pthread_join(inputQueueThread, NULL);
}

static void onDestroy(ANativeActivity *activity) {
    boat.isLoaded = 0;

    if (boat.boatActivityClass != NULL) {
        (*activity->env)->DeleteGlobalRef(activity->env, boat.boatActivityClass);
    }

    if (boat.boatInputClass != NULL) {
        (*activity->env)->DeleteGlobalRef(activity->env, boat.boatInputClass);
    }
}

void BoatActivity_onCreate(ANativeActivity *activity, void *savedState, size_t savedStateSize) {
    JNIEnv *env = activity->env;

    // Save the NativeActivity globally for later use
    boat.boatActivity = activity;

    // Set the required callbacks
    activity->callbacks->onNativeWindowCreated = onNativeWindowCreated;
    activity->callbacks->onInputQueueCreated = onInputQueueCreated;
    activity->callbacks->onInputQueueDestroyed = onInputQueueDestroyed;
    activity->callbacks->onDestroy = onDestroy;

    // Get the BoatActivity class
    jclass localBoatActivityClass = (*env)->GetObjectClass(env, activity->clazz);
    boat.boatActivityClass = (*env)->NewGlobalRef(env, localBoatActivityClass);

    // Get the loadClass function from NativeActivity's ClassLoader instance
    jmethodID getClassLoaderId = (*env)->GetMethodID(env,
            boat.boatActivityClass, "getClassLoader", "()Ljava/lang/ClassLoader;");
    jobject classLoader = (*env)->CallObjectMethod(env, activity->clazz, getClassLoaderId);
    jclass classLoaderClass = (*env)->GetObjectClass(env, classLoader);
    jmethodID loadClassId = (*env)->GetMethodID(env,
            classLoaderClass, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");

    // Get the BoatInput class
    jstring boatInputClassName = (*env)->NewStringUTF(env, "cosine/boat/BoatInput");
    jclass localBoatInputClass = (*env)->CallObjectMethod(env, classLoader, loadClassId, boatInputClassName);
    if (localBoatInputClass == NULL) {
        BOAT_LOGE("Failed to find class: cosine/boat/BoatInput.");
        abort();
    }
    boat.boatInputClass = (*env)->NewGlobalRef(env, localBoatInputClass);

    boat.isLoaded = 1;
}

JNIEXPORT jboolean JNICALL
Java_cosine_boat_BoatActivity_isLoaded(JNIEnv *env, jobject thiz) {
    return boat.isLoaded;
}
