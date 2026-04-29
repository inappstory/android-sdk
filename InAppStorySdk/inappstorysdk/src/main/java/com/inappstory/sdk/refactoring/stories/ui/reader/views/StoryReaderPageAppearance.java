package com.inappstory.sdk.refactoring.stories.ui.reader.views;

import com.inappstory.sdk.stories.ui.reader.StoriesGradientObject;

public class StoryReaderPageAppearance {
    public final boolean hasLikeFeature;
    public final boolean hasFavoriteFeature;
    public final boolean hasShareFeature;
    public final boolean closeOnVerticalSwipe;
    public final boolean closeOnHorizontalSwipe;
    public final int closePosition;
    public final boolean timerGradientEnable;
    public final int readerBackgroundColor;
    public final StoriesGradientObject timerGradient;
    public final ScreenPosition screenPosition;

    public StoryReaderPageAppearance(
            boolean hasLikeFeature,
            boolean hasFavoriteFeature,
            boolean hasShareFeature,
            boolean closeOnVerticalSwipe,
            boolean closeOnHorizontalSwipe,
            int closePosition,
            int readerBackgroundColor,
            boolean timerGradientEnable,
            StoriesGradientObject timerGradient,
            ScreenPosition screenPosition
    ) {
        this.hasLikeFeature = hasLikeFeature;
        this.hasFavoriteFeature = hasFavoriteFeature;
        this.hasShareFeature = hasShareFeature;
        this.closeOnVerticalSwipe = closeOnVerticalSwipe;
        this.closeOnHorizontalSwipe = closeOnHorizontalSwipe;
        this.closePosition = closePosition;
        this.timerGradientEnable = timerGradientEnable;
        this.readerBackgroundColor = readerBackgroundColor;
        this.timerGradient = timerGradient;
        this.screenPosition = screenPosition;
    }
}
