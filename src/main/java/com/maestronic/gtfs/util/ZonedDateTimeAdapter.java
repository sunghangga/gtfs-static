package com.maestronic.gtfs.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ZonedDateTimeAdapter {
    public ZonedDateTimeAdapter() {
    }

    public static ZonedDateTime parse(String dateTime) {
        ZonedDateTime parsed;
        if (dateTime != null && isNumeric(dateTime)) {
            parsed = parse(Long.valueOf(dateTime));
        } else {
            try {
                parsed = ZonedDateTime.parse(dateTime);
            } catch (DateTimeParseException var4) {
                LocalDateTime parse1 = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                parsed = ZonedDateTime.ofLocal(parse1, ZoneId.systemDefault(), ZoneOffset.ofHours(0));
            }
        }

        return parsed.withZoneSameInstant(ZoneId.systemDefault());
    }

    private static boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    private static ZonedDateTime parse(long dateTime) {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(dateTime), ZoneId.systemDefault());
    }
}
