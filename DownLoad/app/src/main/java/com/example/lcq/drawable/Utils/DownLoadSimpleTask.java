package com.example.lcq.drawable.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.lcq.drawable.db.ThreadDao;
import com.example.lcq.drawable.db.ThreadDaoImp;
import com.example.lcq.drawable.services.DownloadService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 任务下载类
 */

public class DownLoadSimpleTask {
    private Context context = null;
    private FileInfo fileInfo = null;
    private ThreadDao threadDao = null;
    private int finished = 0;
    public boolean isPause = false;
    private int threadCount = 1;
    private List<DownLoadThread> downLoadThreadList = null;

    public static ExecutorService executorService = Executors.newCachedThreadPool();

    public DownLoadSimpleTask(Context context, FileInfo fileInfo) {
        this.context = context;
        this.fileInfo = fileInfo;
//        this.threadCount = threadCount;
        threadDao = new ThreadDaoImp(context);
    }


    public void downLoad() {
        //读取数据库的线程信息
       List<ThreadInfo> threadInfos =  threadDao.getThreads(fileInfo.getUrl());
        ThreadInfo threadInfo = null;
       if(threadInfos.size() == 0) {
           //初始化数据信息
          threadInfo= new ThreadInfo(0,fileInfo.getUrl(),0,fileInfo.getLength(),0);
       } else {
           threadInfo = threadInfos.get(0);
       }
       //创建子线程
        new DownLoadThread(threadInfo).start();
    }


    //下载线程
    class DownLoadThread extends Thread {
        private ThreadInfo threadInfo = null;
        public boolean isFinished = false; //是否执行完毕

        public DownLoadThread(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {
//            super.run();
            if(!threadDao.isexit(fileInfo.getUrl(),threadInfo.getId())) {
                threadDao.insertThread(threadInfo);
            }
            HttpURLConnection connection = null;
            RandomAccessFile randomAccessFile = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(threadInfo.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(3000);
                //设置下载位置
                int start = threadInfo.getStart() + threadInfo.getFinished();
                connection.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.getEnd());
                //设置文件写入位置
                File file = new File(DownloadService.DOWNLOAD_PATH, fileInfo.getFileName());
                randomAccessFile = new RandomAccessFile(file, "rwd");
                randomAccessFile.seek(start);
                Intent intent = new Intent(DownloadService.ACTION_UPDATE);
                finished += threadInfo.getFinished();
                long time = System.currentTimeMillis();
                //开始下载
                int coo = connection.getResponseCode();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                    //读取数据
                     inputStream = connection.getInputStream();
                    byte[] buffer = new byte[1024 * 1000];
                    int len = -1;
                    while ((len = inputStream.read(buffer)) != -1) {
                        //写入文件
                        randomAccessFile.write(buffer, 0, len);
                        //把下载进度发送广播给Activity
                        //累加没个进程完成的进度
                        threadInfo.setFinished(threadInfo.getFinished() + len);
                        finished += len;
                        int value = (finished * 10 / fileInfo.getLength())*10;
                        if(finished == fileInfo.getLength()) {
                            Log.i("ss","sss");
                        }

                        if (System.currentTimeMillis() - time > 1000 || finished == fileInfo.getLength()) {
                            intent.putExtra("finished", (finished * 10 / fileInfo.getLength())*10);
                            intent.putExtra("id",fileInfo.getId());
                            context.sendBroadcast(intent);
                        }
                        //下载暂停时进度保存数据库
                        if (isPause) {
                            threadDao.updateThread(threadInfo.getUrl(), threadInfo.getId(), finished);
                            return;
                        }
                    }

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(inputStream != null) {
                        inputStream.close();
                    }
                   if(randomAccessFile != null) {
                       randomAccessFile.close();
                   }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
