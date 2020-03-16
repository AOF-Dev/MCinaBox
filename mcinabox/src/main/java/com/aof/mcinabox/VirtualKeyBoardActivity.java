package com.aof.mcinabox;
//yi 12 si 23
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.aof.mcinabox.colorUtils.ColorUtils;
import com.google.gson.Gson;
import com.aof.mcinabox.keyboardUtils.*;
import com.shixia.colorpickerview.ColorPickerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.aof.mcinabox.DataPathManifest.*;

public class VirtualKeyBoardActivity extends AppCompatActivity {

    ConstraintLayout layout_keyboard;
    ArrayList<GameButton> keyboardList,tempKeyboardList;
    Button toolbar_button_backhome,button_addKey,button_newModel,button_saveModel,button_loadModel,dialog_button_finish,dialog_button_cancel,dialog_button_load,dialog_button_cancelload,dialog_button_save,dialog_button_cancelsave,dialog_button_cancel_colorpicker,dialog_button_confirm_colorpicker;
    Button[] launcherBts;
    ImageButton dialog_button_colorpicker;
    EditText editText_key_name,editText_key_lx,editText_key_ly,editText_key_sizeW,editText_key_sizeH,editText_model_name,editText_model_color;
    CheckBox checkBox_isKeep,checkBox_isHide,checkBox_isMult;
    ConfigDialog configDialog,loadDialog,saveDialog,colorPickDialog;
    ColorPickerView colorPicker;
    SeekBar seekBar_corner;
    TextView text_corner_progress;
    Spinner key_main_selected,key_special_oneselected,key_special_twoselected,model_selected;
    int selectedModelPos;
    ArrayList<String> modelNameList;
    Toolbar toolbar;
    String KeyboardDirPath;
    int ColorPickerTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //再设置布局之前，先设置Activity必须全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_virtual_keyboard);

        KeyboardDirPath = MCINABOX_KEYBOARD;

        configDialog = new ConfigDialog(VirtualKeyBoardActivity.this,R.layout.dialog_configkey,true);
        loadDialog = new ConfigDialog(VirtualKeyBoardActivity.this,R.layout.dialog_loadmodel,true);
        saveDialog = new ConfigDialog(VirtualKeyBoardActivity.this,R.layout.dialog_savemodel,true);
        colorPickDialog = new ConfigDialog(VirtualKeyBoardActivity.this,R.layout.dialog_colorpicker,false);


        colorPicker = colorPickDialog.findViewById(R.id.cpv_color);
        dialog_button_cancel_colorpicker = colorPickDialog.findViewById(R.id.dialog_button_cancle_colorpicker);
        dialog_button_confirm_colorpicker = colorPickDialog.findViewById(R.id.dialog_button_confirm_colorpicker);
        dialog_button_colorpicker = configDialog.findViewById(R.id.dialog_button_colorpicker);

        checkBox_isHide = configDialog.findViewById(R.id.dialog_key_hide);
        checkBox_isKeep = configDialog.findViewById(R.id.dialog_key_keep);
        checkBox_isMult = configDialog.findViewById(R.id.dialog_key_mult);

        editText_key_name = configDialog.findViewById(R.id.dialog_key_name);
        editText_key_lx = configDialog.findViewById(R.id.dialog_key_lx);
        editText_key_ly = configDialog.findViewById(R.id.dialog_key_ly);
        editText_key_sizeW = configDialog.findViewById(R.id.dialog_key_sizeW);
        editText_key_sizeH = configDialog.findViewById(R.id.dialog_key_sizeH);
        editText_model_name = saveDialog.findViewById(R.id.dialog_edittext_modelname);
        editText_model_color = configDialog.findViewById(R.id.dialog_color);

        seekBar_corner = configDialog.findViewById(R.id.dialog_corner);
        text_corner_progress = configDialog.findViewById(R.id.dialog_text_cornerprogress);

        key_main_selected = configDialog.findViewById(R.id.dialog_key_main);
        key_special_oneselected = configDialog.findViewById(R.id.dialog_key_specialone);
        key_special_twoselected = configDialog.findViewById(R.id.dialog_key_specialtwo);
        model_selected = loadDialog.findViewById(R.id.dialog_spinner_modelselected);



