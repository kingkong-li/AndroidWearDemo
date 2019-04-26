package com.emt.fatri.wearbaidusdkdemo.utils;

import android.annotation.SuppressLint;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Create by KingKong
 * 封装的Toast工具类
 * 2018/5/3
 */

public class ToastUtils {

    private static ToastUtils mInstance;
    private Toast mToast;
    private String mBackup = "";
    private long mLastToastTime;
    /**Toast时间间隔，小于这个时间间隔相同的两个Toast不重复显示 单位ms*/
    private static final int TOAST_TIME_INTERVAL = 2000;

    @SuppressLint("ShowToast")
    private ToastUtils(){
        mToast = Toast.makeText(MainApplication.getInstance(), mBackup, Toast.LENGTH_SHORT);
    }
    public static synchronized ToastUtils getInstance(){
        if(null == mInstance){
            mInstance = new ToastUtils();
        }
        return mInstance;
    }

    /**
     * 通过Toast方式简单显示内容
     * @param str 需要Toast显示的内容
     */
    public void show(String str){
        if(TextUtils.isEmpty(str)){
            return;
        }
        //2秒内，不重复toast相同字串
        long time = SystemClock.elapsedRealtime();
        if(mBackup.equals(str) && time < (mLastToastTime + TOAST_TIME_INTERVAL)){
            return;
        }
        mLastToastTime = time;
        mBackup = str; // 网络上原来是写法mBackup=new String(str) 这里需要斟酌下
        mToast.setText(str);
        mToast.show();
    }
}
