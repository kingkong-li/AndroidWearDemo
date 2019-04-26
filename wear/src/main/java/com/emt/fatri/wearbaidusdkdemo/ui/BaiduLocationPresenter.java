package com.emt.fatri.wearbaidusdkdemo.ui;

import android.support.annotation.NonNull;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.emt.fatri.wearbaidusdkdemo.R;
import com.emt.fatri.wearbaidusdkdemo.api.BaiduLocationContract;

import com.emt.fatri.wearbaidusdkdemo.datamodel.BaiduLocationModel;
import com.emt.fatri.wearbaidusdkdemo.datamodel.BaiDuLocationObserver;
import com.emt.fatri.wearbaidusdkdemo.utils.GlobalConstant;
import com.emt.fatri.wearbaidusdkdemo.utils.MainApplication;
import com.emt.fatri.wearbaidusdkdemo.utils.SharedPreferenceUtil;
import com.emt.fatri.wearbaidusdkdemo.utils.ToastUtils;

import io.reactivex.disposables.Disposable;



/**
 * description:
 * Created by kingkong on 2018/7/9 0009.
 * changed by kingkong on 2018/7/9 0009.
 */

public class BaiduLocationPresenter implements BaiduLocationContract.ILocationPresenter {
    private static final String TAG =BaiduLocationPresenter.class.getSimpleName() ;
    private BaiduLocationContract.ILocationView mLocationView;
    private BaiduLocationContract.ILocationModel mLocationModel;
    private BDLocation mLocation;
    private BaiDuLocationObserver mLocationObserver=new BaiDuLocationObserver() {
        @Override
        public void onLocatedSuccess(@NonNull BDLocation bdLocation) {
            Log.v(TAG,"bdLocation="+bdLocation.getAddrStr());
            mLocationView.showCurrentLocation(bdLocation);
            mLocation=bdLocation;
        }

        @Override
        public void onLocatedFail(BDLocation bdLocation) {
            if(GlobalConstant.DEBUG_LOG)Log.v(TAG," error bdLocation="+bdLocation.getAddrStr());
            String errorString=MainApplication.getInstance().getString(R.string.location_fail)+bdLocation.getLocType();
            mLocationView.showErrorMsg(errorString);
        }

        @Override
        public void onSubscribe(Disposable d) {

        }
    };

    public BaiduLocationPresenter(BaiduLocationContract.ILocationView iLocationView)
    {
        this.mLocationView=iLocationView;
        mLocationModel=new BaiduLocationModel();

    }

    @Override
    public void startLocate() {
        mLocationModel.startLocate(mLocationObserver);
    }

    @Override
    public void stopLocate() {
        mLocationModel.stopLocate();

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

    @Override
    public void saveCurrentLocationAsSensor(int id) {

        if(mLocation==null)
        {
            ToastUtils.getInstance().show("当前尚未获得位置，请等待重新定位");
            return;
        }
        // 不同地图存储不同的点
        int mapId=SharedPreferenceUtil.getInt(MainApplication.getInstance(),
                GlobalConstant.MAP_ID,GlobalConstant.SHAO_BING_ID);
        // 存储点的key加上地图标志
        String latitudeKey="sensor"+id+"latitude"+mapId;
        SharedPreferenceUtil.putDouble(MainApplication.getInstance(),latitudeKey,mLocation.getLatitude());
        String longitudeKey="sensor"+id+"longitude"+mapId;
        if(GlobalConstant.DEBUG_LOG)Log.v(TAG ,"latitudeKey="+latitudeKey+", longitudeKey="+latitudeKey);
        SharedPreferenceUtil.putDouble(MainApplication.getInstance(),longitudeKey,mLocation.getLongitude());
        ToastUtils.getInstance().show(""+SharedPreferenceUtil.getDouble(MainApplication.getInstance(),
                latitudeKey,1.00));
    }
}
