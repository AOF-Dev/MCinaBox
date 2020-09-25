package com.aof.mcinabox.launcher.launch;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.definitions.models.BoatArgs;
import com.aof.mcinabox.launcher.launch.support.AsyncManager;
import com.aof.mcinabox.launcher.launch.support.BoatArgsMaker;
import com.aof.mcinabox.launcher.launch.support.FeedBackDialog;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.launcher.tipper.TipperManager;
import com.aof.mcinabox.launcher.tipper.support.TipperRunable;
import com.aof.utils.dialog.DialogUtils;
import com.aof.utils.dialog.support.TaskDialog;

public class LaunchManager {

    public final static int LAUNCH_PRECHECK = 0;
    public final static int LAUNCH_PARM_SETUP = 1;
    public final static int LAUNCH_PARM_MAKE = 2;
    public final static int LAUNCH_GAME = 3;

    private Context mContext;
    private final static String TAG = "LaunchManager";
    private TaskDialog fbDialog;
    private BoatArgsMaker maker;

    public LaunchManager(Context context){
        this.mContext = context;
        fbDialog = DialogUtils.createTaskDialog(mContext,"正在启动...","",false);
    }

    public void brige_setProgressText(String des){
        fbDialog.setCurrentTaskName(des);
    }

    public void brige_exitWithSuccess(){
        fbDialog.dismiss();
    }

    public void brige_exitWithError(String des){
        if(des != null && ! des.equals("")){
            DialogUtils.createSingleChoiceDialog(mContext,"错误",des,"确定",null);
        }
        fbDialog.dismiss();
    }

    public void launchMinecraft(SettingJson setting, int i){

        switch (i){
            case LAUNCH_PRECHECK:
                fbDialog.show();
                new AsyncManager(mContext,this,setting).start();
                break;
            case LAUNCH_PARM_SETUP:
                maker = new BoatArgsMaker(mContext,setting,this);
                maker.setup(setting.getLastVersion());
                break;
            case LAUNCH_PARM_MAKE:
                maker.make();
                break;
            case LAUNCH_GAME:

                BoatArgs args = maker.getBoatArgs();

                brige_exitWithSuccess();
                mContext.startActivity(new Intent(mContext, cosine.boat.LauncherActivity.class).putExtra("LauncherConfig",maker.getBoatArgs()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
        }

    }

}




