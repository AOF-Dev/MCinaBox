package com.aof.mcinabox;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.aof.mcinabox.downloadUtils.Downloader;
import com.aof.mcinabox.jsonUtils.ModelMinecraftVersionJson;

import java.io.File;

public class DownloadMinecraft {

    //构造函数
    public DownloadMinecraft(String url,String assetsUrl,String home) {
        super();

        MCinaBox_Home = home;
        MINECRAFT_URL = url;
        MINECRAFT_ASSETS_URL =assetsUrl;
        MINECRAFT_DIR = MCinaBox_Home + "/.minecraft/";
        MINECRAFT_TEMP = MINECRAFT_DIR + "Temp/";
        MINECRAFT_VERSION_DIR = MINECRAFT_DIR + "versions/";
        MINECRAFT_LIBRARIES_DIR = MINECRAFT_DIR + "libraries/";
        MINECRAFT_ASSETS_DIR = MINECRAFT_DIR+"assets/";
        VERSION_MANIFEST_URL = MINECRAFT_URL + "/mc/game/version_manifest.json";

        if(MCinaBox_Home.equals("/sdcard/MCinaBox")){
            DOWNLOAD_DIR = "/MCinaBox/.minecraft/";
        }else{
            DOWNLOAD_DIR = "/Android/data/com.aof.mcinabox/files/MCinaBox/.minecraft/";
        }

        Log.e("下载路径 ",DOWNLOAD_DIR);
        DOWNLOAD_TEMP = DOWNLOAD_DIR + "Temp/";
        DOWNLOAD_VERSION_DIR = DOWNLOAD_DIR + "versions/";
        DOWNLOAD_LIBRARIES_DIR = DOWNLOAD_DIR + "libraries/";
        DOWNLOAD_ASSETS_DIR = DOWNLOAD_DIR + "assets/";
    }

    //全局目录
    String MCinaBox_Home;

    //下列路径定义为绝对路径
    private String MINECRAFT_URL; //Minecraft 的 版本清单 version.json assetsIndex.json 下载地址
    private String MINECRAFT_ASSETS_URL; //Minecraft 的 assets资源 下载地址
    private String MINECRAFT_DIR; //Minecraft本地路径
    private String MINECRAFT_TEMP; //Minecraft临时目录-用于保存其他文件
    private String MINECRAFT_VERSION_DIR; //Minecraft的version文件夹路径
    private String MINECRAFT_LIBRARIES_DIR; //Minecraft的libraries文件夹路径
    private String MINECRAFT_ASSETS_DIR; //Minecraft的assets文件夹路径

    //下列路径定义为缺省/sdcard的路径
    private String DOWNLOAD_DIR; //Minecraft下载保存路径
    private String DOWNLOAD_TEMP; //Minecraft下载临时保存路径
    private String DOWNLOAD_VERSION_DIR; //Minecraft版本保存路径
    private String DOWNLOAD_LIBRARIES_DIR; //Minecraft依赖库的保存路径
    private String DOWNLOAD_ASSETS_DIR; //Minecraft资源的保存路径


    //部分文件
    private String VERSION_MANIFEST_URL; //version_manifest.json文件下载地址

