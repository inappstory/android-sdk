package com.inappstory.sdk.usecase.callbacks;

import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;

public class UseCaseCallbackLikeDislikeStory implements IUseCaseCallback {
    private SlideData slideData;
    private boolean value;

    private boolean like;

    public UseCaseCallbackLikeDislikeStory(SlideData slideData,
                                           boolean like,
                                           boolean value) {
        this.slideData = slideData;
        this.value = value;
        this.like = like;
    }

    @Override
    public void invoke() {
        if (CallbackManager.getInstance().getLikeDislikeStoryCallback() != null) {
            if (like) {
                CallbackManager.getInstance().getLikeDislikeStoryCallback().likeStory(slideData, value);
            } else  {
                CallbackManager.getInstance().getLikeDislikeStoryCallback().dislikeStory(slideData, value);
            }
        }
    }
}
