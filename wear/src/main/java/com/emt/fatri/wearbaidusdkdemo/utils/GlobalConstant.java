package com.emt.fatri.wearbaidusdkdemo.utils;

/**
 * description:全局的一些设置
 * Created by kingkong on 2018/7/11 0011.
 * changed by kingkong on 2018/7/11 0011.
 */

public class GlobalConstant {
    public static final String MAP_ID = "map_id";
    public static final int SHAO_BING_ID=104;
    public static final int DA_YAO_KU_ID=107;
    public static final int MAX_lOCATION_POINT=16;
    /**同步时间设置：在系统启动后多久没有收到同步信号就会报警,单位ms*/
    public static final int ALARM_TIME_OUT=20000;
     /**GPS精度设置  单位m 小于这个精度的我们认为是脏数据，也可以根据实测来进行设置*/
    public static final int GPS_PRECISION=3;

    /**系统最大监测距离*/
    public static final String KEY_MAP_SIZE ="map_size";
    public static final int  DEFAULT_MAP_RADIUS=100;
    public static final String SENSOR_TOTAL_NUMBER="sensor_total_number";
    /**传感器状态列表*/
    public static final String SENSOR_LIST_STATE ="sensor_state_list";
    /**网络状态*/
    public static final String NETWORK_STATE ="net_work_state";
    public static final int NETWORK_ERROR_CODE = 1346;
    /**启动界面显示时间，单位ms*/
    public static final long SPLASH_SHOW_TIME = 1000;
    /**是否首次启动的SP*/
    public static final String IS_FIRST_LAUNCH ="is_first_launch";
    /**震动报警间隔时间，单位ms*/
    public static final long ALARM_INTERVAL = 15000;

    /**debug log开关*/
    public static final boolean DEBUG_LOG=false;
    /**error log 开关*/
    public static final boolean ERROR_LOG=true;
    /** 需要开的log开关*/
    public static final boolean ALWAYS_LOG=true;
}
