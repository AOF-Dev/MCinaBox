package com.aof.mcinabox.utils;

import java.util.Locale;

public class LanguageUtils {
    public static boolean checkSystemSameAsLauncher(String launcherLan){
        if (Locale.getDefault().equals(getLocaleFromConfig(launcherLan))){
            return true;
        }else{
            return false;
        }
    }
    public static Locale getLocaleFromConfig(String config){
        Locale mLocale;
        switch (config){
            case "English(en)":
                mLocale = Locale.ENGLISH;
                break;
            case "日本語(ja)":
                mLocale = Locale.JAPANESE;
                break;
            case "简体中文(zh-CN)":
                mLocale = Locale.SIMPLIFIED_CHINESE;
                break;
            case "繁体中文(zh-TW)":
                mLocale = Locale.TRADITIONAL_CHINESE;
                break;
            case "Español(es)":
                mLocale = Locale.forLanguageTag("es");
                break;
            case "Русский(ru)":
                mLocale = Locale.forLanguageTag("ru");
                break;
            case "Brazilian(pt-BR)":
                mLocale = Locale.forLanguageTag("pt-rBR");
                break;
            case "한국어(ko-KR)":
                mLocale = Locale.forLanguageTag("ko-rKR");
                break;
            default:
                mLocale = Locale.getDefault();
                break;
        }
        return mLocale;
    }

}
