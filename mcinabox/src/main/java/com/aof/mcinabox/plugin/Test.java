package com.aof.mcinabox.plugin;

import android.app.Activity;

public class Test implements MCinaBoxPlugin {
    //Test Class
    @Override
    public void launchPlugin(Activity context) {

    }

    @Override
    public void stopPlugin(Activity context) {

    }

    @Override
    public boolean deployPlugin(Activity context) {
        return false;
    }

    @Override
    public boolean removePlugin(Activity context) {
        return false;
    }

}
