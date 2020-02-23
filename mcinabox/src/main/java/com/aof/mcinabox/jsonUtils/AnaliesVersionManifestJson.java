package com.aof.mcinabox.jsonUtils;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class AnaliesVersionManifestJson {
    //解析version_manifest.json
    public ListVersionManifestJson.Version[] getVersionList(String filePath) {
        try {
            //将version_manifest.json文件加入输入流
            File file = new File(filePath);
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            //使用Gson将ListVersionManifestJson实例化
            ListVersionManifestJson listVersionManifestJson = gson.fromJson(reader, ListVersionManifestJson.class);
            ListVersionManifestJson.Version[] result = listVersionManifestJson.getVersions();
            return result;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
