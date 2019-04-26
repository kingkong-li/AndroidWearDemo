package com.emt.fatri.wearbaidusdkdemo.api;

import com.baidu.location.BDLocation;
import com.emt.fatri.wearbaidusdkdemo.datamodel.BaiDuLocationObserver;

/**
 * description:百度定位MVP接口
 * Created by kingkong on 2018/7/9 0009.
 * changed by kingkong on 2018/7/9 0009.
 */

public class BaiduLocationContract {
    /**
     * V视图层
     */
    public interface ILocationView {
        /**显示当前定位*/
        void showCurrentLocation(BDLocation bdLocation);
        /**发生错误就显示错误信息*/
        void showErrorMsg(String msg);
    }

    /**
     * P视图与逻辑处理的连接层
     */
    public interface ILocationPresenter {
        /**开始定位*/
        void startLocate();
        /**停止定位*/
        void stopLocate();
        /**保存观测点位置*/
        void savePeopleLocation();
        /**记录当前定位 作为传感器id的位置*/
        void saveCurrentLocationAsSensor(int id);
    }

    /**
     * 逻辑处理层
     */
    public interface ILocationModel {
        void startLocate(BaiDuLocationObserver locationObserver);
        void stopLocate();

    }
}
