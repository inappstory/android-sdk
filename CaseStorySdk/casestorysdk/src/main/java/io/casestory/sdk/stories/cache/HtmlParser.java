package io.casestory.sdk.stories.cache;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Используется для вычленения изображений из html статьи
 */

public class HtmlParser {
    public static final Pattern SRC = Pattern.compile("img src[\\s]*=[\\s]*\"(http[^\"]+)\"");
    public static final Pattern SRC_POSTER = Pattern.compile("poster[\\s]*=[\\s]*\"(http[^\"]+)\"");
    public static final Pattern SRC_VIDEO = Pattern.compile("source type=\"video[^\"]*\" src[\\s]*=[\\s]*\"(http[^\"]+)\"");

    public static List<String> getSrcUrls(String html) {
        List<String> urls = new ArrayList<>();

        Matcher urlMatcher = SRC.matcher(html);
        while (urlMatcher.find()) {
            if (urlMatcher.groupCount() == 1) {
                if (fromHtml(urlMatcher.group(1)).toString().contains(".gif")) continue;
                urls.add(fromHtml(urlMatcher.group(1)).toString());
            }
        }

        Matcher posterMatcher = SRC_POSTER.matcher(html);
        while (posterMatcher.find()) {
            if (posterMatcher.groupCount() == 1) {
                urls.add(fromHtml(posterMatcher.group(1)).toString());
            }
        }
        return urls;
    }

    public static Spanned fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
            // we are using this flag to give a consistent behaviour
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    public static List<String> getSrcVideoUrls(String html) {
        List<String> urls = new ArrayList<>();

        Matcher urlMatcher = SRC_VIDEO.matcher(html);
        while (urlMatcher.find()) {
            if (urlMatcher.groupCount() == 1) {
                urls.add(fromHtml(urlMatcher.group(1)).toString());
            }
        }
        return urls;
    }
}
