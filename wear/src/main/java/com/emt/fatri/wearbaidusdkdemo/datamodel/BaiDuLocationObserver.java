package com.emt.fatri.wearbaidusdkdemo.datamodel;

import android.support.annotation.NonNull;

import com.baidu.location.BDLocation;
import com.emt.fatri.wearbaidusdkdemo.utils.LocationUtil;

import io.reactivex.Observer;


/**
 * description:
 * Created by kingkong on 2018/7/10 0010.
 * changed by kingkong on 2018/7/10 0010.
 */

public abstract class BaiDuLocationObserver implements Observer<BDLocation> {

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable throwable) {
        onLocatedFail(null);
    }

    @Override
    public void onNext(BDLocation bdLocation) {
        if (LocationUtil.isLocationResultEffective(bdLocation)) {
            onLocatedSuccess(bdLocation);
        } else {
            onLocatedFail(bdLocation);
        }
    }

    public abstract void onLocatedSuccess(@NonNull BDLocation bdLocation);
    public abstract void onLocatedFail(BDLocation bdLocation);

}
