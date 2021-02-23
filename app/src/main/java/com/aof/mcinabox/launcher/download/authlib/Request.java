package com.aof.mcinabox.launcher.download.authlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.aof.mcinabox.R;
import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest;
import com.aof.mcinabox.launcher.download.DownloadManager;
import com.aof.mcinabox.launcher.download.support.DownloadHelper;
import com.aof.mcinabox.launcher.download.support.UrlSource;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.google.gson.Gson;
import com.liulishuo.filedownloader.BaseDownloadTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class Request {

    private final static String TAG = "Request";
    private final static String AUTHLIB_INJECTOR = "authlib-injector";
    private final static int REQUEST_DOWNLOAD = 1;
    private final static int REQUEST_FAILED = 2;
    private AuthlibVersionResponse mVersionResponse;
    private final Context mContext;
    private final SettingJson mSetting;
    private Exception mException;

    public Request(Context context) {
        this(context, OldMainActivity.Setting);
    }

    public Request(Context context, SettingJson setting) {
        super();
        this.mContext = context;
        this.mSetting = setting;
    }

    public void requestLastestVersion() {
        final Gson gson = new Gson();
        OkHttpClient mOkHttpClient = new OkHttpClient();
        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(new UrlSource().getSourceUrl(mSetting.getDownloadType(), UrlSource.TYPE_AUTHLIB_INJECTOR_JAR))
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                onFailure(e);
            }

            private void onFailure(Exception e) {
                Request.this.mException = e;
                Message msg = new Message();
                msg.what = REQUEST_FAILED;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    mVersionResponse = gson.fromJson(response.body().string(), AuthlibVersionResponse.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(e);
                    return;
                }
                Message msg = new Message();
                msg.what = REQUEST_DOWNLOAD;
                mHandler.sendMessage(msg);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_DOWNLOAD:
                    requestDownload(mVersionResponse);
                    break;
                case REQUEST_FAILED:
                    DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(R.string.title_error), String.format(mContext.getString(R.string.tips_error), mException.getMessage()), mContext.getString(R.string.title_ok), null);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void requestDownload(AuthlibVersionResponse response) {
        BaseDownloadTask[] tasks = {DownloadHelper.createDownloadTask(AppManifest.AUTHLIB_INJETOR_JAR, response.download_url, null)};
        new DownloadManager(mContext).startDownload(AUTHLIB_INJECTOR, mContext.getString(R.string.tips_downloading_authlib_injector), 1, 0, tasks, null);
    }

}
