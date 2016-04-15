package com.keepfit.triggers;

import com.keepfit.triggers.interests.PointsOfInterestResponse;
import com.keepfit.triggers.utils.TriggerCache;
import com.keepfit.triggers.utils.enums.TriggerType;
import com.keepfit.triggers.weather.WeatherEvent;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

/**
 * Created by Edward on 4/13/2016.
 */
public class CacheTest {

    @Test
    public void testCache_works() throws Exception {
        WeatherEvent event = new WeatherEvent();
        TriggerType key = TriggerType.WEATHER;
        TriggerCache.put(key, event);

        WeatherEvent cachedEvent = TriggerCache.get(key, WeatherEvent.class);
        Assert.assertThat(cachedEvent, is(notNullValue()));
        Assert.assertThat(cachedEvent, is(equalTo(event)));
    }

    @Test
    public void testCache_throwsException() throws Exception {
        WeatherEvent event = new WeatherEvent();
        TriggerType key = TriggerType.WEATHER;
        TriggerCache.put(key, event);

        PointsOfInterestResponse cachedEvent = TriggerCache.get(key, PointsOfInterestResponse.class);
        Assert.assertThat(cachedEvent, is(nullValue()));
    }

}
