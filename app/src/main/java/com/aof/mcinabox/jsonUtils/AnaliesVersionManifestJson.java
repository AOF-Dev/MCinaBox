package com.aof.mcinabox.jsonUtils;

import android.widget.Toast;

import com.aof.mcinabox.DownloadMinecraft;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class AnaliesVersionManifestJson {
    //解析version_manifest.
    public ListVersionManifestJson.Version[] getVersionList(DownloadMinecraft downloadTask) {
        try {
            //将version_manifest.json文件加入输入流
            InputStream inputStream = new FileInputStream(new File(downloadTask.getMINECRAFT_TEMP() + "version_manifest.json"));
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            //使用Gson将ListVersionManifestJson实例化
            ListVersionManifestJson listVersionManifestJson = gson.fromJson(reader, ListVersionManifestJson.class);
            ListVersionManifestJson.Version[] result = listVersionManifestJson.versions;
            return result;

        } catch (
                FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
