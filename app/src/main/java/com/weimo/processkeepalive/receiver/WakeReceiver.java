package com.weimo.processkeepalive.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.weimo.processkeepalive.service.WakeService;

/**
 * 唤醒receiver
 * @author weimo
 */
public class WakeReceiver extends BroadcastReceiver {
    public static final String WEAK_ACTION = "com.weimo.processkeepalive.weak";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (WEAK_ACTION.equals(intent.getAction())){
            Intent wakeIntent = new Intent(context, WakeService.class);
            context.startService(wakeIntent);
        }
    }
}
