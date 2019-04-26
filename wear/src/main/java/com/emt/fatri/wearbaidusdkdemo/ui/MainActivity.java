package com.emt.fatri.wearbaidusdkdemo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.emt.fatri.wearbaidusdkdemo.R;
import com.emt.fatri.wearbaidusdkdemo.datamodel.CompassHelper;
import com.emt.fatri.wearbaidusdkdemo.utils.GlobalConstant;
import com.emt.fatri.wearbaidusdkdemo.utils.MainApplication;
import com.emt.fatri.wearbaidusdkdemo.utils.NetworkUtil;
import com.emt.fatri.wearbaidusdkdemo.utils.ToastUtils;
import com.emt.fatri.wearbaidusdkdemo.utils.WakePhoneUtil;

import java.util.ArrayList;


/**
 * 主预警界面
 */
public class MainActivity extends WearableActivity implements RadarView.AnimationListener {
    /**Log标签*/
    private static String LOG_CLASS_NAME = MainActivity.class.getSimpleName();
    /**传感器状态列表*/
    private ArrayList<Integer> mSensorStateList;
    /**雷达view*/
    private RadarView mAddPointView;
    /**显示wifi状态的图标*/
    private ImageView mWifiStateIcon;
    private float currentAzimuth;
    private CompassHelper compassHelper;
    private BatterChangedReceiver batterChangedReceiver;
    private static final String ACTION_POWER_CONNECTED = "android.intent.action.ACTION_POWER_CONNECTED";
    private static final String ACTION_POWER_DISCONNECTED = "android.intent.action.ACTION_POWER_DISCONNECTED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(GlobalConstant.DEBUG_LOG)Log.v(LOG_CLASS_NAME, "onCreate");
        setContentView(R.layout.activity_main);

        mWifiStateIcon=findViewById(R.id.wifi_state);
        mWifiStateIcon.setVisibility(View.VISIBLE);
        updateNetworkState();


        mAddPointView = findViewById(R.id.add_point_layout);
        // 长按主界面跳转到设置、帮助、和历史信息界面
        mAddPointView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent=new Intent();
                intent.setClass(MainActivity.this,SettingsEntranceActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
                return false;
            }
        });
        mAddPointView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNetworkState();
            }
        });
        // 设置动画结束监听，动画结束时候我们要显示当前wifi状态
        mAddPointView.setAnimationListener(this);

        setupCompass();

        mAddPointView.initSettings();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(GlobalConstant.DEBUG_LOG)Log.v(LOG_CLASS_NAME, "onStart");



        // 每次可见状态就重新监听充电状态
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_POWER_CONNECTED);
        filter.addAction(ACTION_POWER_DISCONNECTED);
        batterChangedReceiver  = new BatterChangedReceiver();
        registerReceiver(batterChangedReceiver,filter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(GlobalConstant.DEBUG_LOG)Log.v(LOG_CLASS_NAME, "onResume");
        //保持屏幕常亮、这是电源管理项，可以使指南针正常工作
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        clearFlag();
        compassHelper.start();

    }

    @Override
    protected void onPause() {
        if(GlobalConstant.DEBUG_LOG)Log.v(LOG_CLASS_NAME, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        compassHelper.stop();
        unregisterReceiver(batterChangedReceiver);
        super.onStop();

    }


    /**
     * 添加指南针
     */
    private void setupCompass() {
        compassHelper = new CompassHelper(this);
        CompassHelper.CompassListener cl = new CompassHelper.CompassListener() {

            @Override
            public void onNewAzimuth(float azimuth) {
                rotateView(currentAzimuth,azimuth,500);
            }
        };
        compassHelper.setListener(cl);
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(GlobalConstant.DEBUG_LOG)Log.v(LOG_CLASS_NAME, "onNewIntent start" );
        setIntent(intent);
        updateNetworkState();
        if(getIntent().getIntExtra(GlobalConstant.NETWORK_STATE,0)==GlobalConstant.NETWORK_ERROR_CODE)
        {
            mWifiStateIcon.setVisibility(View.VISIBLE);
            return;
        }
        WakePhoneUtil.screenOn();

        mSensorStateList = getIntent().getIntegerArrayListExtra(GlobalConstant.SENSOR_LIST_STATE);
        if (mSensorStateList != null && mSensorStateList.size() > 0) {
            Log.v(LOG_CLASS_NAME, "onNewIntent mSensorStateList=" + mSensorStateList.get(0));
            //报警SensorId列表
            ArrayList<Integer> alarmSensorList = new ArrayList<>();
            for (int i = 0; i < mSensorStateList.size(); i++) {
                if (mSensorStateList.get(i) == 0) {
                    alarmSensorList.add(i);
                }
            }
            // 设置报警传感器ids
            mAddPointView.setErrorImageIds(alarmSensorList);
            mAddPointView.startAnim();

        }
    }

    /**
     * 旋转view
     * @param fromDegree 起始角度
     * @param toDegree   终止角度
     * @param time      旋转动画时间
     */
    public void rotateView(float fromDegree, float toDegree, int time) {
        Animation an = new RotateAnimation(-fromDegree, -toDegree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth=toDegree;
        if(GlobalConstant.DEBUG_LOG)Log.v(LOG_CLASS_NAME,"rotateView time="+time);
        an.setDuration(time);
        an.setRepeatCount(0);
        an.setFillAfter(true);
        mAddPointView.startAnimation(an);
    }

    @Override
    public void onAnimationStart() {
        mWifiStateIcon.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationEnd() {
        if(GlobalConstant.DEBUG_LOG)Log.v(LOG_CLASS_NAME,"onAnimationEnd");
        updateNetworkState();
        mWifiStateIcon.setVisibility(View.VISIBLE);
        //清除flag
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }


    class BatterChangedReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_POWER_CONNECTED)) {
                ToastUtils.getInstance().show("连接上充电器");
                compassHelper.stop();
                compassHelper.start();
            } else if (intent.getAction().equals(ACTION_POWER_DISCONNECTED)) {
                ToastUtils.getInstance().show("拔掉充电器");
                compassHelper.stop();
                compassHelper.start();
            }

        }
    }


    /**
     * 更新wifi显示状态
     *
     */
    private void updateNetworkState(){

        boolean isConnected= NetworkUtil.isWifiConnected(MainApplication.getInstance());
        if(GlobalConstant.DEBUG_LOG)Log.v(LOG_CLASS_NAME,"updateNetworkState state ="+isConnected);
        if(isConnected)
        {
            mWifiStateIcon.setImageResource(R.drawable.wifi);

        }else{
            mWifiStateIcon.setImageResource(R.drawable.wifi_off);
        }

    }

    /**
     * 清除常亮flag，这个flag可以保证指南针电量供应。
     */
    private void clearFlag()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        },15000);
    }

}
