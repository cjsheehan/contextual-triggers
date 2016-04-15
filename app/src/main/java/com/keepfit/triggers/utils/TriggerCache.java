package com.keepfit.triggers.utils;

import android.util.Log;

import com.keepfit.triggers.utils.enums.Scenario;
import com.keepfit.triggers.utils.enums.TriggerType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Edward on 4/14/2016.
 */
public class TriggerCache {
    private static final String TAG = "TriggerCache";

    private static final Map<String, Object> cache = new HashMap<>();

    private static <T> T get(Object item, Class<T> type) {
        T cacheItem = null;
        try {
            cacheItem = type.cast(item);
        } catch (ClassCastException e) {
            Log.w(TAG, String.format("Error casting %s to %s", item.getClass(), type.getName()), e);
        }
        return cacheItem;
    }

    public static <T> T get(Scenario scenario, Class<T> type) {
        Object item = cache.get(scenario.title);
        if (item == null) {
            Log.w(TAG, String.format("The item for %s was null.", scenario.title));
            return null;
        }
        return get(item, type);
    }

    public static <T> T get(TriggerType triggerType, Class<T> type) {
        Object item = cache.get(triggerType.title);
        if (item == null) {
            Log.w(TAG, String.format("The item for %s was null.", triggerType.title));
            return null;
        }
        return get(item, type);
    }

    public static Object get(TriggerType triggerType) {
        return cache.get(triggerType.title);
    }

    public static void put(TriggerType triggerType, Object item) {
        cache.put(triggerType.title, item);
    }

    public static void put(Scenario scenario, Object item) {
        cache.put(scenario.title, item);
    }

}
