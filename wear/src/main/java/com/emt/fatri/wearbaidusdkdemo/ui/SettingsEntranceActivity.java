package com.emt.fatri.wearbaidusdkdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;

import com.emt.fatri.wearbaidusdkdemo.R;

public class SettingsEntranceActivity extends WearableActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_entrance);
        Button settingEntry=findViewById(R.id.setting_entry_button);
        settingEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SettingsEntranceActivity.this,
                        MapSizeSettingActivity.class);
                SettingsEntranceActivity.this.startActivity(mainIntent);
                SettingsEntranceActivity.this.finish();
            }
        });
        Button helpButton=findViewById(R.id.help);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SettingsEntranceActivity.this,
                        HelpActivity.class);
                SettingsEntranceActivity.this.startActivity(mainIntent);
            }
        });
        Button checkHistoryButton=findViewById(R.id.check_history_alarm);
        checkHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SettingsEntranceActivity.this,
                        AlarmHistoryActivity.class);
                SettingsEntranceActivity.this.startActivity(mainIntent);
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }
}
