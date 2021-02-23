package com.aof.mcinabox.launcher.lang.support;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LanguageUtils {

    public final static String TAG_SYSTEM = "System";
    public final static String TAG_ENGLISH_EN = "English(en)";
    public final static String TAG_JAPANESE_JA = "日本語(ja)";
    public final static String TAG_CHINESE_ZH_CN = "简体中文(zh-CN)";
    public final static String TAG_CHINESE_ZH_TW = "繁体中文(zh-TW)";
    public final static String TAG_SPANISH_ES = "Español(es)";
    public final static String TAG_PORTUGUESE_RU = "Русский(ru)";
    public final static String TAG_BRAZILIAN_PT_BR = "Brazilian(pt-BR)";
    public final static String TAG_KOREAN_KO_KR = "한국어(ko-KR)";

    /*
     * if you want to add a new language
     * create a TAG for the language
     * and add the TAG to #LANG_TAGS
     */
    public final static String[] LANG_TAGS = new String[]{TAG_SYSTEM, TAG_ENGLISH_EN, TAG_JAPANESE_JA, TAG_CHINESE_ZH_CN,
            TAG_CHINESE_ZH_TW, TAG_SPANISH_ES, TAG_PORTUGUESE_RU, TAG_BRAZILIAN_PT_BR, TAG_KOREAN_KO_KR};

    public static Locale getLocaleFromConfig(String config){
        Locale mLocale;
        switch (config){
            case TAG_SYSTEM:
                mLocale = Locale.getDefault();
                break;
            case TAG_ENGLISH_EN:
                mLocale = Locale.ENGLISH;
                break;
            case TAG_JAPANESE_JA:
                mLocale = Locale.JAPANESE;
                break;
            case TAG_CHINESE_ZH_CN:
                mLocale = Locale.SIMPLIFIED_CHINESE;
                break;
            case TAG_CHINESE_ZH_TW:
                mLocale = Locale.TRADITIONAL_CHINESE;
                break;
            case TAG_SPANISH_ES:
                mLocale = Locale.forLanguageTag("es");
                break;
            case TAG_PORTUGUESE_RU:
                mLocale = Locale.forLanguageTag("ru");
                break;
            case TAG_BRAZILIAN_PT_BR:
                mLocale = Locale.forLanguageTag("pt-rBR");
                break;
            case TAG_KOREAN_KO_KR:
                mLocale = Locale.KOREAN;
                break;
            default:
                mLocale = null;
                break;
        }
        return mLocale;
    }

    public static void switchLang(Context context, Locale locale){
        if(locale == null){
            return;
        }
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = locale;
        config.setLayoutDirection(locale);
        resources.updateConfiguration(config, dm);
    }

}
