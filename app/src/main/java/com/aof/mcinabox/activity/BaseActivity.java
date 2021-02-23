package com.aof.mcinabox.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.aof.mcinabox.MCinaBox;

public abstract class BaseActivity extends AppCompatActivity {
    public BaseActivity() {
        super();
    }

    public BaseActivity(int contentLayoutId) {
        super(contentLayoutId);
    }

    public MCinaBox getMCinaBox() {
        return (MCinaBox) getApplication();
    }
}
