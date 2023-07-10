package com.inappstory.sdk.stories.api.models.callbacks;

import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryListType;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.utils.SessionManager;

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
        if (CallbackManager.getInstance().getErrorCallback() != null) {
            CallbackManager.getInstance().getErrorCallback().loadListError("");
        }
    }

    @Override
    protected void error424(String message) {
        if (CallbackManager.getInstance().getErrorCallback() != null) {
            CallbackManager.getInstance().getErrorCallback().loadListError("");
        }
        SessionManager.getInstance().closeSession(true, false);
    }

}
