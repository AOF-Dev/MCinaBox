package com.aof.mcinabox;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
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
