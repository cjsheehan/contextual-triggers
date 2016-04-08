package com.mcnedward.triggers.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.mcnedward.triggers.R;
import com.mcnedward.triggers.activity.MainActivity;

/**
 * Created by Edward on 4/8/2016.
 */
public class Extension {
    private static final String TAG = "Extension";

    /**
     * Creates a new RippleDrawable for a ripple effect on a View.
     *
     * @param rippleColor     The color of the ripple.
     * @param backgroundColor The color of the background for the ripple. If this is 0, then there will be no background and the ripple effect will be circular.
     * @param context         The context.
     * @return A RippleDrawable.
     */
    public static void setRippleBackground(View view, int rippleColor, int backgroundColor, Context context) {
        view.setBackground(new RippleDrawable(
                new ColorStateList(
                        new int[][]
                                {
                                        new int[]{android.R.attr.state_window_focused},
                                },
                        new int[]
                                {
                                        ContextCompat.getColor(context, rippleColor),
                                }),
                backgroundColor == 0 ? null : new ColorDrawable(ContextCompat.getColor(context, backgroundColor)),
                null));
    }

    /**
     * Creates a new RippleDrawable for a ripple effect on a View. This will create a ripple with the default color of FireBrick for the ripple and GhostWhite for the background.
     *
     * @param context The context.
     * @return A RippleDrawable.
     */
    public static void setRippleBackground(View view, Context context) {
        setRippleBackground(view, R.color.FireBrick, R.color.GhostWhite, context);
    }

    /*****
     * Starting activity
     *****/

    public static void startAddGoalPopup(final Activity activity) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Intent intent = new Intent(activity, AddGoalPopup.class);
//                activity.startActivityForResult(intent, Action.ADD_GOAL_POPUP.id);
            }
        }, 300);
    }

    public static void startSettingsResetActivity(final Activity activity) {
//        Intent intent = new Intent(activity, SettingsResetActivity.class);
//        activity.startActivity(intent);
    }

    public static void setAlgorithmRunning(boolean running, final Activity activity) {
//        Intent intent = new Intent(activity, AlgorithmService.class);
//        intent.putExtra(Settings.RUNNING_ALGORITHM.name(), running);
//        activity.startService(intent);
//        broadcastAlgorithmRunning(running, activity);
    }

    public static void restartApplication(final Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("exiting", true);
        activity.startActivity(intent);
    }

    /*****
     * Broadcasting
     *****/

    public static void broadcastAddGoal(Context context) {
//        Intent intent = new Intent(Action.ADD_GOAL.title);
//        intent.putExtra("goal", goal);
//        intent.putExtra("action", Action.ADD_GOAL.id);
//        context.sendBroadcast(intent);
    }

    /*****
     * Notifications
     *****/

    /**
     * Source: http://developer.android.com/training/notify-user/build-notification.html
     * @param activity
     */
    public static void sendNotificationGoalReached(final Activity activity) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(activity)
//                        .setSmallIcon(R.drawable.ic_sentiment_very_satisfied_black_24dp)
                        .setContentTitle("Notify!")
                        .setContentText("Notification here!");

        Intent intent = new Intent(activity, MainActivity.class);
//        intent.setAction(Action.GOAL_REACHED.title);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        int notificationId = 10;
        // Gets an instance of the NotificationManager service
        NotificationManager notificationManager =
                (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        notificationManager.notify(notificationId, builder.build());
    }
}
