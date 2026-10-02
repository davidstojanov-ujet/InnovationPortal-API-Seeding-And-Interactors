package com.innovationportal.utils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Unique values for data that must not collide between runs.
 *
 * <pre>{@code
 * String title = RandomDataUtils.uniqueString("Idea");   // Idea_1a2b3c4d
 * String email = RandomDataUtils.uniqueEmail();          // test_1a2b3c4d@example.com
 * }</pre>
 */
public final class RandomDataUtils {

    private static final String ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final String EMAIL_DOMAIN = "example.com";

    private RandomDataUtils() {
    }

    /** {@code prefix_} followed by an 8-character unique suffix. */
    public static String uniqueString(String prefix) {
        return prefix + "_" + uniqueSuffix();
    }

    /** Random lowercase alphanumeric string of the given length. */
    public static String randomAlphanumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    public static String uniqueEmail() {
        return uniqueEmail("test");
    }

    public static String uniqueEmail(String prefix) {
        return prefix + "_" + uniqueSuffix() + "@" + EMAIL_DOMAIN;
    }

    private static String uniqueSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
