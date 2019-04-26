package com.emt.fatri.wearbaidusdkdemo.datamodel;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.emt.fatri.wearbaidusdkdemo.utils.GlobalConstant;

/**
 * description:
 * Created by kingkong on 2018/7/10 0010.
 * changed by kingkong on 2018/7/10 0010.
 */

public class BaiDuLocationClient {
    private static final String TAG = BaiDuLocationClient.class.getSimpleName();
    private LocationClient mRealClient;
    private BDAbstractLocationListener mRealListener;
    /**单例*/
    private static volatile BaiDuLocationClient proxyClient;

    private BaiDuLocationClient(Context context) {
        mRealClient = new LocationClient(context);
        LocationClientOption option = new LocationClientOption();
        //设置百度定位参数
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

        mRealClient.setLocOption(option);
    }

    public static BaiDuLocationClient get(Context context) {
        if (proxyClient == null) {
            synchronized (BaiDuLocationClient.class) {
                if (proxyClient == null) {
                    proxyClient = new BaiDuLocationClient(context.getApplicationContext());
                }
            }
        }
        return proxyClient;
    }

    public void locate(final BDAbstractLocationListener bdLocationListener) {
            mRealListener= new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                bdLocationListener.onReceiveLocation(bdLocation);
                Log.v(TAG,"bdLocation"+bdLocation.getLocType());
                // 防止内存溢出 停止  如果定位一次就停止
                // mRealClient.unRegisterLocationListener(this);
                // stop();
            }
        };
        mRealClient.registerLocationListener(mRealListener);
        if (!mRealClient.isStarted()) {
            mRealClient.start();
        }
    }

    public BDLocation getLateKnownLocation() {
        return mRealClient.getLastKnownLocation();
    }

    public void stop() {
        if(GlobalConstant.DEBUG_LOG)Log.v(TAG,"stop locate start");
        if(mRealListener!=null)
        {
            mRealClient.unRegisterLocationListener(mRealListener);
        } else
        {
            if(GlobalConstant.ERROR_LOG)Log.v(TAG,"mRealListener==null");
        }
        mRealClient.stop();
    }

}
