package com.inappstory.sdk.core.ui.screens.storyreader;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SerializableWithKey;
import com.inappstory.sdk.stories.ui.reader.StoriesGradientObject;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.ArrayList;
import java.util.List;

public class LaunchStoryScreenAppearance implements SerializableWithKey {
    public static String SERIALIZABLE_KEY = "storiesReaderAppearanceSettings";

    public int csClosePosition() {
        return csClosePosition;
    }

    public int csStoryReaderAnimation() {
        return csStoryReaderAnimation;
    }

    public boolean csCloseOnOverscroll() {
        return csCloseOnOverscroll;
    }

    public boolean csCloseOnSwipe() {
        return csCloseOnSwipe;
    }

    public boolean csHasLike() {
        return csHasLike;
    }

    public boolean csHasFavorite() {
        return csHasFavorite;
    }

    public boolean csHasShare() {
        return csHasShare;
    }

    public int csReaderRadius() {
        return csReaderRadius;
    }

    public boolean csTimerGradientEnable() {
        return csTimerGradientEnable;
    }

    public int csReaderBackgroundColor() {
        return csReaderBackgroundColor;
    }

    public StoriesGradientObject csTimerGradient() {
        return csTimerGradient;
    }

    public StoriesGradientObject csFullscreenBottomGradient() {
        return csFullscreenBottomGradient;
    }

    public boolean csIsDraggable() {
        return csIsDraggable;
    }

    public int csNavBarColor() {
        return csNavBarColor;
    }

    public int csStoryReaderPresentationStyle() {
        return csStoryReaderPresentationStyle;
    }

    private final int csClosePosition;
    private final int csStoryReaderAnimation;
    private final int csStoryReaderPresentationStyle;
    private final boolean csCloseOnOverscroll;
    private final boolean csCloseOnSwipe;
    private final boolean csHasLike;
    private final boolean csHasFavorite;
    private final boolean csHasShare;
    private final int csReaderRadius;
    private final boolean csTimerGradientEnable;
    private final int csReaderBackgroundColor;
    private final StoriesGradientObject csTimerGradient;
    private final StoriesGradientObject csFullscreenBottomGradient;
    private final boolean csIsDraggable;
    private final int csNavBarColor;

    public AppearanceManager toAppearanceManager() {
        AppearanceManager appearanceManager = new AppearanceManager();
        appearanceManager.csHasLike(csHasLike());
        appearanceManager.csHasFavorite(csHasFavorite());
        appearanceManager.csHasShare(csHasShare());
        appearanceManager.csClosePosition(csClosePosition());
        appearanceManager.csCloseOnOverscroll(csCloseOnOverscroll());
        appearanceManager.csCloseOnSwipe(csCloseOnSwipe());
        appearanceManager.csIsDraggable(csIsDraggable());
        appearanceManager.csTimerGradientEnable(csTimerGradientEnable());
        appearanceManager.csStoryReaderAnimation(csStoryReaderAnimation());
        appearanceManager.csStoryReaderPresentationStyle(
                csStoryReaderPresentationStyle()
        );
        appearanceManager.csTimerGradient(csTimerGradient());
        appearanceManager.csFullscreenBottomGradient(csFullscreenBottomGradient());
        appearanceManager.csReaderRadius(csReaderRadius());
        appearanceManager.csReaderBackgroundColor(csReaderBackgroundColor());
        return appearanceManager;
    }

    public LaunchStoryScreenAppearance(
            AppearanceManager manager,
            Context context,
            boolean nonAnonymousFeaturesAvailable
    ) {
        csClosePosition = manager.csClosePosition();
        csStoryReaderAnimation = manager.csStoryReaderAnimation();
        csStoryReaderPresentationStyle = manager.csStoryReaderPresentationStyle();
        csCloseOnOverscroll = manager.csCloseOnOverscroll();
        csCloseOnSwipe = manager.csCloseOnSwipe();
        csHasLike = nonAnonymousFeaturesAvailable && manager.csHasLike();
        csIsDraggable = manager.csIsDraggable();
        csHasFavorite = nonAnonymousFeaturesAvailable && manager.csHasFavorite();
        csHasShare = manager.csHasShare();
        csReaderRadius = manager.csReaderRadius(context);
        csTimerGradientEnable = manager.csTimerGradientEnable();
        csReaderBackgroundColor = manager.csReaderBackgroundColor();
        int nightModeFlags = Configuration.UI_MODE_NIGHT_MASK;
        try {
            nightModeFlags = context.getResources().getConfiguration().uiMode &
                    Configuration.UI_MODE_NIGHT_MASK;
        } catch (Exception ignored) {
        }
        csNavBarColor = nightModeFlags == Configuration.UI_MODE_NIGHT_YES ?
                manager.csNightNavBarColor() : manager.csNavBarColor();
        if (manager.csTimerGradient() != null) {
            csTimerGradient = manager.csTimerGradient();
        } else {
            List<Integer> csColors = new ArrayList<>();
            List<Float> csLocations = new ArrayList<>();
            csColors.add(Color.parseColor("#30000000"));
            csColors.add(Color.parseColor("#00000000"));
            csLocations.add(0f);
            csLocations.add(1f);
            csTimerGradient = new StoriesGradientObject()
                    .csGradientHeight(200)
                    .csColors(csColors)
                    .csLocations(csLocations);
        }
        if (manager.csTimerGradient() != null) {
            csFullscreenBottomGradient = manager.csFullscreenBottomGradient();
        } else {
            List<Integer> csColors = new ArrayList<>();
            List<Float> csLocations = new ArrayList<>();
            csColors.add(Color.parseColor("#00000000"));
            csColors.add(Color.parseColor("#30000000"));
            csLocations.add(0f);
            csLocations.add(1f);
            csFullscreenBottomGradient = new StoriesGradientObject()
                    .csGradientHeight(200)
                    .csColors(csColors)
                    .csLocations(csLocations);
        }
    }

    @Override
    public String getSerializableKey() {
        return SERIALIZABLE_KEY;
    }
}