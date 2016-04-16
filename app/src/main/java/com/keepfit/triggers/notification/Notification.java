package com.keepfit.triggers.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.keepfit.triggers.R;
import com.keepfit.triggers.activity.MainActivity;
import com.keepfit.triggers.utils.TriggerCache;
import com.keepfit.triggers.utils.enums.Scenario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 4/15/2016.
 */
public class Notification implements Serializable {
    public static final String IS_NOTIFICATION = "isNotification";

    private String title;
    private String message;
    private Scenario scenario;
    private boolean isViewed;

    public Notification(String title, String message, Scenario scenario) {
        this.title = title;
        this.scenario = scenario;
        this.message = message;
        isViewed = false;
    }

    /**
     * Source: http://developer.android.com/training/notify-user/build-notification.html
     *
     * @param context
     */
    public static void sendNotification(final Context context, String title, String message, Scenario scenario, int
            icon) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(icon)
                        .setContentTitle(title)
                        .setContentText(message);

        Notification notification = new Notification(title, message, scenario);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(IS_NOTIFICATION, true);
        intent.putExtra(scenario.title, notification);

        List<Notification> notifications = new ArrayList<>();
        notifications.add(notification);
        TriggerCache.put(scenario, notification);
        // Check the cache to see if there are any notifications active already
        for (Scenario s : Scenario.values()) {
            Notification cachedNotification = TriggerCache.get(s, Notification.class);
            if (cachedNotification == null || cachedNotification.getScenario().id == notification.getScenario().id ||
                    cachedNotification.isViewed())
                continue;
            notifications.add(cachedNotification);
            intent.putExtra(cachedNotification.getScenario().title, cachedNotification);
        }

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < notifications.size(); i++) {
            Notification n = notifications.get(i);
            stringBuilder.append(n.getMessage());
            if (i != notifications.size() - 1)
                stringBuilder.append("\n");
        }
        bigTextStyle.setBigContentTitle("The context changed around you!");
        bigTextStyle.bigText(stringBuilder.toString());
        bigTextStyle.setSummaryText("You have hit some triggers!");
        builder.setStyle(bigTextStyle);

        builder.setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setAutoCancel(true);
        int notificationId = 10;
        // Gets an instance of the NotificationManager service
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        notificationManager.notify(notificationId, builder.build());
    }

    public static void sendNotification(final Context context, String title, String message, Scenario scenario) {
        sendNotification(context, title, message, scenario, R.drawable.ic_sentiment_very_satisfied_black_24dp);
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public void setIsViewed(boolean isViewed) {
        this.isViewed = isViewed;
    }

    @Override
    public String toString() {
        return String.format("Notification[%s]", scenario.title);
    }
}
