package com.inappstory.sdk.refactoring.stories.data.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class NStoryCoverListType implements ParameterizedType {
    @NonNull
    @Override
    public Type[] getActualTypeArguments() {
        return new Type[]{NStoryCover.class};
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
