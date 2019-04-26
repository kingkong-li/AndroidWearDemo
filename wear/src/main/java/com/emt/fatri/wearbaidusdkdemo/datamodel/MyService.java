package com.emt.fatri.wearbaidusdkdemo.datamodel;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;

import com.emt.fatri.wearbaidusdkdemo.provider.NodeManageApi;
import com.emt.fatri.wearbaidusdkdemo.ui.MainActivity;
import com.emt.fatri.wearbaidusdkdemo.utils.GlobalConstant;
import com.emt.fatri.wearbaidusdkdemo.utils.MainApplication;
import com.emt.fatri.wearbaidusdkdemo.utils.NetworkUtil;

import java.util.ArrayList;

public class MyService extends Service implements SocketServer.OnDataReceiveListener {
    private static String LOG_CLASS_NAME = MyService.class.getSimpleName();
    private Context mContext;
    private volatile int mSensorNumber=0;
    private long mLastAlarmTime=0;

    public MyService() {

        if(GlobalConstant.DEBUG_LOG)Log.v(LOG_CLASS_NAME, "MyService()");
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            switch (message.what) {
                case 1:

                    if (vibrator != null) {
                        vibrator.vibrate(168);
                    }

                    ArrayList<Integer> sensorStateList = message.getData().getIntegerArrayList(GlobalConstant.SENSOR_LIST_STATE);
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(mContext, MainActivity.class);
                    intent.putExtra(GlobalConstant.SENSOR_LIST_STATE, sensorStateList);
                    if(GlobalConstant.DEBUG_LOG)Log.v(LOG_CLASS_NAME,sensorStateList.toString());
                    startActivity(intent);

                    break;

                case 2:

                    if(!NetworkUtil.isWifiConnected(MainApplication.getInstance()))
                    {
                        Intent networkIntent = new Intent();
                        networkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        networkIntent.setClass(mContext, MainActivity.class);
                        networkIntent.putExtra(GlobalConstant.NETWORK_STATE,GlobalConstant.NETWORK_ERROR_CODE);
                        startActivity(networkIntent);
                        break;
                    }else
                    {
                        if (vibrator != null) {
                            vibrator.vibrate(200);  // 震动0.1s
                        }
                        Intent unkownErrorIntent = new Intent();
                        unkownErrorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        unkownErrorIntent.setClass(mContext, MainActivity.class);
                        unkownErrorIntent.putExtra(GlobalConstant.NETWORK_STATE,GlobalConstant.NETWORK_ERROR_CODE);
                        startActivity(unkownErrorIntent);
                    }


            }
            return false;
        }
    });

    @Override
    public void onCreate() {
        if(GlobalConstant.DEBUG_LOG)Log.v(LOG_CLASS_NAME, "onCreate");
        startForeground(1314, new Notification());
        mContext = this;
        SocketServer.getInstance().setOnDataListener(this);
        SocketServer.getInstance().start();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(GlobalConstant.DEBUG_LOG)Log.v(LOG_CLASS_NAME, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onReceiveData(String dataSource) {
        // 心跳包
        Log.e(LOG_CLASS_NAME, "dataSource=" + dataSource);
        mHandler.removeMessages(2);
        mHandler.sendEmptyMessageDelayed(2, GlobalConstant.ALARM_TIME_OUT);
        //
        String[] data = dataSource.split("#");
        for (int number = 0; number < data.length; number++) {

            boolean shouldShock = false;
            String[] per = data[number].split(",", GlobalConstant.MAX_lOCATION_POINT+1);
            if(GlobalConstant.DEBUG_LOG)Log.d(LOG_CLASS_NAME, "dataSource=" +
                    data[number] + ",per.length=" + per.length);
            if(per.length>GlobalConstant.MAX_lOCATION_POINT+1)
            {
                Log.e(LOG_CLASS_NAME,"非法数据");
                return;
            }
            // 传感器状态列表， 0 代表传感器收到异常信息 1 正常信息
            ArrayList<Integer> sensorStateList = new ArrayList<>();
            try {
                for (int i = 0; i < (per.length-1); i++) {
                    Log.e(LOG_CLASS_NAME, "i=" + i + ", " + per[i]);
                    int x = 1; //默认值1 数据出错，认为是1正常
                    if (per[i] != null && per[i].length() > 0) {
                        String m = String.valueOf(per[i].charAt(0));
                        x = Integer.parseInt(m);
                    }
                    if (x == 0) {
                        shouldShock = true;
                    }
                    sensorStateList.add(x);
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            mSensorNumber=sensorStateList.size();

            if (!shouldShock) {
                return;
            }
            if((System.currentTimeMillis()-mLastAlarmTime)<10000)
            {
                // 10s内不重复报警
                Log.v(LOG_CLASS_NAME,"interval<10s");
                return;
            }
            mLastAlarmTime=System.currentTimeMillis();
            NodeInfo nodeInfo=new NodeInfo();
            nodeInfo.createTimeInMills=System.currentTimeMillis();
            nodeInfo.stateCode=data[number];
            NodeManageApi.insertNewItemToDb(nodeInfo);

            Message msg = new Message();
            msg.what = 1;
            Bundle bundle = new Bundle();
            bundle.putIntegerArrayList(GlobalConstant.SENSOR_LIST_STATE, sensorStateList); //往Bundle中put数据
            msg.setData(bundle);//mes利用Bundle传递数据
            mHandler.sendMessage(msg);//用activity中的handler发送消息


        }

    }

}
