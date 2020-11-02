package com.aof.mcinabox.launcher.setting;

import android.content.Context;
import android.util.Log;

import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.launcher.setting.support.SettingChecker;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SettingManager {

    private Context mContext;
    private File settingFile;
    private Timer mTimer;

    private final static String TAG = "SettingManager";

    public SettingManager(Context context){
        this.mContext = context;
        settingFile = new File(Objects.requireNonNull(mContext.getExternalFilesDir("mcinabox")).getAbsolutePath() + "/mcinabox.json");
    }

    /**【读入mcinabox.json】**/
    public SettingJson getSettingFromFile(){
        SettingJson settingModel;

        if (!settingFile.exists()) {
            settingModel = new SettingJson();
        } else {
            try {
                InputStream inputStream = new FileInputStream(settingFile);
                Reader reader = new InputStreamReader(inputStream);
                Gson gson = new Gson();
                //使用Gson将ListVersionManifestJson实例化
                settingModel = gson.fromJson(reader, SettingJson.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                settingModel = null;
            }
        }

        if(settingModel == null){
            settingModel = new SettingJson();
        }

        return settingModel;
    }

    /**【保存mcinabox.json文件】**/
    public void saveSettingToFile(){
        Gson gson = new Gson();
        String jsonString = gson.toJson(OldMainActivity.Setting);
        try {
            FileWriter jsonWriter = new FileWriter(settingFile);
            BufferedWriter out = new BufferedWriter(jsonWriter);
            out.write(jsonString);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"save failed.");
        }
    }

    public void startChecking(){
        if(mTimer == null){
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SettingChecker sc = new SettingChecker(mContext,null,null);
                    sc.checkIfChoseUser();
                    sc.checkIfInstallGame();
                    sc.checkIfInstallRuntime();
                    sc.checkMenmrySize();
                    sc.checkIfDisableFileCheck();
                    sc.checkAuthlibInjector();
                }
            },0,500);
        }
    }

    public void stopChecking(){
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }

}
