package com.inappstory.sdk.inappmessage.ui.widgets.impl;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.core.ui.widgets.roundedlayout.RoundedCornerLayout;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageModalAppearance;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContainerCallback;

public class ModalContentContainer extends IAMContentContainer<InAppMessageModalAppearance> {
    RoundedCornerLayout roundedCornerLayout;

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
    public void showWithAnimation() {

    }

    @Override
    public void showWithoutAnimation() {

    }

    @Override
    public void closeWithAnimation() {

    }

    @Override
    public void closeWithoutAnimation() {

    }

    @Override
    protected void init(Context context) {

        if (appearance != null) appearance(appearance);
    }
}
