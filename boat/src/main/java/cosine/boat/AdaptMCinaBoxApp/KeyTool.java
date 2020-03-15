package cosine.boat.AdaptMCinaBoxApp;

import android.util.Log;

import java.security.Key;

import static org.lwjgl.glfw.GLFW.*;

public class KeyTool {

    /**
     * 【键值索引】
     **/
    //根据BoatApp所使用的键值表对GameButton进行键值的设定
    public static int IndexKeyMap(String KeyName) {
        int KeyIndex;
        //基本按键采用首项索引
        int NUM_ZERO = GLFW_KEY_0;
        int KEY_A = GLFW_KEY_A;
        int KEY_F1 = GLFW_KEY_F1;
        int KEY_UNKNOW = GLFW_KEY_UNKNOWN;
        //特殊按键完全匹配索引

        Log.e("KeyTool","长度 "+ KeyName.length() + " 首项" + KeyName.charAt(0));
        if (KeyName.length() == 1 && (KeyName.charAt(0) >= '0' && KeyName.charAt(0) <= '9')) {
            return (KeyName.toCharArray()[0] - '0' + NUM_ZERO);
        } else if (KeyName.length() == 1 && (KeyName.charAt(0) >= 'A' && KeyName.charAt(0) <= 'Z')) {
            return (KeyName.toCharArray()[0] - 'A' + KEY_A);
        } else if ((KeyName.length() >= 2 && KeyName.charAt(0) == 'F') && (KeyName.charAt(1) >= '1' && KeyName.charAt(1) <= '9')) {
            return (KeyName.charAt(1) - '1' + KEY_F1);
        } else {
            switch (KeyName) {
                case "ESC":
                    return GLFW_KEY_ESCAPE;
                case "TAB":
                    return GLFW_KEY_TAB;
                case "SPACE":
                    return GLFW_KEY_SPACE;
                case "LSHIFT":
                    return GLFW_KEY_LEFT_SHIFT;
                case "LALT":
                    return GLFW_KEY_LEFT_ALT;
                case "LCTRL":
                    return  GLFW_KEY_LEFT_CONTROL;
                case "RSHIFT":
                    return GLFW_KEY_RIGHT_SHIFT;
                case "RALT":
                    return  GLFW_KEY_RIGHT_ALT;
                case "RCTRL":
                    return  GLFW_KEY_RIGHT_CONTROL;
                default:
                    return KEY_UNKNOW;
            }
        }

    }

}
