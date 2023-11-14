package com.inappstory.sdk.core.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.core.models.api.Story;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class StoryListType implements ParameterizedType {
    @NonNull
    @Override
    public Type[] getActualTypeArguments() {
        return new Type[]{Story.class};
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
