package com.inappstory.sdk.inappmessage.ui.widgets.impl;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageModalAppearance;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContainerCallback;

public class ModalContentContainer extends IAMContentContainer<InAppMessageModalAppearance> {
    public ModalContentContainer(
            @NonNull Context context
    ) {
        super(context);
    }

    public ModalContentContainer(
            @NonNull Context context,
            @Nullable AttributeSet attrs
    ) {
        super(context, attrs);
    }

    public ModalContentContainer(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void appearance(InAppMessageModalAppearance appearance) {
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
