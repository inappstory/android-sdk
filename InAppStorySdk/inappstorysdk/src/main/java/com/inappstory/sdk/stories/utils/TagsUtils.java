package com.inappstory.sdk.stories.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagsUtils {
    public static boolean checkTagPattern(String tag) {
        Pattern pattern = Pattern.compile("^[\\p{L}_\\d][\\p{L}_\\d\\-]{0,49}$");
        Matcher matcher = pattern.matcher(tag);
        return matcher.matches();
    }
}
