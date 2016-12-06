package com.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

public class DateConverter {

    /***
     * toLDTime returns local date without the time zone
     */
    public static LocalDate toLDate(Date date) {
        Objects.requireNonNull(date);
        LocalDate ldTime = LocalDate.from(Instant.ofEpochMilli(date.getTime()));
        return ldTime;
    }

    /***
     * toLDTime returns local date time without the time zone
     */
    public static LocalDateTime toLDTime(Date date) {
        Objects.requireNonNull(date);
        LocalDateTime ldTime = LocalDateTime
                .from(Instant.ofEpochMilli(date.getTime()));
        return ldTime;
    }

    /***
     * toLDTime returns local date time with the time zone
     */
    public static LocalDateTime toZonedLDTime(Date date, ZoneId zone) {
        Objects.requireNonNull(date);
        LocalDateTime ldTime = LocalDateTime
                .from(Instant.ofEpochMilli(date.getTime()));
        ldTime.atZone(zone);
        return ldTime;
    }

}
