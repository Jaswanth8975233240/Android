package com.intelliq.appengine.util;

import java.util.concurrent.TimeUnit;

/**
 * Created by Steppschuh on 07/03/2017.
 */

public final class TimeUtils {

    public static final long roundMilliseconds(long milliseconds, long rounding) {
        rounding = Math.max(1, rounding);
        return (milliseconds / rounding) * rounding;
    }

    public static final String getReadableTimeFromMillis(long milliseconds) {
        long millis = Math.max(0, milliseconds);
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder();
        boolean appendDelimiter = false;

        if (days > 0) {
            sb.append(days).append(" day").append(days != 1 ? "s" : "");
            appendDelimiter = true;
        }
        if (hours > 0) {
            sb.append(appendDelimiter ? ", " : "");
            sb.append(hours).append(" hour").append(hours != 1 ? "s" : "");
            appendDelimiter = true;
        }
        if (minutes > 0) {
            sb.append(appendDelimiter ? ", " : "");
            sb.append(minutes).append(" minute").append(minutes != 1 ? "s" : "");
            appendDelimiter = true;
        }
        if (seconds > 0 || sb.length() == 0) {
            sb.append(appendDelimiter ? ", " : "");
            sb.append(seconds).append(" second").append(seconds != 1 ? "s" : "");
        }

        // replace last comma with and
        String readableTime = sb.toString();
        int lastCommaIndex = readableTime.lastIndexOf(",");
        if (lastCommaIndex != -1) {
            readableTime = readableTime.substring(0, lastCommaIndex) + " and" + readableTime.substring(lastCommaIndex + 1);
        }

        return readableTime;
    }

}
