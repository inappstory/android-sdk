package com.inappstory.sdk.share;


import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IASShareData implements Serializable {
    String url;
    public List<String> files;
    String payload;

    public String getUrl() {
        return url;
    }

    public String getPayload() {
        return payload;
    }

    public @NonNull List<String> getFiles() {
        if (files == null) return new ArrayList<>();
        return files;
    }

    public IASShareData() {}

    public IASShareData(List<String> files) {
        this.files = files;
    }

    public IASShareData(String url) {
        this.url = url;
    }

    public IASShareData(String url, List<String> files) {
        this.url = url;
        this.files = files;
    }

    public IASShareData(String url, String payload) {
        this.url = url;
        this.payload = payload;
    }

    public IASShareData(String text, String url, String payload) {
        if (text == null || text.isEmpty())
            this.url = url;
        else
            this.url = text;
        this.payload = payload;
    }

    public IASShareData(String url, List<String> files, String payload) {
        this.url = url;
        this.files = files;
        this.payload = payload;
    }

    public IASShareData(String text, String url, List<String> files, String payload) {
        if (text == null || text.isEmpty())
            this.url = url;
        else
            this.url = text;
        this.files = files;
        this.payload = payload;
    }
}
