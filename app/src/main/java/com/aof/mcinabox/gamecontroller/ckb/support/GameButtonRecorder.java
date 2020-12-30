package com.aof.mcinabox.gamecontroller.ckb.support;

import android.content.Context;

import com.aof.mcinabox.gamecontroller.ckb.achieve.CkbManager;
import com.aof.mcinabox.gamecontroller.ckb.button.GameButton;
import com.aof.mcinabox.gamecontroller.controller.Controller;
import com.aof.mcinabox.utils.ColorUtils;

public class GameButtonRecorder {
    public String[] keyMaps = new String[GameButton.MAX_KEYMAP_SIZE];
    public int[] keyTypes = new int[GameButton.MAX_KEYMAP_SIZE];
    public int show;
    public int designIndex;
    public int cornerRadius;
    public String textColor;
    public String[] themeColors = new String[CkbThemeRecorder.COLOR_INDEX_LENGTH];
    public boolean isKeep;
    public boolean isHide;
    public float[] keyPos = new float[2];
    public float[] keySize = new float[2];
    public int alphaSize;
    public String keyName;
    public int textSize;
    public boolean isViewerFollow;
    public boolean isChars;
    public String keyChars;

    public void recordData(GameButton gb) {
        System.arraycopy(gb.getKeyMaps(), 0, this.keyMaps, 0, GameButton.MAX_KEYMAP_SIZE);
        System.arraycopy(gb.getKeyTypes(), 0, this.keyTypes, 0, GameButton.MAX_KEYMAP_SIZE);
        this.designIndex = gb.getDesignIndex();
        this.cornerRadius = gb.getCornerRadius();
        this.textColor = gb.getTextColorHex();
        System.arraycopy(gb.getColorHexs(), 0, this.themeColors, 0, CkbThemeRecorder.COLOR_INDEX_LENGTH);
        this.isKeep = gb.isKeep();
        this.isHide = gb.isHide();
        System.arraycopy(gb.getKeyPos(), 0, this.keyPos, 0, 2);
        System.arraycopy(gb.getKeySize(), 0, this.keySize, 0, 2);
        this.alphaSize = gb.getAlphaSize();
        this.keyName = gb.getKeyName();
        this.isViewerFollow = gb.isViewerFollow();
        this.show = gb.getShow();
        this.textSize = gb.getTextProgress();
        this.isChars = gb.isInputChars();
        this.keyChars = gb.getChars();
    }

    public GameButton recoverData(Context context, CallCustomizeKeyboard call, Controller controller, CkbManager manager) {
        GameButton gb = new GameButton(context, call, controller, manager);
        gb.setKeyMaps(this.keyMaps);
        gb.setKeyTypes(this.keyTypes);
        gb.setDesignIndex(this.designIndex);
        gb.setCornerRadius(this.cornerRadius);
        gb.setTextColor(this.textColor);
        for (int a = 0; a < CkbThemeRecorder.COLOR_INDEX_LENGTH; a++) {
            gb.getThemeRecorder().setColors(a, ColorUtils.hex2Int(this.themeColors[a]));
        }
        gb.setKeep(this.isKeep);
        gb.setHide(this.isHide);
        gb.setKeySize(this.keySize[0], this.keySize[1]);
        gb.setKeyPos(this.keyPos[0], this.keyPos[1]);
        gb.setAlphaSize(this.alphaSize);
        gb.setKeyName(this.keyName);
        gb.setTextSize(this.textSize);
        gb.setViewerFollow(this.isViewerFollow);
        gb.setShow(this.show);
        gb.setChars(this.keyChars);
        gb.setInputChars(this.isChars);
        return gb;
    }
}
