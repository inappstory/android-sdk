package com.inappstory.sdk.core.ui.screens.inappmessagereader;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.data.IInAppMessage;

public interface GetLocalInAppMessage {
    void get(@NonNull IInAppMessage readerContent);
    void error();
}
