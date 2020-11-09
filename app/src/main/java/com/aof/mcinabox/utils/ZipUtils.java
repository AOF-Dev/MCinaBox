package com.aof.mcinabox.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

    private final static String TAG = "ZipUtils";
    private final static String TYPE = "Type";
    private final static String RESULT = "Result";
    private final static String TYPE_ERROR = "Error";
    private final static String TYPE_SUCCESS = "Success";

    private final Gson gson = new Gson();

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (callable) {
                switch (Objects.requireNonNull(msg.getData().getString(TYPE))) {
                    case TYPE_ERROR:
                        mCallback.onFailed(gson.fromJson(msg.getData().getString(RESULT), Exception.class));
                        break;
                    case TYPE_SUCCESS:
                        mCallback.onSuccess();
                        break;
                }
                mCallback.onFinish();
            }
        }
    };

    private Callback mCallback;
    private boolean callable = false;

    public ZipUtils setCallback(@NonNull Callback call) {
        this.mCallback = call;
        this.callable = true;
        return this;
    }

    public void UnZipFolder(final String zipFileString, final String outPathString) {
        if (callable) {
            mCallback.onStart();
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                Bundle bundle = new Bundle();
                try (ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString))) {
                    ZipEntry zipEntry;
                    String szName;
                    while ((zipEntry = inZip.getNextEntry()) != null) {
                        szName = zipEntry.getName();
                        if (zipEntry.isDirectory()) {
                            //获取部件的文件夹名
                            szName = szName.substring(0, szName.length() - 1);
                            File folder = new File(outPathString + File.separator + szName);
                            folder.mkdirs();
                        } else {
                            Log.e(TAG, outPathString + File.separator + szName);
                            File file = new File(outPathString + File.separator + szName);
                            if (!file.exists()) {
                                Log.e(TAG, "Create the file:" + outPathString + File.separator + szName);
                                file.getParentFile().mkdirs();
                                file.createNewFile();
                            }
                            // 获取文件的输出流
                            try (FileOutputStream out = new FileOutputStream(file)) {
                                int len;
                                byte[] buffer = new byte[1024];
                                // 读取（字节）字节到缓冲区
                                while ((len = inZip.read(buffer)) != -1) {
                                    // 从缓冲区（0）位置写入（字节）字节
                                    out.write(buffer, 0, len);
                                }
                            }
                        }
                    }
                    bundle.putString(TYPE, TYPE_SUCCESS);
                    bundle.putString(RESULT, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    bundle.putString(TYPE, TYPE_ERROR);
                    bundle.putString(RESULT, gson.toJson(e));
                }
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    public interface Callback {
        void onStart();

        void onFailed(Exception e);

        void onSuccess();

        void onFinish();
    }
}
