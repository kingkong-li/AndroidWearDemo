package com.emt.fatri.wearbaidusdkdemo.utils;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.emt.fatri.wearbaidusdkdemo.datamodel.MyService;

/**
 * description:
 * Created by kingkong on 2018/5/29 0029.
 * changed by kingkong on 2018/5/29 0029.
 */

public class MainApplication extends Application {
    private static MainApplication mInstance;

    public static MainApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(mInstance==null)
        {
            mInstance=this;
        }
        // 开启接收数据服务
        Intent intent=new Intent();
        intent.setClass(this,MyService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            Log.e("MainApp","startForegroundService");
           startForegroundService(intent);
        } else {
            // Pre-O behavior.
          startService(intent);
        }

    }
}
