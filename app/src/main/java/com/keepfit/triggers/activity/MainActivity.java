package com.keepfit.triggers.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.keepfit.triggers.R;
import com.keepfit.triggers.listener.PermissionResponseListener;
import com.keepfit.triggers.notification.Notification;
import com.keepfit.triggers.service.TriggerService;
import com.keepfit.triggers.listener.PermissionRequestListener;
import com.keepfit.triggers.thread.CalendarThread;
import com.keepfit.triggers.thread.LocationThread;
import com.keepfit.triggers.thread.PointsOfInterestThread;
import com.keepfit.triggers.thread.StepCounterThread;
import com.keepfit.triggers.thread.TimeThread;
import com.keepfit.triggers.thread.TriggerThread;
import com.keepfit.triggers.thread.WeatherThread;
import com.keepfit.triggers.utils.TriggerCache;
import com.keepfit.triggers.utils.enums.Scenario;
import com.keepfit.triggers.utils.enums.TriggerType;
import com.keepfit.triggers.view.TriggerSettingView;

public class MainActivity extends AppCompatActivity implements PermissionRequestListener {
    private static final String TAG = "MainActivity";

    Intent serviceIntent;
    Button btnTriggers;
    boolean fromNotification;
    PermissionResponseListener permissionResponseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromNotification = false;
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            fromNotification = bundle.getBoolean("isNotification");
            if (fromNotification) {
                for (Scenario scenario : Scenario.values()) {
                    Notification notification = (Notification) bundle.get(scenario.title);
                    if (notification != null) {
                        notification.setIsViewed(true);
                        TriggerCache.put(scenario, notification);
                        Log.w(TAG, String.format("Update %s in the cache.", notification));
                    }
                }
            }
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
                if (!TriggerService.isStarted()) {
                    startService(serviceIntent);
                    btnTriggers.setText(R.string.stop_triggers);

                } else {
                    if (TriggerService.isRunning()) {
                        TriggerService.pauseService(true);
                        btnTriggers.setText(R.string.start_triggers);
                    } else {
                        TriggerService.pauseService(false);
                        btnTriggers.setText(R.string.stop_triggers);
                    }
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
            TriggerService.addThread(new CalendarThread(this));
            TriggerService.addThread(new StepCounterThread(this));
            TriggerService.addThread(new WeatherThread(this));
            TriggerService.addThread(new LocationThread(this, this));
            TriggerService.addThread(new PointsOfInterestThread(this, this));
            TriggerService.setContext(this);
        }

        for (TriggerThread thread : TriggerService.getThreads()) {
            if (thread.getTriggerType() == TriggerType.TIME || thread.getTriggerType() == TriggerType.STEP_COUNTER)
                container.addView(new TriggerSettingView(this, thread));
            else
                container.addView(new TriggerSettingView(this, thread, true));
        }
    }

    public static final int LOCATION_PERMISSION_CODE = 100;

    @Override
    public void notifyPermissionRequested(PermissionResponseListener permissionResponseListener) {
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission
                                .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_CODE);
        this.permissionResponseListener = permissionResponseListener;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    permissionResponseListener.notifyPermissionGranted();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Location Access Denied", Toast.LENGTH_SHORT)
                            .show();
                    permissionResponseListener.notifyPermissionDenied();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        stopService(serviceIntent);
        super.onDestroy();
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
