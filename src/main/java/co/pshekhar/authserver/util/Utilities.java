package co.pshekhar.authserver.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public final class Utilities {
    private static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd hh:mm:ss";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String formatDate(@NonNull LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT_DEFAULT));
    }

    public static String formatDate(@NonNull ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT_DEFAULT));
    }

    public static String generateRandomSecure(int byteLength) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[byteLength];
        secureRandom.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }

    public static ObjectMapper objectMapper() {
        return objectMapper;
    }
}
