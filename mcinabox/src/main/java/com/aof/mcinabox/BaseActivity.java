package com.aof.mcinabox;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    public MCinaBox getMCinaBox() {
        return (MCinaBox) getApplication();
    }
}
