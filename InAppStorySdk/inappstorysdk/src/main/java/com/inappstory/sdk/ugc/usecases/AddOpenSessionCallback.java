package com.inappstory.sdk.ugc.usecases;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.stories.utils.SessionManager;
import com.inappstory.sdk.ugc.extinterfaces.IOpenSessionCallback;

public class AddOpenSessionCallback {
    public void add(final IOpenSessionCallback callback) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.sessionManager().addStaticOpenSessionCallback(callback);
            }
        });
    }
}
