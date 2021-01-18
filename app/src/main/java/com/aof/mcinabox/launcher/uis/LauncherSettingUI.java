package com.aof.mcinabox.launcher.uis;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.widget.SwitchCompat;

import com.aof.mcinabox.R;
import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest;
import com.aof.mcinabox.launcher.runtime.RuntimeManager;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.launcher.uis.support.Utils;
import com.aof.mcinabox.minecraft.forge.ForgeInstaller;
import com.aof.mcinabox.utils.FileTool;
import com.aof.mcinabox.utils.ZipUtils;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.aof.mcinabox.utils.dialog.support.DialogSupports;
import com.aof.mcinabox.utils.dialog.support.TaskDialog;

import java.util.ArrayList;
import java.util.Objects;

public class LauncherSettingUI extends BaseUI implements Spinner.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    public LauncherSettingUI(Context context) {
        super(context);
    }

    private LinearLayout layout_setting;
    private Spinner listDownloaderSources;
    private Button buttonImportRuntime;
    private Button buttonInstallForge;
    private Button buttonShowControbutors;
    private Button buttonClearRuntime;
    private SwitchCompat switchAutoBackground;
    private SwitchCompat switchFullscreen;
    private Animation showAnim;
    private SettingJson setting;

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == buttonImportRuntime) {
                ArrayList<String> tmp = FileTool.listChildFilesFromTargetDir(AppManifest.MCINABOX_RUNTIME);
                String[] files;
                final String[] tmp2;
                if (tmp.size() != 0) {
                    files = tmp.toArray(new String[0]);
                    tmp2 = new String[Objects.requireNonNull(files).length + 1];
                    System.arraycopy(files, 0, tmp2, 1, files.length);
                } else {
                    tmp2 = new String[1];
                }
                tmp2[0] = mContext.getString(R.string.tips_select_from_storage);
                DialogUtils.createItemsChoiceDialog(mContext, mContext.getString(R.string.title_import_runtime), null, mContext.getString(R.string.title_cancel), null, false, tmp2, new DialogSupports() {
                    @Override
                    public void runWhenItemsSelected(int pos) {
                        super.runWhenItemsSelected(pos);
                        if (pos == 0) {
                            DialogUtils.createFileSelectorDialog(mContext, mContext.getString(R.string.title_import_runtime), AppManifest.SDCARD_HOME, "xz", new DialogSupports() {
                                @Override
                                public void runWhenItemsSelected(Object path) {
                                    RuntimeManager.installRuntimeFromPath(mContext, (String) path);
                                }
                            });
                        } else {
                            RuntimeManager.installRuntimeFromPath(mContext, AppManifest.MCINABOX_RUNTIME + "/" + tmp2[pos]);
                        }

                    }
                });

            }
            if (v == buttonInstallForge) {
                DialogUtils.createFileSelectorDialog(mContext, mContext.getString(R.string.title_forge_installer), AppManifest.SDCARD_HOME, "jar", new DialogSupports() {
                    @Override
                    public void runWhenItemsSelected(Object filePath) {
                        super.runWhenItemsSelected(filePath);
                        final ForgeInstaller installer = new ForgeInstaller(mContext);
                        installer.unzipForgeInstaller((String) filePath, new ZipUtils.Callback() {
                            final TaskDialog mDialog = DialogUtils.createTaskDialog(mContext, mContext.getString(R.string.tips_unzipping), "", false);

                            @Override
                            public void onStart() {
                                mDialog.show();
                            }

                            @Override
                            public void onFailed(Exception e) {
                                DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(R.string.title_error), mContext.getString(R.string.tips_unzip_failed).concat(" : ").concat(e.getMessage()), mContext.getString(R.string.title_ok), null);
                            }

                            @Override
                            public void onSuccess() {
                                try {
                                    installer.startDownloadForge(installer.makeForgeData());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(R.string.title_error), String.format(mContext.getString(R.string.tips_error), e.getMessage()), mContext.getString(R.string.title_ok), null);
                                }
                            }

                            @Override
                            public void onFinish() {
                                mDialog.dismiss();
                            }
                        });
                    }
                });
            }
            if (v == buttonShowControbutors) {
                new AlertDialog.Builder(mContext)
                        .setView(R.layout.dialog_contributors)
                        .setCancelable(true)
                        .show();
            }
            if (v == buttonClearRuntime) {
                DialogUtils.createBothChoicesDialog(mContext, mContext.getString(R.string.title_warn), mContext.getString(R.string.tips_are_you_sure_to_delete_runtime), mContext.getString(R.string.title_continue), mContext.getString(R.string.title_cancel), new DialogSupports() {
                    @Override
                    public void runWhenPositive() {
                        RuntimeManager.clearRuntime(mContext);
                    }
                });
            }
        }
    };

    @Override
    public void refreshUI() {

    }

    @Override
    public void saveUIConfig() {
        setting.setDownloadType(listDownloaderSources.getSelectedItem().toString());
    }

    @Override
    public void setUIVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            layout_setting.startAnimation(showAnim);
        }
        layout_setting.setVisibility(visibility);
    }

    @Override
    public int getUIVisibility() {
        return layout_setting.getVisibility();
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

    @Override
    public void onCreate() {
        super.onCreate();
        setting = OldMainActivity.Setting;
        showAnim = AnimationUtils.loadAnimation(mContext, R.anim.layout_show);
        layout_setting = OldMainActivity.CURRENT_ACTIVITY.get().findViewById(R.id.layout_launchersetting);
        listDownloaderSources = layout_setting.findViewById(R.id.setting_spinner_downloadtype);
        buttonImportRuntime = layout_setting.findViewById(R.id.launchersetting_button_import);
        buttonInstallForge = layout_setting.findViewById(R.id.launchersetting_button_forgeinstaller);
        buttonShowControbutors = layout_setting.findViewById(R.id.setting_show_contributors);
        buttonClearRuntime = layout_setting.findViewById(R.id.launchersetting_button_clear_runtime);
        switchAutoBackground = layout_setting.findViewById(R.id.launchersetting_switch_auto_background);
        switchFullscreen = layout_setting.findViewById(R.id.launchersetting_switch_fullscreen);

        switchAutoBackground.setChecked(setting.isBackgroundAutoSwitch());
        switchFullscreen.setChecked(setting.isFullscreen());

        //设定监听器
        for (View v : new View[]{buttonInstallForge, buttonImportRuntime, buttonShowControbutors, buttonClearRuntime}) {
            v.setOnClickListener(clickListener);
        }
        for (SwitchCompat sc : new SwitchCompat[]{switchAutoBackground, switchFullscreen}) {
            sc.setOnCheckedChangeListener(this);
        }
        listDownloaderSources.setOnItemSelectedListener(this);

        setConfigureToDownloadtype(setting.getDownloadType(), listDownloaderSources);

        //调用主题管理器设定主题
        if (setting.isBackgroundAutoSwitch()) {
            if (!OldMainActivity.CURRENT_ACTIVITY.get().mThemeManager.autoSetBackground(OldMainActivity.CURRENT_ACTIVITY.get().findViewById(R.id.layout_main))) {
                DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(R.string.title_error), mContext.getString(R.string.tips_failed_to_change_backfround_pic_is_broken), mContext.getString(R.string.title_ok), null);
            }
        }

        if (setting.isFullscreen()) {
            OldMainActivity.CURRENT_ACTIVITY.get().mThemeManager.setFullScreen(OldMainActivity.CURRENT_ACTIVITY.get(), true);
        }

    }


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
        if (buttonView == switchAutoBackground) {
            if (isChecked) {
                DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(R.string.title_note), String.format(mContext.getString(R.string.tips_please_put_pic_to_background_dir), AppManifest.MCINABOX_BACKGROUND), mContext.getString(R.string.title_ok), null);
            }
            setting.setBackgroundAutoSwitch(isChecked);
        }

        if (buttonView == switchFullscreen) {
            /*
            if (isChecked) {
                OldMainActivity.CURRENT_ACTIVITY.get().mThemeManager.setFullScreen(OldMainActivity.CURRENT_ACTIVITY.get(), true);
            } else {
                OldMainActivity.CURRENT_ACTIVITY.get().mThemeManager.setFullScreen(OldMainActivity.CURRENT_ACTIVITY.get(), false);
                DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(R.string.title_note), mContext.getString(R.string.tips_successed_to_disable_hide_stat_bar), mContext.getString(R.string.title_ok), null);
            }*/
            OldMainActivity.CURRENT_ACTIVITY.get().mThemeManager.setFullScreen(OldMainActivity.CURRENT_ACTIVITY.get(), isChecked);
            setting.setFullscreen(isChecked);
        }
    }
}
