package com.inappstory.sdk.share;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class IASShareData {
    public String url;
    public List<Uri> files;

    public @NonNull List<Uri> getFiles() {
        if (files == null) return new ArrayList<>();
        return files;
    }

    public IASShareData() {}

    public IASShareData(List<Uri> files) {
        this.files = files;
    }

    public IASShareData(String url) {
        this.url = url;
    }

    public IASShareData(String url, List<Uri> files) {
        this.url = url;
        this.files = files;
    }
}
