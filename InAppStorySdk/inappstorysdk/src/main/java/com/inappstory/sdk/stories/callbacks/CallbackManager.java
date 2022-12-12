package com.inappstory.sdk.stories.callbacks;

import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.outercallbacks.common.gamereader.GameCallback;
import com.inappstory.sdk.stories.outercallbacks.common.onboarding.OnboardingLoadCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickOnShareStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseReader;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CustomActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.FavoriteStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.LikeDislikeStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowSlideCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryWidgetCallback;
import com.inappstory.sdk.stories.outercallbacks.common.single.SingleLoadCallback;
import com.inappstory.sdk.stories.outerevents.CloseStory;

public class CallbackManager {

    public ErrorCallback getErrorCallback() {
        return errorCallback;
    }


    public GameCallback getGameCallback() {
        return gameCallback;
    }

    public OnboardingLoadCallback getOnboardingLoadCallback() {
        return onboardingLoadCallback;
    }

    public CallToActionCallback getCallToActionCallback() {
        return callToActionCallback;
    }
    public StoryWidgetCallback getStoryWidgetCallback() {
        return storyWidgetCallback;
    }

    public CloseStoryCallback getCloseStoryCallback() {
        return closeStoryCallback;
    }

    public FavoriteStoryCallback getFavoriteStoryCallback() {
        return favoriteStoryCallback;
    }

    public LikeDislikeStoryCallback getLikeDislikeStoryCallback() {
        return likeDislikeStoryCallback;
    }

    public ShowSlideCallback getShowSlideCallback() {
        return showSlideCallback;
    }

    public ShowStoryCallback getShowStoryCallback() {
        return showStoryCallback;
    }

    public SingleLoadCallback getSingleLoadCallback() {
        return singleLoadCallback;
    }

    public SourceType getSourceFromInt(int intSourceType) {
        SourceType sourceType = SourceType.LIST;
        switch (intSourceType) {
            case CloseStory.SINGLE:
                sourceType = SourceType.SINGLE;
                break;
            case CloseStory.ONBOARDING:
                sourceType = SourceType.ONBOARDING;
                break;
            case CloseStory.FAVORITE:
                sourceType = SourceType.FAVORITE;
                break;
        }
        return sourceType;
    }

    public CloseReader getCloseTypeFromInt(int intCloseType) {
        CloseReader closeType = CloseReader.CLICK;
        switch (intCloseType) {
            case CloseStory.AUTO:
                closeType = CloseReader.AUTO;
                break;
            case CloseStory.SWIPE:
                closeType = CloseReader.SWIPE;
                break;
            case CloseStory.CUSTOM:
                closeType = CloseReader.CUSTOM;
                break;
        }
        return closeType;
    }

    private ErrorCallback errorCallback;
    private GameCallback gameCallback;
    private OnboardingLoadCallback onboardingLoadCallback;
    private StoryWidgetCallback storyWidgetCallback;


    public void setStoryWidgetCallback(StoryWidgetCallback storyWidgetCallback) {
        this.storyWidgetCallback = storyWidgetCallback;
    }

    public ClickOnShareStoryCallback getClickOnShareStoryCallback() {
        return clickOnShareStoryCallback;
    }

    public void setClickOnShareStoryCallback(ClickOnShareStoryCallback clickOnShareStoryCallback) {
        this.clickOnShareStoryCallback = clickOnShareStoryCallback;
    }

    private ClickOnShareStoryCallback clickOnShareStoryCallback;

    public void setErrorCallback(ErrorCallback errorCallback) {
        this.errorCallback = errorCallback;
    }

    public void setGameCallback(GameCallback gameCallback) {
        this.gameCallback = gameCallback;
    }

    public void setOnboardingLoadCallback(OnboardingLoadCallback onboardingLoadCallback) {
        this.onboardingLoadCallback = onboardingLoadCallback;
    }

    public void setCallToActionCallback(CallToActionCallback callToActionCallback) {
        this.callToActionCallback = callToActionCallback;
    }

    public void setCloseStoryCallback(CloseStoryCallback closeStoryCallback) {
        this.closeStoryCallback = closeStoryCallback;
    }

    public void setFavoriteStoryCallback(FavoriteStoryCallback favoriteStoryCallback) {
        this.favoriteStoryCallback = favoriteStoryCallback;
    }

    public void setLikeDislikeStoryCallback(LikeDislikeStoryCallback likeDislikeStoryCallback) {
        this.likeDislikeStoryCallback = likeDislikeStoryCallback;
    }

    public void setShowSlideCallback(ShowSlideCallback showSlideCallback) {
        this.showSlideCallback = showSlideCallback;
    }

    public void setShowStoryCallback(ShowStoryCallback showStoryCallback) {
        this.showStoryCallback = showStoryCallback;
    }

    public void setSingleLoadCallback(SingleLoadCallback singleLoadCallback) {
        this.singleLoadCallback = singleLoadCallback;
    }

    private CallToActionCallback callToActionCallback;
    private CloseStoryCallback closeStoryCallback;
    private FavoriteStoryCallback favoriteStoryCallback;
    private LikeDislikeStoryCallback likeDislikeStoryCallback;
    private ShowSlideCallback showSlideCallback;
    private ShowStoryCallback showStoryCallback;
    private SingleLoadCallback singleLoadCallback;

    public CustomActionCallback getCustomActionCallback() {
        return customActionCallback;
    }

    public void setCustomActionCallback(CustomActionCallback customActionCallback) {
        this.customActionCallback = customActionCallback;
    }

    private CustomActionCallback customActionCallback;


    private UrlClickCallback urlClickCallback;
    private AppClickCallback appClickCallback;
    private ShareCallback shareCallback;

    public void setUrlClickCallback(UrlClickCallback urlClickCallback) {
        this.urlClickCallback = urlClickCallback;
    }

    public void setAppClickCallback(AppClickCallback appClickCallback) {
        this.appClickCallback = appClickCallback;
    }

    public void setShareCallback(ShareCallback shareCallback) {
        this.shareCallback = shareCallback;
    }

    public ShareCallback getShareCallback() {
        return shareCallback;
    }


    public UrlClickCallback getUrlClickCallback() {
        return urlClickCallback;
    }

    public AppClickCallback getAppClickCallback() {
        return appClickCallback;
    }

    private CallbackManager() {

    }

    private static CallbackManager INSTANCE;

    private static Object lock = new Object();

    public static CallbackManager getInstance() {
        synchronized (lock) {
            if (INSTANCE == null)
                INSTANCE = new CallbackManager();
            return INSTANCE;
        }
    }
}
