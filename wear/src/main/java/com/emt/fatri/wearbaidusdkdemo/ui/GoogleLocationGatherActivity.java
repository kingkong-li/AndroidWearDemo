package com.emt.fatri.wearbaidusdkdemo.ui;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.emt.fatri.wearbaidusdkdemo.R;
import com.emt.fatri.wearbaidusdkdemo.api.GoogleLocationContract;
import com.emt.fatri.wearbaidusdkdemo.datamodel.LocationHelper;
import com.emt.fatri.wearbaidusdkdemo.utils.GlobalConstant;
import com.emt.fatri.wearbaidusdkdemo.utils.ToastUtils;

public class GoogleLocationGatherActivity extends WearableActivity implements GoogleLocationContract.ILocationView {

    private LocationHelper mLocationHelper;
    private TextView mTextView;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationHelper=new LocationHelper(this);
        mLocationHelper.startLocate();

        setContentView(R.layout.activity_location_gather);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mTextView =  findViewById(R.id.text);
        mEditText = findViewById(R.id.edit_text);
        mEditText.setText("1");

        Button mSettingButton = findViewById(R.id.Setting);

        mSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sensorId=Integer.parseInt(String.valueOf(mEditText.getText()));
                if(sensorId> GlobalConstant.MAX_lOCATION_POINT)
                {
                    ToastUtils.getInstance().show("当前最多添加"+GlobalConstant.MAX_lOCATION_POINT+"个传感器");
                    return;
                }
                mLocationHelper.saveCurrentLocationAsSensor(sensorId);
                mEditText.setText(String.valueOf(sensorId+1));

            }
        });
        Button mFinishButton = findViewById(R.id.finish);
        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationHelper.savePeopleLocation();
                GoogleLocationGatherActivity.this.finish();
                Intent intent=new Intent();
                intent.setClass(GoogleLocationGatherActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        // Enables Always-on
        setAmbientEnabled();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationHelper.stopLocate();
    }

    @Override
    public void showCurrentLocation(Location location) {
        mTextView.setText("纬度:"+location.getLatitude()
                +"经度:"+location.getLongitude());
    }

    @Override
    public void showErrorMsg(String msg) {

            mTextView.setText(msg);
    }
}
