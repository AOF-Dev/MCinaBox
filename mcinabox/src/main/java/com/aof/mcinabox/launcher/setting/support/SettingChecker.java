package com.aof.mcinabox.launcher.setting.support;

import android.content.Context;
import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.definitions.manifest.AppManifest;
import com.aof.mcinabox.launcher.runtime.RuntimeManager;
import com.aof.mcinabox.launcher.tipper.TipperManager;
import com.aof.mcinabox.launcher.tipper.support.TipperRunable;
import com.aof.mcinabox.launcher.user.UserManager;
import com.aof.utils.FileTool;
import com.aof.utils.MemoryUtils;
import com.aof.utils.dialog.DialogUtils;

public class SettingChecker {

    private final static int CHECKER_ID_NOT_CHOOSE_USER = 10;
    private final static int CHECKER_ID_NOT_INSTALL_RUNTIME = 11;
    private final static int CHECKER_ID_NOT_INSTALL_GAME = 12;
    private final static int CHECKER_ID_MEMORY_LOW = 13;
    private final static int CHECKER_ID_MEMORY_OVER = 14;
    private final static int CHECKER_ID_NOT_CHECK_GAME = 15;

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
            mTipperManager.addTip(TipperManager.createTipBean(mContext, TipperManager.TIPPER_LEVEL_WARN, "未选择用户", new TipperRunable() {
                @Override
                public void run() {
                    DialogUtils.createSingleChoiceDialog(mContext,"警告","您还没有选择用户，请在左侧导航栏选择用户管理并创建或选择一个用户。","确定",null);
                }
            },CHECKER_ID_NOT_CHOOSE_USER));
        }else{
            mTipperManager.removeTip(CHECKER_ID_NOT_CHOOSE_USER);
        }
    }
    public void checkIfInstallRuntime(){
        if(RuntimeManager.getPackInfo() == null){
            mTipperManager.addTip(TipperManager.createTipBean(mContext, TipperManager.TIPPER_LEVEL_WARN, "未安装运行库", new TipperRunable() {
                @Override
                public void run() {
                    DialogUtils.createSingleChoiceDialog(mContext,"警告","您还没有安装运行库，请在左侧导航栏选择启动器设置并安装运行库。","确定",null);
                }
            },CHECKER_ID_NOT_INSTALL_RUNTIME));
        }else{
            mTipperManager.removeTip(CHECKER_ID_NOT_INSTALL_RUNTIME);
        }
    }
    public void checkIfInstallGame(){
        if(FileTool.listChildDirFromTargetDir(AppManifest.MINECRAFT_VERSIONS).size() == 0){
            mTipperManager.addTip(TipperManager.createTipBean(mContext, TipperManager.TIPPER_LEVEL_WARN, "未安装游戏", new TipperRunable() {
                @Override
                public void run() {
                    DialogUtils.createSingleChoiceDialog(mContext,"警告","您还没有安装任何的游戏版本，请在左侧导航栏选择游戏列表-安装新游戏版本并安装一个游戏版本。","确定",null);
                }
            },CHECKER_ID_NOT_INSTALL_GAME));
        }else{
            mTipperManager.removeTip(CHECKER_ID_NOT_INSTALL_GAME);
        }
    }
    public void checkMenmrySize(){
        if(mSetting.getConfigurations().getMaxMemory() < 256){
            mTipperManager.addTip(TipperManager.createTipBean(mContext, TipperManager.TIPPER_LEVEL_NOTE, "内存分配过低", new TipperRunable() {
                @Override
                public void run() {
                    DialogUtils.createSingleChoiceDialog(mContext,"提示","您设定的内存上限过低，可能导致java虚拟机崩溃，请在左侧导航栏选择启动器设置调整内存大小。","确定",null);
                }
            },CHECKER_ID_MEMORY_LOW));
        }else{
            mTipperManager.removeTip(CHECKER_ID_MEMORY_LOW);
        }

        if(mSetting.getConfigurations().getMaxMemory() > MemoryUtils.getDynamicHeapSize(mContext) * 2 - 20){
            mTipperManager.addTip(TipperManager.createTipBean(mContext, TipperManager.TIPPER_LEVEL_NOTE, "内存分配过高", new TipperRunable() {
                @Override
                public void run() {
                    DialogUtils.createSingleChoiceDialog(mContext,"提示","您设定的内存上限过高，可能导致java虚拟机崩溃，请在左侧导航栏选择启动器设置调整内存大小。","确定",null);
                }
            },CHECKER_ID_MEMORY_OVER));
        }else{
            mTipperManager.removeTip(CHECKER_ID_MEMORY_OVER);
        }
    }

    public void checkIfDisableFileCheck(){
        if(mSetting.getConfigurations().isNotCheckGame()){
            mTipperManager.addTip(TipperManager.createTipBean(mContext, TipperManager.TIPPER_LEVEL_NOTE, "不检查文件完整性", new TipperRunable() {
                @Override
                public void run() {
                    DialogUtils.createSingleChoiceDialog(mContext,"提示","您禁用了文件完整性检查，如果游戏文件不完整，您将不会得到任何提示，在游戏不完整的情况下继续启动，有可能造成minecraft异常崩溃。因此建议您开启文件完整性检查。","确定",null);
                }
            },CHECKER_ID_NOT_CHECK_GAME));
        }else{
            mTipperManager.removeTip(CHECKER_ID_NOT_CHECK_GAME);
        }
    }

}
