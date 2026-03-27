package com.example.springbootplayground.constant;

public final class ErrorMessages {
    
    public static final String RATE_LIMIT_EXCEEDED = "Rate limit exceeded. Try again later.";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String TOO_MANY_REQUESTS = "Too Many Requests";
    
    private ErrorMessages() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
