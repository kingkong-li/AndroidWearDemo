package com.emt.fatri.wearbaidusdkdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.emt.fatri.wearbaidusdkdemo.R;
import com.emt.fatri.wearbaidusdkdemo.utils.GlobalConstant;
import com.emt.fatri.wearbaidusdkdemo.utils.MainApplication;
import com.emt.fatri.wearbaidusdkdemo.utils.SharedPreferenceUtil;
import com.emt.fatri.wearbaidusdkdemo.utils.ToastUtils;

public class MapSizeSettingActivity extends WearableActivity {


    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_size_setting);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mEditText = findViewById(R.id.edit_text);
        int lastAlarmDistance=SharedPreferenceUtil.getInt(MainApplication.getInstance(),
                GlobalConstant.KEY_MAP_SIZE,GlobalConstant.DEFAULT_MAP_RADIUS);
        mEditText.setText(String.valueOf(lastAlarmDistance));

        Button finishButton = findViewById(R.id.finish);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int alarmDistance=Integer.parseInt(String.valueOf(mEditText.getText()));
                if(alarmDistance<GlobalConstant.GPS_PRECISION)
                {
                    ToastUtils.getInstance().show("距离太小，gps精度不够，请重新设定");
                    return;
                }
                SharedPreferenceUtil.putInt(MainApplication.getInstance(),GlobalConstant.KEY_MAP_SIZE,alarmDistance);
                int result=SharedPreferenceUtil.getInt(MainApplication.getInstance(),
                        GlobalConstant.KEY_MAP_SIZE,GlobalConstant.DEFAULT_MAP_RADIUS);
                if(result==alarmDistance)
                {
                    ToastUtils.getInstance().show("设定预警距离为"+result+"米");
                    MapSizeSettingActivity.this.finish();
                    Intent intent=new Intent();
                    intent.setClass(MapSizeSettingActivity.this,MapChooseActivity.class);
                    startActivity(intent);
                }else {
                    ToastUtils.getInstance().show("设定失败，请重新尝试");
                }

            }
        });
        // Enables Always-on
        setAmbientEnabled();
    }
}
