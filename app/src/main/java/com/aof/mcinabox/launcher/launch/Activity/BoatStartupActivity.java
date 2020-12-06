package com.aof.mcinabox.launcher.launch.Activity;

import android.app.Activity;
import android.view.View;
import com.aof.mcinabox.gamecontroller.client.Client;
import cosine.boat.BoatActivity;

public class BoatStartupActivity extends BoatActivity implements Client {

    @Override
    public void setKey(int keyCode, boolean pressed) {
        this.setKey(keyCode,0,pressed);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void addView(View v) {
        this.addContentView(v, v.getLayoutParams());
    }

    @Override
    public void typeWords(String str) {
        for(int i = 0; i < str.length(); i++){
            setKey(0, str.charAt(i),true);
            setKey(0, str.charAt(i),false);
        }
    }
}
