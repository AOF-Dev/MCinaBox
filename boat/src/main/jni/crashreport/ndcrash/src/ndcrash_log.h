#ifndef NDCRASHDEMO_NDCRASH_LOG_H
#define NDCRASHDEMO_NDCRASH_LOG_H
#include <android/log.h>

#ifndef NDCRASH_LOG_TAG
#define NDCRASH_LOG_TAG "NDCRASH"
#endif

#ifdef NDCRASH_NO_LOG
#define NDCRASHLOG(level, ...)
#else
#define NDCRASHLOG(level, ...) __android_log_print(ANDROID_LOG_##level, NDCRASH_LOG_TAG, __VA_ARGS__)
#endif

#endif //NDCRASHDEMO_NDCRASH_LOG_H
