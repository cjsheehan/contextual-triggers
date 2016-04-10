package com.keepfit.triggers.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.Calendar;
import java.util.GregorianCalendar;

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
