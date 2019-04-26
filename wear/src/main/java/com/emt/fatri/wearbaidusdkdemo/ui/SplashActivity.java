package com.emt.fatri.wearbaidusdkdemo.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;

import com.emt.fatri.wearbaidusdkdemo.R;
import com.emt.fatri.wearbaidusdkdemo.utils.GlobalConstant;
import com.emt.fatri.wearbaidusdkdemo.utils.MainApplication;
import com.emt.fatri.wearbaidusdkdemo.utils.SharedPreferenceUtil;
import com.emt.fatri.wearbaidusdkdemo.utils.ToastUtils;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class SplashActivity extends WearableActivity {
    private static final String TAG ="SplashActivity";
    /**标记是否第一次启动*/
    private boolean mIsFirstLaunch =true;
    /**定位权限请求码*/
    private static final int BAIDU_READ_PHONE_STATE = 100;
    /**定位权限*/
    static final String[] LOCATION_GPS = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_PHONE_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PERMISSION_GRANTED) {
                // 没有权限，申请权限。
                ActivityCompat.requestPermissions(this, LOCATION_GPS,
                        BAIDU_READ_PHONE_STATE);
            }
            else
            {
                if(GlobalConstant.DEBUG_LOG)Log.v(TAG,"has added permission");
                startApp();
            }

        } else
        {
            if(GlobalConstant.DEBUG_LOG)Log.v(TAG,"android version is very low ,no need to request");
            startApp();
        }



        // Enables Always-on
        setAmbientEnabled();
    }

    /**
     * 开启应用
     */
    private void startApp(){
        mIsFirstLaunch = SharedPreferenceUtil.getBoolean(this,
                GlobalConstant.IS_FIRST_LAUNCH, true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIsFirstLaunch) {
                    //首次启动直接跳转到设置界面
                    SharedPreferenceUtil.putBoolean(SplashActivity.this, GlobalConstant.IS_FIRST_LAUNCH, false);
                    Intent mainIntent = new Intent(SplashActivity.this,
                            SettingsEntranceActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();

                } else {
                    // 其他时候，可以不进去具体设置，设置收起来
                    Intent mainIntent = new Intent(SplashActivity.this,
                            MainActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }

            }
        }, GlobalConstant.SPLASH_SHOW_TIME);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case BAIDU_READ_PHONE_STATE:
                //如果用户取消，permissions可能为null.
                if (grantResults[0] == PERMISSION_GRANTED && grantResults.length > 0) {  //有权限
                    // 获取到权限，作相应处理
                    startApp();
                    if(GlobalConstant.DEBUG_LOG)Log.v(TAG,"request permission ok");
                } else {
                    if(GlobalConstant.DEBUG_LOG)Log.v(TAG,"no permission");
                    ToastUtils.getInstance().show("no permission you need");
                }
                break;
            default:
                break;
        }
    }
}
