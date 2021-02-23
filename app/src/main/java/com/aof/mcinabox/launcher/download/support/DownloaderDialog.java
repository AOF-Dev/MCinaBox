package com.aof.mcinabox.launcher.download.support;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.download.DownloadManager;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.aof.mcinabox.utils.dialog.support.DialogSupports;

public class DownloaderDialog extends Dialog implements Dialog.OnCancelListener, View.OnClickListener{

    private Context mContext;
    private DownloadManager mDownloadManager;
    private final static String TAG = "DownloaderDialog";

    private Button buttonOK;
    private Button buttonCancel;
    private ImageView ivFinish;
    private TextView textProgress;
    private TextView textProgressName;
    private TextView textCurrentPrecentage;
    private TextView textVersionId;
    private TextView textSpeed;
    private TextView textFileName;
    private ProgressBar pbTotal;
    private ProgressBar pbCurrent;

    public DownloaderDialog(@NonNull Context context, DownloadManager manager) {
        super(context);
        setContentView(R.layout.dialog_download);
        setCancelable(false);
        this.mContext = context;
        this.mDownloadManager = manager;
        init();
    }

    private void init(){
        buttonOK = findViewById(R.id.dialog_download_button_ok);
        buttonCancel = findViewById(R.id.dialog_download_button_cancle);
        ivFinish = findViewById(R.id.dialog_download_image_finish);
        textProgress = findViewById(R.id.dialog_download_text_progress);
        textProgressName = findViewById(R.id.dialog_download_text_progress_name);
        textCurrentPrecentage = findViewById(R.id.dialog_download_text_percentage);
        textVersionId = findViewById(R.id.dialog_download_text_versionid);
        textSpeed = findViewById(R.id.dialog_download_text_speed);
        textFileName = findViewById(R.id.dialog_download_text_filename);
        pbCurrent = findViewById(R.id.dialog_download_processbar_current);
        pbTotal = findViewById(R.id.dialog_download_processbar_total);

        //设置监听器
        for(View v : new View[]{buttonCancel,buttonOK}){
            v.setOnClickListener(this);
        }
        //设置控件属性
        pbTotal.setMax(100);
        pbCurrent.setMax(100);
        setOnCancelListener(this);
        buttonOK.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        mDownloadManager.cancelDownload();
    }

    @Override
    public void onClick(View v) {
        if(v == buttonOK){
            dismiss();
        }

        if(v == buttonCancel){
            DialogUtils.createBothChoicesDialog(mContext,mContext.getString(R.string.title_warn),mContext.getString(R.string.tips_are_you_sure_to_cancel_download_task),mContext.getString(R.string.title_ok),mContext.getString(R.string.title_cancel),new DialogSupports(){
                @Override
                public void runWhenPositive(){
                    DownloaderDialog.this.cancel();
                }
            });
        }
    }

    public DownloaderDialog setId(String id){
        this.textVersionId.setText(id);
        return this;
    }

    public DownloaderDialog setTitle(String title){
        this.textProgressName.setText(title);
        return this;
    }

    public DownloaderDialog setTotalProgress(int all, int current){
        this.pbTotal.setProgress(current *100 / all);
        this.textProgress.setText(current + "/" + all);
        return this;
    }

    public DownloaderDialog setTotalProgress(int progress){
        this.pbTotal.setProgress(progress);
        return this;
    }

    public DownloaderDialog setCurrentProgress(int progress){
        this.pbCurrent.setProgress(progress);
        this.textCurrentPrecentage.setText(progress + "%");
        return this;
    }

    public DownloaderDialog setFinished(){
        this.ivFinish.setVisibility(View.VISIBLE);
        this.textProgress.setVisibility(View.GONE);
        this.textProgressName.setText(mContext.getString(R.string.tips_download_finished));
        this.buttonOK.setVisibility(View.VISIBLE);
        this.buttonCancel.setVisibility(View.GONE);
        this.setCurrentProgress(100);
        this.setTotalProgress(100);
        return this;
    }

    public DownloaderDialog setFailed(){
        this.textProgressName.setText(mContext.getString(R.string.tips_download_failed));
        return this;
    }

    public DownloaderDialog restoreStat(){
        this.ivFinish.setVisibility(View.GONE);
        this.textProgress.setVisibility(View.VISIBLE);
        setTitle("");
        setId("");
        setCurrentProgress(0);
        setTotalProgress(1,0);
        return  this;
    }

    public DownloaderDialog setSpeed(String speed){
        this.textSpeed.setText(speed);
        return this;
    }

    public DownloaderDialog setFileName(String name){
        this.textFileName.setText(name);
        return this;
    }


}
