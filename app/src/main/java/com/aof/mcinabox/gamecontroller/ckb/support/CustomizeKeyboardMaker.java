package com.aof.mcinabox.gamecontroller.ckb.support;

import android.content.Context;

import com.aof.mcinabox.gamecontroller.ckb.button.GameButton;
import com.aof.mcinabox.gamecontroller.definitions.map.KeyMap;
import com.aof.mcinabox.gamecontroller.definitions.map.MouseMap;
import com.aof.mcinabox.utils.DisplayUtils;

import java.util.ArrayList;

import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.KEYBOARD_BUTTON;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MOUSE_BUTTON;

public class CustomizeKeyboardMaker {

    private Context mContext;

    public CustomizeKeyboardMaker(Context context) {
        this.mContext = context;
    }


    public KeyboardRecorder createDefaultKeyboard() {
        KeyboardRecorder defKR = new KeyboardRecorder();
        ArrayList<GameButtonRecorder> defGBRs = new ArrayList<>();
        //设置屏幕像素参数
        defKR.setScreenArgs(DisplayUtils.getDisplayWindowSize(mContext)[0], DisplayUtils.getDisplayWindowSize(mContext)[1]);
        //设置版本号
        defKR.setVersionCode(KeyboardRecorder.VERSION_THIS);
        //添加默认按键
        defGBRs.add(new GameButtonBuilder(mContext)
                .setSize()
                .setMargin(5,5,0,0)
                .setKeyMap(KeyMap.KEYMAP_KEY_ESC)
                .setKeyTypes(KEYBOARD_BUTTON)
                .setTheme()
                .setAttribute("ESC")
                .build());
        defGBRs.add(new GameButtonBuilder(mContext)
                .setSize()
                .setMargin(60,5,0,0)
                .setKeyMap(KeyMap.KEYMAP_KEY_F3)
                .setKeyTypes(KEYBOARD_BUTTON)
                .setTheme()
                .setAttribute("F3")
                .build());
        defGBRs.add(new GameButtonBuilder(mContext)
                .setSize()
                .setMargin(115,5,0,0)
                .setKeyMap(KeyMap.KEYMAP_KEY_T)
                .setKeyTypes(KEYBOARD_BUTTON)
                .setTheme()
                .setAttribute("T",false,false,GameButton.SHOW_IN_GAME)
                .build());
        defGBRs.add(new GameButtonBuilder(mContext)
                .setSize()
                .setMargin(170,5,0,0)
                .setKeyMap(KeyMap.KEYMAP_KEY_Q)
                .setKeyTypes(KEYBOARD_BUTTON)
                .setTheme()
                .setAttribute("Q",false,false,GameButton.SHOW_IN_GAME)
                .build());
        defGBRs.add(new GameButtonBuilder(mContext)
                .setSize()
                .setMargin(170,0,0,5)
                .setKeyMap(KeyMap.KEYMAP_KEY_E)
                .setKeyTypes(KEYBOARD_BUTTON)
                .setTheme()
                .setAttribute("E")
                .build());
        defGBRs.add(new GameButtonBuilder(mContext)
                .setSize()
                .setMargin(0,180,130,0)
                .setKeyMap(KeyMap.KEYMAP_KEY_SPACE)
                .setKeyTypes(KEYBOARD_BUTTON)
                .setTheme(2,4,15,70,"#FFFFFF","#FFFFFF")
                .setAttribute("SPACE")
                .build());
        defGBRs.add(new GameButtonBuilder(mContext)
                .setSize()
                .setMargin(0,180,185,0)
                .setKeyMap(MouseMap.MOUSEMAP_BUTTON_LEFT)
                .setKeyTypes(MOUSE_BUTTON)
                .setTheme()
                .setAttribute("PRI", false, true, GameButton.SHOW_ALL)
                .build());
        defGBRs.add(new GameButtonBuilder(mContext)
                .setSize()
                .setMargin(0,180,75,0)
                .setKeyMap(MouseMap.MOUSEMAP_BUTTON_RIGHT)
                .setKeyTypes(MOUSE_BUTTON)
                .setTheme()
                .setAttribute("SEC")
                .build());
        defGBRs.add(new GameButtonBuilder(mContext)
                .setSize()
                .setMargin(0,125,130,0)
                .setKeyMap(MouseMap.MOUSEMAP_WHEEL_UP)
                .setKeyTypes(MOUSE_BUTTON)
                .setTheme(2,2,15,70,"#FFFFFF","#FFFFFF")
                .setAttribute("WHEELP.UP")
                .build());
        defGBRs.add(new GameButtonBuilder(mContext)
                .setSize()
                .setMargin(0,235,130,0)
                .setKeyMap(MouseMap.MOUSEMAP_WHEEL_DOWN)
                .setKeyTypes(MOUSE_BUTTON)
                .setTheme(2,2,15,70,"#FFFFFF","#FFFFFF")
                .setAttribute("WHEELP.DOWN")
                .build());
        defGBRs.add(new GameButtonBuilder(mContext)
                .setSize()
                .setMargin(225,5,0,0)
                .setKeyMap(KeyMap.KEYMAP_KEY_ENTER)
                .setKeyTypes(KEYBOARD_BUTTON)
                .setTheme(2,4,15,70,"#FFFFFF","#FFFFFF")
                .setAttribute("ENTER")
                .build());
        defGBRs.add(new GameButtonBuilder(mContext)
                .setSize()
                .setMargin(5,60,0,0)
                .setKeyMap(KeyMap.KEYMAP_KEY_TAB)
                .setKeyTypes(KEYBOARD_BUTTON)
                .setTheme()
                .setAttribute("TAB")
                .build());
        defGBRs.add(new GameButtonBuilder(mContext)
                .setSize()
                .setMargin(60,60,0,0)
                .setKeyMap(KeyMap.KEYMAP_KEY_W, MouseMap.MOUSEMAP_BUTTON_LEFT)
                .setKeyTypes(KEYBOARD_BUTTON, MOUSE_BUTTON)
                .setTheme(2,4,15,70,"#FFFFFF","#FFFFFF")
                .setAttribute("CRAZY", true,false,GameButton.SHOW_IN_GAME)
                .build());
        defKR.setRecorderDatas(defGBRs.toArray(new GameButtonRecorder[0]));
        return defKR;
    }

