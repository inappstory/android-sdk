package com.inappstory.sdk.inappmessage.ui.widgets.impl;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.ui.widgets.roundedlayout.RoundedCornerLayout;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBottomSheetAppearance;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContainerCallback;
import com.inappstory.sdk.stories.utils.Sizes;

public final class BottomSheetContentContainer extends IAMContentContainer<InAppMessageBottomSheetAppearance> {
    BottomSheetBehavior<RoundedCornerLayout> bottomSheetBehavior;
    RoundedCornerLayout roundedCornerLayout;
    CoordinatorLayout.LayoutParams layoutParams;
    FrameLayout content;

    public BottomSheetContentContainer(
            @NonNull Context context
    ) {
        super(context);
    }

    public BottomSheetContentContainer(
            @NonNull Context context,
            @Nullable AttributeSet attrs
    ) {
        super(context, attrs);
    }

    public BottomSheetContentContainer(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        CoordinatorLayout coordinatorLayout = new CoordinatorLayout(context);
        coordinatorLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        roundedCornerLayout = new RoundedCornerLayout(context);
        roundedCornerLayout.setRadius(
                Sizes.dpToPxExt(16, context),
                Sizes.dpToPxExt(16, context),
                0,
                0

        );
        layoutParams = new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Sizes.dpToPxExt(600, context)
        );
        layoutParams.setBehavior(new BottomSheetBehavior<RoundedCornerLayout>());
        roundedCornerLayout.setLayoutParams(
                layoutParams
        );
        content = new FrameLayout(context);
        content.setLayoutParams(
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        content.setId(CONTENT_ID);
        roundedCornerLayout.addView(content);
        bottomSheetBehavior = BottomSheetBehavior.from(roundedCornerLayout);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                bsState = newState;
                Log.e("bsStates", newState + "");
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        coordinatorLayout.addView(roundedCornerLayout);
        addView(coordinatorLayout);
        setId(CONTAINER_ID);
    }

    @Override
    public void appearance(InAppMessageBottomSheetAppearance appearance) {
        super.appearance(appearance);
        if (appearance.maxHeight() == -1)
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        else
            layoutParams.height = Sizes.dpToPxExt(appearance.maxHeight(), getContext());
        roundedCornerLayout.setRadius(
                appearance.cornerRadius(),
                appearance.cornerRadius(),
                0,
                0
        );
        roundedCornerLayout.setLayoutParams(
                layoutParams
        );
        roundedCornerLayout.requestLayout();
    }

    private int bsState = 0;

    @Override
    public void showWithAnimation(IAMContainerCallback callback) {
        bottomSheetBehavior.setState(STATE_EXPANDED);
    }

    @Override
    public void showWithoutAnimation(IAMContainerCallback callback) {
        bottomSheetBehavior.setState(STATE_EXPANDED);
    }

    @Override
    public void closeWithAnimation(IAMContainerCallback callback) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void closeWithoutAnimation(IAMContainerCallback callback) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}
