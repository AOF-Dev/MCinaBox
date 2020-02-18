package com.aof.mcinabox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;

public class VirtualKeyBoardActivity extends AppCompatActivity {

    ConstraintLayout layout_keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_keyboard);

        layout_keyboard = findViewById(R.id.layout_keyboard);
        Button testButton = new Button(getApplicationContext());
        testButton.setText("Test");
        testButton.setX(50);
        testButton.setY(30);
        testButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        layout_keyboard.addView(testButton);

    }
}
