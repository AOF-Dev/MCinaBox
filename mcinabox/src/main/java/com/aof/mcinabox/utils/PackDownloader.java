package com.aof.mcinabox.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.json.PackIndexJson;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.aof.sharedmodule.Data.DataPathManifest.MCINABOX_HOME;

public class PackDownloader {
    private PackDownloaderDialog callback;
    MainActivity act;
    public PackDownloader(MainActivity act,PackDownloaderDialog d) {
        System.out.println("Pack downloader constructor initialized");
        this.act = act;
        this.callback = d;
    }
    public void startDownload() {
         //download the runtime pack
        PackDownloaderDialog.disableClose = true;
        try {
            new AsyncDownload("https://github.com/artdeell/MCinaBox/raw/master/pack_index.json","pack_json.json",false).start();
            FileInputStream fis = new FileInputStream(new File(MCINABOX_HOME+File.separator+"pack_json.json"));
            String data = "";
            byte[] temp =new byte[256];
            int tmp;
            while((tmp = fis.read(temp)) != -1){
                data = data + new String(temp,0,tmp);
            }
            PackIndexJson idx = new Gson().fromJson(data, PackIndexJson.class);
            String arch = "armeabi";
            for(int i = 0; i <idx.archs.split(",").length; i++) {
                if(idx.archs.split(",")[i].contains(Build.SUPPORTED_ABIS[0])) {
                    arch = idx.archs.split(",")[i];

                }

            }
            callback.message(R.string.downloader_data," Architecture:" + Build.SUPPORTED_ABIS[0] +" AvA: " + arch);
            Thread.sleep(500);
            for(int i = 0; i <idx.URLList.split(";").length; i++) {
                if(idx.URLList.split(";")[i].equals(arch)) {
                    callback.message(R.string.downloader_data," Downloading...");
                    new AsyncDownload(idx.URLList.split(";")[i+1],"pack.tar.xz",true).start();
                }
            }

        } catch (FileNotFoundException e) {
            new AsyncDownload("https://github.com/longjunyu2/MCinaBox/releases/download/v0.1.0/aarch32_runtime_20200328.tar.xz","pack.tar.xz",true).start();
        } catch (IOException e) {
            new AsyncDownload("https://github.com/longjunyu2/MCinaBox/releases/download/v0.1.0/aarch32_runtime_20200328.tar.xz","pack.tar.xz",true).start();
        } catch (InterruptedException e) {

        }
        //new AsyncDownload().run();

    }

    public static class PackDownloaderDialog {
        AlertDialog dialog;
        View dialogView;
        boolean downloadAborted;
        static MainActivity a;
        static boolean disableClose = true;
        public void errorOccured(final int stringResID,final String errApendix){
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView)dialogView.findViewById(R.id.packdownloader_infoMessage)).setText(a.getString(stringResID) + errApendix);
                    ((TextView)dialogView.findViewById(R.id.packdownloader_infoMessage)).setTextColor(Color.RED);
                    downloadAborted = true;
                    disableClose=false;
                }
            });

        }
        public void sizeReceived(final int size){
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ProgressBar) dialogView.findViewById(R.id.packdownloader_progress)).setMax(size);
                }
            });
        }
        public void progressAdded(final int newProgress){
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ProgressBar) dialogView.findViewById(R.id.packdownloader_progress)).setProgress(newProgress);
                }
            });
        }
        public void downloadFinished(){
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            });
        }
        public void message(final int stringResID,final String appendix){
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!downloadAborted) {
                        ((TextView) dialogView.findViewById(R.id.packdownloader_infoMessage)).setTextColor(Color.GRAY);
                        ((TextView) dialogView.findViewById(R.id.packdownloader_infoMessage)).setText(a.getString(stringResID) + appendix);
                    }
                }
            });
        }
        public View getView() {
            return dialogView;
        }
        public AlertDialog getDialog() {
            return dialog;
        }

        public static PackDownloaderDialog initDalog(MainActivity a) {
            PackDownloaderDialog.a = a;
            PackDownloaderDialog d = new PackDownloaderDialog();
            AlertDialog.Builder builder;
            System.out.println("Creating Builder...");
            builder = new AlertDialog.Builder(a);
            System.out.println("Inflating View...");
            d.dialogView = a.getLayoutInflater().inflate(R.layout.dalog_packdownloader,null);
            System.out.println("Setting View...");
            builder.setView(d.dialogView);
            builder.setPositiveButton(R.string.tips_ok,new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                      if(!disableClose) {
                          dialogInterface.dismiss();
                      }
                }
            });
            d.dialog = builder.create();
            d.dialog.show();
            return d;
        }


    }
    class AsyncDownload extends Thread{
         String url;String name;boolean closeAfterFinish;
        public AsyncDownload(String url,String name,boolean closeAfterFinish) {
            this.url=url;
            this.name=name;
            this.closeAfterFinish=closeAfterFinish;
        }
        @Override
        public void run() {
            System.out.println("Initializing dialog...");
            //callback.initDalog(act);
            System.out.println("Dialog initialization done");
            System.out.println("Allow auto-close: " + closeAfterFinish);
            try{
                callback.message(R.string.downloader_openingconnection,"");
                HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    int len = conn.getContentLength();
                    callback.sizeReceived(len);
                    callback.message(R.string.downloader_openingstreams,"");
                    InputStream is = conn.getInputStream();
                    FileOutputStream os = new FileOutputStream(new File(MCINABOX_HOME+File.separator+name));
                    int read = -1;
                    int overall = 0;
                    byte[] buffer = new byte[1024];
                    callback.message(R.string.downloader_receivingdata,"");
                    while ((read = is.read(buffer)) != -1) {
                        os.write(buffer, 0, read);
                        overall = overall+read;
                        callback.progressAdded(overall);
                        callback.message(R.string.downloader_receivingdata," ("+overall+"/"+len+")");
                    }

                    if(closeAfterFinish) {
                        callback.message(R.string.downloader_installingruntime, "");
                        act.InstallRuntimeFromPath(MCINABOX_HOME + File.separator + name, new RuntimeInstallationListener() {
                            @Override
                            public void onInstallFinished() {
                                callback.downloadFinished();
                            }
                        });
                    }else{
                     callback.message(R.string.downloader_pleasewait,"");
                    }
                }else{
                    callback.errorOccured(R.string.downloader_fail," "+name+" code " + conn.getResponseCode() + " != 200");
                }
            } catch (IOException e) {
                callback.errorOccured(R.string.downloader_fail," "+name+" " + e.toString());
            }

        }
    }
}
