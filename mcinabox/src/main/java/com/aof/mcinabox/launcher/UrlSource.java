package com.aof.mcinabox.launcher;

import android.util.Log;

import java.util.HashMap;

public class UrlSource {
    public HashMap<String ,HashMap<String,String>> SourceMap;

    public UrlSource(){
        initSourceMap(new String[][] {
                //官方下载源
                {"official","version_manifest_json","https://launchermeta.mojang.com/mc/game/version_manifest.json"},
                {"official","version_json","https://launchermeta.mojang.com"},
                {"official","version_jar","https://launcher.mojang.com"},
                {"official","assetsIndex_json","https://launchermeta.mojang.com"},
                {"official","assets","http://resources.download.minecraft.net"},
                {"official","libraries","https://libraries.minecraft.net"},
                {"official","forge","https://files.minecraftforge.net/maven"},
                {"official","liteloader_version_json","http://dl.liteloader.com/versions/versions.json"},
                {"official","optifine",""},
                //BMCLAPI下载源
                {"bmclapi","version_manifest_json","https://bmclapi2.bangbang93.com/mc/game/version_manifest.json"},
                {"bmclapi","version_json","https://bmclapi2.bangbang93.com"},
                {"bmclapi","version_jar","https://bmclapi2.bangbang93.com"},
                {"bmclapi","assetsIndex_json","https://bmclapi2.bangbang93.com"},
                {"bmclapi","assets","https://bmclapi2.bangbang93.com/assets"},
                {"bmclapi","libraries","https://bmclapi2.bangbang93.com/maven"},
                {"bmclapi","forge","https://bmclapi2.bangbang93.com/maven"},
                {"bmclapi","liteloader_version_json","https://bmclapi.bangbang93.com/maven/com/mumfrey/liteloader/versions.json"},
                //MCBBS下载源
                {"mcbbs","version_manifest_json","https://download.mcbbs.net/mc/game/version_manifest.json"},
                {"mcbbs","version_json","https://download.mcbbs.net"},
                {"mcbbs","version_jar","https://download.mcbbs.net"},
                {"mcbbs","assetsIndex_json","https://download.mcbbs.net"},
                {"mcbbs","assets","https://download.mcbbs.net/assets"},
                {"mcbbs","libraries","https://download.mcbbs.net/maven"},
                {"mcbbs","forge","https://download.mcbbs.net/maven"},
                {"mcbbs","liteloader_version_json","https://download.mcbbs.net/maven/com/mumfrey/liteloader/versions.json"},
        });
    }

    //String ... {{sourceName,Type,Url}, ...}
    private void initSourceMap(String[][] originMap){
        SourceMap = new HashMap<String, HashMap<String, String>>();
        for(String[] couple : originMap){
            if(SourceMap.containsKey(couple[0])){
                SourceMap.get(couple[0]).put(couple[1],couple[2]);
            }else{
                HashMap<String,String> tmp = new HashMap<String, String>();
                tmp.put(couple[1],couple[2]);
                SourceMap.put(couple[0],tmp);
            }
        }
    }

    public String getSourceUrl(String sourceName,String type){
        Log.e("SourceUrl","下载源:"+sourceName+" 类型:"+type);
        return SourceMap.get(sourceName).get(type);
    }
    public String getFileUrl(String originUrl,String sourceName, String type){
        String convertedUrl = "";
        String Str1 = "";
        String Str2 = getSourceUrl("official",type);
        for(int i = Str2.length(); i < originUrl.length() ; i++){
            Str1 = Str1 + originUrl.charAt(i);
        }
        convertedUrl = getSourceUrl(sourceName,type) + Str1;
        return convertedUrl;
    }
}
