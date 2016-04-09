package com.keepfit.triggers.utils;

import android.content.Context;
import android.content.Intent;

import com.keepfit.triggers.utils.enums.Action;

/**
 * Created by Edward on 4/9/2016.
 */
public class Broadcast {

    public static String ACTION = "action";
    /*****
     * Broadcasting
     *****/

    public static void broadcastTimeReached(Context context, String timeStamp) {
        Intent intent = new Intent(Action.TIME.title);
        intent.putExtra("timeStamp", timeStamp);
        intent.putExtra(ACTION, Action.TIME.id);
        context.sendBroadcast(intent);
    }

}