        dialog_button_cancel = configDialog.findViewById(R.id.dialog_button_cancel);
        dialog_button_finish = configDialog.findViewById(R.id.dialog_button_finish);
        dialog_button_load = loadDialog.findViewById(R.id.dialog_button_load);
        dialog_button_cancelload = loadDialog.findViewById(R.id.dialog_button_cancelload);
        dialog_button_save = saveDialog.findViewById(R.id.dialog_button_save);
        dialog_button_cancelsave = saveDialog.findViewById(R.id.dialog_button_cancelsave);
        button_addKey = findViewById(R.id.keyboard_button_addKey);
        button_newModel = findViewById(R.id.keyboard_button_newModel);
        button_saveModel = findViewById(R.id.keyboard_button_saveModel);
        button_loadModel = findViewById(R.id.keyboard_button_loadModel);
        toolbar_button_backhome = findViewById(R.id.toolbar2_button_backhome);
        launcherBts = new Button[]{button_addKey,button_newModel,button_saveModel,button_loadModel,dialog_button_finish,dialog_button_cancel,dialog_button_load,dialog_button_cancelload,dialog_button_save,dialog_button_cancelsave,toolbar_button_backhome,dialog_button_cancel_colorpicker,dialog_button_confirm_colorpicker};
        for (Button button : launcherBts) {
            button.setOnClickListener(listener);
        }

        toolbar = findViewById(R.id.keyboard_toolbar);


        layout_keyboard = findViewById(R.id.layout_keyboard);
        layout_keyboard.setOnClickListener(listener);
        keyboardList = new ArrayList<GameButton>();
        tempKeyboardList = new ArrayList<GameButton>();

        //执行完初始化后的
        colorPicker.setOnColorChangeListener(colorChangedListener);
        dialog_button_colorpicker.setOnClickListener(listener);

        //SeekBar 圆角大小
        seekBar_corner.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                text_corner_progress.setText(progress+"");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    //键名称 键宽 键长 透明度 X轴位置 Y轴位置 主按键 特殊键1 特殊键2 是否保持 是否隐藏 是否是组合键 形状 主按键位置 组合键一位置 组合键二位置 按键颜色 圆角半径
    public void addStandKey(String KeyName, int KeySizeW, int KeySizeH , int KeyLX, int KeyLY, String KeyMain, String SpecialOne, String SpecialTwo, boolean isAutoKeep, boolean isHide, boolean isMult,int MainPos,int SpecialOnePos,int SpecialTwoPos,String colorhex,int conerRadius) {
        GameButton KeyButton = new GameButton(getApplicationContext());
        KeyButton.setText(KeyName);
        KeyButton.setLayoutParams(new ViewGroup.LayoutParams(getPxFromDp(this,KeySizeW),getPxFromDp(this,KeySizeH) ));
        KeyButton.setX(getPxFromDp(this,KeyLX));
        KeyButton.setY(getPxFromDp(this,KeyLY));
        KeyButton.setKeyLX_dp(KeyLX);
        KeyButton.setKeyLY_dp(KeyLY);
        KeyButton.setKeySizeW(KeySizeW);
        KeyButton.setKeySizeH(KeySizeH);
        KeyButton.setKeep(isAutoKeep);
        KeyButton.setHide(isHide);
        KeyButton.setSpecialOne(SpecialOne);
        KeyButton.setSpecialTwo(SpecialTwo);
        KeyButton.setKeyMain(KeyMain);
        KeyButton.setMult(isMult);
        KeyButton.setClickable(true);
        KeyButton.setId(KeyButton.hashCode());
        KeyButton.setGravity(Gravity.CENTER);
        KeyButton.setMainPos(MainPos);
        KeyButton.setSpecialOnePos(SpecialOnePos);
        KeyButton.setSpecialTwoPos(SpecialTwoPos);
        KeyButton.setCornerRadius(conerRadius);
        KeyButton.setColorHex(colorhex);

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(ColorUtils.hex2Int(colorhex));
        gradientDrawable.setCornerRadius(conerRadius);
        KeyButton.setBackground(gradientDrawable);
        //先执行清除操作，再添加按键，再执行显示操作 才算做一次刷新！！
        removeKeyboard();
        keyboardList.add(KeyButton);
        reflashKeyboard();
    }

