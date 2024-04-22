package com.inappstory.sdk.modulesconnector.utils.filepicker;

import android.net.Uri;

import java.util.List;

import kotlin.Pair;

public interface OnFilesChooseCallback {
    void onChoose(List<Pair<String, Uri>> filesWithTypes);

    void onCancel();

    void onError(String reason);
}
