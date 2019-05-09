package com.weimo.processkeepalive.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * 其他进程唤醒ui进程的service
 * @author weimo
 */
public class WakeService  extends Service {
    private static final String TAG = WakeService.class.getSimpleName();
    private static final int WAKE_NOTIFICATION_ID = 0x03;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "WakeService onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "WakeService onStartCommand");
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(WAKE_NOTIFICATION_ID, new Notification());
        } else {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
                Intent innerIntent = new Intent(this, WakeInnerService.class);
                startService(innerIntent);
                startForeground(WAKE_NOTIFICATION_ID, new Notification());
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "WakeService onDestroy");
        //如果service被杀死，清除通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (null != notificationManager) {
                notificationManager.cancel(WAKE_NOTIFICATION_ID);
            }
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            startService(new Intent(getApplicationContext(), WakeService.class));
        }
        super.onDestroy();
    }

    public static class WakeInnerService extends Service {

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onCreate() {
            Log.e(TAG, "WakeInnerService onCreate");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.e(TAG, "WakeInnerService onStartCommand");
            //发送与KeppLiveService中ID相同的Notification，并将通知取消，并取消自身的前台显示
            startForeground(WAKE_NOTIFICATION_ID, new Notification());
            //取消InnerService的前台
            stopForeground(true);
            //移除KeepLiveService弹出的通知
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (null != notificationManager) {
                notificationManager.cancel(WAKE_NOTIFICATION_ID);
            }
            //任务完成，终止自身
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public void onDestroy() {
            Log.e(TAG, "WakeInnerService onDestroy");
            super.onDestroy();
        }
    }
}
