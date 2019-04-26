package com.emt.fatri.wearbaidusdkdemo.datamodel;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.emt.fatri.wearbaidusdkdemo.utils.GlobalConstant;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * description:
 * Created by kingkong on 2018/7/10 0010.
 * changed by kingkong on 2018/7/10 0010.
 */


public class BaiDuLocationOnSubscribe implements ObservableOnSubscribe {
    private final static String TAG="BaiDuLocationOnSub";
    private final Context context;

    public BaiDuLocationOnSubscribe(Context context) {
        this.context = context;
    }

    @Override
    public void subscribe(final ObservableEmitter e) throws Exception {
        BDAbstractLocationListener bdLocationListener = new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if(GlobalConstant.DEBUG_LOG)Log.v(TAG,"onReceiveLocation bdLocation="+bdLocation.getLongitude());
                e.onNext(bdLocation);
            }
        };
        BaiDuLocationClient.get(context).locate(bdLocationListener);
    }
}