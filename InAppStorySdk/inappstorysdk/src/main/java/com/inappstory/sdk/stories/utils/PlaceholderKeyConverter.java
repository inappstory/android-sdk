package com.inappstory.sdk.stories.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderKeyConverter {
    public static String getPlaceholderNameFromKey(String key) {
        Pattern pattern = Pattern.compile("\\{\\{@placeholder:(.*?)\\}\\}");
        Matcher matcher = pattern.matcher(key);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
