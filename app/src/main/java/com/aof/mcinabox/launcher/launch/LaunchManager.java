package com.aof.mcinabox.launcher.launch;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.aof.mcinabox.R;
import cosine.boat.BoatArgs;
import com.aof.mcinabox.gamecontroller.controller.HardwareController;
import com.aof.mcinabox.gamecontroller.controller.VirtualController;
import com.aof.mcinabox.launcher.launch.support.AsyncManager;
import com.aof.mcinabox.launcher.launch.support.BoatArgsMaker;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.aof.mcinabox.utils.dialog.support.TaskDialog;

import cosine.boat.BoatActivity;

import static cosine.boat.definitions.id.key.KeyEvent.KEYMAP_TO_X;

public class LaunchManager {

    public final static int LAUNCH_PRECHECK = 0;
    public final static int LAUNCH_PARM_SETUP = 1;
    public final static int LAUNCH_PARM_MAKE = 2;
    public final static int LAUNCH_GAME = 3;

    private final Context mContext;
    private final static String TAG = "LaunchManager";
    private final TaskDialog fbDialog;
    private BoatArgsMaker maker;

    public LaunchManager(Context context) {
        this.mContext = context;
        fbDialog = DialogUtils.createTaskDialog(mContext, mContext.getString(R.string.tips_launching), "", false);
    }

    public void brige_setProgressText(String des) {
        fbDialog.setCurrentTaskName(des);
    }

    public void brige_exitWithSuccess() {
        fbDialog.dismiss();
    }

    public void brige_exitWithError(String des) {
        if (des != null && !des.equals("")) {
            DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(R.string.title_error), des, mContext.getString(R.string.title_ok), null);
        }
        fbDialog.dismiss();
    }

    public void launchMinecraft(SettingJson setting, int i) {
        switch (i) {
            case LAUNCH_PRECHECK:
                fbDialog.show();
                new AsyncManager(mContext, this, setting).start();
                break;
            case LAUNCH_PARM_SETUP:
                maker = new BoatArgsMaker(mContext, setting, this);
                maker.setup(setting.getLastVersion());
                break;
            case LAUNCH_PARM_MAKE:
                maker.make();

                break;
            case LAUNCH_GAME:
                BoatArgs args = maker.getBoatArgs();
                brige_exitWithSuccess();
                attachControllerInterface();
                mContext.startActivity(new Intent(mContext, BoatActivity.class).putExtra("LauncherConfig", maker.getBoatArgs()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
        }
    }

    private void attachControllerInterface() {
        BoatActivity.controllerInterface = new BoatActivity.IController() {
            private VirtualController virtualController;
            private HardwareController hardwareController;

            @Override
            public void onActivityCreate(BoatActivity boatActivity) {
                virtualController = new VirtualController(boatActivity, boatActivity, KEYMAP_TO_X);
                hardwareController = new HardwareController(boatActivity, boatActivity, KEYMAP_TO_X);
            }

            @Override
            public void saveConfig() {
                virtualController.saveConfig();
                hardwareController.saveConfig();
            }

            @Override
            public void setInputMode(int inputMode) {
                virtualController.setInputMode(inputMode);
                hardwareController.setInputMode(inputMode);
            }

            @Override
            public void onStop() {
                virtualController.onStop();
                hardwareController.onStop();
            }

            @Override
            public void dispatchKeyEvent(KeyEvent event) {
                hardwareController.dispatchKeyEvent(event);
            }

            @Override
            public void dispatchMotionKeyEvent(MotionEvent event) {
                hardwareController.dispatchMotionKeyEvent(event);
            }
        };
    }
}