    private void reloadStantKey(GameButton targetButton){
        //给各个控件重载按键的属性
        editText_key_name.setText(targetButton.getText().toString());
        editText_key_sizeW.setText(""+targetButton.getKeySizeW());
        editText_key_sizeH.setText(""+targetButton.getKeySizeH());
        editText_key_lx.setText(""+targetButton.getKeyLX_dp());
        editText_key_ly.setText(""+targetButton.getKeyLY_dp());
        editText_model_color.setText(targetButton.getColorHex());

        ColorDrawable drawable = new ColorDrawable();
        drawable.setColor(ColorUtils.hex2Int(targetButton.getColorHex()));
        dialog_button_colorpicker.setImageDrawable(drawable);

        checkBox_isKeep.setChecked(targetButton.isKeep());
        checkBox_isHide.setChecked(targetButton.isHide());
        checkBox_isMult.setChecked(targetButton.isMult());

        seekBar_corner.setProgress((int)targetButton.getCornerRadius());
        key_main_selected.setSelection(targetButton.getMainPos());
        key_special_oneselected.setSelection(targetButton.getSpecialOnePos());
        key_special_twoselected.setSelection(targetButton.getSpecialTwoPos());
        //显示dialog对话框
        configDialog.show();
        //刷新一次界面
        removeKeyboard();
        keyboardList.remove(targetButton);
        reflashKeyboard();
    }

