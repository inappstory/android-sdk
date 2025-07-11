package com.inappstory.sdk.core.ui.widgets.bs;


import android.view.animation.Interpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

public interface BaseConfig {

    float MIN_DIM_AMOUNT = 0f;
    float MAX_DIM_AMOUNT = 1f;
    float DEFAULT_DIM_AMOUNT = 0.65f;

    long DEFAULT_ANIMATION_DURATION = 300L;

    /**
     * Retrieves the background dimming color.
     *
     * @return the color used for the background dimming
     */
    @ColorInt
    int getDimColor();

    /**
     * Retrieves the background dim amount (The background dimming color alpha).
     *
     * @return the dim amount used for the background dimming
     */
    @FloatRange(from = 0.0f, to = 1.0f)
    float getDimAmount();

    /**
     * Retrieves the top gap size.
     *
     * @return the top gap size
     */
    float getTopGapSize();

    /**
     * Retrieves the extra padding top.
     *
     * @return the extra padding from the top.
     */
    float getExtraPaddingTop();

    /**
     * Retrieves the extra padding bottom.
     *
     * @return the extra padding from the bottom.
     */
    float getExtraPaddingBottom();

    /**
     * Retrieves the sheet's maximum width.
     *
     * @return the sheet's maximum width
     */
    float getMaxSheetWidth();

    /**
     * Retrieves the sheet's background color.
     *
     * @return the sheet's background color
     */
    @ColorInt
    int getSheetBackgroundColor();

    /**
     * Retrieves the sheet's top corner radius.
     *
     * @return the sheet's top corner radius
     */
    float getSheetCornerRadius();

    /**
     * Retrieves the sheet's animation duration.
     *
     * @return the sheet's animation duration (in milliseconds)
     */
    long getSheetAnimationDuration();

    /**
     * Retrieves the sheet's animation interpolator.
     *
     * @return the sheet's animation interpolator
     */
    @NonNull
    Interpolator getSheetAnimationInterpolator();

    /**
     * Used to determine if the sheet is to be dismissed when an outside touch is detected.
     *
     * @return whether the sheet will be dismissed on touch outside or not
     */
    boolean isDismissableOnTouchOutside();

}