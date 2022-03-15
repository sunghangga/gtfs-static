package com.maestronic.gtfs.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class Time {

    public static Duration strTimeToDuration(String time) {
        String[] parts = time.split(":");
        Duration duration = Duration.ZERO;
        if (parts.length == 3) {
            int hours = Integer.parseInt (parts[0]);
            int minutes = Integer.parseInt (parts[1]);
            int seconds = Integer.parseInt (parts[2]);
            duration = duration.plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
        } else if (parts.length == 2) {
            int hours = Integer.parseInt (parts[0]);
            int minutes = Integer.parseInt (parts[1]);
            duration = duration.plusHours(hours).plusMinutes(minutes);
        } else {
            String logMessage = "Unexpected input '" + time + "' string time format.";
            Logger.error(logMessage);
        }

        return duration;
    }

    public static String calculateTime(long seconds) {

        long day = TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
        return day + " days " + hours + " hours " + minute + " minutes " + second + " seconds";
    }

    public static Integer localDateZoneGTFS() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        return Integer.parseInt(LocalDate.now(ZoneId.of("Europe/Amsterdam")).format(dateFormat));
    }

    public static Integer localDateZoneBeforeGTFS() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        return Integer.parseInt((LocalDate.now(ZoneId.of("Europe/Amsterdam")).minusDays(1)).format(dateFormat));
    }

    public static ZonedDateTime unixToZoneDateTime(Long unixTime) {
        if (unixTime <= 0) {
            return null;
        }
        DateTimeFormatter zoneFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Instant instant = Instant.ofEpochSecond(unixTime);
        return ZonedDateTime.parse(ZonedDateTime.ofInstant(instant, ZoneId.of("Europe/Amsterdam")).format(zoneFormat));
    }

    public static LocalTime unixToTime(Long unixTime) {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        Instant instant = Instant.ofEpochSecond(unixTime);
        return LocalTime.parse(LocalTime.from(instant.atZone(ZoneId.of("Europe/Amsterdam"))).format(timeFormat));
    }

    public static ZonedDateTime durToZoneDateTime(Duration duration) {
        DateTimeFormatter zoneFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        LocalDate nowDate = LocalDate.now(ZoneId.of("Europe/Amsterdam"));
        LocalTime localTime = LocalTime.MIDNIGHT.plus(duration);
        return ZonedDateTime.parse(LocalDateTime.of(nowDate, localTime).format(zoneFormat));
    }

    public static ZonedDateTime durToZoneDateTime(Duration duration, String gtfsDate) {
        DateTimeFormatter zoneFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        LocalDate nowDate;
        try {
            nowDate = new java.sql.Date(new SimpleDateFormat("yyyyMMdd").parse(gtfsDate).getTime()).toLocalDate();
        } catch (ParseException e) {
            return null;
        }
        // Get datetime after addition with duration example '25:50:00'
        LocalDateTime localDateTime = nowDate.atStartOfDay().plus(duration);
        return ZonedDateTime.parse(localDateTime.format(zoneFormat));
    }

    public static ZonedDateTime localDateTimeZone() {
        DateTimeFormatter zoneFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return ZonedDateTime.parse(ZonedDateTime.now(ZoneId.of("Europe/Amsterdam")).format(zoneFormat));
    }

    public static LocalTime timeZone() {
        DateTimeFormatter zoneFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        return LocalTime.parse(LocalTime.now(ZoneId.of("Europe/Amsterdam")).format(zoneFormat));
    }

    public static LocalDateTime localDateTime() {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(LocalDateTime.now(ZoneId.of("Europe/Amsterdam")).format(dateTimeFormat));
    }

    public static LocalDateTime strToLocalDateTime(String strTime) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(strTime, dateTimeFormat);
    }

    public static long concatDateTime(String strTime) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDateTime.of(LocalDate.parse(LocalDate.now(
                ZoneId.of("Europe/Amsterdam")).format(dateTimeFormat)),
                LocalTime.parse(strTime)).atZone(ZoneId.of("Europe/Amsterdam")).toEpochSecond();
    }
}
