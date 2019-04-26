package com.emt.fatri.wearbaidusdkdemo.utils;

import com.baidu.location.BDLocation;

/**
 * description:定位工具类
 * Created by kingkong on 2018/7/10 0010.
 * changed by kingkong on 2018/7/10 0010.
 */

public class LocationUtil {

    /**地球半径*/
    private static  final double EARTH_RADIUS = 6378137.0;
   /**
    * 检查当前定位结果是否有效
    */
    public static boolean isLocationResultEffective(BDLocation bdLocation) {
        return bdLocation != null
                && (bdLocation.getLocType() == BDLocation.TypeGpsLocation
                || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation);
    }

    /**
     * 根据两点经纬度得到两点的距离。单位m
     * @param longitude1 点1的经度
     * @param latitude1  点1的维度
     * @param longitude2 点2的经度
     * @param latitude2  点2的维度
     * @return 亮点间的距离
     */
    public static double getDistance(double longitude1, double latitude1,
                              double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

}
