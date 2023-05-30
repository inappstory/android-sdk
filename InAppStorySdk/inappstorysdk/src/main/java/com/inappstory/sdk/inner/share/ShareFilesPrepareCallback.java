package com.inappstory.sdk.inner.share;

import android.net.Uri;

import java.util.List;

public interface ShareFilesPrepareCallback {
    void onPrepared(List<String> files);
}
