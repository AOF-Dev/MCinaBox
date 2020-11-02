package com.aof.mcinabox;

import android.app.Application;

public class MCinaBox extends Application {
    private static final String TAG = "MCinaBox";

    private boolean initFailed;

    @Override
    public void onCreate() {
        super.onCreate();

        initFailed = false;
    }

    public boolean isInitFailed() {
        return initFailed;
    }
}
