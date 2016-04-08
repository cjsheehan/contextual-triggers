package com.mcnedward.triggers.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mcnedward.triggers.R;
import com.mcnedward.triggers.TriggerService;
import com.mcnedward.triggers.view.TriggerSettingView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Intent serviceIntent;
    Button btnTriggers;
    private List<TriggerSettingView> triggerSettingViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize() {
        serviceIntent = new Intent(this, TriggerService.class);

        initializeTriggerSettings();

        btnTriggers = (Button) findViewById(R.id.btn_triggers);
        btnTriggers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TriggerService.isRunning()) {
                    TriggerService.stop();
                    stopService(serviceIntent);
                    btnTriggers.setText(R.string.start_triggers);
                } else {
                    startService(serviceIntent);
                    btnTriggers.setText(R.string.stop_triggers);
                }
            }
        });
    }

    private void initializeTriggerSettings() {
        triggerSettingViewList = new ArrayList<>();
        triggerSettingViewList.add((TriggerSettingView) findViewById(R.id.setting_first));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
