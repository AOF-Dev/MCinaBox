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

    // Get the loadClass function from NativeActivity's ClassLoader instance
    jmethodID getClassLoaderId = (*env)->GetMethodID(env,
                                                     boat.boatActivityClass, "getClassLoader",
                                                     "()Ljava/lang/ClassLoader;");
    jobject classLoader = (*env)->CallObjectMethod(env, thiz, getClassLoaderId);
    jclass classLoaderClass = (*env)->GetObjectClass(env, classLoader);
    jmethodID loadClassId = (*env)->GetMethodID(env,
                                                classLoaderClass, "loadClass",
                                                "(Ljava/lang/String;)Ljava/lang/Class;");

    // Get the BoatInput class
    jstring boatInputClassName = (*env)->NewStringUTF(env, "cosine/boat/BoatInput");
    jclass localBoatInputClass = (*env)->CallObjectMethod(env, classLoader, loadClassId,
                                                          boatInputClassName);
    if (localBoatInputClass == NULL) {
        BOAT_LOGE("Failed to find class: cosine/boat/BoatInput.");
        abort();
    }
    boat.boatInputClass = (*env)->NewGlobalRef(env, localBoatInputClass);

    boat.boatActivity = (*env)->NewGlobalRef(env, thiz);

    boat.isLoaded = 1;
}

JNIEXPORT void JNICALL
Java_cosine_boat_BoatActivity_nOnDestroy(JNIEnv *env, jobject thiz) {
    boat.isLoaded = 0;

    if (boat.boatActivityClass != NULL) {
        (*env)->DeleteGlobalRef(env, boat.boatActivityClass);
    }

    if (boat.boatInputClass != NULL) {
        (*env)->DeleteGlobalRef(env, boat.boatInputClass);
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
