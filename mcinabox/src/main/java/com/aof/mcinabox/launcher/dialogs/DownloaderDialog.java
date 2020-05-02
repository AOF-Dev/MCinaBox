package com.aof.mcinabox.launcher.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aof.mcinabox.DataPathManifest;
import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.JsonUtils;
import com.aof.mcinabox.launcher.uis.InstallVersionUI;
import com.aof.mcinabox.utils.FileTool;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.util.ArrayList;

public class DownloaderDialog extends StandDialog {

    public DownloaderDialog(MainActivity context, int layoutID){
        super(context,layoutID);
    }

    private ProgressBar downloader_total_process,downloader_current_process;
    private  TextView downloader_total_count,downloader_current_count,downloader_current_task,downloader_target_version;
    private Button download_ok,download_cancel;
    private ImageView finishMark;
    private FileDownloadQueueSet queueSet;
    public com.aof.mcinabox.minecraft.DownloadMinecraft mDownloadMinecraft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        downloader_total_process = findViewById(R.id.dialog_total_process);
        downloader_current_process = findViewById(R.id.dialog_current_process);
        downloader_total_count = findViewById(R.id.dialog_total_count);
        downloader_current_count = findViewById(R.id.dialog_current_count);
        downloader_current_task = findViewById(R.id.dialog_process_name);
        downloader_target_version = findViewById(R.id.dialog_version_id);
        download_ok = findViewById(R.id.dialog_download_ok);
        download_cancel = findViewById(R.id.dialog_download_cancle);
        finishMark = findViewById(R.id.dialog_download_finish);
        initDownloader();

    }

    private void initDownloader(){
        //初始化下载器
        FileDownloader.setup(mContext);
        queueSet = new FileDownloadQueueSet(downloadListener);
        mDownloadMinecraft = new com.aof.mcinabox.minecraft.DownloadMinecraft();

    }

    private void initDownloaderUI(String id){
        downloader_total_count.setVisibility(View.VISIBLE);
        finishMark.setVisibility(View.GONE);
        download_ok.setClickable(false);
        downloader_target_version.setText(id);
        downloader_current_task.setText("");
        downloader_total_process.setProgress(0);
        downloader_current_process.setProgress(0);
        downloader_current_count.setText("0%");
        downloader_total_count.setText("0/0");
    }

    private void stateDownloaderUI(String task,String totalCount,int totalProcess,int currentProcess,boolean finished){
        downloader_current_task.setText(task);
        downloader_total_count.setText(totalCount);
        downloader_total_process.setProgress(totalProcess);
        downloader_current_count.setText(currentProcess + "%");
        downloader_current_process.setProgress(currentProcess);
        if(finished){
            download_ok.setClickable(true);
            finishMark.setVisibility(View.VISIBLE);
        }
    }

    private String minecraftId;
    private int finishCount;
    private int totalProcess;
    private ArrayList<BaseDownloadTask> downloadTasks = new ArrayList<BaseDownloadTask>();
    public void startDownloadMinecraft(String id){
        initDownloaderUI(id);
        minecraftId = id;
        finishCount = 0;
        downloadTasks.clear();
        show();
        totalProcess = 1;
        ChangeDownloadPrcess(1,0);
        StartDownload(1,id);
    }
    public void startDownloadForge(String id){
        initDownloaderUI(id);
        minecraftId = id;
        finishCount = 0;
        downloadTasks.clear();
        show();
        totalProcess = 7;
        ChangeDownloadPrcess(7,0);
        StartDownload(7,id);
    }
    public void startDownloadManifest(){
        downloadTasks.add(mDownloadMinecraft.createVersionManifestDownloadTask());
        StartDownloadQueueSet(queueSet,downloadTasks);
    }

    private void ChangeDownloadPrcess(int taskId,int currentProcess){
        String task = "";
        String totalCount = "";
        int totalProcess = 0;
        boolean finished = false;
        switch(taskId){
            case 1:
                task = mContext.getString(R.string.tips_download_version_json);
                totalCount = "1/4";
                totalProcess = 25;
                break;
            case 2:
                task = mContext.getString(R.string.tips_download_version_libraries);
                totalCount = "2/4";
                totalProcess = 50;
                break;
            case 3:
                task = mContext.getString(R.string.tips_download_assets_index);
                totalCount = "3/4";
                totalProcess = 75;
                break;
            case 4:
                task = mContext.getString(R.string.tips_download_assets_object);
                totalCount = "4/4";
                totalProcess = 100;
                break;
            case 5:
                task = mContext.getString(R.string.tips_download_finish);
                totalCount = "4/4";
                totalProcess = 100;
                finished = true;
                break;
            case 7:
                task = mContext.getString(R.string.tips_download_forge_object);
                totalCount = "1/1";
                totalProcess = 100;
                break;
            case 8:
                task = mContext.getString(R.string.tips_download_finish);
                totalCount = "1/1";
                totalProcess = 100;
                finished = true;
                fitness_attribute();
                break;
        }
        stateDownloaderUI(task,totalCount,totalProcess,currentProcess,finished);
    }

    private void fitness_attribute() {
        String homePath;
        if(JsonUtils.getSettingFromFile(DataPathManifest.MCINABOX_FILE_JSON).getLocalization().equals("private")){
            homePath = DataPathManifest.MCINABOX_DATA_PRIVATE;
        }else{
            homePath = DataPathManifest.MCINABOX_DATA_PUBLIC;
        }
        FileTool.checkFilePath(new File(homePath), true);
        FileTool.checkFilePath(new File(homePath + "/config"), true);
        String config_file = homePath + "/config/splash.properties";
        if( ! FileTool.isFileExists(config_file)){
            FileTool.addFile(config_file);
        }
        FileTool.writeData(config_file, "enabled=false");
    }

    private void StartDownload(int totalProcess,String id){
        switch(totalProcess){
            case 1:
                downloadTasks.add(mDownloadMinecraft.createVersionJsonDownloadTask(id));
                StartDownloadQueueSet(queueSet,downloadTasks);
                break;
            case 2:
                downloadTasks.add(mDownloadMinecraft.createVersionJarDownloadTask(id));
                downloadTasks.addAll(mDownloadMinecraft.createLibrariesDownloadTask(id));
                StartDownloadQueueSet(queueSet,downloadTasks);
                break;
            case 3:
                downloadTasks.add(mDownloadMinecraft.createAssetIndexDownloadTask(id));
                StartDownloadQueueSet(queueSet,downloadTasks);
                break;
            case 4:
                downloadTasks.addAll(mDownloadMinecraft.createAssetObjectsDownloadTask(id));
                StartDownloadQueueSet(queueSet,downloadTasks);
                break;
            case 7:
                downloadTasks.addAll(mDownloadMinecraft.createForgeDownloadTask(id));
                StartDownloadQueueSet(queueSet,downloadTasks);
                break;
        }
    }

    //并行方式执行下载队列
    private void StartDownloadQueueSet(FileDownloadQueueSet queueSet, ArrayList<BaseDownloadTask> downloadTasks){
        queueSet.downloadTogether(downloadTasks);
        queueSet.start();
    }

    private FileDownloadListener downloadListener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            if(totalProcess == 1 || totalProcess == 3){
                ChangeDownloadPrcess(totalProcess,soFarBytes*100/totalBytes);
            }
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            //如果完成的任务为清单文件下载，就刷新版本列表
            finishCount++;

            if(task.getFilename().equals("version_manifest.json")){
                downloadTasks.clear();
                //清单文件下载完成后刷新一次列表
                MainActivity context = (MainActivity) mContext;
                InstallVersionUI uiInstallVersion = context.uiInstallVersion;
                uiInstallVersion.refreshOnlineVersionList();

                finishCount = 0;
                return;
            }

            if(finishCount == downloadTasks.size() && (totalProcess != 7)){
                if(finishCount == downloadTasks.size() && totalProcess != 4){
                    finishCount = 0;
                    downloadTasks.clear();
                    totalProcess++;
                    StartDownload(totalProcess,minecraftId);
                }else if(finishCount == downloadTasks.size() && (totalProcess == 4 || totalProcess == 6)){
                    finishCount = 0;
                    downloadTasks.clear();
                    totalProcess =0;
                    ChangeDownloadPrcess(5,100);
                }
            } else if(finishCount == downloadTasks.size()){
                finishCount = 0;
                downloadTasks.clear();
                totalProcess =0;
                ChangeDownloadPrcess(8,100);

            }

            if(totalProcess == 2||totalProcess == 4||totalProcess == 5||totalProcess == 7){
                ChangeDownloadPrcess(totalProcess,(finishCount*100)/downloadTasks.size());
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            //任务全部取消
            Log.e("Downloader",e.toString());
            Toast.makeText(mContext, mContext.getString(R.string.tips_download_failed), Toast.LENGTH_SHORT).show();
            download_cancel.performClick();
        }

        @Override
        protected void warn(BaseDownloadTask task) {

        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if(v == download_ok){
                dismiss();
            }
            if(v == download_cancel){
                FileDownloader.getImpl().pauseAll();
                dismiss();
            }
        }
    };

}
