package co.pshekhar.authserver.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public final class Generator {
    public static String getRandomString(String suffix, String prefix, int len) {
        len = len == 0 ? 13 : len;
        suffix = StringUtils.isBlank(suffix) ? "" : suffix;
        prefix = StringUtils.isBlank(prefix) ? "" : prefix;
        return prefix + RandomStringUtils.randomAlphanumeric(len) + suffix;
    }
}
