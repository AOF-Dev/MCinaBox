package com.aof.mcinabox.gamecontroller.ckb.support;

import android.content.Context;

import com.aof.mcinabox.gamecontroller.ckb.achieve.CkbManager;
import com.aof.mcinabox.gamecontroller.ckb.button.GameButton;
import com.aof.mcinabox.gamecontroller.ckb.button.GameButtonOld;
import com.aof.mcinabox.gamecontroller.definitions.map.MouseMap;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

public class GameButtonConverter {

    private final Context mContext;

    public GameButtonConverter(Context context){
        this.mContext = context;
    }

    public boolean output(File file){
        StringBuilder fileName = new StringBuilder();
        for(int i = 0; i < file.getName().length() - 5; i++){
            fileName.append(file.getName().charAt(i));
        }
        try {
            CkbManager.outputFile(getNewKeyboardFromOldKeyboard(getOldKeyboardFormJson(file)), fileName.toString() + "-new");
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public GameButtonOld[] getOldKeyboardFormJson(File file){
        InputStream inputStream;
        Gson gson = new Gson();
        File jsonFile = file;
        if(!jsonFile.exists()){
            return null;
        }
        try {
            inputStream = new FileInputStream(jsonFile);
            Reader reader = new InputStreamReader(inputStream);
            GameButtonOld[] jsonArray = new Gson().fromJson(reader, GameButtonOld[].class);
            List<GameButtonOld> tempList1 = Arrays.asList(jsonArray);
            if(tempList1.size() != 0){
                return (GameButtonOld[]) tempList1.toArray();
            }else{
                return new GameButtonOld[]{};
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public KeyboardRecorder getNewKeyboardFromOldKeyboard(GameButtonOld[] gbos){
        KeyboardRecorder kr = new KeyboardRecorder();
        GameButtonRecorder[] gbrs = new GameButtonRecorder[gbos.length];
        for(int i = 0; i < gbrs.length; i++){
            gbrs[i] = getGameButtonRecoderFromOldKeyboardModel(gbos[i]);
        }
        kr.setRecorderDatas(gbrs);
        kr.setVersionCode(KeyboardRecorder.VERSION_THIS);
        kr.setScreenArgs(mContext.getResources().getDisplayMetrics().widthPixels, mContext.getResources().getDisplayMetrics().heightPixels);

        return kr;
    }

    private GameButtonRecorder getGameButtonRecoderFromOldKeyboardModel(GameButtonOld gbo){
        final String OLD_MOUSE_PRI = "MOUSE_Pri";
        final String OLD_MOUSE_SEC = "MOUSE_Sec";

        GameButtonRecorder gbr = new GameButtonRecorder();
        gbr.keyPos = new float[]{gbo.getKeyLX(), gbo.getKeyLY()}; //KeyLx -> Dp
        gbr.keyChars = "";
        gbr.keySize = new float[]{gbo.getKeySizeW(), gbo.getKeySizeH()};
        gbr.isChars = false;
        gbr.alphaSize = GameButton.DEFAULT_ALPHA_SIZE_PT; //透明度采用默认值
        gbr.cornerRadius = (gbo.getCornerRadius() / 180) * 100; //圆角值转化为百分比
        gbr.designIndex = GameButton.DEFAULT_DESIGN_INDEX; //主题采用默认值
        gbr.isHide = gbo.isHide();
        gbr.isKeep = gbo.isAutoKeep();
        gbr.isViewerFollow = false; //视角跟随默认关闭

        String[] keyMap = new String[GameButton.MAX_KEYMAP_SIZE];
        int[] keyTypes = new int[GameButton.MAX_KEYMAP_SIZE];
        if (gbo.getKeyMain().equals("空")){
            //nothing
        }else if (gbo.getKeyMain().equals(OLD_MOUSE_PRI)){
            keyMap[0] = MouseMap.MOUSEMAP_BUTTON_LEFT;
            keyTypes[0] = GameButton.MOUSE_TYPE;
        }else if (gbo.getKeyMain().equals(OLD_MOUSE_SEC)){
            keyMap[0] = MouseMap.MOUSEMAP_BUTTON_RIGHT;
            keyTypes[0] = GameButton.MOUSE_TYPE;
        }else{
            keyMap[0] = gbo.getKeyMain();
            keyTypes[0] = GameButton.KEY_TYPE;
        }
        if(gbo.isMult()){
            if (gbo.getSpecialOne().equals("空")){
                //nothing
            }else if (gbo.getSpecialOne().equals(OLD_MOUSE_PRI)){
                keyMap[1] = MouseMap.MOUSEMAP_BUTTON_LEFT;
                keyTypes[1] = GameButton.MOUSE_TYPE;
            }else if (gbo.getSpecialOne().equals(OLD_MOUSE_SEC)){
                keyMap[1] = MouseMap.MOUSEMAP_BUTTON_RIGHT;
                keyTypes[1] = GameButton.MOUSE_TYPE;
            }else{
                keyMap[1] = gbo.getKeyMain();
                keyTypes[1] = GameButton.KEY_TYPE;
            }

            if (gbo.getSpecialTwo().equals("空")){
                //nothing
            }else if (gbo.getSpecialTwo().equals(OLD_MOUSE_PRI)){
                keyMap[2] = MouseMap.MOUSEMAP_BUTTON_LEFT;
                keyTypes[2] = GameButton.MOUSE_TYPE;
            }else if (gbo.getSpecialOne().equals(OLD_MOUSE_SEC)){
                keyMap[2] = MouseMap.MOUSEMAP_BUTTON_RIGHT;
                keyTypes[2] = GameButton.MOUSE_TYPE;
            }else{
                keyMap[2] = gbo.getKeyMain();
                keyTypes[2] = GameButton.KEY_TYPE;
            }
        }
        gbr.keyTypes = keyTypes;
        gbr.keyMaps = keyMap;

        gbr.keyName = gbo.getKeyName();
        gbr.show = GameButton.SHOW_ALL; //默认全局显示
        gbr.textColor = gbo.getTextColorHex();
        gbr.themeColors = new String[CkbThemeRecorder.COLOR_INDEX_LENGTH];
        gbr.themeColors[0] = gbo.getColorhex(); //默认只给主题的第一个颜色为赋值
        gbr.textSize = GameButton.DEFAULT_TEXT_SIZE_SP; //文字大小采用默认值

        return gbr;
    }


}
