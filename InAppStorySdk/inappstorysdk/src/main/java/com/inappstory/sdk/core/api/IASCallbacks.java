package com.inappstory.sdk.core.api;

import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickOnShareStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.FavoriteStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.LikeDislikeStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowSlideCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryWidgetCallback;

public interface IASCallbacks {
    void error(ErrorCallback errorCallback);
    void clickOnShareStory(ClickOnShareStoryCallback clickOnShareStoryCallback);
    void callToAction(CallToActionCallback callToActionCallback);
    void storyWidget(StoryWidgetCallback storyWidgetCallback);
    void closeStory(CloseStoryCallback closeStoryCallback);
    void favoriteStory(FavoriteStoryCallback favoriteStoryCallback);
    void likeDislikeStory(LikeDislikeStoryCallback likeDislikeStoryCallback);
    void showSlide(ShowSlideCallback showSlideCallback);
    void showStory(ShowStoryCallback showStoryCallback);
}
