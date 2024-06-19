package com.inappstory.sdk.utils;

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

public interface ICallbacksApi {
    void setShareCallback(ShareCallback shareCallback);
    void setSingleLoadCallback(SingleLoadCallback singleLoadCallback);
    void setShowStoryCallback(ShowStoryCallback showStoryCallback);
    void setShowSlideCallback(ShowSlideCallback showSlideCallback);
    void setLikeDislikeStoryCallback(LikeDislikeStoryCallback likeDislikeStoryCallback);
    void setFavoriteStoryCallback(FavoriteStoryCallback favoriteStoryCallback);
    void setCloseStoryCallback(CloseStoryCallback closeStoryCallback);
    void setStoryWidgetCallback(StoryWidgetCallback storyWidgetCallback);
    void setCallToActionCallback(CallToActionCallback callToActionCallback);
    void setOnboardingLoadCallback(OnboardingLoadCallback onboardingLoadCallback);
    void setGameReaderCallback(GameReaderCallback gameReaderCallback);
    void setClickOnShareStoryCallback(ClickOnShareStoryCallback clickOnShareStoryCallback);
    void setErrorCallback(ErrorCallback errorCallback);
}
