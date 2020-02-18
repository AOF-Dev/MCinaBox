package com.aof.mcinabox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;

import com.aof.mcinabox.keyboardUtils.GameButton;

import java.util.ArrayList;

public class VirtualKeyBoardActivity extends AppCompatActivity {

    ConstraintLayout layout_keyboard;
    ArrayList<GameButton> keyboardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_keyboard);


        layout_keyboard = findViewById(R.id.layout_keyboard);
        keyboardList = new ArrayList<GameButton>();
        addStandKey("Test",ViewGroup.LayoutParams.WRAP_CONTENT,100,50,30,1,0,0,false,false,false);
        for(GameButton targetButton: keyboardList){
            layout_keyboard.addView(targetButton);
        }

    }

    //键名称 键大小 透明度 X轴位置 Y轴位置 键值 特殊键1 特殊键2 是否保持 是否隐藏 是否是组合键
    private void addStandKey(String KeyName,int KeySize,int KeyAlpha,float KeyLX,float KeyLY,int KeyIndex,int SpecialOne,int SpecialTwo,boolean isAutoKeep,boolean isHide,boolean isMult){
        GameButton KeyButton = new GameButton(getApplicationContext());
        KeyButton.setText(KeyName);
        KeyButton.setLayoutParams(new ViewGroup.LayoutParams(KeySize,KeySize));
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

}
