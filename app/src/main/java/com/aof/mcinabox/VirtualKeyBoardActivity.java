package com.aof.mcinabox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aof.mcinabox.keyboardUtils.ConfigDialog;
import com.aof.mcinabox.keyboardUtils.GameButton;

import java.util.ArrayList;

public class VirtualKeyBoardActivity extends AppCompatActivity {

    ConstraintLayout layout_keyboard;
    ArrayList<GameButton> keyboardList;
    Button button_addKey;
    Button[] launcherBts;
    ConfigDialog configDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_keyboard);
        configDialog = new ConfigDialog(VirtualKeyBoardActivity.this);

        button_addKey = findViewById(R.id.keyboard_button_addKey);
        launcherBts = new Button[]{button_addKey};
        for (Button button : launcherBts) {
            button.setOnClickListener(listener);
        }


        layout_keyboard = findViewById(R.id.layout_keyboard);
        keyboardList = new ArrayList<GameButton>();
        addStandKey("Test", ViewGroup.LayoutParams.WRAP_CONTENT, 225, 50, 30, 1, 0, 0, false, false, false);
        for (GameButton targetButton : keyboardList) {
            layout_keyboard.addView(targetButton);
        }

    }

    //键名称 键大小 透明度 X轴位置 Y轴位置 键值 特殊键1 特殊键2 是否保持 是否隐藏 是否是组合键
    private void addStandKey(String KeyName, int KeySize, int KeyAlpha, float KeyLX, float KeyLY, int KeyIndex, int SpecialOne, int SpecialTwo, boolean isAutoKeep, boolean isHide, boolean isMult) {
        GameButton KeyButton = new GameButton(getApplicationContext());
        KeyButton.setText(KeyName);
        KeyButton.setLayoutParams(new ViewGroup.LayoutParams(KeySize, KeySize));
        KeyButton.setAlpha(KeyAlpha);
        KeyButton.setX(KeyLX);
        KeyButton.setY(KeyLY);
        KeyButton.setKeep(isAutoKeep);
        KeyButton.setHide(isHide);
        KeyButton.setSpecialOne(SpecialOne);
        KeyButton.setSpecialTwo(SpecialTwo);
        KeyButton.setKeyboardIndex(KeyIndex);
        KeyButton.setMult(isMult);
        keyboardList.add(KeyButton);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            switch (arg0.getId()) {
                default:
                    break;
                case R.id.keyboard_button_addKey:
                    configDialog.show();
                    break;
            }
        }
    };
}