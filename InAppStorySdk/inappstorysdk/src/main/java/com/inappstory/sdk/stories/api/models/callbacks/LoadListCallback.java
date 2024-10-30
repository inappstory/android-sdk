package com.inappstory.sdk.stories.api.models.callbacks;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.stories.api.models.StoryListType;
import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;

import java.lang.reflect.Type;
import java.util.List;

public abstract class LoadListCallback extends NetworkCallback<List<Story>> {
    @Override
    public abstract void onSuccess(List<Story> response);

    @Override
    public Type getType() {
        return new StoryListType();
    }

    @Override
    public void onError(int code, String message) {
        loadListError();
    }

    private void loadListError() {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().useCallback(IASCallbackType.ERROR,
                        new UseIASCallback<ErrorCallback>() {
                            @Override
                            public void use(@NonNull ErrorCallback callback) {
                                callback.loadListError("");
                            }
                        });
            }
        });
    }

    @Override
    public void error424(String message) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().useCallback(IASCallbackType.ERROR,
                        new UseIASCallback<ErrorCallback>() {
                            @Override
                            public void use(@NonNull ErrorCallback callback) {
                                callback.loadListError("");
                            }
                        });
                IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
                core.sessionManager().closeSession(
                        true,
                        false,
                        settingsHolder.lang(),
                        settingsHolder.userId(),
                        core.sessionManager().getSession().getSessionId()
                );
            }
        });

    }

}
