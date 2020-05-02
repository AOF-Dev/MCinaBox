package com.aof.mcinabox.launcher.uis;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.json.SettingJson;
import com.aof.mcinabox.utils.MemoryUtils;

import java.io.File;
import java.util.ArrayList;

import static com.aof.sharedmodule.Data.DataPathManifest.MCINABOX_KEYBOARD;

public class GameSettingUI extends StandUI {

    public GameSettingUI(Activity context) {
        super(context);
        initUI();
    }

    public GameSettingUI(Activity context, SettingJson setting) {
        this(context);
        refreshUI(setting);
    }

    private LinearLayout layout_gamesetting;
    private TextView textPhysicalMem;
    private EditText editMaxMem;
    private EditText editJavaExtArgs;
    private EditText editMCExtArgs;
    private Switch switchDisJVMCheck;
    private Switch switchDisMCCheck;
    private Spinner listKeyboards;


    @Override
    public void initUI() {
        layout_gamesetting = mContext.findViewById(R.id.layout_gamelist_setting);
        textPhysicalMem = layout_gamesetting.findViewById(R.id.game_setting_text_memory);
        editMaxMem = layout_gamesetting.findViewById(R.id.setting_edit_maxmemory);
        editJavaExtArgs = layout_gamesetting.findViewById(R.id.setting_edit_javaargs);
        editMCExtArgs = layout_gamesetting.findViewById(R.id.setting_edit_minecraftargs);
        switchDisJVMCheck = layout_gamesetting.findViewById(R.id.setting_switch_notcheckjvm);
        switchDisMCCheck = layout_gamesetting.findViewById(R.id.setting_switch_notcheckminecraft);
        listKeyboards = layout_gamesetting.findViewById(R.id.setting_spinner_keyboard);
    }

    @Override
    public void refreshUI(SettingJson setting) {
        refreshAvailableMemories();
        refreshLocalKeyboardList();
        setConfigureToKeyboardList(setting.getKeyboard());
        editJavaExtArgs.setText(setting.getConfigurations().getJavaArgs());
        editMaxMem.setText((Integer.valueOf(setting.getConfigurations().getMaxMemory())).toString());
        switchDisJVMCheck.setChecked(setting.getConfigurations().isNotCheckJvm());
        switchDisMCCheck.setChecked(setting.getConfigurations().isNotCheckGame());
    }

    @Override
    public SettingJson saveUIConfig(SettingJson setting) {

        SettingJson.Configurations configurations = setting.getConfigurations();

        if (editMaxMem.getText().toString().equals("")) {
            configurations.setMaxMemory(0);
        } else {
            configurations.setMaxMemory(Integer.parseInt((String) editMaxMem.getText().toString()));
        }
        configurations.setJavaArgs(editJavaExtArgs.getText().toString());
        configurations.setMinecraftArgs(editMCExtArgs.getText().toString());
        configurations.setNotCheckJvm(switchDisJVMCheck.isChecked());
        configurations.setNotCheckGame(switchDisMCCheck.isChecked());

        if (listKeyboards.getSelectedItem() == null) {
            setting.setKeyboard("");
        } else {
            setting.setKeyboard(listKeyboards.getSelectedItem().toString());
        }

        setting.setConfigurations(configurations);
        return setting;
    }

    @Override
    public void setUIVisiability(int visiability) {
        layout_gamesetting.setVisibility(visiability);
    }

    @Override
    public int getUIVisiability() {
        return layout_gamesetting.getVisibility();
    }

    /**
     * 【刷新物理内存大小】
     * Refresh the Physical Memory Size.
     **/
    private void refreshAvailableMemories() {
        textPhysicalMem.setText(MemoryUtils.getTotalMemory(mContext.getApplication()));
    }

    /**
     * 【刷新键盘模板列表】
     * Refresh the Keyboard layout list.
     **/
    private ArrayList<String> KeyboardList = new ArrayList<String>();

    private void refreshLocalKeyboardList() {
        ArrayList<String> keyboardList = new ArrayList<String>();
        File file = new File(MCINABOX_KEYBOARD + "/");
        File[] files = file.listFiles();
        if (files == null) {
            KeyboardList.clear();
        } else {
            for (File targetFile : files) {
                keyboardList.add(targetFile.getName());
            }
            if (listKeyboards.getAdapter() == null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, KeyboardList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listKeyboards.setAdapter(adapter);
            } else {
                KeyboardList.clear();
                KeyboardList.addAll(keyboardList);
                ((BaseAdapter) listKeyboards.getAdapter()).notifyDataSetChanged();
            }
        }
    }

    /**
     * 【根据Keyboard参数匹配键盘布局】
     * Select the last Keyboard layout.
     **/
    private void setConfigureToKeyboardList(String itemName) {
        int pos = Utils.getItemPosByString(itemName, listKeyboards);
        if (pos != -1) {
            listKeyboards.setSelection(pos);
        }
    }

}
