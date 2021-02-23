package com.aof.mcinabox.launcher.uis;

public interface UILifecycleCallbacks {
    void onCreate();

    void onStart();

    void onResume();

    void onRestart();

    void onPause();

    void onStop();

    void onDestory();
}
