package com.emt.fatri.wearbaidusdkdemo.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.emt.fatri.wearbaidusdkdemo.R;
import com.emt.fatri.wearbaidusdkdemo.utils.GlobalConstant;
import com.emt.fatri.wearbaidusdkdemo.utils.MainApplication;
import com.emt.fatri.wearbaidusdkdemo.utils.SharedPreferenceUtil;
import com.emt.fatri.wearbaidusdkdemo.utils.ToastUtils;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * 这是一个测试Activity ，等app 稳定正式发布需要删掉。
 */
public class SettingsActivity extends WearableActivity {
    private static String TAG=SettingsActivity.class.getSimpleName();
    private TextView mTextView;
    private Button mSettingButton;
    private Button mFinishButton;
    private EditText mEditText;
    private double mCurrentLatitude;
    private double mCurrentLongitude;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    static final String[] LOCATIONGPS = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_PHONE_STATE};
    private static final int BAIDU_READ_PHONE_STATE = 100;//定位权限请求

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mTextView =  findViewById(R.id.text);
        mEditText = findViewById(R.id.edit_text);
        // Enables Always-on
        setAmbientEnabled();

        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数

        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
    //可选，设置定位模式，默认高精度
    //LocationMode.Hight_Accuracy：高精度；
    //LocationMode. Battery_Saving：低功耗；
    //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
    //可选，设置返回经纬度坐标类型，默认gcj02
    //gcj02：国测局坐标；
    //bd09ll：百度经纬度坐标；
    //bd09：百度墨卡托坐标；
    //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

        option.setScanSpan(1000);
    //可选，设置发起定位请求的间隔，int类型，单位ms
    //如果设置为0，则代表单次定位，即仅定位一次，默认为0
    //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
    //可选，设置是否使用gps，默认false
    //使用高精度和仅用设备两种定位模式的，参数必须设置为true
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        option.setLocationNotify(true);
    //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
    //可选，定位SDK内部是一个service，并放到了独立进程。
    //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
    //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5*60*1000);
    //可选，7.2版本新增能力
    //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位

        option.setEnableSimulateGps(false);
//可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        mLocationClient.setLocOption(option);
    //mLocationClient为第二步初始化过的LocationClient对象
    //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
    //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
    //判断是否为android6.0系统版本，如果是，需要动态添加权限
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PERMISSION_GRANTED) {
                // 没有权限，申请权限。
                ActivityCompat.requestPermissions(this, LOCATIONGPS,
                        BAIDU_READ_PHONE_STATE);
            }
            else
            {
                mLocationClient.restart();
                Log.e(TAG,"mLocationClient.start()  0 ;");
            }

        }
        else
        {
            mLocationClient.restart();
            Log.e(TAG,"mLocationClient.start( ) 1;");
        }
        mSettingButton=findViewById(R.id.Setting);
// 不同地图存储不同的点
        final int mapId=SharedPreferenceUtil.getInt(MainApplication.getInstance(),
                GlobalConstant.MAP_ID,GlobalConstant.SHAO_BING_ID);
        mSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitudeKey="sensor"+mEditText.getText()+"latitude"+mapId;
                Log.e(TAG ,"latitudeKey="+latitudeKey);
                SharedPreferenceUtil.putDouble(MainApplication.getInstance(),latitudeKey,mCurrentLatitude);

                String longitudeKey="sensor"+mEditText.getText()+"longitude"+mapId;
                Log.e(TAG ,"longitudeKey="+latitudeKey);
                SharedPreferenceUtil.putDouble(MainApplication.getInstance(),longitudeKey,mCurrentLongitude);

                if(Integer.parseInt(String.valueOf(mEditText.getText()))>14)
                {
                    latitudeKey="originLatitudeKey";
                    SharedPreferenceUtil.putDouble(MainApplication.getInstance(),latitudeKey,mCurrentLatitude);
                    longitudeKey="originLongitudeKey";
                    SharedPreferenceUtil.putDouble(MainApplication.getInstance(),longitudeKey,mCurrentLongitude);
                }

                ToastUtils.getInstance().show(""+SharedPreferenceUtil.getDouble(MainApplication.getInstance(),
                        latitudeKey,1.00));
            }
        });
        mFinishButton=findViewById(R.id.finish);
        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(SettingsActivity.this,MainActivity.class);
                startActivity(intent);
                mLocationClient.stop();
            }
        });
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
                    mLocationClient.restart();
                    Log.e(TAG,"mLocationClient.start() 2;");
                } else {
                    Log.v(TAG,"no permission");
//                    mLocationClient.stop();

                }
                break;
            default:
                break;
        }
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            mCurrentLatitude = (float) location.getLatitude();    //获取纬度信息
            mCurrentLongitude = (float)location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f


            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            int errorCode = location.getLocType();
            String source="不知道啥定位呢";
            if(errorCode==61)
            {
                source="gps定位成功";
            }
            if(errorCode==161)
            {
                source="网络定位成功";
            }
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            mTextView.setText("维度="+mCurrentLatitude+"经度="+mCurrentLongitude);
            Log.e("JG","latitude="+mCurrentLatitude+", longitude="+mCurrentLongitude+", radius="+radius
                    +"  errorCode="+errorCode+ ""+  location.getAddrStr()+location.getBuildingName()+source);
        }
    }
}
