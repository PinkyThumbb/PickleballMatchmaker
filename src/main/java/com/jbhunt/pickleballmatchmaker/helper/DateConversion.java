package com.jbhunt.pickleballmatchmaker.helper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateConversion {
    public static Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
