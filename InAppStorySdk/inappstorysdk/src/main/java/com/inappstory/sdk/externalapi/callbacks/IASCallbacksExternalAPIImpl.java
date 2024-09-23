package com.inappstory.sdk.externalapi.callbacks;

import com.inappstory.sdk.core.api.IASCallbacks;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickOnShareStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.FavoriteStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.LikeDislikeStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowSlideCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryWidgetCallback;

public class IASCallbacksExternalAPIImpl implements IASCallbacks {

    public void error(ErrorCallback errorCallback) {
        CallbackManager.getInstance().setErrorCallback(errorCallback);
    }

    public void clickOnShareStory(ClickOnShareStoryCallback clickOnShareStoryCallback) {
        CallbackManager.getInstance().setClickOnShareStoryCallback(clickOnShareStoryCallback);
    }

    public void callToAction(CallToActionCallback callToActionCallback) {
        CallbackManager.getInstance().setCallToActionCallback(callToActionCallback);
    }

    public void storyWidget(StoryWidgetCallback storyWidgetCallback) {
        CallbackManager.getInstance().setStoryWidgetCallback(storyWidgetCallback);
    }

    public void closeStory(CloseStoryCallback closeStoryCallback) {
        CallbackManager.getInstance().setCloseStoryCallback(closeStoryCallback);
    }

    public void favoriteStory(FavoriteStoryCallback favoriteStoryCallback) {
        CallbackManager.getInstance().setFavoriteStoryCallback(favoriteStoryCallback);
    }

    public void likeDislikeStory(LikeDislikeStoryCallback likeDislikeStoryCallback) {
        CallbackManager.getInstance().setLikeDislikeStoryCallback(likeDislikeStoryCallback);
    }

    public void showSlide(ShowSlideCallback showSlideCallback) {
        CallbackManager.getInstance().setShowSlideCallback(showSlideCallback);
    }

    public void showStory(ShowStoryCallback showStoryCallback) {
        CallbackManager.getInstance().setShowStoryCallback(showStoryCallback);
    }
}
