package com.inappstory.sdk.stories.api.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class PreloadGameCenterDataListType implements ParameterizedType {
    @NonNull
    @Override
    public Type[] getActualTypeArguments() {
        return new Type[]{PreloadGameCenterData.class};
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
