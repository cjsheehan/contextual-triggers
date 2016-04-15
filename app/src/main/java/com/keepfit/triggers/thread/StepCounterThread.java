package com.keepfit.triggers.thread;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;

import com.keepfit.triggers.utils.Broadcast;
import com.keepfit.triggers.utils.TriggerCache;
import com.keepfit.triggers.utils.enums.TriggerPreference;
import com.keepfit.triggers.utils.enums.TriggerType;

/**
 * Created by Edward on 4/8/2016.
 */
public class StepCounterThread extends TriggerThread<Double> implements SensorEventListener {
    private static final String TAG = "StepCounterThread";
    private static final String TITLE = "Step Counter";

    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private SharedPreferences prefs;
    private double steps;
    double dailyGoal;
    double previousProgress;

    public StepCounterThread(Context context) {
        super(TITLE, TriggerType.STEP_COUNTER, false, context);
    }

    @Override
    public void doRunAction() {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRunning()) {

                    dailyGoal = Double.parseDouble(prefs.getString(TriggerPreference.STEP_LENGTH.title, "1000"));

                    double progress = steps * 100 / dailyGoal;
                    TriggerCache.put(TriggerType.STEP_COUNTER, progress);

                    txtDisplay.setText(String.valueOf(steps));
                    if (previousProgress + 10 < progress) {
                        previousProgress = progress;
                        Broadcast.broadcastStepCompleteness(context, progress);
                    }
                }
            }
        });
    }

    @Override
    public void doStartAction() {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        mSensorManager.registerListener(this, mStepCounterSensor,
                SensorManager.SENSOR_DELAY_FASTEST);

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void doStopAction() {

    }

    @Override
    public Double getTriggerObject() {
        return steps;
    }

    @Override
    protected String getTitle() {
        return TITLE;
    }

    @Override
    protected String getMessage() {
        return String.format("You reached the goal for the step counter!");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//            count.setText(String.valueOf(event.values[0]));
        steps = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
