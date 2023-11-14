package com.inappstory.sdk.core.models.callbacks;

import com.inappstory.sdk.core.utils.network.callbacks.SimpleApiCallback;
import com.inappstory.sdk.core.models.api.Story;
import com.inappstory.sdk.core.models.StoryListType;

import java.lang.reflect.Type;
import java.util.List;

public abstract class SimpleListCallback implements SimpleApiCallback<List<Story>> {
    @Override
    public abstract void onSuccess(List<Story> response, Object...args);

    @Override
    public Type getType() {
        return new StoryListType();
    }

    @Override
    public abstract void onError(String message);
}
