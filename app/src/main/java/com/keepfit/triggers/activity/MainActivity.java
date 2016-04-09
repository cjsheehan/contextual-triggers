package com.keepfit.triggers.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.keepfit.triggers.R;
import com.keepfit.triggers.TriggerService;
import com.keepfit.triggers.thread.DateThread;
import com.keepfit.triggers.thread.LocationThread;
import com.keepfit.triggers.thread.StepCounterThread;
import com.keepfit.triggers.thread.TimeThread;
import com.keepfit.triggers.thread.TriggerThread;
import com.keepfit.triggers.thread.WeatherThread;
import com.keepfit.triggers.view.TriggerSettingView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Intent serviceIntent;
    Button btnTriggers;
    boolean fromNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fromNotification = false;
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            fromNotification = bundle.getBoolean("isNotification");
        }
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
        LinearLayout container = (LinearLayout) findViewById(R.id.container_triggers);

        // Only create the threads on the first time the application is created, and not if the user is coming from a notification
        if (!fromNotification) {
            // Add new trigger threads here
            TriggerService.addThread(new TimeThread(this));
            TriggerService.addThread(new DateThread(this));
            TriggerService.addThread(new StepCounterThread(this));
            TriggerService.addThread(new WeatherThread(this));
            TriggerService.addThread(new LocationThread(this));
            TriggerService.setContext(this);
        }

        for (TriggerThread thread : TriggerService.getThreads()) {
            container.addView(new TriggerSettingView(this, thread));
        }
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
