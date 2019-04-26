package com.emt.fatri.wearbaidusdkdemo.utils;

import android.content.Context;
import android.os.PowerManager;
import android.view.WindowManager;

/**
 * description:
 * Created by kingkong on 2018/8/16 0016.
 * changed by kingkong on 2018/8/16 0016.
 */

public class WakePhoneUtil {
    public static void screenOn() {
        // turn on screen
        PowerManager mPowerManager = (PowerManager) MainApplication.getInstance().
                getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock
                (PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        mWakeLock.acquire(10000);

    }
}
