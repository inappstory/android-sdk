package com.inappstory.sdk.utils;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.inappstory.sdk.stories.cache.vod.ContentRange;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringsUtils {
    public static @NonNull String getNonNull(String str) {
        if (str == null) return "";
        return str;
    }

    public static MDStringModel generateMDStringReplacement(String raw) {
        Pattern p;
        try {
            p = Pattern.compile("\\[([^\\]]+)\\]\\(([^\\)]+)\\)");
        } catch (PatternSyntaxException ex) {
            ex.printStackTrace();
            return null;
        }
        String keyText = raw;
        String valueText = raw;
        Matcher matcher = p.matcher(raw);
        int index = 0;
        int keyCount = 0;
        List<MDStringReplacement> replacements = new ArrayList<>();
        while (true) {
            if (matcher.find(index)) {
                String key = "%%key" + keyCount + "%%";
                keyText = keyText.replace(matcher.group(0), key);
                valueText = valueText.replace(matcher.group(0), matcher.group(1));
                replacements.add(
                        new MDStringReplacement(
                                key, matcher.group(1), matcher.group(2)
                        )
                );
                keyCount++;
                index = matcher.end();
            } else {
                break;
            }
        }
        return new MDStringModel(raw, keyText, valueText, replacements);
    }

    public static String getErrorStringFromContext(Context context, @StringRes int resourceId) {
        if (context != null)
            return context.getResources().getString(resourceId);
        return "";
    }


    public static String getFormattedErrorStringFromContext(
            Context context,
            @StringRes int resourceId,
            Object... formatArgs
    ) {
        if (context != null)
            return context.getResources().getString(resourceId, formatArgs);
        return "";
    }

    public static int getBytesLength(String value) {
        if (value == null) return 0;
        int encodedLength = 0;
        try {
            encodedLength = URLEncoder.encode(value, StandardCharsets.UTF_8.name()).length();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return Math.max(value.getBytes(StandardCharsets.UTF_8).length, encodedLength);
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return "";
    }

    public static String getEscapedString(String raw) {
        return new EscapeString().escape(raw);
    }

    public static ContentRange getRange(String rangeHeader, long contentLength) {
        String[] sections = rangeHeader.split("/");
        String rangeSection = "";
        rangeSection = sections[0];

        String rangeReplaced = rangeSection.replaceAll("[^0-9]+", " ").trim();
        String[] ranges = rangeReplaced.split(" ");
        long start = -1;
        long length = 0;
        try {
            start = Long.parseLong(ranges[0]);
        } catch (Exception e) {

        }
        long end = -1;
        try {
            end = Long.parseLong(ranges[1]);
        } catch (Exception e) {

        }
        if (sections.length == 2) {
            length = Long.parseLong(sections[1]);
        } else {
            if (end != -1) {
                length = end;
            } else {
                length = contentLength;
            }
        }
        return new ContentRange(start, end, length);
    }

    public static String escapeSingleQuotes(String raw) {
        return raw.replaceAll(Pattern.quote("'"), "'").replaceAll("'", "\\\\'");
    }
}
