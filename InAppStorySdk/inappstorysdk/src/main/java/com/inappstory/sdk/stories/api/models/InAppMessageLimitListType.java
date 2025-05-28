package com.inappstory.sdk.stories.api.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.core.network.content.models.InAppMessageLimit;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class InAppMessageLimitListType implements ParameterizedType {
    @NonNull
    @Override
    public Type[] getActualTypeArguments() {
        return new Type[]{
                InAppMessageLimit.class
        };
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
