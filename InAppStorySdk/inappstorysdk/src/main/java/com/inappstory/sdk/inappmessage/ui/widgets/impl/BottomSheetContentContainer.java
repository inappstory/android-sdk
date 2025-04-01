package com.inappstory.sdk.inappmessage.ui.widgets.impl;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.inappstory.sdk.core.ui.widgets.roundedlayout.RoundedCornerLayout;
import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBottomSheetAppearance;
import com.inappstory.sdk.inappmessage.ui.widgets.BottomSheetLine;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer;
import com.inappstory.sdk.stories.utils.Sizes;

public final class BottomSheetContentContainer extends IAMContentContainer<InAppMessageBottomSheetAppearance> {
    private BottomSheetBehavior<RoundedCornerLayout> bottomSheetBehavior;
    private RoundedCornerLayout roundedCornerLayout;
    private CoordinatorLayout.LayoutParams layoutParams;
    private FrameLayout content;
    private BottomSheetLine bottomSheetLine;
    private FrameLayout bottomSheetLineContainer;

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
        super.init(context);
        CoordinatorLayout coordinatorLayout = new CoordinatorLayout(context);
        coordinatorLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        background.setAlpha(0f);
        roundedCornerLayout = new RoundedCornerLayout(context);
        roundedCornerLayout.setRadius(0);
        layoutParams = new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
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
        bottomSheetLine = new BottomSheetLine(context);
        bottomSheetLineContainer = new FrameLayout(context);
        bottomSheetLineContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeWithAnimation();
            }
        });
        roundedCornerLayout.addView(bottomSheetLineContainer);
        bottomSheetLineContainer.addView(bottomSheetLine);
        bottomSheetBehavior = BottomSheetBehavior.from(roundedCornerLayout);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                bsState = newState;
                if (callback != null) {
                    if (newState == STATE_EXPANDED)
                        callback.onShown();
                    else if (newState == STATE_COLLAPSED || newState == STATE_HIDDEN) {
                        callback.onClosed();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset >= 0 && slideOffset <= 1f)
                    background.setAlpha(slideOffset);
            }
        });
        coordinatorLayout.addView(roundedCornerLayout);
        addView(coordinatorLayout);
        if (appearance != null) appearance(appearance);
    }

    @Override
    protected void visibleRectIsCalculated() {
        float contentRatio = 1.33f;
        if (appearance.contentRatio() < 5f && appearance.contentRatio() > 0.01f) {
            contentRatio = appearance.contentRatio();
        }
        float availableWidth = externalContainerRect.width();
        float availableHeight = externalContainerRect.height();

        Point screenSize = Sizes.getScreenSize(getContext());
        if (Sizes.isTablet(getContext())) {
            availableWidth = Math.min(availableWidth, Sizes.dpToPxExt(340, getContext()));
        } else {
            availableWidth = Math.min(
                    availableWidth,
                    Math.min(screenSize.x, screenSize.y)
            );
        }
        float screenContentRatio = availableWidth / availableHeight;
        if (contentRatio >= screenContentRatio) {
            layoutParams.height = Math.min(
                    Math.round(availableHeight),
                    Math.round(availableWidth / contentRatio)
            );
            layoutParams.width = Math.round(availableWidth);
        } else {
            layoutParams.height = Math.round(availableHeight);
            layoutParams.width = Math.round(availableHeight * contentRatio);
        }

        roundedCornerLayout.setRadius(
                Sizes.dpToPxExt(appearance.cornerRadius(), getContext()),
                Sizes.dpToPxExt(appearance.cornerRadius(), getContext()),
                0,
                0
        );
        roundedCornerLayout.setLayoutParams(
                layoutParams
        );
        roundedCornerLayout.requestLayout();
    }

    @Override
    public void appearance(InAppMessageBottomSheetAppearance appearance) {
        super.appearance(appearance);
        if (content == null) return;

        float backdropAlpha = appearance.backdrop().alpha();
        int backdropColor = ColorUtils.modifyAlpha(Color.BLACK, backdropAlpha);
        background.setBackgroundColor(backdropColor);
        int backgroundColor = ColorUtils.parseColorRGBA(
                appearance.backgroundColor()
        );
        content.setBackgroundColor(backgroundColor);
        generateLoader(backgroundColor);
        roundedCornerLayout.addView(loaderContainer);
        if (appearance.lineAppearance().height() == 0) {
            bottomSheetLineContainer.setVisibility(GONE);
            return;
        }
        bottomSheetLineContainer.setVisibility(VISIBLE);
        FrameLayout.LayoutParams blcLp = new FrameLayout.LayoutParams(
                Sizes.dpToPxExt(appearance.lineAppearance().width() + 16, getContext()),
                Sizes.dpToPxExt(appearance.lineAppearance().height() +
                                Math.min(2 * appearance.lineAppearance().topMargin(), 24),
                        getContext()
                )
        );
        FrameLayout.LayoutParams blLp = new FrameLayout.LayoutParams(
                Sizes.dpToPxExt(appearance.lineAppearance().width(), getContext()),
                Sizes.dpToPxExt(appearance.lineAppearance().height(),
                        getContext()
                )
        );
        blcLp.topMargin = Sizes.dpToPxExt(appearance.lineAppearance().topMargin(), getContext());
        blcLp.gravity = Gravity.CENTER_HORIZONTAL;
        blLp.gravity = Gravity.CENTER;
        bottomSheetLine.setLayoutParams(blLp);
        bottomSheetLineContainer.setElevation(8);
        bottomSheetLineContainer.setLayoutParams(blcLp);
        bottomSheetLine.setColor(ColorUtils.parseColorRGBA(appearance.lineAppearance().color()));
        bottomSheetLine.requestLayout();
        bottomSheetLineContainer.requestLayout();
    }

    private int bsState = 0;

    @Override
    public void showWithAnimation() {
        setVisibility(VISIBLE);
        bottomSheetBehavior.setState(STATE_EXPANDED);
    }

    @Override
    public void showWithoutAnimation() {
        showWithAnimation();
    }


    @Override
    public void closeWithAnimation() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void closeWithoutAnimation() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

}
