package com.inappstory.sdk.core.api.impl;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASCallbacks;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.stories.callbacks.ShareCallback;
import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.outercallbacks.common.gamereader.GameReaderCallback;
import com.inappstory.sdk.stories.outercallbacks.common.onboarding.OnboardingLoadCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickOnShareStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.FavoriteStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.LikeDislikeStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowSlideCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryWidgetCallback;
import com.inappstory.sdk.stories.outercallbacks.common.single.SingleLoadCallback;

public class IASCallbacksImpl implements IASCallbacks {
    private final IASCore core;

    public IASCallbacksImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void error(ErrorCallback errorCallback) {
        this.errorCallback = errorCallback;
    }

    @Override
    public void clickOnShareStory(ClickOnShareStoryCallback clickOnShareStoryCallback) {
        this.clickOnShareStoryCallback = clickOnShareStoryCallback;
    }

    @Override
    public void callToAction(CallToActionCallback callToActionCallback) {
        this.callToActionCallback = callToActionCallback;
    }

    @Override
    public void storyWidget(StoryWidgetCallback storyWidgetCallback) {
        this.storyWidgetCallback = storyWidgetCallback;
    }

    @Override
    public void closeStory(CloseStoryCallback closeStoryCallback) {
        this.closeStoryCallback = closeStoryCallback;
    }

    @Override
    public void favoriteStory(FavoriteStoryCallback favoriteStoryCallback) {
        this.favoriteStoryCallback = favoriteStoryCallback;
    }

    @Override
    public void likeDislikeStory(LikeDislikeStoryCallback likeDislikeStoryCallback) {
        this.likeDislikeStoryCallback = likeDislikeStoryCallback;
    }

    @Override
    public void showSlide(ShowSlideCallback showSlideCallback) {
        this.showSlideCallback = showSlideCallback;
    }

    @Override
    public void showStory(ShowStoryCallback showStoryCallback) {
        this.showStoryCallback = showStoryCallback;
    }

    public void gameReader(GameReaderCallback gameReaderCallback) {
        this.gameReaderCallback = gameReaderCallback;
    }

    public void onboardingLoad(OnboardingLoadCallback onboardingLoadCallback) {
        this.onboardingLoadCallback = onboardingLoadCallback;
    }

    public void singleLoad(SingleLoadCallback singleLoadCallback) {
        this.singleLoadCallback = singleLoadCallback;
    }

    public void share(ShareCallback shareCallback) {
        this.shareCallback = shareCallback;
    }


    @Override
    public void useCallback(
            IASCallbackType type,
            @NonNull UseIASCallback useIASCallback
    ) {
        switch (type) {
            case ERROR:
                if (errorCallback != null)
                    useIASCallback.use(errorCallback);
                return;
            case FAVORITE:
                if (favoriteStoryCallback != null)
                    useIASCallback.use(favoriteStoryCallback);
                return;
            case SHOW_SLIDE:
                if (showSlideCallback != null)
                    useIASCallback.use(showSlideCallback);
                return;
            case STORY_WIDGET:
                if (storyWidgetCallback != null)
                    useIASCallback.use(storyWidgetCallback);
                return;
            case SHOW_STORY:
                if (showStoryCallback != null)
                    useIASCallback.use(showStoryCallback);
                return;
            case CLOSE_STORY:
                if (closeStoryCallback != null)
                    useIASCallback.use(closeStoryCallback);
                return;
            case LIKE_DISLIKE:
                if (likeDislikeStoryCallback != null)
                    useIASCallback.use(likeDislikeStoryCallback);
                return;
            case ONBOARDING:
                if (onboardingLoadCallback != null)
                    useIASCallback.use(onboardingLoadCallback);
                return;
            case SINGLE:
                if (singleLoadCallback != null)
                    useIASCallback.use(singleLoadCallback);
                return;
            case SHARE:
                if (shareCallback != null)
                    useIASCallback.use(shareCallback);
                return;
            case GAME_READER:
                if (gameReaderCallback != null)
                    useIASCallback.use(gameReaderCallback);
                return;
            default:
                break;
        }
        useIASCallback.onDefault();
    }


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
    private ShareCallback shareCallback;

}
