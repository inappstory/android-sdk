package com.inappstory.sdk.stories.cache;

import androidx.annotation.NonNull;

public class UrlWithAlter {
    public UrlWithAlter(@NonNull String url) {
        this.url = url;
    }

    private String url;

    public UrlWithAlter(@NonNull String url, String alter) {
        this.url = url;
        this.alter = alter;
    }

    private String alter;

    public String getUrl() {
        return url;
    }

    public String getAlter() {
        return alter;
    }

    @NonNull
    @Override
    public String toString() {
        return url + " " + alter;
    }
}
