package com.inappstory.sdk.stories.callbacks;

import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.outercallbacks.common.gamereader.GameReaderCallback;
import com.inappstory.sdk.stories.outercallbacks.common.onboarding.OnboardingLoadCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickOnShareStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseReader;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.FavoriteStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.LikeDislikeStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowSlideCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowStoryAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryWidgetCallback;
import com.inappstory.sdk.stories.outercallbacks.common.single.SingleLoadCallback;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.outerevents.ShowStory;

public class CallbackManager {
    private ErrorCallback errorCallback;
    private GameReaderCallback gameReaderCallback;
    private OnboardingLoadCallback onboardingLoadCallback;
    private StoryWidgetCallback storyWidgetCallback;
    private ClickOnShareStoryCallback clickOnShareStoryCallback;
    private CallToActionCallback callToActionCallback;
    private CloseStoryCallback closeStoryCallback;
    private FavoriteStoryCallback favoriteStoryCallback;
    private LikeDislikeStoryCallback likeDislikeStoryCallback;
    private ShowSlideCallback showSlideCallback;
    private ShowStoryCallback showStoryCallback;
    private SingleLoadCallback singleLoadCallback;
    private UrlClickCallback urlClickCallback;
    private AppClickCallback appClickCallback;
    private ShareCallback shareCallback;

    public ErrorCallback getErrorCallback() {
        return errorCallback;
    }

    public GameReaderCallback getGameReaderCallback() {
        return gameReaderCallback;
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

    public ShowStoryAction getShowStoryActionTypeFromInt(int intActionType) {
        ShowStoryAction type = ShowStoryAction.OPEN;
        switch (intActionType) {
            case ShowStory.ACTION_AUTO:
                type = ShowStoryAction.AUTO;
                break;
            case ShowStory.ACTION_TAP:
                type = ShowStoryAction.TAP;
                break;
            case ShowStory.ACTION_CUSTOM:
                type = ShowStoryAction.CUSTOM;
                break;
            case ShowStory.ACTION_SWIPE:
                type = ShowStoryAction.SWIPE;
                break;
        }
        return type;
    }

    public ClickOnShareStoryCallback getClickOnShareStoryCallback() {
        return clickOnShareStoryCallback;
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
            case -1:
                closeType = CloseReader.CUSTOM;
                break;
        }
        return closeType;
    }


    public void setStoryWidgetCallback(StoryWidgetCallback storyWidgetCallback) {
        this.storyWidgetCallback = storyWidgetCallback;
    }

    public void setClickOnShareStoryCallback(ClickOnShareStoryCallback clickOnShareStoryCallback) {
        this.clickOnShareStoryCallback = clickOnShareStoryCallback;
    }


    public void setErrorCallback(ErrorCallback errorCallback) {
        this.errorCallback = errorCallback;
    }
    public void setGameReaderCallback(GameReaderCallback gameReaderCallback) {
        this.gameReaderCallback = gameReaderCallback;
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

    @Deprecated
    public void setUrlClickCallback(UrlClickCallback urlClickCallback) {
        this.urlClickCallback = urlClickCallback;
    }

    public void setAppClickCallback(AppClickCallback appClickCallback) {
        this.appClickCallback = appClickCallback;
    }

    public void setShareCallback(ShareCallback readerTopContainerCallback) {
        this.shareCallback = readerTopContainerCallback;
    }

    public ShareCallback getShareCallback() {
        return shareCallback;
    }



    @Deprecated
    public UrlClickCallback getUrlClickCallback() {
        return urlClickCallback;
    }

    public AppClickCallback getAppClickCallback() {
        return appClickCallback;
    }

    private CallbackManager() {

    }

    private static CallbackManager INSTANCE;
    private static final Object lock = new Object();

    public static CallbackManager getInstance() {
        synchronized (lock) {
            if (INSTANCE == null)
                INSTANCE = new CallbackManager();
            return INSTANCE;
        }
    }


}
