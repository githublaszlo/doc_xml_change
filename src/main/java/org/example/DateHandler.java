package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateHandler {
    public static String isoToString(String date) {
        LocalDateTime localDateTime = LocalDateTime.parse(date, dateFormatterIsoInstant());
        return localDateTime.format(dateFormatterYearMonthDay());
    }

    public static String localDateToString(LocalDateTime localDateTime) {
        return localDateTime.format(dateFormatterYearMonthDay());
    }

    private static DateTimeFormatter dateFormatterIsoInstant() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    }

    private static DateTimeFormatter dateFormatterYearMonthDay() {
        return DateTimeFormatter.ofPattern("yyyy.MM.dd.");
    }

}
