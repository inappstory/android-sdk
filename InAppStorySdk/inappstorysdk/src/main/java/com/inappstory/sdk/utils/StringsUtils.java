package com.inappstory.sdk.utils;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringsUtils {
    public static @NonNull String getNonNull(String str) {
        if (str == null) return "";
        return str;
    }


    public static String getErrorStringFromContext(Context context, @StringRes int resourceId) {
        if (context != null)
            return context.getResources().getString(resourceId);
        return "";
    }

    public static int getBytesLength(String value) {
        if (value == null) return 0;
        return value.getBytes(StandardCharsets.UTF_8).length;
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
        }
        return "";
    }

    public static String getEscapedString(String raw) {
        return new EscapeString().escape(raw);
    }
}
