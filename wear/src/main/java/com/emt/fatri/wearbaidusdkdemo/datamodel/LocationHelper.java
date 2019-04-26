package com.emt.fatri.wearbaidusdkdemo.datamodel;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.emt.fatri.wearbaidusdkdemo.api.GoogleLocationContract;
import com.emt.fatri.wearbaidusdkdemo.utils.GlobalConstant;
import com.emt.fatri.wearbaidusdkdemo.utils.MainApplication;
import com.emt.fatri.wearbaidusdkdemo.utils.SharedPreferenceUtil;
import com.emt.fatri.wearbaidusdkdemo.utils.ToastUtils;

import java.io.IOException;
import java.util.List;

/**
 * description:谷歌定位帮助类
 * Created by kingkong on 2018/7/7 0007.
 * changed by kingkong on 2018/7/7 0007.
 */

public class LocationHelper implements LocationListener,GoogleLocationContract.ILocationHelper {
    private static final String TAG=LocationHelper.class.getSimpleName();
    private LocationManager mLocationManager;
    private GoogleLocationContract.ILocationView mLocationView;
    private Location mLocation;

    public LocationHelper(GoogleLocationContract.ILocationView iLocationView) {

        mLocationView=iLocationView;
        // 定位初始化
        mLocationManager = (LocationManager) MainApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
        if (!(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) ||
                mLocationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            ToastUtils.getInstance().show("请打开网络或GPS定位功能!");
        }else
        {
            //设置GPS 请求权限

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(GlobalConstant.DEBUG_LOG)Log.d(TAG, "onProviderDisabled.location = " + location);
        updateLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if(GlobalConstant.DEBUG_LOG)Log.d(TAG, "onStatusChanged() called with " + "provider = [" + provider + "], " +
                "status = [" + status + "], extras = [" + extras + "]");
        switch (status) {
            case LocationProvider.AVAILABLE:
                if(GlobalConstant.DEBUG_LOG)Log.v(TAG, "AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                if(GlobalConstant.DEBUG_LOG)Log.v(TAG, "OUT_OF_SERVICE");
                mLocationView.showErrorMsg("定位失败，重新定位");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                if(GlobalConstant.DEBUG_LOG)Log.v(TAG, "TEMPORARILY_UNAVAILABLE");
                mLocationView.showErrorMsg("定位失败，重新定位");
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {

        if(GlobalConstant.DEBUG_LOG)Log.d(TAG, "onProviderEnabled() called with " + "provider = [" + provider + "]");
        try {
            Location location = mLocationManager.getLastKnownLocation(provider);
            if(GlobalConstant.DEBUG_LOG)Log.d(TAG, "onProviderDisabled.location = " + location);
            updateLocation(location);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if(GlobalConstant.DEBUG_LOG)Log.d(TAG, "onProviderDisabled() called with "
                + "provider = [" + provider + "]");
    }

    @Override
    public void saveCurrentLocationAsSensor(int id) {
        if(mLocation==null)
        {
            ToastUtils.getInstance().show("当前尚未获得位置，请等待重新定位");
            return;
        }
        // 不同地图存储不同的点
        int mapId= SharedPreferenceUtil.getInt(MainApplication.getInstance(),
                GlobalConstant.MAP_ID,GlobalConstant.SHAO_BING_ID);
        // 存储点的key加上地图标志
        String latitudeKey="sensor"+id+"latitude"+mapId;
        if(GlobalConstant.DEBUG_LOG)Log.d(TAG ,"latitudeKey="+latitudeKey);
        SharedPreferenceUtil.putDouble(MainApplication.getInstance(),latitudeKey,mLocation.getLatitude());
        String longitudeKey="sensor"+id+"longitude"+mapId;
        if(GlobalConstant.DEBUG_LOG)Log.d(TAG ,"longitudeKey="+latitudeKey);
        SharedPreferenceUtil.putDouble(MainApplication.getInstance(),longitudeKey,mLocation.getLongitude());
        ToastUtils.getInstance().show(""+SharedPreferenceUtil.getDouble(MainApplication.getInstance(),
                latitudeKey,1.00));

    }
    @Override
    public void savePeopleLocation() {
        if(mLocation==null)
        {
            ToastUtils.getInstance().show("当前尚未获得位置，请等待重新定位");
            return;
        }
        String latitudeKey="originLatitudeKey";
        SharedPreferenceUtil.putDouble(MainApplication.getInstance(),latitudeKey,mLocation.getLatitude());
        String longitudeKey="originLongitudeKey";
        SharedPreferenceUtil.putDouble(MainApplication.getInstance(),longitudeKey,mLocation.getLongitude());
        ToastUtils.getInstance().show(""+SharedPreferenceUtil.getDouble(MainApplication.getInstance(),
                latitudeKey,1.00));
    }


    public void startLocate()
    {
        // 查找到服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired( false );  // 是否需要高度
        criteria.setBearingRequired( false );
        criteria.setCostAllowed( true );
        criteria.setPowerRequirement(Criteria.POWER_HIGH); // 低功耗
        String provider = mLocationManager.getBestProvider(criteria, true);
        try {
            Location location;
            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null){
                if(GlobalConstant.DEBUG_LOG)Log.d(TAG, "onCreate.location = null");
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if(GlobalConstant.DEBUG_LOG)Log.d(TAG, "onCreate.location = " + location);
            mLocationManager.requestLocationUpdates(provider, 3000, 5, this);
        }catch (SecurityException  e){
            e.printStackTrace();
        }
     }
     public void stopLocate()
     {
         mLocationManager.removeUpdates(this);
     }

    /**
     * 更新定位消息
     */
    private void updateLocation(Location location) {
        Geocoder gc = new Geocoder(MainApplication.getInstance());
        List<Address> addresses = null;

        // 解析出来经纬度和地址
        String msg = "";
        if(GlobalConstant.DEBUG_LOG)Log.d(TAG, "updateLocation.location = " + location);
        if (location != null) {
            try {
                addresses = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if(GlobalConstant.DEBUG_LOG)Log.d(TAG, "updateView.addresses = " + addresses);
                if (addresses.size() > 0) {
                    msg += addresses.get(0).getAdminArea().substring(0,2);
                    msg += " " + addresses.get(0).getLocality().substring(0,2);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 经纬度
            if(GlobalConstant.DEBUG_LOG)Log.v(TAG,"\n经度："+location.getLongitude()+"\n纬度："+location.getLatitude()+msg);
//            ToastUtils.getInstance().show("google location 经度="+location.getLongitude()
//            +", 维度="+location.getLatitude());
            mLocation=location;
            mLocationView.showCurrentLocation(location);
        } else {
            if(GlobalConstant.DEBUG_LOG)Log.v(TAG,"定位中");
        }
    }
}
