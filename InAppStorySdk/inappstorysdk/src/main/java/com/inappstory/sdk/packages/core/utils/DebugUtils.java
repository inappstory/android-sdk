package com.inappstory.sdk.packages.core.utils;

public class DebugUtils {
    public static String getMethodName() {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste[4].getMethodName();
    }
}
