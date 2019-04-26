package com.emt.fatri.wearbaidusdkdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.emt.fatri.wearbaidusdkdemo.R;
import com.emt.fatri.wearbaidusdkdemo.utils.GlobalConstant;
import com.emt.fatri.wearbaidusdkdemo.utils.MainApplication;
import com.emt.fatri.wearbaidusdkdemo.utils.SharedPreferenceUtil;
import com.emt.fatri.wearbaidusdkdemo.utils.ToastUtils;

public class MapChooseActivity extends WearableActivity {

    private static final String TAG = MapChooseActivity.class.getSimpleName();
    private Button mChooseMap1;
    private Button mChooseMap2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_map_setting);
        init();



    }

    private void init() {
        mChooseMap1 =  findViewById(R.id.shao_bing);
        mChooseMap1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceUtil.putInt(MainApplication.getInstance(), GlobalConstant.MAP_ID,GlobalConstant.SHAO_BING_ID);
                int result=SharedPreferenceUtil.getInt(MainApplication.getInstance(),
                        GlobalConstant.MAP_ID,GlobalConstant.SHAO_BING_ID);
                if(result==GlobalConstant.SHAO_BING_ID)
                {
                    ToastUtils.getInstance().show("当前地图已经设置为哨兵地图");
                } else
                {
                    ToastUtils.getInstance().show("设置失败，保持上一次设置");
                }

                MapChooseActivity.this.finish();
                Intent intent=new Intent();
                intent.setClass(MapChooseActivity.this,BaiduLocationGatherActivity.class);
//                intent.setClass(MapChooseActivity.this,GoogleLocationGatherActivity.class);
                startActivity(intent);
            }
        });
        mChooseMap2 =  findViewById(R.id.dao_yao_ku);
        mChooseMap2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceUtil.putInt(MainApplication.getInstance(), GlobalConstant.MAP_ID,GlobalConstant.DA_YAO_KU_ID);
                int result=SharedPreferenceUtil.getInt(MainApplication.getInstance(),
                        GlobalConstant.MAP_ID,GlobalConstant.SHAO_BING_ID);
                if(result==GlobalConstant.DA_YAO_KU_ID)
                {
                    ToastUtils.getInstance().show("当前地图已经设置为弹药库地图");
                } else
                {
                    ToastUtils.getInstance().show("设置失败，保持上一次设置");
                }
                MapChooseActivity.this.finish();
                Intent intent=new Intent();
                intent.setClass(MapChooseActivity.this,BaiduLocationGatherActivity.class);
//                intent.setClass(MapChooseActivity.this,GoogleLocationGatherActivity.class);
                startActivity(intent);
            }
        });



        // Enables Always-on
        setAmbientEnabled();
    }


}
