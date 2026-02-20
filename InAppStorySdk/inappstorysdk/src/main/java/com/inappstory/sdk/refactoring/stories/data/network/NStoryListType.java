package com.inappstory.sdk.refactoring.stories.data.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.core.network.content.models.Story;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class NStoryListType implements ParameterizedType {
    @NonNull
    @Override
    public Type[] getActualTypeArguments() {
        return new Type[]{NStory.class};
    }

    @NonNull
    @Override
    public Type getRawType() {
        return List.class;
    }

    @Nullable
    @Override
    public Type getOwnerType() {
        return List.class;
    }
}
