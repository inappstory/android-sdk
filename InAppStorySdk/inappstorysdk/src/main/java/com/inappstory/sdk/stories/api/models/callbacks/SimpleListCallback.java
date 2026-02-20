package com.inappstory.sdk.stories.api.models.callbacks;

import com.inappstory.sdk.network.callbacks.SimpleApiCallback;
import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.refactoring.stories.data.network.NStoryListType;

import java.lang.reflect.Type;
import java.util.List;

public abstract class SimpleListCallback implements SimpleApiCallback<List<Story>> {
    @Override
    public abstract void onSuccess(List<Story> response, Object...args);

    @Override
    public Type getType() {
        return new NStoryListType();
    }

    @Override
    public abstract void onError(String message);
}
