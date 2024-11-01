package org.example.utils;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
@Component
public class DateUtil {

    public static String toStr(Instant instant, String format) {
        if (instant == null)
            return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }

    public static Instant minusHour(Instant instant, int hour) {
        if (instant == null)
            return null;
        return instant.minusSeconds((long) hour * 60 * 60);
    }

    public static String toStr(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        return sdf.format(date);
    }
}