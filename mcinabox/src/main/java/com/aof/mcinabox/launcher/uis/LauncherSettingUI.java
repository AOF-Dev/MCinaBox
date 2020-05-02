package com.aof.mcinabox.launcher.uis;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aof.mcinabox.FileChooser;
import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.JsonUtils;
import com.aof.mcinabox.launcher.dialogs.DownloaderDialog;
import com.aof.mcinabox.launcher.json.SettingJson;
import com.aof.mcinabox.minecraft.ForgeInstaller;

import java.io.File;
import java.util.ArrayList;

import static com.aof.sharedmodule.Data.DataPathManifest.FORGEINSTALLER_HOME;
import static com.aof.sharedmodule.Data.DataPathManifest.MCINABOX_FILE_JSON;

public class LauncherSettingUI extends StandUI {

    public LauncherSettingUI(Activity context) {
        super(context);
        initUI();
    }

    public LauncherSettingUI(Activity context, SettingJson setting) {
        this(context);
        refreshUI(setting);
    }

    private LinearLayout layout_setting;
    private Spinner listDownloaderSources;
    private Spinner listForgeInstallers;
    private Button buttonImportRuntime;
    private Button buttonInstallForge;

    private View[] views;

    @Override
    public void initUI() {
        layout_setting = mContext.findViewById(R.id.layout_launchersetting);
        listDownloaderSources = layout_setting.findViewById(R.id.setting_spinner_downloadtype);
        buttonImportRuntime = layout_setting.findViewById(R.id.launchersetting_button_import);
        listForgeInstallers = layout_setting.findViewById(R.id.launchersetting_spinner_forgeinstaller);
        buttonInstallForge = layout_setting.findViewById(R.id.launchersetting_button_forgeinstaller);

        views = new View[]{buttonInstallForge, buttonImportRuntime};
        for (View v : views) {
            v.setOnClickListener(clickListener);
        }

    }

    @Override
    public void refreshUI(SettingJson setting) {
        refreshForgeInstallerList();
        setConfigureToDownloadtype(setting.getDownloadType(), listDownloaderSources);
    }

    @Override
    public SettingJson saveUIConfig(SettingJson setting) {
        setting.setDownloadType(listDownloaderSources.getSelectedItem().toString());
        return setting;
    }

    @Override
    public void setUIVisiability(int visiability) {
        layout_setting.setVisibility(visiability);
    }

    @Override
    public int getUIVisiability() {
        return layout_setting.getVisibility();
    }

    /**
     * 【刷新forgeinstaller列表】
     * Refresh the ForgeInstaller list.
     **/
    private ArrayList<String> forgeInstallerList = new ArrayList<String>();

    private void refreshForgeInstallerList() {
        ArrayList<String> packlist = new ArrayList<String>();
        File file = new File(FORGEINSTALLER_HOME + "/");
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, this.forgeInstallerList);
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
     * Install Forge via ForgeInstaller
     * This function is very primary....
     * Will be kicked later...
     **/
    private void installForgeFromInstaller() {
        MainActivity context = (MainActivity) mContext;
        DownloaderDialog downloaderDialog = context.dialogDownloader;
        if (JsonUtils.getSettingFromFile(MCINABOX_FILE_JSON).getDownloadType().equals("official")) {
            Toast.makeText(mContext, R.string.toast_change_downloadtype, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, mContext.getString(R.string.tips_unzip_failed), Toast.LENGTH_SHORT).show();
                return;
            }
            String id = installer.makeForgeData();
            downloaderDialog.startDownloadForge(id);
        }
    }

    /**
     * 【匹配下载源】
     * Select the Download Souce.
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
                    ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2048);
                }
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "Please allow read storage permission to import runtime packs externally.", Toast.LENGTH_LONG).show();
                    return;
                }
                FileChooser fc = new FileChooser(mContext);
                fc.setExtension(".tar.xz");
                fc.setFileListener(new FileChooser.FileSelectedListener() {
                    @Override
                    public void fileSelected(File file) {
                        installRuntimeFromPath(file.getPath());
                    }
                });
                fc.showDialog();
            }
            if (v == buttonInstallForge) {
                installForgeFromInstaller();
            }
        }
    };

    /**
     * 【从路径安装运行库】
     * Install Runtime from path.
     **/
    public void installRuntimeFromPath(String globalPath) {
        //check the permissions first, we want to ensure that app have it. weird things can happen i we have denied.
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2048);
        }
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "Please allow read storage permission to import runtime packs externally.", Toast.LENGTH_LONG).show();
            return;
        }

        final String mpackagePath = globalPath;
        new Thread() {
            @Override
            public void run() {
                Handler handler = ((MainActivity) mContext).handler;
                File packageFile = new File(mpackagePath);
                if (!packageFile.exists()) {

                    Message msg_1 = new Message();
                    msg_1.what = 4;
                    handler.sendMessage(msg_1);
                    return;

                } else {
                    if (packageFile.isDirectory()) {
                        Toast.makeText(mContext, "Runtime packs should not be directories!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                Message msg_2 = new Message();
                Message msg_3 = new Message();
                msg_2.what = 5;
                handler.sendMessage(msg_2);
                cosine.boat.Utils.extractTarXZ(mpackagePath, mContext.getDir("runtime", 0));
                if (cosine.boat.Utils.setExecutable(mContext.getDir("runtime", 0))) {
                    msg_3.what = 6;
                    handler.sendMessage(msg_3);
                } else {
                    msg_3.what = 7;
                    handler.sendMessage(msg_3);
                }
            }
        }.start();
    }

}
