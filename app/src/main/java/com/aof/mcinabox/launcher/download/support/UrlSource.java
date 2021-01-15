package com.aof.mcinabox.launcher.download.support;

import com.aof.mcinabox.launcher.setting.support.SettingJson;

import java.util.HashMap;
import java.util.Objects;

public class UrlSource {

    public HashMap<String ,HashMap<String,String>> SourceMap;

    public final static String TYPE_VERSION_MANIFEST = "version_manifest_json";
    public final static String TYPE_VERSION_JSON = "version_json";
    public final static String TYPE_VERSION_JAR = "version_jar";
    public final static String TYPE_ASSETS_INDEX_JSON = "assetsIndex_json";
    public final static String TYPE_ASSETS_OBJS = "assets";
    public final static String TYPE_LIBRARIES = "libraries";
    public final static String TYPE_FORGE_LIBRARIES = "forge";
    public final static String TYPE_FABRIC_LIBRARIES = "fabric";
    public final static String TYPE_LITELOADER_VERSION_JSON = "liteloader_version_json";
    public final static String TYPE_AUTHLIB_INJECTOR_JAR = "authlib_injector_jar";

    public UrlSource(){
        initSourceMap(new String[][] {
                //官方下载源
                {SettingJson.DOWNLOAD_SOURCE_OFFICIAL,TYPE_VERSION_MANIFEST,"https://launchermeta.mojang.com/mc/game/version_manifest.json"},
                {SettingJson.DOWNLOAD_SOURCE_OFFICIAL,TYPE_VERSION_JSON,"https://launchermeta.mojang.com"},
                {SettingJson.DOWNLOAD_SOURCE_OFFICIAL,TYPE_VERSION_JAR,"https://launcher.mojang.com"},
                {SettingJson.DOWNLOAD_SOURCE_OFFICIAL,TYPE_ASSETS_INDEX_JSON,"https://launchermeta.mojang.com"},
                {SettingJson.DOWNLOAD_SOURCE_OFFICIAL,TYPE_ASSETS_OBJS,"http://resources.download.minecraft.net"},
                {SettingJson.DOWNLOAD_SOURCE_OFFICIAL,TYPE_LIBRARIES,"https://libraries.minecraft.net"},
                {SettingJson.DOWNLOAD_SOURCE_OFFICIAL,TYPE_FORGE_LIBRARIES,"https://files.minecraftforge.net/maven"},
                {SettingJson.DOWNLOAD_SOURCE_OFFICIAL,TYPE_LITELOADER_VERSION_JSON,"http://dl.liteloader.com/versions/versions.json"},
                {SettingJson.DOWNLOAD_SOURCE_OFFICIAL,TYPE_AUTHLIB_INJECTOR_JAR,"https://authlib-injector.yushi.moe/artifact/latest.json"},
                {SettingJson.DOWNLOAD_SOURCE_OFFICIAL,TYPE_FABRIC_LIBRARIES,"https://maven.fabricmc.net"},
                //BMCLAPI下载源
                {SettingJson.DOWNLOAD_SOURCE_BMCLAPI,TYPE_VERSION_MANIFEST,"https://bmclapi2.bangbang93.com/mc/game/version_manifest.json"},
                {SettingJson.DOWNLOAD_SOURCE_BMCLAPI,TYPE_VERSION_JSON,"https://bmclapi2.bangbang93.com"},
                {SettingJson.DOWNLOAD_SOURCE_BMCLAPI,TYPE_VERSION_JAR,"https://bmclapi2.bangbang93.com"},
                {SettingJson.DOWNLOAD_SOURCE_BMCLAPI,TYPE_ASSETS_INDEX_JSON,"https://bmclapi2.bangbang93.com"},
                {SettingJson.DOWNLOAD_SOURCE_BMCLAPI,TYPE_ASSETS_OBJS,"https://bmclapi2.bangbang93.com/assets"},
                {SettingJson.DOWNLOAD_SOURCE_BMCLAPI,TYPE_LIBRARIES,"https://bmclapi2.bangbang93.com/maven"},
                {SettingJson.DOWNLOAD_SOURCE_BMCLAPI,TYPE_FORGE_LIBRARIES,"https://bmclapi2.bangbang93.com/maven"},
                {SettingJson.DOWNLOAD_SOURCE_BMCLAPI,TYPE_LITELOADER_VERSION_JSON,"https://bmclapi.bangbang93.com/maven/com/mumfrey/liteloader/versions.json"},
                {SettingJson.DOWNLOAD_SOURCE_BMCLAPI,TYPE_AUTHLIB_INJECTOR_JAR,"https://bmclapi2.bangbang93.com/mirrors/authlib-injector/artifact/latest.json"},
                //TODO: BMCLAPI 截至目前，没有提供Fabric的镜像
                {SettingJson.DOWNLOAD_SOURCE_BMCLAPI,TYPE_AUTHLIB_INJECTOR_JAR,"https://authlib-injector.yushi.moe/artifact/latest.json"},
                //MCBBS下载源
                {SettingJson.DOWNLOAD_SOURCE_MCBBS,TYPE_VERSION_MANIFEST,"https://download.mcbbs.net/mc/game/version_manifest.json"},
                {SettingJson.DOWNLOAD_SOURCE_MCBBS,TYPE_VERSION_JSON,"https://download.mcbbs.net"},
                {SettingJson.DOWNLOAD_SOURCE_MCBBS,TYPE_VERSION_JAR,"https://download.mcbbs.net"},
                {SettingJson.DOWNLOAD_SOURCE_MCBBS,TYPE_ASSETS_INDEX_JSON,"https://download.mcbbs.net"},
                {SettingJson.DOWNLOAD_SOURCE_MCBBS,TYPE_ASSETS_OBJS,"https://download.mcbbs.net/assets"},
                {SettingJson.DOWNLOAD_SOURCE_MCBBS,TYPE_LIBRARIES,"https://download.mcbbs.net/maven"},
                {SettingJson.DOWNLOAD_SOURCE_MCBBS,TYPE_FORGE_LIBRARIES,"https://download.mcbbs.net/maven"},
                {SettingJson.DOWNLOAD_SOURCE_MCBBS,TYPE_LITELOADER_VERSION_JSON,"https://download.mcbbs.net/maven/com/mumfrey/liteloader/versions.json"},
                {SettingJson.DOWNLOAD_SOURCE_MCBBS,TYPE_AUTHLIB_INJECTOR_JAR,"https://download.mcbbs.net/mirrors/authlib-injector/artifact/latest.json"},
                //TODO: MCBBS 截至目前，没有提供Fabric的镜像
                {SettingJson.DOWNLOAD_SOURCE_MCBBS,TYPE_AUTHLIB_INJECTOR_JAR,"https://authlib-injector.yushi.moe/artifact/latest.json"},
        });
    }

    //String ... {{sourceName,Type,Url}, ...}
    private void initSourceMap(String[][] originMap){
        SourceMap = new HashMap<>();
        for(String[] couple : originMap){
            if(SourceMap.containsKey(couple[0])){
                Objects.requireNonNull(SourceMap.get(couple[0])).put(couple[1],couple[2]);
            }else{
                HashMap<String,String> tmp = new HashMap<>();
                tmp.put(couple[1],couple[2]);
                SourceMap.put(couple[0],tmp);
            }
        }
    }

    public String getSourceUrl(String sourceName,String type){
        return Objects.requireNonNull(SourceMap.get(sourceName)).get(type);
    }
    public String getFileUrl(String originUrl,String sourceName, String type){
        String convertedUrl;
        StringBuilder Str1 = new StringBuilder();
        String Str2 = getSourceUrl(SettingJson.DOWNLOAD_SOURCE_OFFICIAL,type);
        for(int i = Str2.length(); i < originUrl.length() ; i++){
            Str1.append(originUrl.charAt(i));
        }
        convertedUrl = getSourceUrl(sourceName,type) + Str1;
        return convertedUrl;
    }
}
