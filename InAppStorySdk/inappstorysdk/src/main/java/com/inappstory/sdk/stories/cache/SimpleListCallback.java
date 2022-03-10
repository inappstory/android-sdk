package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryListType;

import java.lang.reflect.Type;
import java.util.List;

public abstract class SimpleListCallback implements SimpleApiCallback<List<Story>> {
    @Override
    public abstract void onSuccess(List<Story> response);

    @Override
    public Type getType() {
        return new StoryListType();
    }

    @Override
    public abstract void onError(String message);
}