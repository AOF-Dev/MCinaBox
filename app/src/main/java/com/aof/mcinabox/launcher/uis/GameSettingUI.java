package com.aof.mcinabox.launcher.uis;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.aof.mcinabox.R;
import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.utils.MemoryUtils;

import java.io.File;
import java.util.ArrayList;

import static com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest.MCINABOX_KEYBOARD;

public class GameSettingUI extends BaseUI implements SwitchCompat.OnCheckedChangeListener {

    public GameSettingUI(Context context) {
        super(context);
    }

    private LinearLayout layout_gamesetting;
    private TextView textPhysicalMem;
    private EditText editMaxMem;
    private EditText editJavaExtArgs;
    private EditText editMCExtArgs;
    private SwitchCompat switchDisJVMCheck;
    private SwitchCompat switchDisMCCheck;
    private SwitchCompat switchDisTipperCheck;
    private SwitchCompat switchEnableDebug;
    private SwitchCompat switchAlwaysChoiceManifest;
    private SwitchCompat switchDisForgeCheck;
    private SwitchCompat switchAutoMenmory;
    private SwitchCompat switchDisOptCheck;
    private Spinner listKeyboards;
    private SettingJson setting;

    private final static String TAG = "GameSettingUI";


    /**
     * 【刷新键盘模板列表】
     * Refresh the Keyboard layout list.
     **/
    private final ArrayList<String> KeyboardList = new ArrayList<>();

    @Override
    public void refreshUI() {
        refreshLocalKeyboardList();
    }

    @Override
    public void saveUIConfig() {

        SettingJson.Configurations configurations = setting.getConfigurations();

        if (editMaxMem.getText().toString().equals("")) {
            configurations.setMaxMemory(0);
        } else {
            configurations.setMaxMemory(Integer.parseInt(editMaxMem.getText().toString()));
        }
        configurations.setJavaArgs(editJavaExtArgs.getText().toString());
        configurations.setMinecraftArgs(editMCExtArgs.getText().toString());

    }

    @Override
    public void setUIVisibility(int visibility) {
        layout_gamesetting.setVisibility(visibility);
    }

    @Override
    public int getUIVisibility() {
        return layout_gamesetting.getVisibility();
    }

    /**
     * 【刷新物理内存大小】
     * Refresh the Physical Memory Size.
     **/
    private void refreshAvailableMemories() {
        textPhysicalMem.setText(MemoryUtils.getTotalMemory(mContext));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setting = OldMainActivity.Setting;
        layout_gamesetting = OldMainActivity.CURRENT_ACTIVITY.get().findViewById(R.id.layout_gamelist_setting);
        textPhysicalMem = layout_gamesetting.findViewById(R.id.game_setting_text_memory);
        editMaxMem = layout_gamesetting.findViewById(R.id.setting_edit_maxmemory);
        editJavaExtArgs = layout_gamesetting.findViewById(R.id.setting_edit_javaargs);
        editMCExtArgs = layout_gamesetting.findViewById(R.id.setting_edit_minecraftargs);
        switchDisJVMCheck = layout_gamesetting.findViewById(R.id.setting_switch_notcheckjvm);
        switchDisMCCheck = layout_gamesetting.findViewById(R.id.setting_switch_notcheckminecraft);
        switchDisTipperCheck = layout_gamesetting.findViewById(R.id.setting_switch_notchecktipper);
        switchEnableDebug = layout_gamesetting.findViewById(R.id.setting_switch_debug);
        switchAlwaysChoiceManifest = layout_gamesetting.findViewById(R.id.setting_switch_always_choice_runtime_manifest);
        switchDisForgeCheck = layout_gamesetting.findViewById(R.id.setting_switch_notcheckforge);
        switchAutoMenmory = layout_gamesetting.findViewById(R.id.setting_swith_auto_memory);
        switchDisOptCheck = layout_gamesetting.findViewById(R.id.setting_switch_notcheckoptions);
        listKeyboards = layout_gamesetting.findViewById(R.id.setting_spinner_keyboard);

        //设定属性
        refreshLocalKeyboardList();
        refreshAvailableMemories();
        editJavaExtArgs.setText(setting.getConfigurations().getJavaArgs());
        editMaxMem.setText(String.valueOf(setting.getConfigurations().getMaxMemory()));
        editMCExtArgs.setText(setting.getConfigurations().getMinecraftArgs());
        switchDisJVMCheck.setChecked(setting.getConfigurations().isNotCheckPlatform());
        switchDisMCCheck.setChecked(setting.getConfigurations().isNotCheckGame());
        switchDisTipperCheck.setChecked(setting.getConfigurations().isNotCheckTipper());
        switchEnableDebug.setChecked(setting.getConfigurations().isEnableDebug());
        switchAlwaysChoiceManifest.setChecked(setting.getConfigurations().isAlwaysChoiceRuntimeManifest());
        switchDisForgeCheck.setChecked(setting.getConfigurations().isNotCheckForge());
        switchDisOptCheck.setChecked(setting.getConfigurations().isNotCheckOptions());

        //设定监听
        for (SwitchCompat s : new SwitchCompat[]{switchDisMCCheck, switchDisJVMCheck, switchDisTipperCheck, switchEnableDebug, switchAlwaysChoiceManifest, switchDisForgeCheck, switchAutoMenmory, switchDisOptCheck}) {
            s.setOnCheckedChangeListener(this);
        }

        switchAutoMenmory.setChecked(setting.getConfigurations().isEnableAutoMemory());

    }

    private void refreshLocalKeyboardList() {
        ArrayList<String> keyboardList = new ArrayList<>();
        File file = new File(MCINABOX_KEYBOARD + "/");
        File[] files = file.listFiles();
        if (files == null) {
            KeyboardList.clear();
        } else {
            for (File targetFile : files) {
                keyboardList.add(targetFile.getName());
            }
            if (listKeyboards.getAdapter() == null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, KeyboardList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listKeyboards.setAdapter(adapter);
            } else {
                KeyboardList.clear();
                KeyboardList.addAll(keyboardList);
                ((BaseAdapter) listKeyboards.getAdapter()).notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == switchDisJVMCheck) {
            setting.getConfigurations().setNotCheckPlatform(isChecked);
        }
        if (buttonView == switchDisMCCheck) {
            setting.getConfigurations().setNotCheckGame(isChecked);
        }
        if (buttonView == switchDisTipperCheck) {
            setting.getConfigurations().setNotCheckTipper(isChecked);
        }
        if (buttonView == switchEnableDebug) {
            setting.getConfigurations().setDebug(isChecked);
        }
        if (buttonView == switchAlwaysChoiceManifest) {
            setting.getConfigurations().setAlwaysChoiceRuntimeMainfest(isChecked);
        }
        if (buttonView == switchDisForgeCheck) {
            setting.getConfigurations().setNotCheckForge(isChecked);
        }
        if (buttonView == switchAutoMenmory) {
            setting.getConfigurations().setAutoMemory(isChecked);
            if (isChecked) {
                editMaxMem.setText(String.valueOf(MemoryUtils.getDynamicHeapSize(mContext) * 2 - 50));
                editMaxMem.setEnabled(false);
            } else {
                editMaxMem.setEnabled(true);
            }
        }
        if (buttonView == switchDisOptCheck) {
            setting.getConfigurations().setNotCheckOptions(isChecked);
        }
    }
}
