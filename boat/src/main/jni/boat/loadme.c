#include <fcntl.h>
#include <jni.h>
#include <dlfcn.h>
#include <android/log.h>
#include <unistd.h>
#include <stdlib.h>

#define TAG "loadme"

JNIEXPORT void JNICALL
Java_cosine_boat_LoadMe_redirectStdio(JNIEnv *env, jclass clazz, jstring path) {
    char const *file = (*env)->GetStringUTFChars(env, path, 0);

    int fd = open(file, O_WRONLY | O_CREAT | O_TRUNC, 0666);
    dup2(fd, STDOUT_FILENO);
    dup2(fd, STDERR_FILENO);

    (*env)->ReleaseStringUTFChars(env, path, file);
}

JNIEXPORT jint JNICALL Java_cosine_boat_LoadMe_chdir(JNIEnv *env, jclass clazz, jstring path) {
    char const *dir = (*env)->GetStringUTFChars(env, path, 0);
    int b = chdir(dir);
    (*env)->ReleaseStringUTFChars(env, path, dir);
    return b;
}

JNIEXPORT void JNICALL
Java_cosine_boat_LoadMe_setenv(JNIEnv *env, jclass clazz, jstring str1, jstring str2) {
    char const *name = (*env)->GetStringUTFChars(env, str1, 0);
    char const *value = (*env)->GetStringUTFChars(env, str2, 0);

    setenv(name, value, 1);

    (*env)->ReleaseStringUTFChars(env, str1, name);
    (*env)->ReleaseStringUTFChars(env, str2, value);
}

JNIEXPORT jint JNICALL Java_cosine_boat_LoadMe_dlopen(JNIEnv *env, jclass clazz, jstring str1) {
    int ret = 0;
    char const *lib_name = (*env)->GetStringUTFChars(env, str1, 0);
    void *handle;

    handle = dlopen(lib_name, RTLD_LAZY);
    if (handle == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Error whlie loading %s: %s.", lib_name, dlerror());
        ret = 1;
    } else {
        __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s loaded successfully.", lib_name);
    }

    (*env)->ReleaseStringUTFChars(env, str1, lib_name);
    return ret;
}

// copy from java.c
#define FULL_VERSION "1.8.0-internal-cosine_2019_12_31_15_53-b00"
#define DOT_VERSION "1.8"
#define PROGNAME "java"
#define LAUNCHER_NAME "openjdk"

static char *const_progname = PROGNAME;
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
    void *handle;
    handle = dlopen("libjli.so", RTLD_LAZY);
    JLI_Launch = (int (*)(int, char **, int, const char **, int, const char **, const char *,
                          const char *, const char *, const char *, jboolean, jboolean, jboolean,
                          jint)) dlsym(handle, "JLI_Launch");
}

JNIEXPORT jint JNICALL
Java_cosine_boat_LoadMe_jliLaunch(JNIEnv *env, jclass clazz, jobjectArray argsArray) {
    int argc = (*env)->GetArrayLength(env, argsArray);
    char *argv[argc];
    for (int i = 0; i < argc; i++) {
        jstring str = (*env)->GetObjectArrayElement(env, argsArray, i);
        int len = (*env)->GetStringUTFLength(env, str);
        char *buf = malloc(len + 1);
        int characterLen = (*env)->GetStringLength(env, str);
        (*env)->GetStringUTFRegion(env, str, 0, characterLen, buf);
        buf[len] = 0;
        argv[i] = buf;
    }

    return JLI_Launch(argc, argv,
                      sizeof(const_jargs) / sizeof(char *), const_jargs,
                      sizeof(const_appclasspath) / sizeof(char *), const_appclasspath,
                      FULL_VERSION,
                      DOT_VERSION,
                      (const_progname != NULL) ? const_progname : *argv,
                      (const_launcher != NULL) ? const_launcher : *argv,
                      (const_jargs != NULL) ? JNI_TRUE : JNI_FALSE,
                      const_cpwildcard, const_javaw, const_ergo_class);
}
