package com.apuntame.backend.constant;

import java.time.format.DateTimeFormatter;

public class DateTimeConstants {

    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);

    private DateTimeConstants() {}
}
