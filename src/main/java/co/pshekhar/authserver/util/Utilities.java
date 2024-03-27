package co.pshekhar.authserver.util;

import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Utilities {
    private static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd hh:mm:ss";

    public static String formatDate(@NonNull LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT_DEFAULT));
    }
}
