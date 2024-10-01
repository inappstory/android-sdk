package com.inappstory.sdk.externalapi.callbacks;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASCallbacks;
import com.inappstory.sdk.core.api.UseIASCallback;
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

    private void useCore(UseIASCoreCallback callback) {
        InAppStoryManager.useCore(callback);
    }

    public void error(final ErrorCallback errorCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().error(errorCallback);
            }
        });
    }

    public void clickOnShareStory(final ClickOnShareStoryCallback clickOnShareStoryCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().clickOnShareStory(clickOnShareStoryCallback);
            }
        });
    }

    public void callToAction(final CallToActionCallback callToActionCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().callToAction(callToActionCallback);
            }
        });
    }

    public void storyWidget(final StoryWidgetCallback storyWidgetCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().storyWidget(storyWidgetCallback);
            }
        });
    }

    public void closeStory(final CloseStoryCallback closeStoryCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().closeStory(closeStoryCallback);
            }
        });
    }

    public void favoriteStory(final FavoriteStoryCallback favoriteStoryCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().favoriteStory(favoriteStoryCallback);
            }
        });
    }

    public void likeDislikeStory(final LikeDislikeStoryCallback likeDislikeStoryCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().likeDislikeStory(likeDislikeStoryCallback);
            }
        });
    }

    public void showSlide(final ShowSlideCallback showSlideCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().showSlide(showSlideCallback);
            }
        });
    }

    public void showStory(final ShowStoryCallback showStoryCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().showStory(showStoryCallback);
            }
        });
    }

    @Override
    public void useCallback(IASCallbackType type, @NonNull UseIASCallback useIASCallback) {

    }
}
