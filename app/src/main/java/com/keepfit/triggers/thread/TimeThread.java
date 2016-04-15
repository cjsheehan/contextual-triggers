package com.keepfit.triggers.thread;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.keepfit.triggers.utils.Broadcast;
import com.keepfit.triggers.utils.Dates;
import com.keepfit.triggers.utils.TriggerCache;
import com.keepfit.triggers.utils.enums.TriggerType;

import java.util.Calendar;

/**
 * Created by Edward on 4/8/2016.
 */
public class TimeThread extends TriggerThread<String> {
    private static final String TAG = "TimeThread";
    private static final String TITLE = "Time";

    String currentTime;
    private TimeInterval[] timeIntervals;

    public TimeThread(Context context) {
        super(TITLE, TriggerType.TIME, false, context);
    }

    @Override
    public void doRunAction() {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRunning()) {
                    currentTime = Dates.getTime();
                    txtDisplay.setText(currentTime);
                    TriggerCache.put(TriggerType.TIME, currentTime);

                    TimeInterval interval = checkInterval();
                    if (interval != null) {
                        Broadcast.broadcastTimeReached(context, interval.timeStamp);
                    }
                }
            }
        });
    }

    public String getTriggerObject() {
        return currentTime;
    }

    private TimeInterval checkInterval() {
        TimeInterval interval = null;
        for (TimeInterval ti : timeIntervals) {
            if (ti.passed)
                continue;
            try {
                ti.passed = Dates.timeIntervalPassed(ti.timeStamp);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            if (ti.passed)
                interval = ti;
        }
        return interval;
    }

    @Override
    public void doStartAction() {
        timeIntervals = new TimeInterval[]{
                new TimeInterval("09:00:00"),
                new TimeInterval("12:00:00"),
                new TimeInterval("15:00:00"),
                new TimeInterval("17:00:00"),
                new TimeInterval("20:00:00")
        };
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
        return String.format("You reached the goal for the time!");
    }

    class TimeInterval {
        String timeStamp;
        boolean passed;

        public TimeInterval(String timeStamp) {
            this.timeStamp = timeStamp;
        }

        public TimeInterval(String timeStamp, boolean passed) {
            this.timeStamp = timeStamp;
            this.passed = passed;
        }
    }

}