    private void configStandKey(){
        int KeyLX = 100;
        int KeyLY = 100;
        int KeySizeW = 40;
        int KeySizeH = 40;
        String KeyName = editText_key_name.getText().toString();
        String KeyColor = editText_model_color.getText().toString();

        try {
            KeySizeW = Integer.parseInt(editText_key_sizeW.getText().toString());
            KeySizeH = Integer.parseInt(editText_key_sizeH.getText().toString());
            KeyLX = Integer.parseInt(editText_key_lx.getText().toString());
            KeyLY = Integer.parseInt(editText_key_ly.getText().toString());
        }catch (NullPointerException e){
            e.printStackTrace();
            Toast.makeText(this, "坐标或按键值不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isAutoKeep = checkBox_isKeep.isChecked();
        boolean isHide = checkBox_isHide.isChecked();
        boolean isMult = checkBox_isMult.isChecked();

        int cornerRadius = seekBar_corner.getProgress();
        String KeyMain = (String)key_main_selected.getSelectedItem();
        String SpecialOne = (String)key_special_oneselected.getSelectedItem();
        String SpecialTwo = (String)key_special_twoselected.getSelectedItem();
        int MainPos = key_main_selected.getSelectedItemPosition();
        int SpecialOnePos = key_special_oneselected.getSelectedItemPosition();
        int SpecialTwoPos = key_special_twoselected.getSelectedItemPosition();
        String colorhex = editText_model_color.getText().toString();

        if(KeyName.equals("")){
            Toast.makeText(this, "按键名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }else if(KeySizeW < 20 || KeySizeH < 20){
            Toast.makeText(this, "按键尺寸不能小于20", Toast.LENGTH_SHORT).show();
            return;
        }else if(KeyColor.equals("")){
            Toast.makeText(this, "颜色不能为空", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
        }

        addStandKey(KeyName,KeySizeW,KeySizeH,KeyLX,KeyLY,KeyMain,SpecialOne,SpecialTwo,isAutoKeep,isHide,isMult,MainPos,SpecialOnePos,SpecialTwoPos,colorhex,cornerRadius);

    }


    private com.shixia.colorpickerview.OnColorChangeListener colorChangedListener = new com.shixia.colorpickerview.OnColorChangeListener(){
        @Override
        public void colorChanged(int color) {
            ColorPickerTemp = color;
        }
    };

    private android.view.View.OnClickListener listener = new android.view.View.OnClickListener() {
        @Override
        public void onClick(View arg0) {

            switch (arg0.getId()) {
                default:
                    break;
                case R.id.dialog_button_finish:
                    configStandKey();
                    configDialog.dismiss();
                    break;
                case R.id.dialog_button_cancel:
                    configDialog.dismiss();
                    break;
                case R.id.keyboard_button_addKey:
                    toolbar.setVisibility(View.INVISIBLE);
                    configDialog.show();
                    break;
                case R.id.keyboard_button_newModel:
                    removeKeyboard();
                    clearKeyboard();
                    reflashKeyboard();
                    break;
                case R.id.keyboard_button_saveModel:
                    saveDialog.show();
                    break;
                case R.id.keyboard_button_loadModel:
                    loadDialog.show();
                    initLoadModelSpinner();
                    break;
                case R.id.dialog_button_load:
                    removeKeyboard();
                    clearKeyboard();
                    reflashKeyboard();
                    getKeyboardModelFromJson();
                    loadDialog.dismiss();
                    break;
                case R.id.dialog_button_cancelload:
                    loadDialog.dismiss();
                    break;
                case R.id.dialog_button_save:
                    if(!editText_model_name.getText().toString().equals("")){
                        getJsonFromKeyboardModel(editText_model_name.getText().toString());
                        saveDialog.dismiss();
                    }else{
                        Toast.makeText(getApplicationContext(), "文件名不能为空", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.dialog_button_cancelsave:
                    saveDialog.dismiss();
                    break;
                case R.id.toolbar2_button_backhome:
                    finish();
                    break;
                case R.id.layout_keyboard:
                    if(toolbar.getVisibility() == View.VISIBLE){
                        toolbar.setVisibility(View.INVISIBLE);
                    }else if(toolbar.getVisibility() == View.INVISIBLE){
                        toolbar.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.dialog_button_colorpicker:
                    colorPickDialog.show();
                    configDialog.hide();
                    break;
                case R.id.dialog_button_cancle_colorpicker:
                    colorPickDialog.hide();
                    configDialog.show();
                    break;
                case R.id.dialog_button_confirm_colorpicker:
                    configDialog.show();
                    ApplyColorChangeToGameButton();
                    colorPickDialog.hide();
                    break;
            }
        }
    };


    private android.view.View.OnClickListener keyboardListenerShort = new android.view.View.OnClickListener() {
        @Override
        public void onClick(View arg0) {

            for (GameButton targetButton : keyboardList) {
                if(arg0.getId() == targetButton.getId()){
                    Toast.makeText(getApplicationContext(), targetButton.getText().toString()+" "+targetButton.getId()+" "+targetButton.getKeyMain() + " " + targetButton.getSpecialOne() + " " +targetButton.getSpecialTwo(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private android.view.View.OnLongClickListener keyboardListenerLong = new android.view.View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View arg0) {

            for (GameButton targetButton : keyboardList) {
                if(arg0.getId() == targetButton.getId()){
                    reloadStantKey(targetButton);
                }
            }
            return true;
        }
    };

    public static int getPxFromDp(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getDpFromPx(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return ((int) ((pxValue - 0.5f)/scale))+1;
    }

    public void reflashKeyboard(){
        for (GameButton targetButton : keyboardList) {
            layout_keyboard.addView(targetButton);
            targetButton.setOnClickListener(keyboardListenerShort);
            targetButton.setOnLongClickListener(keyboardListenerLong);
        }
    }

    public void removeKeyboard(){
        for(GameButton targetButton : keyboardList){
            if(targetButton != null){
                layout_keyboard.removeView(targetButton);
            }
        }
        fixArrayError();
    }

    //由于ArrayList表在多次执行remove操作后会导致不连续而抛出异常，在这里写一个修复函数，每次执行remove操作后都将旧表copy到新表中。
    public void fixArrayError(){
        ArrayList<GameButton> tempList = new ArrayList<GameButton>(){};
        for(GameButton targetButton : keyboardList) {
            if (targetButton != null) {
                tempList.add(targetButton);
            }
        }
        keyboardList = tempList;
    }
    //彻底清空虚拟按键
    public void clearKeyboard(){
        ArrayList<GameButton> tempList = new ArrayList<GameButton>(){};
        keyboardList = tempList;
    }

    public void getJsonFromKeyboardModel(String name){
        String jsonName = name;
        Gson gson = new Gson();
        File jsonFile = new File(KeyboardDirPath+"/"+jsonName+".json");
        if(!jsonFile.exists()){
            try {
                jsonFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ArrayList<KeyboardJsonModel> modelList = new ArrayList<KeyboardJsonModel>(){};
        for(GameButton button : keyboardList){
            modelList.add(new KeyboardJsonModel(button.getText().toString(),button.getKeySizeW(),button.getKeySizeH(),button.getKeyLX_dp(),button.getKeyLY_dp(),button.getKeyMain(),button.getSpecialOne(),button.getSpecialTwo(),button.isKeep(),button.isHide(),button.isMult(),button.getMainPos(),button.getSpecialOnePos(),button.getSpecialTwoPos(),button.getColorHex(),button.getCornerRadius()));
        }

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < modelList.size(); i++) {
            String accountStr = gson.toJson(modelList.get(i));
            JSONObject keyboardModelObject;
            try {
                keyboardModelObject = new JSONObject(accountStr);
                jsonArray.put(i, keyboardModelObject);
                try {
                    FileWriter jsonWriter = new FileWriter(jsonFile);
                    BufferedWriter out = new BufferedWriter(jsonWriter);
                    out.write(jsonArray.toString());
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(this, "导出成功", Toast.LENGTH_SHORT).show();
    }

    public void getKeyboardModelFromJson(){
        InputStream inputStream;
        Gson gson = new Gson();
        File jsonFile = new File(KeyboardDirPath + "/" + modelNameList.get(selectedModelPos));
        if(!jsonFile.exists()){
            Toast.makeText(this, "找不到键盘模板", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            inputStream = new FileInputStream(jsonFile);
            Reader reader = new InputStreamReader(inputStream);
            KeyboardJsonModel[] jsonArray = new Gson().fromJson(reader, KeyboardJsonModel[].class);
            List<KeyboardJsonModel> tempList1 = Arrays.asList(jsonArray);
            ArrayList<KeyboardJsonModel> tempList2 = new ArrayList<KeyboardJsonModel>(tempList1);
            if(tempList2.size() != 0){
                Toast.makeText(this, "导入成功", Toast.LENGTH_SHORT).show();
                for(KeyboardJsonModel targetModel : tempList2){
                    //这里采用了逐个添加，对性能有一定影响，但是编程更简单。 性能影响！
                    addStandKey(targetModel.getKeyName(),targetModel.getKeySizeW(),targetModel.getKeySizeH(),targetModel.getKeyLX(),targetModel.getKeyLY(),targetModel.getKeyMain(),targetModel.getSpecialOne(),targetModel.getSpecialTwo(),targetModel.isAutoKeep(),targetModel.isHide(),targetModel.isMult(),targetModel.getMainPos(),targetModel.getSpecialOnePos(),targetModel.getSpecialTwoPos(),targetModel.getColorhex(),targetModel.getCornerRadius());
                }
            }else{
                Toast.makeText(this, "导入失败", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void initLoadModelSpinner() {

        File file = new File(KeyboardDirPath+"/");
        File[] files = file.listFiles();
        if (files == null) {
            Toast.makeText(this, "没有发现模板", Toast.LENGTH_SHORT).show();
            return;
        }
        //每次都先清空列表,再修创建列表内容
        modelNameList = new ArrayList<String>() {
        };
        for (File targetFile : files) {
            modelNameList.add(targetFile.getName());
        }

        //设置 Adapter源
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, modelNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        model_selected.setAdapter(adapter);
        model_selected.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedModelPos = pos;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    public void ApplyColorChangeToGameButton(){
        String color = ColorUtils.int2Hex3(ColorPickerTemp);
        editText_model_color.setText(color);
        ColorDrawable drawable = new ColorDrawable();
        drawable.setColor(ColorPickerTemp);
        dialog_button_colorpicker.setImageDrawable(drawable);
    }

}