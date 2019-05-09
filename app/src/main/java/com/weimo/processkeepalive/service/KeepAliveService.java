package com.weimo.processkeepalive.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.weimo.processkeepalive.receiver.WakeReceiver;

/**
 * @author weimo
 * 提升进程优先级的service
 * 此方案只在8.0以下系统生效
 */
public class KeepAliveService extends Service {
    private static final String TAG = KeepAliveService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 0x01;
    private static final int ALARM_TIME = 5 * 60 * 1000;
    private static final int WAKE_REQUEST_CODE = 0x02;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "KeepAliveService onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "KeepAliveService onStartCommand");
        //API小于18，直接送Notification，将service从后台置于前台
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            startForeground(NOTIFICATION_ID, new Notification());
        } else {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
                //API 18以上，发送Notifacation将service从后台置于前台后，启动InnerService
                startService(new Intent(this, InnerService.class));
                startForeground(NOTIFICATION_ID, new Notification());
            }
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(WakeReceiver.WEAK_ACTION);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, WAKE_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ALARM_TIME, pendingIntent);
        }
        //如果Service被终止,当资源允许情况下，重启service
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "KeepAliveService onDestroy");
        //如果service被杀死，清除通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (null != notificationManager) {
                notificationManager.cancel(NOTIFICATION_ID);
            }
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            startService(new Intent(getApplicationContext(), KeepAliveService.class));
        }
    }

    public static class InnerService extends Service {

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onCreate() {
            Log.e(TAG, "InnerService onCreate");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.e(TAG, "InnerService onStartCommand");
            //发送与KeppLiveService中ID相同的Notification，并将通知取消，并取消自身的前台显示
            startForeground(NOTIFICATION_ID, new Notification());
            //取消InnerService的前台
            stopForeground(true);
            //移除KeepLiveService弹出的通知
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (null != notificationManager) {
                notificationManager.cancel(NOTIFICATION_ID);
            }
            //任务完成，终止自身
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public void onDestroy() {
            Log.e(TAG, "InnerService onDestroy");
            super.onDestroy();
        }
    }
}
