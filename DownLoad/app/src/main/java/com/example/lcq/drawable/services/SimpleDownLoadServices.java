package com.example.lcq.drawable.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lcq.drawable.SimpleDownActivity;
import com.example.lcq.drawable.Utils.DownLoadSimpleTask;
import com.example.lcq.drawable.Utils.FileInfo;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.lcq.drawable.services.DownloadService.DOWNLOAD_PATH;
import static com.example.lcq.drawable.services.DownloadService.MSG_INIT;

public class SimpleDownLoadServices extends Service {
    private DownLoadSimpleTask downLoadSimpleTask;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(SimpleDownActivity.LOAD_START.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.i("test","start= "+fileInfo.toString());
            //启动出事话子线程
            initThread initThread = new initThread(fileInfo);
            initThread.start();
        } else if(SimpleDownActivity.LOAD_STOP.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.i("test","stop= "+fileInfo.toString());
            if(downLoadSimpleTask != null) {
                downLoadSimpleTask.isPause = true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }



    /**
     * 初始化子线程
     */
    class initThread extends Thread {
        private FileInfo fileInfo = null;
        public initThread (FileInfo fileInfo) {
            this.fileInfo = fileInfo;
        }

        @Override
        public void run() {
            HttpURLConnection connection = null;
            RandomAccessFile raf =null;
            try {
                //1 链接网络文件
                URL url = new URL(fileInfo.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(3000);
                int len = 0;
                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    len = connection.getContentLength();
                }
                if(len <= 0) {
                    return;
                }
                //本地创建文件
                File dir = new File(DOWNLOAD_PATH);
                if(!dir.exists()) {
                    dir.mkdir();
                }

                boolean is = dir.exists();

                File file = new File(dir,fileInfo.getFileName());
                 raf = new RandomAccessFile(file,"rwd");
                 //设置文件长度
                raf.setLength(len);
                fileInfo.setLength(len);
                //通过handle发送长度到主线程
                mhandler.obtainMessage(MSG_INIT,fileInfo).sendToTarget();
            }catch (Exception e)
            {
                e.printStackTrace();

            } finally {
                try {
                    if(connection != null) {
                        connection.disconnect();
                    }
                    if(raf != null) {
                        raf.close();
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            super.run();
        }
    }


    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT: {
                    FileInfo fileInfo = (FileInfo) msg.obj;
                    Log.i("test from initThread",fileInfo.toString());
                    downLoadSimpleTask = new DownLoadSimpleTask(SimpleDownLoadServices.this,fileInfo);
                    downLoadSimpleTask.downLoad();
                }
                break;
                default:
                 break;
            }
        }

    };
}
