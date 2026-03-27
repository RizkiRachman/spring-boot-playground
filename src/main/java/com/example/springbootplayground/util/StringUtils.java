package com.example.springbootplayground.util;

import java.util.concurrent.ThreadLocalRandom;

public final class StringUtils {

    private static final String ID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ID_LENGTH = 16;

    private StringUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    public static String generateFastId() {
        StringBuilder sb = new StringBuilder(ID_LENGTH);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < ID_LENGTH; i++) {
            sb.append(ID_CHARS.charAt(random.nextInt(ID_CHARS.length())));
        }
        return sb.toString();
    }
}
