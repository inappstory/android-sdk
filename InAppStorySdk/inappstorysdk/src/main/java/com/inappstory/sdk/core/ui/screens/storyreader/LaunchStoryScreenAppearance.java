package com.inappstory.sdk.core.ui.screens.storyreader;

import android.content.Context;
import android.content.res.Configuration;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SerializableWithKey;
import com.inappstory.sdk.stories.ui.reader.StoriesGradientObject;
import com.inappstory.sdk.stories.utils.Sizes;

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

   /* public int csCloseIcon() {
        return csCloseIcon;
    }

    public int csRefreshIcon() {
        return csRefreshIcon;
    }

    public int csSoundIcon() {
        return csSoundIcon;
    }

    public int csFavoriteIcon() {
        return csFavoriteIcon;
    }

    public int csLikeIcon() {
        return csLikeIcon;
    }

    public int csDislikeIcon() {
        return csDislikeIcon;
    }

    public int csShareIcon() {
        return csShareIcon;
    }*/

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
    // private final int csCloseIcon;
    //  private final int csRefreshIcon;
    // private final int csSoundIcon;
    // private final int csFavoriteIcon;
    // private final int csLikeIcon;
    // private final int csDislikeIcon;
    //  private final int csShareIcon;
    private final int csReaderRadius;
    private final boolean csTimerGradientEnable;
    private final int csReaderBackgroundColor;
    private final StoriesGradientObject csTimerGradient;
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
        //   appearanceManager.csCloseIcon(csCloseIcon());
        //  appearanceManager.csDislikeIcon(csDislikeIcon());
        //   appearanceManager.csLikeIcon(csLikeIcon());
        //    appearanceManager.csRefreshIcon(csRefreshIcon());
        //    appearanceManager.csFavoriteIcon(csFavoriteIcon());
        //     appearanceManager.csShareIcon(csShareIcon());
        //    appearanceManager.csSoundIcon(csSoundIcon());
        appearanceManager.csStoryReaderPresentationStyle(
                csStoryReaderPresentationStyle()
        );
        appearanceManager.csTimerGradient(csTimerGradient());
        appearanceManager.csReaderRadius(csReaderRadius());
        appearanceManager.csReaderBackgroundColor(csReaderBackgroundColor());
        return appearanceManager;
    }

    public LaunchStoryScreenAppearance(
            AppearanceManager manager,
            Context context
    ) {
        csClosePosition = manager.csClosePosition();//
        csStoryReaderAnimation = manager.csStoryReaderAnimation();//
        csStoryReaderPresentationStyle = manager.csStoryReaderPresentationStyle();//
        csCloseOnOverscroll = manager.csCloseOnOverscroll();//
        csCloseOnSwipe = manager.csCloseOnSwipe();//
        csHasLike = manager.csHasLike();//
        csIsDraggable = manager.csIsDraggable();//
        csHasFavorite = manager.csHasFavorite();//
        csHasShare = manager.csHasShare();//
        //  csCloseIcon = manager.csCloseIcon();//
        ///  csRefreshIcon = manager.csRefreshIcon();//
        //     csSoundIcon = manager.csSoundIcon();//
        //   csFavoriteIcon = manager.csFavoriteIcon();//
        //   csLikeIcon = manager.csLikeIcon();//
        //   csDislikeIcon = manager.csDislikeIcon();//
        //   csShareIcon = manager.csShareIcon();//
        csReaderRadius = manager.csReaderRadius(context);
        csTimerGradientEnable = manager.csTimerGradientEnable();//
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
            csTimerGradient = manager.csTimerGradient();//
        } else {
            csTimerGradient = new StoriesGradientObject()
                    .csGradientHeight(Sizes.getScreenSize(context).y);
        }
    }

    @Override
    public String getSerializableKey() {
        return SERIALIZABLE_KEY;
    }
}