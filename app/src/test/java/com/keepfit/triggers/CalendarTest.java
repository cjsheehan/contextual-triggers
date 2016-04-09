package com.keepfit.triggers;

import com.keepfit.triggers.utils.Dates;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class CalendarTest {
    @Test
    public void calendarIntervalCheck_works() throws Exception {
        String currentTime = "12:00:00";
        String intervalTime = "11:00:00";
        boolean intervalPassed = Dates.timeIntervalPassed(currentTime, intervalTime);
        Assert.assertThat(intervalPassed, is(true));
    }
}