    //Getter and Setter
    public String getMINECRAFT_ASSETS_URL() { return MINECRAFT_ASSETS_URL; }
    public void setMINECRAFT_ASSETS_URL(String MINECRAFT_ASSETS_URL) { this.MINECRAFT_ASSETS_URL = MINECRAFT_ASSETS_URL; }
    public void setMINECRAFT_LIBRARIES_DIR(String MINECRAFT_LIBRARIES_DIR) { this.MINECRAFT_LIBRARIES_DIR = MINECRAFT_LIBRARIES_DIR; }
    public String getMINECRAFT_ASSETS_DIR() { return MINECRAFT_ASSETS_DIR;}
    public void setMINECRAFT_ASSETS_DIR(String MINECRAFT_ASSETS_DIR) { this.MINECRAFT_ASSETS_DIR = MINECRAFT_ASSETS_DIR; }
    public String getDOWNLOAD_ASSETS_DIR() { return DOWNLOAD_ASSETS_DIR;}
    public void setDOWNLOAD_ASSETS_DIR(String DOWNLOAD_ASSETS_DIR) { this.DOWNLOAD_ASSETS_DIR = DOWNLOAD_ASSETS_DIR; }
    public String getMCinaBox_Home() { return MCinaBox_Home; }
    public void setMCinaBox_Home(String MCinaBox_Home) { this.MCinaBox_Home = MCinaBox_Home; }
    public String getMINECRAFT_TEMP(){ return MINECRAFT_TEMP; }
    public void setMINECRAFT_TEMP(String MINECRAFT_TEMP){this.MINECRAFT_TEMP = MINECRAFT_TEMP;}
    public String getMINECRAFT_URL() { return MINECRAFT_URL; }
    public void setMINECRAFT_URL(String MINECRAFT_URL) { this.MINECRAFT_URL = MINECRAFT_URL; }
    public String getVERSION_MANIFEST_URL() { return VERSION_MANIFEST_URL; }
    public void setVERSION_MANIFEST_URL(String VERSION_MANIFEST_URL) { this.VERSION_MANIFEST_URL = VERSION_MANIFEST_URL; }
    public String getDOWNLOAD_TEMP() { return DOWNLOAD_TEMP; }
    public void setDOWNLOAD_TEMP(String DOWNLOAD_TEMP) { this.DOWNLOAD_TEMP = DOWNLOAD_TEMP; }
    public String getDOWNLOAD_DIR() { return DOWNLOAD_DIR; }
    public void setDOWNLOAD_DIR(String DOWNLOAD_DIR) { this.DOWNLOAD_DIR = DOWNLOAD_DIR; }
    public String getMINECRAFT_DIR() { return MINECRAFT_DIR; }
    public void setMINECRAFT_DIR(String MINECRAFT_DIR) { this.MINECRAFT_DIR = MINECRAFT_DIR; }
    public String getDOWNLOAD_VERSION_DIR() { return DOWNLOAD_VERSION_DIR; }
    public void setDOWNLOAD_VERSION_DIR(String DOWNLOAD_VERSION_DIR) { this.DOWNLOAD_VERSION_DIR = DOWNLOAD_VERSION_DIR; }
    public String getMINECRAFT_VERSION_DIR() { return MINECRAFT_VERSION_DIR; }
    public void setMINECRAFT_VERSION_DIR(String MINECRAFT_VERSION_DIR) { this.MINECRAFT_VERSION_DIR = MINECRAFT_VERSION_DIR; }
    public String getMINECRAFT_LIBRARIES_DIR() {return MINECRAFT_LIBRARIES_DIR;}
    public void setDOWNLOAD_LIBRARIES_DIR(String DOWNLOAD_LIBRARIES_DIR){this.DOWNLOAD_LIBRARIES_DIR = DOWNLOAD_LIBRARIES_DIR;}
    public String getDOWNLOAD_LIBRARIES_DIR() {return DOWNLOAD_LIBRARIES_DIR;}

    //!!!传入缺省/sdcard的相对路径!!!
    public void setInformation(String downloadType, String b,String home){
        //设置下载器参数
        this.setMINECRAFT_URL(downloadType);
        this.setDOWNLOAD_DIR(b);
        this.setDOWNLOAD_VERSION_DIR(getDOWNLOAD_DIR()+"versions/");
        this.setDOWNLOAD_TEMP(getDOWNLOAD_DIR()+"Temp/");
        this.setMINECRAFT_DIR("/sdcard"+getDOWNLOAD_DIR());
        this.setMINECRAFT_TEMP(getMINECRAFT_DIR()+"Temp/");
        this.setMINECRAFT_VERSION_DIR(getMINECRAFT_DIR()+"versions/");
        this.setMCinaBox_Home(home);
    }


