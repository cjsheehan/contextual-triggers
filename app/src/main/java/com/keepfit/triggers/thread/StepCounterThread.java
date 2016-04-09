package com.keepfit.triggers.thread;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Edward on 4/8/2016.
 */
public class StepCounterThread extends TriggerThread implements SensorEventListener{
    private static final String TAG = "StepCounterThread";
    private static final String TITLE = "Step Counter";

    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private double steps;


    public StepCounterThread() {
        super(TITLE, false);
    }

    public StepCounterThread(Context context) {
        super(context, TITLE, false);
    }

    @Override
    public void doRunAction() {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRunning()) {
                    txtDisplay.setText(String.valueOf(steps));
                    if (steps == 2000) {
                        sendNotification();
                    }
                }
            }
        });
    }

    @Override
    public void doStartAction() {
        mSensorManager =  (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        mSensorManager.registerListener(this, mStepCounterSensor,
                SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    public void doStopAction() {

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
