package com.inappstory.sdk.core.ui.widgets.bs;

import android.content.Context;
import android.graphics.Color;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.core.math.MathUtils;

import com.inappstory.sdk.stories.utils.Sizes;

public final class Config implements BaseConfig {

    private final float dimAmount;
    private final float sheetCornerRadius;
    private final float maxSheetWidth;
    private final float topGapSize;
    private final float extraPaddingTop;
    private final float extraPaddingBottom;

    private final int dimColor;
    private final int sheetBackgroundColor;

    private final long animationDuration;
    private final Interpolator animationInterpolator;

    private final boolean isDismissableOnTouchOutside;

    private static final String defaultBottomSheetDimColor = "#000000";
    private static final String defaultBottomSheetBackgroundColor = "#ffffff";

    private Config(Builder builder) {
        this.dimAmount = builder.dimAmount;
        this.sheetCornerRadius = builder.sheetCornerRadius;
        this.maxSheetWidth = builder.maxSheetWidth;
        this.topGapSize = builder.topGapSize;
        this.extraPaddingTop = builder.extraPaddingTop;
        this.extraPaddingBottom = builder.extraPaddingBottom;
        this.dimColor = builder.dimColor;
        this.sheetBackgroundColor = builder.sheetBackgroundColor;
        this.animationDuration = builder.animationDuration;
        this.animationInterpolator = builder.animationInterpolator;
        this.isDismissableOnTouchOutside = builder.isDismissableOnTouchOutside;
    }

    @Override
    public int getDimColor() {
        return this.dimColor;
    }

    @Override
    public float getDimAmount() {
        return this.dimAmount;
    }

    @Override
    public float getTopGapSize() {
        return this.topGapSize;
    }

    @Override
    public float getExtraPaddingTop() {
        return this.extraPaddingTop;
    }

    @Override
    public float getExtraPaddingBottom() {
        return this.extraPaddingBottom;
    }

    @Override
    public float getMaxSheetWidth() {
        return this.maxSheetWidth;
    }

    @Override
    public int getSheetBackgroundColor() {
        return this.sheetBackgroundColor;
    }

    @Override
    public float getSheetCornerRadius() {
        return this.sheetCornerRadius;
    }

    @Override
    public long getSheetAnimationDuration() {
        return this.animationDuration;
    }

    @NonNull
    @Override
    public Interpolator getSheetAnimationInterpolator() {
        return this.animationInterpolator;
    }

    @Override
    public boolean isDismissableOnTouchOutside() {
        return this.isDismissableOnTouchOutside;
    }

    public static final class Builder implements BaseConfigBuilder<Builder, BaseConfig> {

        private float dimAmount;
        private float sheetCornerRadius;
        private float maxSheetWidth;
        private float topGapSize;
        private float extraPaddingTop;
        private float extraPaddingBottom;

        private int dimColor;
        private int sheetBackgroundColor;

        private long animationDuration;
        private Interpolator animationInterpolator;

        private boolean isDismissableOnTouchOutside;

        public Builder(@NonNull Context context) {
            Preconditions.nonNull(context);

            this.dimAmount = DEFAULT_DIM_AMOUNT;
            this.sheetCornerRadius = Sizes.dpToPxExt(8, context);
            this.topGapSize = 0;
            this.extraPaddingTop = 0;
            this.extraPaddingBottom = 0;
            this.maxSheetWidth = Sizes.dpToPxExt(512, context);
            this.dimColor = Color.parseColor(defaultBottomSheetDimColor);
            this.sheetBackgroundColor = Color.parseColor(defaultBottomSheetBackgroundColor);
            this.animationDuration = DEFAULT_ANIMATION_DURATION;
            this.animationInterpolator = new DecelerateInterpolator(1.5f);
            this.isDismissableOnTouchOutside = true;
        }

        @NonNull
        public Builder dimAmount(@FloatRange(from = 0.0f, to = 1.0f) float dimAmount) {
            this.dimAmount = MathUtils.clamp(dimAmount, MIN_DIM_AMOUNT, MAX_DIM_AMOUNT);
            return this;
        }

        @NonNull
        public Builder sheetCornerRadius(float cornerRadius) {
            this.sheetCornerRadius = cornerRadius;
            return this;
        }

        @NonNull
        public Builder topGapSize(float topGapSize) {
            this.topGapSize = topGapSize;
            return this;
        }

        @NonNull
        @Override
        public Builder extraPaddingTop(float extraPaddingTop) {
            this.extraPaddingTop = extraPaddingTop;
            return this;
        }

        @NonNull
        @Override
        public Builder extraPaddingBottom(float extraPaddingBottom) {
            this.extraPaddingBottom = extraPaddingBottom;
            return this;
        }

        @NonNull
        public Builder maxSheetWidth(float maxWidth) {
            this.maxSheetWidth = maxWidth;
            return this;
        }

        @NonNull
        public Builder dimColor(@ColorInt int dimColor) {
            this.dimColor = dimColor;
            return this;
        }

        @NonNull
        public Builder sheetBackgroundColor(@ColorInt int color) {
            this.sheetBackgroundColor = color;
            return this;
        }

        @NonNull
        public Builder sheetAnimationDuration(long animationDuration) {
            this.animationDuration = animationDuration;
            return this;
        }

        @NonNull
        public Builder sheetAnimationInterpolator(@NonNull Interpolator interpolator) {
            this.animationInterpolator = Preconditions.checkNonNull(interpolator);
            return this;
        }

        @NonNull
        public Builder dismissOnTouchOutside(boolean dismissOnTouchOutside) {
            this.isDismissableOnTouchOutside = dismissOnTouchOutside;
            return this;
        }

        @NonNull
        @Override
        public BaseConfig build() {
            return new Config(this);
        }

    }

}