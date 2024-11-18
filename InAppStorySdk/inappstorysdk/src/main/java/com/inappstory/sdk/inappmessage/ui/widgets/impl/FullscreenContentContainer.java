package com.inappstory.sdk.inappmessage.ui.widgets.impl;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageFullscreenAppearance;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContainerCallback;

public class FullscreenContentContainer extends IAMContentContainer<InAppMessageFullscreenAppearance> {
    public FullscreenContentContainer(
            @NonNull Context context
    ) {
        super(context);
    }

    public FullscreenContentContainer(
            @NonNull Context context,
            @Nullable AttributeSet attrs
    ) {
        super(context, attrs);
    }

    public FullscreenContentContainer(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void appearance(InAppMessageFullscreenAppearance appearance) {
        super.appearance(appearance);
    }

    @Override
    public void showWithAnimation(IAMContainerCallback callback) {

    }

    @Override
    public void showWithoutAnimation(IAMContainerCallback callback) {

    }

    @Override
    public void closeWithAnimation(IAMContainerCallback callback) {

    }

    @Override
    public void closeWithoutAnimation(IAMContainerCallback callback) {

    }

    @Override
    protected void init(Context context) {

    }
}
