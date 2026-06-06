package ru.newsaggregator.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Time {

    public static final DateTimeFormatter STORAGE = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    public static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private Time() {
    }

    public static String store(LocalDateTime value) {
        return value.format(STORAGE);
    }

    public static LocalDateTime parse(String value) {
        return LocalDateTime.parse(value, STORAGE);
    }

    public static String display(LocalDateTime value) {
        return value.format(DISPLAY);
    }
}
