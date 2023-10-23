package com.inappstory.sdk.ugc.usecases;

import com.inappstory.sdk.stories.utils.SessionManager;
import com.inappstory.sdk.ugc.extinterfaces.IOpenSessionCallback;

public class AddOpenSessionCallback {
    public void add(IOpenSessionCallback callback) {
        SessionManager.getInstance().addStaticOpenSessionCallback(callback);
    }
}
