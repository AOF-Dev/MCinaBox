package com.aof.mcinabox.plugin;

import android.app.Activity;
import android.view.View;

import java.util.HashMap;

public class Plugin {

}

interface MCinaBoxPlugin{
    //Life circle
    void launchPlugin(Activity context); //Plugin入口方法
    void stopPlugin(Activity context);  //Plugin停止方法
    boolean deployPlugin(Activity context);  //Plugin装载方法
    boolean removePlugin(Activity context);  //Plugin移除方法
}

interface BoatPlugin extends MCinaBoxPlugin{
    //传入BoatClientActivity中的控制器引用的数组
    void registerControler (HashMap<String, View> defControler,Activity context);
}