    //下载或更新Minecraft的版本信息文件version_manifest.json
    public long UpdateVersionManifestJson(Context context){
        String fileUrl = getMINECRAFT_URL() + "/mc/game/version_manifest.json";
        String fileName = "version_manifest.json";
        String savePath = getDOWNLOAD_TEMP();
        String filePath = getMINECRAFT_TEMP()+fileName;
        long taskId;


        //Toast.makeText(context,"执行版本信息更新",Toast.LENGTH_LONG).show();

        //先判断文件是否存在
        //若存在则删掉再下载
        File file=new File(filePath);
        if(file.exists()){
            file.delete();
        }
        //执行下载操作
        Downloader downloader = new Downloader();
        taskId = downloader.FileDownloader(context,savePath,fileName,fileUrl);

        return taskId;
    }

    //用于下载或更新version.json
    public long DownloadMinecraftVersionJson(String id,String url,Context context){
        String fileUrl = url;
        String fileName = id + ".json";
        String savePath = getDOWNLOAD_VERSION_DIR()+id+"/";
        String filePath = getMINECRAFT_VERSION_DIR()+fileName;
        long taskId;

        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
        Downloader downloader = new Downloader();
        taskId = downloader.FileDownloader(context,savePath,fileName,fileUrl);
        return taskId;
    }
    //用于下载version jar
    public long DownloadMinecraftJar(String id,String url,Context context){
        String fileUrl = url;
        String fileName = id + ".jar";
        String savePath = getDOWNLOAD_VERSION_DIR() + id + "/";
        String filePath = getMINECRAFT_VERSION_DIR() + fileName;
        long taskId;

        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
        Downloader downloade = new Downloader();
        taskId = downloade.FileDownloader(context,savePath,fileName,fileUrl);
        return taskId;

    }
    //用于下载或更新Minecraft依赖库
    public long DownloadMinecraftDependentLibraries(String path,String url,Context context){
        String fileUrl = url;
        String filePath;
        String savePath = getDOWNLOAD_LIBRARIES_DIR();
        long taskId =0 ;

        //TODO:格式化传递的字符串，使其符合下载器的接受标准
        //TODO:功能测试

        String editSavePath = "";
        String editFileName = "";

        int targetChar = 0;
        int a = 0;
        for(int i = 0;i<=path.length()-1;i++){
            if(path.charAt(i) == '/'){
                targetChar = i;
            }
        }
        //注：targetChar就是最后一个/的位置，在这之前的是路径，在这之后的是文件名
        for(a = 0;a <= targetChar -1;a++){
            editSavePath += path.charAt(a);
        }
        for(a=targetChar+1;a<=path.length()-1;a++){
            editFileName += path.charAt(a);
        }

        editSavePath = getDOWNLOAD_LIBRARIES_DIR() + editSavePath +"/";
        filePath = getMINECRAFT_LIBRARIES_DIR() + editSavePath + editFileName;

        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }

        //Toast.makeText(context,"开始下载 "+editFileName,Toast.LENGTH_SHORT).show();

        Downloader downloader = new Downloader();
        taskId = downloader.FileDownloader(context,editSavePath,editFileName,fileUrl);

        return taskId;
    }

    public long DownloadMinecraftAssetJson(String Id,String url,Context context){
        String fileUrl = url;
        String fileName = Id+".json";
        String filePath = getMINECRAFT_ASSETS_DIR() + "objects/indexes/" + fileName;
        String savePath = getDOWNLOAD_ASSETS_DIR() + "objects/indexes/";
        long taskId;

        Downloader downloader = new Downloader();
        taskId = downloader.FileDownloader(context,savePath,fileName,fileUrl);
        return taskId;
    }

    public long DownloadMinecraftAssetFile(String hashCode,Context context){
        String tip = "";
        for(int i=0;i<2;i++){
            tip = tip + hashCode.charAt(i);
        }
        String fileName = hashCode;
        String fileUrl = MINECRAFT_ASSETS_URL + "/" + tip + "/" + fileName;
        String filePath = getMINECRAFT_ASSETS_DIR() + "objects/" + tip + "/" + fileName;
        String savePath = getDOWNLOAD_ASSETS_DIR() + "objects/" + tip + "/";
        long taskId;

        Downloader downloader = new Downloader();
        taskId = downloader.FileDownloader(context,savePath,fileName,fileUrl);
        return taskId;
    }
}
