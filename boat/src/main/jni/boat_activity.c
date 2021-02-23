#include "boat_activity.h"
#include <pthread.h>
#include <stdlib.h>

JNIEXPORT jboolean JNICALL
Java_cosine_boat_BoatActivity_nIsLoaded(JNIEnv *env, jobject thiz) {
    return boat.isLoaded;
}

JNIEXPORT void JNICALL
Java_cosine_boat_BoatActivity_nOnCreate(JNIEnv *env, jobject thiz) {
    jint result = (*env)->GetJavaVM(env, &boat.vm);
    if (result) {
        BOAT_LOGE("Failed to get the Java VM!");
        return;
    }

    // Get the BoatActivity class
    jclass localBoatActivityClass = (*env)->GetObjectClass(env, thiz);
    boat.boatActivityClass = (*env)->NewGlobalRef(env, localBoatActivityClass);

    // Get the setGrabCursor function from the BoatActivity class
    boat.setGrabCursorId = (*env)->GetMethodID(env,
                                               boat.boatActivityClass, "setGrabCursor",
                                               "(Z)V");
    if (boat.setGrabCursorId == NULL) {
        BOAT_LOGE("Failed to find method: BoatActivity::setGrabCursor");
        abort();
    }

    boat.boatActivity = (*env)->NewGlobalRef(env, thiz);

    boat.isLoaded = 1;
}

JNIEXPORT void JNICALL
Java_cosine_boat_BoatActivity_nOnDestroy(JNIEnv *env, jobject thiz) {
    boat.isLoaded = 0;

    if (boat.boatActivityClass != NULL) {
        (*env)->DeleteGlobalRef(env, boat.boatActivityClass);
    }

    if (boat.boatActivity != NULL) {
        (*env)->DeleteGlobalRef(env, boat.boatActivity);
    }
}

JNIEXPORT void JNICALL
Java_cosine_boat_BoatActivity_nSurfaceCreated(JNIEnv *env, jobject thiz, jobject surface) {
    boat.window = ANativeWindow_fromSurface(env, surface);
}

JNIEXPORT void JNICALL
Java_cosine_boat_BoatActivity_nSurfaceDestroyed(JNIEnv *env, jobject thiz, jobject surface) {
    ANativeWindow_release(boat.window);
}
