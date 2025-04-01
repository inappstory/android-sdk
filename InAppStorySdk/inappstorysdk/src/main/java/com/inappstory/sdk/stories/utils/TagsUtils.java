package com.inappstory.sdk.stories.utils;

import android.text.TextUtils;

import com.inappstory.sdk.utils.StringsUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagsUtils {
    public static boolean checkTagPattern(String tag) {
        Pattern pattern = Pattern.compile("^[\\p{L}_\\d][\\p{L}_\\d\\-]{0,49}$");
        Matcher matcher = pattern.matcher(tag);
        return matcher.matches();
    }

    public static String tagsHash(List<String> tags) {
        String total = "";
        if (tags != null) {
            Collections.sort(tags);
            total = TextUtils.join("|", tags);
        }
        return StringsUtils.md5(total);
    }
}
