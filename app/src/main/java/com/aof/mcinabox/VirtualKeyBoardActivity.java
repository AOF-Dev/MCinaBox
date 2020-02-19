package com.aof.mcinabox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.aof.mcinabox.keyboardUtils.ConfigDialog;
import com.aof.mcinabox.keyboardUtils.GameButton;

import java.util.ArrayList;

public class VirtualKeyBoardActivity extends AppCompatActivity {

    ConstraintLayout layout_keyboard;
    ArrayList<GameButton> keyboardList,tempKeyboardList;
    Button button_addKey,dialog_button_finish,dialog_button_cancel;
    Button[] launcherBts;
    EditText editText_key_name,editText_key_lx,editText_key_ly,editText_key_size;
    RadioGroup radioGroup;
    RadioButton radioButton_square,radioButton_round;
    CheckBox checkBox_isKeep,checkBox_isHide,checkBox_isMult;
    ConfigDialog configDialog;
    int keyIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_keyboard);
        configDialog = new ConfigDialog(VirtualKeyBoardActivity.this);

        radioGroup = configDialog.findViewById(R.id.dialog_key_shape);
        radioButton_round = configDialog.findViewById(R.id.shape_round);
        radioButton_square = configDialog.findViewById(R.id.shape_square);

        checkBox_isHide = configDialog.findViewById(R.id.dialog_key_hide);
        checkBox_isKeep = configDialog.findViewById(R.id.dialog_key_keep);
        checkBox_isMult = configDialog.findViewById(R.id.dialog_key_mult);

        editText_key_name = configDialog.findViewById(R.id.dialog_key_name);
        editText_key_lx = configDialog.findViewById(R.id.dialog_key_lx);
        editText_key_ly = configDialog.findViewById(R.id.dialog_key_ly);
        editText_key_size = configDialog.findViewById(R.id.dialog_key_size);

        dialog_button_cancel = configDialog.findViewById(R.id.dialog_button_cancel);
        dialog_button_finish = configDialog.findViewById(R.id.dialog_button_finish);
        button_addKey = findViewById(R.id.keyboard_button_addKey);
        launcherBts = new Button[]{button_addKey,dialog_button_finish,dialog_button_cancel};
        for (Button button : launcherBts) {
            button.setOnClickListener(listener);
        }


        layout_keyboard = findViewById(R.id.layout_keyboard);
        keyboardList = new ArrayList<GameButton>();
        tempKeyboardList = new ArrayList<GameButton>();
        //addStandKey("Test", getPxFromDp(this,50), 225, 50, 30, 1, 0, 0, false, false, false);

    }

    //键名称 键大小 透明度 X轴位置 Y轴位置 键值 特殊键1 特殊键2 是否保持 是否隐藏 是否是组合键 形状 唯一id号
    public void addStandKey(String KeyName, int KeySize, int KeyAlpha, float KeyLX, float KeyLY, int KeyMain, int SpecialOne, int SpecialTwo, boolean isAutoKeep, boolean isHide, boolean isMult,int shape,int KeyIndex) {
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
        KeyButton.setKeyMain(KeyMain);
        KeyButton.setMult(isMult);
        KeyButton.setClickable(true);
        KeyButton.setId(KeyIndex);

        //KeyButton.setBackgroundColor(Color.parseColor("#A6A4A2"));
        if(shape == R.id.shape_square){
            KeyButton.setBackground(this.getDrawable(R.drawable.control_button_square));
        }else if(shape == R.id.shape_round){
            KeyButton.setBackground(this.getDrawable(R.drawable.control_button_round));
        }

        //先执行清除操作，再添加按键，再执行显示操作 才算做一次刷新！！
        removeKeyboard();
        keyboardList.add(KeyButton);
        reflashKeyboard();
    }

    private void configStandKey(){
        String KeyName = editText_key_name.getText().toString();
        int KeySize = Integer.parseInt(editText_key_size.getText().toString());
        int KeyLX = Integer.parseInt(editText_key_lx.getText().toString());
        int KeyLY = Integer.parseInt(editText_key_ly.getText().toString());
        boolean isAutoKeep = checkBox_isKeep.isChecked();
        boolean isHide = checkBox_isHide.isChecked();
        boolean isMult = checkBox_isMult.isChecked();
        int shape = radioGroup.getCheckedRadioButtonId();
        int KeyIndex = keyIndex;
        keyIndex++;
        if(KeyName != null && KeySize != 0){
            Toast.makeText(this, "添加成功", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "请完成基本设置", Toast.LENGTH_LONG).show();
            return;
        }
        addStandKey(KeyName,getPxFromDp(this,KeySize),225,KeyLX,KeyLY,0,0,0,isAutoKeep,isHide,isMult,shape,KeyIndex);

    }

    private android.view.View.OnClickListener listener = new android.view.View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            switch (arg0.getId()) {
                default:
                    break;
                case R.id.dialog_button_finish:
                    configStandKey();
                    break;
                case R.id.dialog_button_cancel:
                    configDialog.dismiss();
                    break;
                case R.id.keyboard_button_addKey:
                    configDialog.show();
                    break;
            }
        }
    };

    private android.view.View.OnClickListener keyboardListener = new android.view.View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            for (GameButton targetButton : keyboardList) {
                if(arg0.getId() == targetButton.getId()){
                    Toast.makeText(getApplicationContext(), "你点击的是 "+targetButton.getText().toString()+" "+targetButton.getId(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    public static int getPxFromDp(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public void reflashKeyboard(){
        for (GameButton targetButton : keyboardList) {
            layout_keyboard.addView(targetButton);
            targetButton.setOnClickListener(keyboardListener);
        }
    }
    public void removeKeyboard(){
        for(GameButton targetButton : keyboardList){
            if(targetButton != null){
                layout_keyboard.removeView(targetButton);
            }
        }
    }
}