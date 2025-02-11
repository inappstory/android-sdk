package com.inappstory.sdk.externalapi.callbacks;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASCallbacks;
import com.inappstory.sdk.inappmessage.CloseInAppMessageCallback;
import com.inappstory.sdk.inappmessage.InAppMessageWidgetCallback;
import com.inappstory.sdk.inappmessage.ShowInAppMessageCallback;
import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickOnShareStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.FavoriteStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.LikeDislikeStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowSlideCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryWidgetCallback;

public interface IASCallbacksExternalAPI extends IASCallbacks {
    void error(final ErrorCallback errorCallback);

    void clickOnShareStory(final ClickOnShareStoryCallback clickOnShareStoryCallback);

    void callToAction(final CallToActionCallback callToActionCallback);

    void storyWidget(final StoryWidgetCallback storyWidgetCallback);

    void closeStory(final CloseStoryCallback closeStoryCallback);

    void favoriteStory(final FavoriteStoryCallback favoriteStoryCallback);

    void likeDislikeStory(final LikeDislikeStoryCallback likeDislikeStoryCallback);

    void showSlide(final ShowSlideCallback showSlideCallback);

    void showStory(final ShowStoryCallback showStoryCallback);

    void showInAppMessage(final ShowInAppMessageCallback showInAppMessageCallback) ;

    void closeInAppMessage(final CloseInAppMessageCallback closeInAppMessageCallback);

    void inAppMessageWidget(final InAppMessageWidgetCallback inAppMessageWidgetCallback);
}