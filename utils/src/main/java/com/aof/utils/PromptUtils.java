package com.aof.utils;

import android.content.Context;
import android.widget.Toast;

public class PromptUtils {
    public static void createPrompt(Context context, String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
