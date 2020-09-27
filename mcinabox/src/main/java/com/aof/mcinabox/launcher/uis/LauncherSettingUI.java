package com.aof.mcinabox.launcher.uis;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.aof.mcinabox.FileChooser;
import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.definitions.manifest.AppManifest;
import com.aof.mcinabox.launcher.dialogs.ContributorsDialog;
import com.aof.mcinabox.launcher.runtime.RuntimeManager;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.launcher.uis.support.Utils;
import com.aof.mcinabox.minecraft.forge.ForgeInstaller;
import com.aof.utils.dialog.DialogUtils;

import java.io.File;
import java.util.ArrayList;

public class LauncherSettingUI extends BaseUI implements Spinner.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    public LauncherSettingUI(Context context) {
        super(context);
    }

    private LinearLayout layout_setting;
    private Spinner listDownloaderSources;
    private Spinner listForgeInstallers;
    private Button buttonImportRuntime;
    private Button buttonInstallForge;
    private Button buttonShowControbutors;
    private SwitchCompat switchAutoBackground;
    private SwitchCompat switchFullscreen;
    private Animation showAnim;
    private SettingJson setting;

    @Override
    public void onCreate() {
        super.onCreate();
        setting = MainActivity.Setting;
        showAnim = AnimationUtils.loadAnimation(mContext, R.anim.layout_show);
        layout_setting = MainActivity.CURRENT_ACTIVITY.findViewById(R.id.layout_launchersetting);
        listDownloaderSources = layout_setting.findViewById(R.id.setting_spinner_downloadtype);
        buttonImportRuntime = layout_setting.findViewById(R.id.launchersetting_button_import);
        listForgeInstallers = layout_setting.findViewById(R.id.launchersetting_spinner_forgeinstaller);
        buttonInstallForge = layout_setting.findViewById(R.id.launchersetting_button_forgeinstaller);
        buttonShowControbutors = layout_setting.findViewById(R.id.setting_show_contributors);
        switchAutoBackground = layout_setting.findViewById(R.id.launchersetting_switch_auto_background);
        switchFullscreen = layout_setting.findViewById(R.id.launchersetting_switch_fullscreen);

        switchAutoBackground.setChecked(setting.isBackgroundAutoSwitch());
        switchFullscreen.setChecked(setting.isFullscreen());

        //设定监听器
        for (View v : new View[]{buttonInstallForge, buttonImportRuntime, buttonShowControbutors}) {
            v.setOnClickListener(clickListener);
        }
        for(SwitchCompat sc : new SwitchCompat[]{switchAutoBackground,switchFullscreen}){
            sc.setOnCheckedChangeListener(this);
        }
        listDownloaderSources.setOnItemSelectedListener(this);

        //设定属性
        refreshForgeInstallerList();
        setConfigureToDownloadtype(setting.getDownloadType(), listDownloaderSources);

        //调用主题管理器设定主题
        if(setting.isBackgroundAutoSwitch()){
            if(!MainActivity.CURRENT_ACTIVITY.mThemeManager.autoSetBackground(MainActivity.CURRENT_ACTIVITY.findViewById(R.id.layout_main))){
                DialogUtils.createSingleChoiceDialog(mContext,mContext.getString(R.string.title_error), mContext.getString(R.string.tips_failed_to_change_backfround_pic_is_broken),mContext.getString(R.string.title_ok),null);
            }
        }

        if(setting.isFullscreen()){
            MainActivity.CURRENT_ACTIVITY.mThemeManager.setFullScreen(MainActivity.CURRENT_ACTIVITY);
        }

    }

    @Override
    public void refreshUI() {
        refreshForgeInstallerList();
    }

    @Override
    public void saveUIConfig() {
        setting.setDownloadType(listDownloaderSources.getSelectedItem().toString());
    }

    @Override
    public void setUIVisiability(int visiability) {
        if (visiability == View.VISIBLE) {
            layout_setting.startAnimation(showAnim);
        }
        layout_setting.setVisibility(visiability);
    }

    @Override
    public int getUIVisiability() {
        return layout_setting.getVisibility();
    }

    /**
     * 【刷新forgeinstaller列表】
     **/
    private ArrayList<String> forgeInstallerList = new ArrayList<>();

    private void refreshForgeInstallerList() {
        ArrayList<String> packlist = new ArrayList<>();
        File file = new File(AppManifest.FORGE_HOME + "/");
        File[] files = file.listFiles();
        if (files == null) {
            //nothing.
            if (listForgeInstallers.getAdapter() != null) {
                forgeInstallerList.clear();
                ((BaseAdapter) listForgeInstallers.getAdapter()).notifyDataSetChanged();
            }
        } else {
            for (File targetFile : files) {
                packlist.add(targetFile.getName());
            }
            if (listForgeInstallers.getAdapter() == null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, this.forgeInstallerList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listForgeInstallers.setAdapter(adapter);
            } else {
                this.forgeInstallerList.clear();
                this.forgeInstallerList.addAll(packlist);
                ((BaseAdapter) listForgeInstallers.getAdapter()).notifyDataSetChanged();
            }
        }
    }

    /**
     * 【安装ForgeInstaller】
     **/
    private void installForgeFromInstaller() {
        if (MainActivity.Setting.getDownloadType().equals(SettingJson.DOWNLOAD_SOURCE_OFFICIAL)) {
            DialogUtils.createSingleChoiceDialog(mContext,mContext.getString(R.string.title_error),mContext.getString(R.string.tips_please_change_downloadtype),mContext.getString(R.string.title_ok),null);
        } else {
            String filename;
            if (listForgeInstallers.getSelectedItem() != null) {
                filename = listForgeInstallers.getSelectedItem().toString();
            } else {
                return;
            }
            ForgeInstaller installer = new ForgeInstaller(mContext);
            try {
                installer.unzipForgeInstaller(filename);
            } catch (Exception e) {
                e.printStackTrace();
                DialogUtils.createSingleChoiceDialog(mContext,mContext.getString(R.string.title_error),mContext.getString(R.string.tips_unzip_failed),mContext.getString(R.string.title_ok),null);
                return;
            }

            try {
                installer.startDownloadForge(installer.makeForgeData());
            }catch (Exception e){
                e.printStackTrace();
                DialogUtils.createSingleChoiceDialog(mContext,mContext.getString(R.string.title_error),mContext.getString(R.string.tips_failed_to_install_forge),mContext.getString(R.string.title_ok),null);
            }
        }
    }

    /**
     * 【匹配下载源】
     **/
    private void setConfigureToDownloadtype(String type, Spinner list) {
        int pos = Utils.getItemPosByString(type, list);
        if (pos != -1) {
            list.setSelection(pos);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == buttonImportRuntime) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.CURRENT_ACTIVITY, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2048);
                }
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "Please allow read storage permission to import runtime packs externally.", Toast.LENGTH_LONG).show();
                    return;
                }
                FileChooser fc = new FileChooser(MainActivity.CURRENT_ACTIVITY).setExtension(".tar.xz").setFileListener(new FileChooser.FileSelectedListener() {
                    @Override
                    public void fileSelected(File file) {
                        RuntimeManager.installRuntimeFromPath(mContext, file.getPath());
                    }
                });
                fc.showDialog();
            }
            if (v == buttonInstallForge) {
                installForgeFromInstaller();
            }
            if (v == buttonShowControbutors) {
                new ContributorsDialog(mContext).show();
            }
        }
    };


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == listDownloaderSources) {
            setting.setDownloadType((String) listDownloaderSources.getItemAtPosition(position));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == switchAutoBackground){
            if(isChecked){
                DialogUtils.createSingleChoiceDialog(mContext,mContext.getString(R.string.title_note),String.format(mContext.getString(R.string.tips_please_put_pic_to_background_dir),AppManifest.MCINABOX_BACKGROUND),mContext.getString(R.string.title_ok),null);
            }
            setting.setBackgroundAutoSwitch(isChecked);
        }

        if(buttonView == switchFullscreen){
            if(isChecked){
                MainActivity.CURRENT_ACTIVITY.mThemeManager.setFullScreen(MainActivity.CURRENT_ACTIVITY);
            }else{
                DialogUtils.createSingleChoiceDialog(mContext,mContext.getString(R.string.title_note),mContext.getString(R.string.tips_successed_to_disable_hide_stat_bar),mContext.getString(R.string.title_ok),null);
            }
            setting.setFullscreen(isChecked);
        }
    }
}
