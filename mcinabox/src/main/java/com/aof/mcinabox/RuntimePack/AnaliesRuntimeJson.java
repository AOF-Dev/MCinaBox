package com.aof.mcinabox.RuntimePack;

import android.content.Context;
import android.util.Log;

import com.aof.mcinabox.R;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import static com.aof.sharedmodule.Data.DataPathManifest.*;

public class AnaliesRuntimeJson {
    public RuntimeModel GetRuntimeModel(String filePath) {
        try {
            File file = new File(filePath);
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            return gson.fromJson(reader, RuntimeModel.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("GetRuntimeModel ",e.toString());
        }
        return null;
    }
    public String GetInformation(Context context){
        RuntimeModel info = GetRuntimeModel(RUNTIME_HOME + "/pack.json");
        if(info == null){
            return "";
        }else{
            return ( context.getString(R.string.title_runtime_package_name) + " " + info.getPackName() + "\n" +
                    context.getString(R.string.title_runtime_package_releasetime) + " " + info.getReleaseTime() + "\n" +
                    context.getString(R.string.title_runtime_package_platform) + " " + info.getPlatform() + "\n" +
                    context.getString(R.string.title_runtime_java_version) + " " + info.getJavaVersion() + "\n" +
                    context.getString(R.string.title_runtime_opengl_version) + " " + info.getOpenGLVersion() + "\n" +
                    context.getString(R.string.title_runtime_openal_version) + " " + info.getOpenALVersion() + "\n" +
                    context.getString(R.string.title_runtime_lwjgl_version) + " " + info.getLwjgl2Version() + " " +  info.getLwjgl3Version() + "\n"
                    );
        }
    }
}
