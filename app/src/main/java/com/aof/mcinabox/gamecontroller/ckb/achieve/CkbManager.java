package com.aof.mcinabox.gamecontroller.ckb.achieve;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.aof.mcinabox.gamecontroller.ckb.button.GameButton;
import com.aof.mcinabox.gamecontroller.ckb.support.CallCustomizeKeyboard;
import com.aof.mcinabox.gamecontroller.ckb.support.GameButtonArray;
import com.aof.mcinabox.gamecontroller.ckb.support.GameButtonRecorder;
import com.aof.mcinabox.gamecontroller.ckb.support.KeyboardRecorder;
import com.aof.mcinabox.gamecontroller.controller.Controller;
import com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class CkbManager {

    private final static String TAG = "CkbManager";
    public final static int MAX_KEYBOARD_SIZE = 80;
    public final static int MIN_KEYBOARD_SIZE = 0;
    public final static String LAST_KEYBOARD_LAYOUT_NAME = "tmp";
    public final static int SHOW_BUTTON = 1;
    public final static int HIDE_BUTTON = 2;

    private final Context mContext;
    private final CallCustomizeKeyboard mCall;
    private final Controller mController;
    private boolean hasHide = false;

    private GameButtonArray<GameButton> buttonList;

    private int buttonMode = GameButton.MODE_MOVEABLE_EDITABLE;

    public CkbManager(@NonNull Context context, @NonNull CallCustomizeKeyboard call, Controller controller) {
        super();
        this.mContext = context;
        this.mCall = call;
        this.mController = controller;
        init();
    }

    private void init() {

        //初始化按键列表
        buttonList = new GameButtonArray<>();
        //当Manager初始化的时候自动加载键盘布局
        autoLoadKeyboard();

    }

    public Controller getController() {
        return mController;
    }

    public boolean addGameButton(GameButton button) {
        if (this.containGameButton(button) || this.buttonList.size() >= MAX_KEYBOARD_SIZE) {
            button.unsetFirstAdded();
            return false;
        } else {
            if (button == null) {
                button = new GameButton(mContext, mCall, mController, this).setButtonMode(this.buttonMode).setFirstAdded();
                (new GameButtonDialog(mContext, button, this)).show();
            }
            this.buttonList.add(button);
            this.addView(button);
            return true;
        }
    }

    public boolean containGameButton(GameButton g) {
        return buttonList.contains(g);
    }

    public boolean removeGameButton(GameButton g) {
        if (this.containGameButton(g)) {
            GameButtonArray<GameButton> gl = new GameButtonArray<>();
            for (GameButton gb : buttonList) {
                if (gb != g) {
                    gl.add(gb);
                }
            }
            this.removeView(g);
            this.buttonList = gl;
            return true;
        } else {
            return false;
        }
    }

    private boolean addView(GameButton g) {

        if (g != null && g.getParent() == null) {
            mCall.addView(g);
            return true;
        } else {
            return false;
        }

    }

    private boolean removeView(GameButton g) {

        if (g != null && g.getParent() != null) {
            ViewGroup vg = (ViewGroup) g.getParent();
            vg.removeView(g);
            return true;
        } else {
            return false;
        }

    }

    public int getButtonCounts() {
        return buttonList.size();
    }

    public boolean setButtonsMode(int mode) {
        if (mode == GameButton.MODE_GAME || mode == GameButton.MODE_MOVEABLE_EDITABLE || mode == GameButton.MODE_PREVIEW) {
            for (GameButton g : buttonList) {
                g.setButtonMode(mode);
            }
            this.buttonMode = mode;
            return true;
        } else {
            return false;
        }
    }

    public int getButtonsMode() {
        return this.buttonMode;
    }

    public GameButton[] getGameButtons() {
        GameButton[] views = new GameButton[getButtonCounts()];
        for (int i = 0; i < views.length; i++) {
            views[i] = buttonList.get(i);
        }
        return views;
    }

    public GameButton getGameButton(int index) {
        if (index >= 0 && index < buttonList.size()) {
            return buttonList.get(index);
        } else {
            return null;
        }
    }

    public void setInputMode(boolean mode) {
        for (GameButton gb : buttonList) {
            gb.setGrabbed(mode);
        }
    }

    public boolean exportKeyboard(String fileName) {
        GameButtonRecorder[] gbrs = new GameButtonRecorder[buttonList.size()];
        for (int a = 0; a < buttonList.size(); a++) {
            GameButtonRecorder gbr = new GameButtonRecorder();
            gbr.recordData(buttonList.get(a));
            gbrs[a] = gbr;
        }
        KeyboardRecorder kr = new KeyboardRecorder();
        kr.setScreenArgs(mContext.getResources().getDisplayMetrics().widthPixels, mContext.getResources().getDisplayMetrics().heightPixels);
        kr.setRecorderDatas(gbrs);

        Gson gson = new Gson();
        String jsonString = gson.toJson(kr);
        try {
            FileWriter jsonWriter = new FileWriter(new File(AppManifest.MCINABOX_KEYBOARD + "/" + fileName + ".json"));
            BufferedWriter out = new BufferedWriter(jsonWriter);
            out.write(jsonString);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void autoSaveKeyboard() {
        exportKeyboard(LAST_KEYBOARD_LAYOUT_NAME);
    }

    public void autoLoadKeyboard() {
        loadKeyboard(LAST_KEYBOARD_LAYOUT_NAME + ".json");
    }

    public boolean loadKeyboard(String fileName) {
        File file = new File(AppManifest.MCINABOX_KEYBOARD + "/" + fileName);
        if (!file.exists()) {
            return false;
        }
        KeyboardRecorder kr;
        try {
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            kr = gson.fromJson(reader, KeyboardRecorder.class);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        GameButtonRecorder[] gbr;
        if (kr != null) {
            gbr = kr.getRecorderDatas();
        } else {
            return false;
        }
        //清除全部按键
        clearKeyboard();
        //添加新的按键
        for (GameButtonRecorder tgbr : gbr) {
            addGameButton(tgbr.recoverData(mContext, mCall, mController, this));
        }
        return true;
    }

    public void clearKeyboard() {
        for (GameButton gb : buttonList) {
            removeView(gb);
        }
        buttonList = new GameButtonArray<>();
    }

    public void showOrHideGameButtons(int i) {
        // 仅仅只是暂时把GameButton对象从显示层中删除
        // 不要更改按键记录
        switch (i) {
            case SHOW_BUTTON:
                if (hasHide) {
                    for (GameButton gb : buttonList) {
                        this.addView(gb);
                    }
                    hasHide = false;
                }
                break;
            case HIDE_BUTTON:
                if (!hasHide) {
                    for (GameButton gb : buttonList) {
                        this.removeView(gb);
                    }
                    hasHide = true;
                }
                break;
        }

    }


}
