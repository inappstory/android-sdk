package com.inappstory.sdk.core.repository.stories.interfaces;

import com.inappstory.sdk.core.repository.session.interfaces.NetworkErrorCallback;

public interface IChangeStatusReaderCallback extends NetworkErrorCallback {
    void onProcess();
    void onSuccess(int val);
}
