#include "boat_activity.h"
#include <fcntl.h>
#include <jni.h>
#include <dlfcn.h>
#include <unistd.h>
#include <stdlib.h>
#include <pthread.h>

JNIEXPORT void JNICALL
Java_cosine_boat_LoadMe_redirectStdio(JNIEnv *env, jclass clazz, jstring filepath) {
    char const *file = (*env)->GetStringUTFChars(env, filepath, NULL);

    int fd = open(file, O_WRONLY | O_CREAT | O_TRUNC, 0666);
    dup2(fd, STDOUT_FILENO);
    dup2(fd, STDERR_FILENO);
    close(fd);

    (*env)->ReleaseStringUTFChars(env, filepath, file);
}

JNIEXPORT void JNICALL
Java_cosine_boat_LoadMe_chdir(JNIEnv *env, jclass clazz, jstring sDir) {
    char const *dir = (*env)->GetStringUTFChars(env, sDir, NULL);

    chdir(dir);

    (*env)->ReleaseStringUTFChars(env, sDir, dir);
}

JNIEXPORT void JNICALL
Java_cosine_boat_LoadMe_setenv(JNIEnv *env, jclass clazz, jstring sName, jstring sValue) {
    char const *name = (*env)->GetStringUTFChars(env, sName, NULL);
    char const *value = (*env)->GetStringUTFChars(env, sValue, NULL);

    setenv(name, value, 1);

    (*env)->ReleaseStringUTFChars(env, sName, name);
    (*env)->ReleaseStringUTFChars(env, sValue, value);
}

JNIEXPORT void JNICALL
Java_cosine_boat_LoadMe_dlopen(JNIEnv *env, jclass clazz, jstring str1) {
    char const *lib_name = (*env)->GetStringUTFChars(env, str1, NULL);

    void *handle = dlopen(lib_name, RTLD_LAZY);
    if (handle == NULL) {
        BOAT_LOGE("Error while loading %s: %s.", lib_name, dlerror());
    } else {
        BOAT_LOGD("%s loaded successfully.", lib_name);
    }

    (*env)->ReleaseStringUTFChars(env, str1, lib_name);
}

#define FULL_VERSION "1.8.0-unknown"
#define DOT_VERSION "1.8"
#define PROGNAME "java"
#define LAUNCHER_NAME "openjdk"

// Copied from java.h
static const char *const_full_version = FULL_VERSION;
static const char *const_dot_version = DOT_VERSION;
static const char *const_progname = PROGNAME;
static const char *const_launcher = LAUNCHER_NAME;
static const char **const_jargs = NULL;
static const char **const_appclasspath = NULL;
static const jboolean const_cpwildcard = JNI_TRUE;
static const jboolean const_javaw = JNI_FALSE;
static const jint const_ergo_class = 0; // DEFAULT_POLICY

int
(*JLI_Launch)(int argc, char **argv,                 /* main argc, argc */
              int jargc, const char **jargv,         /* java args */
              int appclassc, const char **appclassv, /* app classpath */
              const char *fullversion,               /* full version defined */
              const char *dotversion,                /* dot version defined */
              const char *pname,                     /* program name */
              const char *lname,                     /* launcher name */
              jboolean javaargs,                     /* JAVA_ARGS */
              jboolean cpwildcard,                   /* classpath wildcard */
              jboolean javaw,                        /* windows-only javaw */
              jint ergo_class                        /* ergnomics policy */
);

JNIEXPORT void JNICALL Java_cosine_boat_LoadMe_setupJLI(JNIEnv *env, jclass clazz) {
    void *handle = dlopen("libjli.so", RTLD_LAZY);
    JLI_Launch = (int (*)(int, char **, int, const char **, int, const char **, const char *,
                          const char *, const char *, const char *, jboolean, jboolean, jboolean,
                          jint)) dlsym(handle, "JLI_Launch");
}

JNIEXPORT jint JNICALL
Java_cosine_boat_LoadMe_jliLaunch(JNIEnv *env, jclass clazz, jobjectArray argsArray) {
    int argc = (*env)->GetArrayLength(env, argsArray);
    char **argv = (char **) malloc(sizeof(char *) * argc); // Should this be freed?

    for (int i = 0; i < argc; i++) {
        jstring str = (jstring) (*env)->GetObjectArrayElement(env, argsArray,
                                                              i); // Should this be freed?
        argv[i] = (char *) (*env)->GetStringUTFChars(env, str, NULL);
    }

    return JLI_Launch(argc, argv,
                      sizeof(const_jargs) / sizeof(char *), const_jargs,
                      sizeof(const_appclasspath) / sizeof(char *), const_appclasspath,
                      (const_full_version != NULL) ? const_full_version : FULL_VERSION,
                      (const_dot_version != NULL) ? const_dot_version : DOT_VERSION,
                      (const_progname != NULL) ? const_progname : *argv,
                      (const_launcher != NULL) ? const_launcher : *argv,
                      (const_jargs != NULL) ? JNI_TRUE : JNI_FALSE,
                      const_cpwildcard, const_javaw, const_ergo_class);
}
