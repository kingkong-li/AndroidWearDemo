package com.emt.fatri.wearbaidusdkdemo.api;

import android.location.Location;

/**
 * description: 定位合同 谷歌 实际应用中百度和谷歌选择一个。
 * Created by kingkong on 2018/7/7 0007.
 * changed by kingkong on 2018/7/7 0007.
 */

public class GoogleLocationContract {
    /**
     * V视图层
     */
   public interface ILocationView {
        /**
         * 显示当前定位
         * @param location 位置
         */
        void showCurrentLocation(Location location);

        /**
         * 发生错误就显示错误信息
         * @param msg 错误提示
         */
        void showErrorMsg(String msg);
    }
    /**
     * P视图与逻辑处理的连接层
     */
    public interface ILocationHelper {
        /**
         * 开始定位
         */
        void startLocate();

        /**
         * 停止定位
         */
        void stopLocate();

        /**
         * 记录当前位置到传感器id
         * @param id 传感器编号
         */
        void saveCurrentLocationAsSensor(int id);//记录当前定位

        /**
         * 记录下当前人的位置，也就是监测点位置
         */
        void savePeopleLocation();
    }

}
