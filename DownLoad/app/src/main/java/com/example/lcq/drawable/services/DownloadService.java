package com.example.lcq.drawable.services;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lcq.drawable.Utils.DownLoadTask;
import com.example.lcq.drawable.Utils.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class DownloadService extends Service {

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final String ACTION_FINISH = "ACTION_FINISH";
    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/downlaods/";
    public static  final  int MSG_INIT = 0;
//    private DownLoadTask downLoadTask = null;
//下载任务的集合
    private Map<Integer,DownLoadTask> tasks = new LinkedHashMap<Integer, DownLoadTask>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (ACTION_START.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.i("test", "开始命令 == " + fileInfo.toString());
            //启动初始化线程
            InitThread initThread = new InitThread(fileInfo);
            new InitThread(fileInfo).start();
//            DownLoadTask.executorService.execute(initThread);
        } else if (ACTION_STOP.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            //从集合中取出下载任务
            DownLoadTask task = tasks.get(fileInfo.getId());
            if(task != null) {
                //停止下载任务
                task.isPause = true;
            }
//            Log.i("test", "结束命令== " + fileInfo.toString());
//            if(downLoadTask != null) {
//                 downLoadTask.isPause = true;
//            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_INIT:
                    FileInfo fileInfo = (FileInfo) msg.obj;
                    Log.i("test initThread",fileInfo.toString());
                    //启动下载任务
                    DownLoadTask downLoadTask = new DownLoadTask(DownloadService.this,fileInfo,3);
                    downLoadTask.downLoad();
                    //把虾藻任务添加到集合中
                    tasks.put(fileInfo.getId(),downLoadTask);
                    break;
            }
        }
    };


    /*
    初始化子线程
     */

    class InitThread extends Thread {

        private FileInfo fileInfo = null;

        public InitThread(FileInfo mFileInfo) {
            this.fileInfo = mFileInfo;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            try {
                // 链接网络文件 ，获得文件长度， 在本地创建文件 ，设置文件长度
                URL url= new URL(fileInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                int length = -1;


               int code = conn.getResponseCode();
                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    length = conn.getContentLength();
                }

                if(length <= 0) {
                    return;
                }
                File dir = new File(DOWNLOAD_PATH);
                if(!dir.exists()) {
                    dir.mkdir();
                }

                //在本地创建文件
                File file = new File(dir,fileInfo.getFileName());
                raf = new RandomAccessFile(file,"rwd");
                raf.setLength(length);
                fileInfo.setLength(length);
                mhandler.obtainMessage(MSG_INIT,fileInfo).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
                try {
                    if(raf != null) {
                        raf.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