    public class GameButtonBuilder {
        private final GameButtonRecorder gbr;
        private final Context mContext;
        private final float[] sizeDp;

        public GameButtonBuilder(Context context) {
            this.mContext = context;
            gbr = new GameButtonRecorder();
            sizeDp = new float[]{DisplayUtils.getDpFromPx(mContext, DisplayUtils.getDisplayWindowSize(mContext)[0]), DisplayUtils.getDpFromPx(mContext, DisplayUtils.getDisplayWindowSize(mContext)[1])};
        }

        public GameButtonRecorder build() {
            return this.gbr;
        }

        public GameButtonBuilder setMargin(int leftDp, int topDp, int rightDp, int bottomDp) {
            int resultLeft = 0, resultTop = 0;
            if (leftDp != 0) {
                resultLeft = leftDp;
            } else if (rightDp != 0) {
                resultLeft = (int) (sizeDp[0] - gbr.keySize[0] - rightDp);
            }

            if(topDp != 0){
                resultTop = topDp;
            } else if(bottomDp != 0){
                resultTop = (int)(sizeDp[1] - gbr.keySize[1] - bottomDp);
            }

            gbr.keyPos = new float[]{resultLeft, resultTop};
            return this;
        }

        public GameButtonBuilder setMargin(int left, int top, int right, int bottom, GameButtonRecorder gbr) {
            return setMargin(left, top, right, bottom);
        }

        public GameButtonBuilder setSize(int widthDp, int heightDp) {
            gbr.keySize = new float[]{widthDp, heightDp};
            return this;
        }

        public GameButtonBuilder setSize() {
            return setSize(50, 50);
        }

        public GameButtonBuilder setKeyMap(String... maps) {
            String[] KeyMap = new String[GameButton.MAX_KEYMAP_SIZE];
            for (String str : KeyMap) {
                str = "";
            }
            System.arraycopy(maps, 0, KeyMap, 0, maps.length);
            gbr.keyMaps = KeyMap;
            return this;
        }

        public GameButtonBuilder setKeyTypes(int... types) {
            int[] KeyTypes = new int[4];
            System.arraycopy(types, 0, KeyTypes, 0, types.length);
            gbr.keyTypes = KeyTypes;
            return this;
        }

        public GameButtonBuilder setTheme(int design, int textSize, int cornerRadius, int alphaSize, String textColor, String... colors) {
            gbr.designIndex = design;
            gbr.textSize = textSize;
            gbr.cornerRadius = cornerRadius;
            gbr.alphaSize = alphaSize;
            gbr.textColor = textColor;
            String[] Colors = new String[CkbThemeRecorder.COLOR_INDEX_LENGTH];
            for (String str : Colors) {
                str = "";
            }
            System.arraycopy(colors, 0, Colors, 0, colors.length);
            gbr.themeColors = Colors;
            return this;
        }

        public GameButtonBuilder setTheme() {
            return setTheme(2, 5, 15, 70, "#FFFFFF", "#FFFFFF");
        }

        public GameButtonBuilder setAttribute(String keyName, boolean isKeep, boolean isViewerFollow, int show) {
            gbr.keyName = keyName;
            gbr.isKeep = isKeep;
            gbr.isViewerFollow = isViewerFollow;
            gbr.show = show;
            return this;
        }

        public GameButtonBuilder setAttribute(String keyName) {
            return setAttribute(keyName, false, false, GameButton.SHOW_ALL);
        }

        public GameButtonBuilder setShow(int show){
            gbr.show = show;
            return this;
        }
    }

}
