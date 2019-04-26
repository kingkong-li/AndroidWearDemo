package com.emt.fatri.wearbaidusdkdemo.ui;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

import com.emt.fatri.wearbaidusdkdemo.R;

public class HelpActivity extends WearableActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Enables Always-on
        setAmbientEnabled();
    }
}
