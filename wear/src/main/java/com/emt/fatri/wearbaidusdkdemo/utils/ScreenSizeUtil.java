package com.emt.fatri.wearbaidusdkdemo.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;

/**
 * description: 获取android设备分辨率的工具
 * Created by kingkong on 2018/7/20 0020.
 * changed by kingkong on 2018/7/20 0020.
 */

public class ScreenSizeUtil {
    private static final String TAG=ScreenSizeUtil.class.getSimpleName();

    public static void printScreenSize(Activity activity) {

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getRealSize(outSize);
        int width = outSize.x;
        int height = outSize.y;
        if(GlobalConstant.DEBUG_LOG)Log.v(TAG, "getSize width=" + width + ",height="
                + height + ",dp=" + px2dip(activity, width));

    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
