package com.keepfit.triggers.utils;

import android.util.Log;

import com.keepfit.triggers.utils.enums.TriggerType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Edward on 4/14/2016.
 */
public class TriggerCache {
    private static final String TAG = "TriggerCache";

    private static final Map<String, Object> cache = new HashMap<>();

    public static <T> T get(TriggerType triggerType, Class<T> type) {
        Object item = cache.get(triggerType.title);
        T cacheItem = null;
        try {
            cacheItem = type.cast(item);
        } catch (ClassCastException e) {
            Log.w(TAG, String.format("Error casting %s to %s", item.getClass(), type.getName()), e);
        }
        return cacheItem;
    }

    public static Object get(TriggerType triggerType) {
        return cache.get(triggerType.title);
    }

    public static void put(TriggerType triggerType, Object item) {
        cache.put(triggerType.title, item);
    }

}
