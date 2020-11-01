package com.aof.mcinabox.launcher.setting.support;

import android.content.Context;
import com.aof.mcinabox.activity.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.definitions.manifest.AppManifest;
import com.aof.mcinabox.launcher.download.authlib.Request;
import com.aof.mcinabox.launcher.runtime.RuntimeManager;
import com.aof.mcinabox.launcher.tipper.TipperManager;
import com.aof.mcinabox.launcher.tipper.support.TipperRunable;
import com.aof.mcinabox.launcher.user.UserManager;
import com.aof.utils.FileTool;
import com.aof.utils.MemoryUtils;
import com.aof.utils.dialog.DialogUtils;
import com.aof.utils.dialog.support.DialogSupports;

import java.io.File;

public class SettingChecker {

    private final static int CHECKER_ID_NOT_CHOOSE_USER = 10;
    private final static int CHECKER_ID_NOT_INSTALL_RUNTIME = 11;
    private final static int CHECKER_ID_NOT_INSTALL_GAME = 12;
    private final static int CHECKER_ID_MEMORY_LOW = 13;
    private final static int CHECKER_ID_MEMORY_OVER = 14;
    private final static int CHECKER_ID_NOT_CHECK_GAME = 15;
    private final static int CHECKER_ID_MISSING_AUTHLIB = 16;

    private Context mContext;
    private SettingJson mSetting;
    private TipperManager mTipperManager;

    public SettingChecker(Context context, SettingJson setting, TipperManager manager){
        this.mContext = context;
        if(setting == null){
            mSetting = MainActivity.Setting;
        }else{
            mSetting = setting;
        }
        if(manager == null){
            mTipperManager = MainActivity.CURRENT_ACTIVITY.mTipperManager;
        }else{
            mTipperManager = manager;
        }
    }

    public void checkIfChoseUser(){
        if(UserManager.getSelectedAccount(mSetting) == null){
            mTipperManager.addTip(TipperManager.createTipBean(mContext, TipperManager.TIPPER_LEVEL_WARN, mContext.getString(R.string.tips_not_selected_user), new TipperRunable() {
                @Override
                public void run() {
                    DialogUtils.createSingleChoiceDialog(mContext,mContext.getString(R.string.title_warn),mContext.getString(R.string.tips_not_create_user_please_do_it),mContext.getString(R.string.title_ok),null);
                }
            },CHECKER_ID_NOT_CHOOSE_USER));
        }else{
            mTipperManager.removeTip(CHECKER_ID_NOT_CHOOSE_USER);
        }
    }
    public void checkIfInstallRuntime(){
        if(RuntimeManager.getPackInfo() == null){
            mTipperManager.addTip(TipperManager.createTipBean(mContext, TipperManager.TIPPER_LEVEL_WARN, mContext.getString(R.string.tips_not_install_runtime), new TipperRunable() {
                @Override
                public void run() {
                    DialogUtils.createSingleChoiceDialog(mContext,mContext.getString(R.string.title_warn),mContext.getString(R.string.tips_not_install_runtime_please_do_it),mContext.getString(R.string.title_ok),null);
                }
            },CHECKER_ID_NOT_INSTALL_RUNTIME));
        }else{
            mTipperManager.removeTip(CHECKER_ID_NOT_INSTALL_RUNTIME);
        }
    }
    public void checkIfInstallGame(){
        if(FileTool.listChildDirFromTargetDir(AppManifest.MINECRAFT_VERSIONS).size() == 0){
            mTipperManager.addTip(TipperManager.createTipBean(mContext, TipperManager.TIPPER_LEVEL_WARN, mContext.getString(R.string.tips_not_select_version), new TipperRunable() {
                @Override
                public void run() {
                    DialogUtils.createSingleChoiceDialog(mContext,mContext.getString(R.string.title_warn),mContext.getString(R.string.tips_not_selected_version_please_do_it),mContext.getString(R.string.title_ok),null);
                }
            },CHECKER_ID_NOT_INSTALL_GAME));
        }else{
            mTipperManager.removeTip(CHECKER_ID_NOT_INSTALL_GAME);
        }
    }
    public void checkMenmrySize(){
        if(mSetting.getConfigurations().getMaxMemory() < 256){
            mTipperManager.addTip(TipperManager.createTipBean(mContext, TipperManager.TIPPER_LEVEL_NOTE, mContext.getString(R.string.tips_available_memory_low), new TipperRunable() {
                @Override
                public void run() {
                    DialogUtils.createSingleChoiceDialog(mContext,mContext.getString(R.string.title_note),mContext.getString(R.string.tips_please_set_more_memory),mContext.getString(R.string.title_ok),null);
                }
            },CHECKER_ID_MEMORY_LOW));
        }else{
            mTipperManager.removeTip(CHECKER_ID_MEMORY_LOW);
        }

        if(mSetting.getConfigurations().getMaxMemory() > MemoryUtils.getDynamicHeapSize(mContext) * 2 - 20 || mSetting.getConfigurations().getMaxMemory() > MemoryUtils.getTotalMemoryMB(mContext)){
            mTipperManager.addTip(TipperManager.createTipBean(mContext, TipperManager.TIPPER_LEVEL_NOTE, mContext.getString(R.string.tips_available_memory_over), new TipperRunable() {
                @Override
                public void run() {
                    DialogUtils.createSingleChoiceDialog(mContext,mContext.getString(R.string.title_note),mContext.getString(R.string.tips_please_set_less_memory),mContext.getString(R.string.title_ok),null);
                }
            },CHECKER_ID_MEMORY_OVER));
        }else{
            mTipperManager.removeTip(CHECKER_ID_MEMORY_OVER);
        }
    }

    public void checkIfDisableFileCheck(){
        if(mSetting.getConfigurations().isNotCheckGame()){
            mTipperManager.addTip(TipperManager.createTipBean(mContext, TipperManager.TIPPER_LEVEL_NOTE, mContext.getString(R.string.title_not_check_minecraft), new TipperRunable() {
                @Override
                public void run() {
                    DialogUtils.createSingleChoiceDialog(mContext,mContext.getString(R.string.title_note),mContext.getString(R.string.tips_please_turn_on_minecraft_check),mContext.getString(R.string.title_ok),null);
                }
            },CHECKER_ID_NOT_CHECK_GAME));
        }else{
            mTipperManager.removeTip(CHECKER_ID_NOT_CHECK_GAME);
        }
    }

    public void checkAuthlibInjector(){
        SettingJson.Account account = UserManager.getSelectedAccount(mSetting);
        File file = new File(AppManifest.AUTHLIB_INJETOR_JAR);
        if(!file.exists() && account != null && account.type.equals(SettingJson.USER_TYPE_EXTERNAL)){
            mTipperManager.addTip(TipperManager.createTipBean(mContext, TipperManager.TIPPER_LEVEL_ERROR, mContext.getString(R.string.title_missing_authlib), new TipperRunable() {
                @Override
                public void run() {
                    DialogUtils.createBothChoicesDialog(mContext,mContext.getString(R.string.title_error),mContext.getString(R.string.tips_please_download_authlib_injector),mContext.getString(R.string.title_ok),mContext.getString(R.string.title_cancel),new DialogSupports(){
                        @Override
                        public void runWhenPositive(){
                            new Request(mContext).requestLastestVersion();
                        }
                    });
                }
            },CHECKER_ID_MISSING_AUTHLIB));
        }else{
            mTipperManager.removeTip(CHECKER_ID_MISSING_AUTHLIB);
        }
    }

}
