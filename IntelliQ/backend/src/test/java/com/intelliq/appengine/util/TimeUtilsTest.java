package com.intelliq.appengine.util;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by Steppschuh on 07/03/2017.
 */
public class TimeUtilsTest {

    @Test
    public void roundMilliseconds_invalidInput_validOutput() throws Exception {
        // TODO: implement
    }

    @Test
    public void roundMilliseconds_validInput_correctOutput() throws Exception {
        int amountOfTimeUnits = 5;

        long milliseconds;
        long rounding;
        long expected;
        long actual;

        milliseconds = TimeUnit.DAYS.toMillis(amountOfTimeUnits)
                + TimeUnit.HOURS.toMillis(amountOfTimeUnits)
                + TimeUnit.MINUTES.toMillis(amountOfTimeUnits)
                + TimeUnit.SECONDS.toMillis(amountOfTimeUnits)
                + TimeUnit.MILLISECONDS.toMillis(amountOfTimeUnits);

        rounding = TimeUnit.DAYS.toMillis(1);
        expected = TimeUnit.DAYS.toMillis(amountOfTimeUnits);
        actual = TimeUtils.roundMilliseconds(milliseconds, rounding);
        assertEquals(expected, actual);

        rounding = TimeUnit.HOURS.toMillis(1);
        expected = TimeUnit.DAYS.toMillis(amountOfTimeUnits)
                + TimeUnit.HOURS.toMillis(amountOfTimeUnits);
        actual = TimeUtils.roundMilliseconds(milliseconds, rounding);
        assertEquals(expected, actual);

        rounding = TimeUnit.MINUTES.toMillis(1);
        expected = TimeUnit.DAYS.toMillis(amountOfTimeUnits)
                + TimeUnit.HOURS.toMillis(amountOfTimeUnits)
                + TimeUnit.MINUTES.toMillis(amountOfTimeUnits);
        actual = TimeUtils.roundMilliseconds(milliseconds, rounding);
        assertEquals(expected, actual);

        rounding = TimeUnit.SECONDS.toMillis(1);
        expected = TimeUnit.DAYS.toMillis(amountOfTimeUnits)
                + TimeUnit.HOURS.toMillis(amountOfTimeUnits)
                + TimeUnit.MINUTES.toMillis(amountOfTimeUnits)
                + TimeUnit.SECONDS.toMillis(amountOfTimeUnits);
        actual = TimeUtils.roundMilliseconds(milliseconds, rounding);
        assertEquals(expected, actual);

        rounding = TimeUnit.MILLISECONDS.toMillis(1);
        expected = milliseconds;
        actual = TimeUtils.roundMilliseconds(milliseconds, rounding);
        assertEquals(expected, actual);
    }

    @Test
    public void getReadableTimeFromMillis_invalidInput_validOutput() throws Exception {
        String actual = TimeUtils.getReadableTimeFromMillis(0);
        String expected = "0 seconds";
        assertEquals(expected, actual);

        actual = TimeUtils.getReadableTimeFromMillis(-1234);
        expected = "0 seconds";
        assertEquals(expected, actual);
    }

    @Test
    public void getReadableTimeFromMillis_validInput_correctOutput() throws Exception {
        int amountOfTimeUnits = 5;
        long milliseconds = TimeUnit.DAYS.toMillis(amountOfTimeUnits)
                + TimeUnit.HOURS.toMillis(amountOfTimeUnits)
                + TimeUnit.MINUTES.toMillis(amountOfTimeUnits)
                + TimeUnit.SECONDS.toMillis(amountOfTimeUnits)
                + TimeUnit.MILLISECONDS.toMillis(amountOfTimeUnits);

        String actual = TimeUtils.getReadableTimeFromMillis(milliseconds);
        String expected = "5 days, 5 hours, 5 minutes and 5 seconds";
        assertEquals(expected, actual);
    }

}