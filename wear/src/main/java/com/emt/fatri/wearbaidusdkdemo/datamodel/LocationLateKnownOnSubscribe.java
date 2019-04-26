package com.emt.fatri.wearbaidusdkdemo.datamodel;

import android.content.Context;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.emt.fatri.wearbaidusdkdemo.utils.LocationUtil;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * description:
 * Created by kingkong on 2018/7/10 0010.
 * changed by kingkong on 2018/7/10 0010.
 */

public class LocationLateKnownOnSubscribe implements ObservableOnSubscribe {

    private final Context context;

    public LocationLateKnownOnSubscribe(Context context) {
        this.context = context;
    }


    @Override
    public void subscribe(final ObservableEmitter e) throws Exception {
        BDLocation lateKnownLocation = BaiDuLocationClient.get(context).getLateKnownLocation();
        if (LocationUtil.isLocationResultEffective(lateKnownLocation)) {
            e.onNext(lateKnownLocation);
            e.onComplete();
        } else {
            BDAbstractLocationListener bdLocationListener = new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    e.onNext(bdLocation);
                    e.onComplete();
                }
            };
            BaiDuLocationClient.get(context).locate(bdLocationListener);
        }
    }
}
