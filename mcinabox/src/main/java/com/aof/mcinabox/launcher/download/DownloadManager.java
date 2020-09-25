package com.aof.mcinabox.launcher.download;

import android.content.Context;
import android.util.Log;
import com.aof.mcinabox.launcher.download.support.DownloadSupport;
import com.aof.mcinabox.launcher.download.support.DownloaderDialog;
import com.aof.utils.FormatUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import java.util.ArrayList;

public class DownloadManager {

    private DownloadSupport mSupport;
    private Context mContext;
    private FileDownloadQueueSet mQueueSet;
    private DownloaderDialog mDialog;

    public final static int DOWNLOAD_PRESET_MANIFEST = 0;
    public final static int DOWNLOAD_PRESET_VERSION_JSON = 1;
    public final static int DOWNLOAD_PRESET_VERSION_LIBS = 2;
    public final static int DOWNLOAD_PRESET_VERSION_JAR = 3;
    public final static int DOWNLOAD_PRESET_ASSETS_INDEX = 4;
    public final static int DOWNLOAD_PRESET_ASSETS_OBJS = 5;
    public final static int DOWNLOAD_FORGE_LIBS = 6;

    private final static String TAG = "DownloadManager";

    private int currentPresetId; //当前预设id值
    private String currentVersionId; //当前版本名称
    private int taskCounts; //当前任务总数
    private int taskFinished = 0; //当前完成的任务数
    private boolean enablePreset; //启动预设下载模式

    public DownloadManager(Context context) {
        this.mSupport = new DownloadSupport();
        this.mContext = context;
        this.mDialog = new DownloaderDialog(mContext, this);
        FileDownloader.setup(mContext);
    }

    /**
     * [启动预设下载任务]
     **/
    private ArrayList<BaseDownloadTask> tasks = new ArrayList<>();

    public void startPresetDownload(int presetId, String id) {
        if(! this.enablePreset) {
            this.enablePreset = true;
        }
        this.currentPresetId = presetId;
        this.currentVersionId = id;
        this.taskFinished = 0;
        tasks.clear();
        String pgName;
        int all, current;
        switch (presetId) {
            case DOWNLOAD_PRESET_MANIFEST:
                tasks.add(mSupport.createVersionManifestDownloadTask());
                pgName = "正在下载清单文件...";
                all = 1;
                current = 1;
                break;
            case DOWNLOAD_PRESET_VERSION_JSON:
                this.mDialog = new DownloaderDialog(mContext,this);
                tasks.add(mSupport.createVersionJsonDownloadTask(id));
                pgName = "正在下载版本信息...";
                all = 5;
                current =1;
                break;
            case DOWNLOAD_PRESET_VERSION_LIBS:
                tasks.addAll(mSupport.createLibrariesDownloadTask(id));
                pgName = "正在下载游戏依赖库...";
                all = 5;
                current = 2;
                break;
            case DOWNLOAD_PRESET_VERSION_JAR:
                tasks.add(mSupport.createVersionJarDownloadTask(id));
                pgName = "正在下载游戏主文件...";
                all = 5;
                current = 3;
                break;
            case DOWNLOAD_PRESET_ASSETS_INDEX:
                tasks.add(mSupport.createAssetIndexDownloadTask(id));
                pgName = "正在下载游戏资源文件信息...";
                all = 5;
                current = 4;
                break;
            case DOWNLOAD_PRESET_ASSETS_OBJS:
                tasks.addAll(mSupport.createAssetObjectsDownloadTask(id));
                pgName = "正在下载游戏资源文件...";
                all = 5;
                current = 5;
                break;
            case DOWNLOAD_FORGE_LIBS:
                tasks.addAll(mSupport.createForgeDownloadTask(id));
                pgName = "正在下载Forge依赖库...";
                all = 1;
                current = 1;
                break;
            default:
                return;
        }
        this.taskCounts = tasks.size();
        Log.e(TAG,"创建任务总数: " + tasks.size());
        for(BaseDownloadTask task : tasks){
            Log.e(TAG,"创建任务: " + task.getFilename());
        }
        mQueueSet = new FileDownloadQueueSet(mFileDownloadListener);
        mQueueSet.downloadSequentially(tasks);
        mQueueSet.start();
        updateDialogUi(id,pgName,all,current);
    }

    private void updateDialogUi(String versionId, String title, int all, int current) {
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
        mDialog.setId(versionId)
                .setTitle(title)
                .setTotalProgress(all,current);
    }

    private void updateDialogUi(boolean success){
        if(success){
            mDialog.setFinished();
        }else{
            mDialog.setFailed();
            this.cancelDownload();
        }
    }

    private FileDownloadListener mFileDownloadListener = new FileDownloadListener() {

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            Log.e(TAG,"加入等待队列：" + task.getFilename());

        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            Log.e(TAG,"加入下载队列：" + task.getFilename());

            //反馈下载速度
            if(currentPresetId == DOWNLOAD_PRESET_VERSION_JSON || currentPresetId == DOWNLOAD_PRESET_VERSION_JAR || currentPresetId == DOWNLOAD_PRESET_ASSETS_INDEX){
                mDialog.setCurrentProgress(soFarBytes *100 / totalBytes);
                mDialog.setSpeed(FormatUtils.formatDataTransferSpeed(task.getSpeed(),FormatUtils.CAPACITY_TYPE_KBYTE,FormatUtils.DTS_TYPE_S));
            }
            //反馈文件名称
            if(currentPresetId != DOWNLOAD_PRESET_MANIFEST){
                mDialog.setFileName(task.getFilename());
            }

        }

        @Override
        protected void completed(BaseDownloadTask task) {

            Log.e(TAG,"完成：" + task.getFilename() + " 模式: " + enablePreset);
            if(enablePreset){
                taskFinished++;
                if(currentPresetId == DOWNLOAD_FORGE_LIBS || currentPresetId == DOWNLOAD_PRESET_VERSION_LIBS || currentPresetId == DOWNLOAD_PRESET_ASSETS_OBJS){
                    mDialog.setSpeed(FormatUtils.formatDataTransferSpeed(task.getSpeed(),FormatUtils.CAPACITY_TYPE_KBYTE,FormatUtils.DTS_TYPE_S));
                }
                mDialog.setCurrentProgress(taskFinished * 100 / taskCounts);
                if(taskCounts == taskFinished){
                    taskFinished = 0;
                    if(currentPresetId != DOWNLOAD_PRESET_ASSETS_OBJS && currentPresetId != DOWNLOAD_FORGE_LIBS){
                        startPresetDownload(currentPresetId + 1, currentVersionId);
                    }else{
                        updateDialogUi(true);
                    }
                }
            }else{
                mRunable.run();
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            updateDialogUi(false);
            Log.e(TAG,"失败：" + task.getFilename());
            Log.e(TAG,"信息: " + e.getMessage() + " 原因: " + e.getCause());
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            Log.e(TAG,"警告：" + task.getFilename());
        }
    };

    private Runable mRunable;
    public void downloadManifestAndUpdateGameListUi(Runable r){
        mQueueSet = new FileDownloadQueueSet(mFileDownloadListener);
        mQueueSet.downloadSequentially(mSupport.createVersionManifestDownloadTask());
        mQueueSet.start();
        this.mRunable = r;
    }

    public class Runable{
        public void run(){}
    }

    public void cancelDownload(){
        new FileDownloader().pauseAll();
    }

}
