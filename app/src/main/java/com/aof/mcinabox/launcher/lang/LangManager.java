package com.aof.mcinabox.launcher.lang;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.launcher.lang.support.LanguageUtils;

public class LangManager {
    private final static String TAG = " LangManager";
    private final static String spFileName = "lang";
    private final static int spMode = Context.MODE_PRIVATE;
    private final static String sp_lang_tag = "lang";
    private static boolean hasFitted = false;

    private final Context mContext;

    public LangManager(Context context){
        super();
        this.mContext = context;
    }

    public boolean fitSystemLang(){
        if(hasFitted){
            hasFitted = false;
            return true;
        }
        SharedPreferences sp = mContext.getSharedPreferences(spFileName,spMode);
        String langData = sp.getString(sp_lang_tag, LanguageUtils.TAG_SYSTEM);
        LanguageUtils.switchLang( mContext,LanguageUtils.getLocaleFromConfig(langData));
        hasFitted = true;
        Log.e(TAG, "fitted");
        restartActivity();
        return false;
    }

    public void switchLang(@NonNull String tag){
        boolean included = false;
        for(String str : LanguageUtils.LANG_TAGS){
            if (str.equals(tag)) {
                included = true;
                break;
            }
        }
        if(!included){
            return;
        }
        LanguageUtils.switchLang(mContext,LanguageUtils.getLocaleFromConfig(tag));
        SharedPreferences.Editor editor = mContext.getSharedPreferences(spFileName,spMode).edit();
        editor.putString(sp_lang_tag,tag);
        editor.apply();
        restartActivity();
    }

    private void restartActivity(){
        OldMainActivity.CURRENT_ACTIVITY.get().restarter();
    }
}
