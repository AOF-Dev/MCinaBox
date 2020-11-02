package com.aof.mcinabox.launcher.uis.support;

import android.widget.Spinner;

public class Utils {

    /**
     * 【匹配字符串在spinner中的位置】
     **/
    public static int getItemPosByString(String str, Spinner spinner) {
        int count = spinner.getAdapter().getCount();
        for (int a = 0; a < count; a++) {
            if (spinner.getItemAtPosition(a).equals(str)) {
                return a;
            }
        }
        return -1;
    }

}
