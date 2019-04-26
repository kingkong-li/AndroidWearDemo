package com.emt.fatri.wearbaidusdkdemo.datamodel;

import com.emt.fatri.wearbaidusdkdemo.api.BaiduLocationContract;
import com.emt.fatri.wearbaidusdkdemo.utils.MainApplication;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * description:
 * Created by kingkong on 2018/7/9 0009.
 * changed by kingkong on 2018/7/9 0009.
 */

public class BaiduLocationModel implements BaiduLocationContract.ILocationModel {
    private static final String TAG=BaiduLocationModel.class.getSimpleName();


    @Override
    public void startLocate(BaiDuLocationObserver locationObserver) {

        BaiDuRxLocation.get().locate(MainApplication.getInstance()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(locationObserver);
    }

    @Override
    public void stopLocate() {
        BaiDuLocationClient.get(MainApplication.getInstance()).stop();
    }

}
