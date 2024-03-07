package com.inappstory.sdk.utils;


import androidx.annotation.NonNull;

public class StringsUtils {
    public static @NonNull String getNonNull(String str) {
        if (str == null) return "";
        return str;
    }


  //  private final static String[] nonEscapedSymbols = {"\"", "\n", "\r"};
 //   private final static String[] escapedSymbols = {"\\\"", "\\\n", "\\\r"};//, "\t", "\b", "\f"};

    public static String getEscapedString(String raw) {
        return new EscapeString().escape(raw);
        /*String doubleSlash = Matcher.quoteReplacement("\\\\");
        String doubleSlashTag = Matcher.quoteReplacement("%double_back_slash%");
        String escapedTag = Matcher.quoteReplacement("%escaped_symbol%");
        String res = raw.replaceAll(
                doubleSlash,
                doubleSlashTag
        );
        for (int i = 0; i < nonEscapedSymbols.length; i++) {
            String escapedReplacement = Matcher.quoteReplacement(escapedSymbols[i]);
            String nonEscapedReplacement = Matcher.quoteReplacement(nonEscapedSymbols[i]);
            res = res.replaceAll(
                    escapedReplacement,
                    escapedTag
            );
            res = res.replaceAll(
                    nonEscapedReplacement,
                    escapedReplacement
            );
            res = res.replaceAll(
                    escapedTag,
                    escapedReplacement
            );

        }
        return res.replaceAll(
                doubleSlashTag,
                doubleSlash
        );*/
    }
}
