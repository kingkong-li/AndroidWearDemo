package com.emt.fatri.wearbaidusdkdemo.datamodel;

import android.content.Context;

import io.reactivex.Observable;

/**
 * description:
 * Created by kingkong on 2018/7/10 0010.
 * changed by kingkong on 2018/7/10 0010.
 */

public class BaiDuRxLocation {

    private static BaiDuRxLocation instance = new BaiDuRxLocation();

    private BaiDuRxLocation() {}

    public static BaiDuRxLocation get() {
        return instance;
    }

    public Observable locate(Context context) {
        return Observable.create(new BaiDuLocationOnSubscribe(context));
    }

    public Observable locateLastKnown(Context context) {
        return Observable.create(new LocationLateKnownOnSubscribe(context));
    }

}