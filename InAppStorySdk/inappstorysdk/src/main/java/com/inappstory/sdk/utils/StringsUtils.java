package com.inappstory.sdk.utils;

import androidx.annotation.NonNull;

public class StringsUtils {
    public static @NonNull String getNonNull(String str) {
        if (str == null) return "";
        return str;
    }
}
