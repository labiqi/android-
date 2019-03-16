package com.example.lcq.drawable.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.lcq.drawable.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class NotificationUtil {
    private NotificationManager notificationManager = null;
    private Map<Integer,Notification> notificationMap = null;
    private Context context;

    public NotificationUtil(Context context) {
        //获得通知系统服务
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationMap = new HashMap<Integer, Notification>();
        this.context = context;
    }

//    public void showNotifacation(FileInputStream fileInfo) {
//        if(!notificationMap.containsKey(fileInfo)){
//            Notification notification = new Notification();
//            notification.tickerText = "开始下载";
//            notification.when = System.currentTimeMillis();
//            notification.flags = Notification.FLAG_AUTO_CANCEL;
//            Intent intent = new Intent(context,MainActivity.class);
//            PendingIntent pendingIntent = new PendingIntent(context,0,intent,0);
//            notification.contentIntent = pendingIntent;
//            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),)
//        }

//    }
}
