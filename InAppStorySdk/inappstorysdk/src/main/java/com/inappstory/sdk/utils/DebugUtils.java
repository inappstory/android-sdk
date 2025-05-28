package com.inappstory.sdk.utils;

public class DebugUtils {
    public static String getMethodName() {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste[4].getMethodName();
    }
}
