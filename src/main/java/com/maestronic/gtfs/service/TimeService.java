package com.maestronic.gtfs.service;

import com.maestronic.gtfs.util.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Service
public class TimeService {

    @Value("${timezone}")
    private String timezone;
    
    public Duration strTimeToDuration(String time) {
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

    public String calculateTime(long seconds) {
        long day = TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
        return day + " days " + hours + " hours " + minute + " minutes " + second + " seconds";
    }

    public Integer localDateZoneGTFS() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        return Integer.parseInt(LocalDate.now(ZoneId.of(timezone)).format(dateFormat));
    }

    public Integer localDateZoneGTFSByDateTime(LocalDateTime dateTime) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        return Integer.parseInt(dateTime.atZone(ZoneId.of(timezone)).format(dateFormat));
    }

    public String localTimeZoneGTFSByDateTime(LocalDateTime dateTime) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        return (String) dateTime.atZone(ZoneId.of(timezone)).format(dateFormat);
    }

    public ZonedDateTime unixToZoneDateTime(Long unixTime) {
        if (unixTime <= 0) {
            return null;
        }
        DateTimeFormatter zoneFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Instant instant = Instant.ofEpochSecond(unixTime);
        return ZonedDateTime.parse(ZonedDateTime.ofInstant(instant, ZoneId.of(timezone)).format(zoneFormat));
    }

    public LocalTime unixToTime(Long unixTime) {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        Instant instant = Instant.ofEpochSecond(unixTime);
        return LocalTime.parse(LocalTime.from(instant.atZone(ZoneId.of(timezone))).format(timeFormat));
    }

    public String durToTime(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }

    public ZonedDateTime durToZoneDateTime(Duration duration, String gtfsDate) {
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

    public ZonedDateTime localDateTimeZone() {
        DateTimeFormatter zoneFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return ZonedDateTime.parse(ZonedDateTime.now(ZoneId.of(timezone)).format(zoneFormat));
    }

    public LocalTime timeZone() {
        DateTimeFormatter zoneFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        return LocalTime.parse(LocalTime.now(ZoneId.of(timezone)).format(zoneFormat));
    }

    public LocalDateTime localDateTime() {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(LocalDateTime.now(ZoneId.of(timezone)).format(dateTimeFormat));
    }

    public LocalDateTime strToLocalDateTime(String strTime) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(strTime, dateTimeFormat);
    }

    public long concatDateTime(String strTime) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDateTime.of(LocalDate.parse(LocalDate.now(
                ZoneId.of(timezone)).format(dateTimeFormat)),
                LocalTime.parse(strTime)).atZone(ZoneId.of(timezone)).toEpochSecond();
    }

    public long currentTimeToUnix() {
        return LocalDateTime.now(ZoneId.of(timezone)).atZone(ZoneId.of(timezone)).toEpochSecond();
    }

    public ZonedDateTime localDateTimeToZonedDateTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.of(timezone));
    }
}
