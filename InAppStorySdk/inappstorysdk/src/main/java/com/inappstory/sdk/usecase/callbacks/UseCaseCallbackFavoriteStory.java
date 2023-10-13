package com.inappstory.sdk.usecase.callbacks;

import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;

public class UseCaseCallbackFavoriteStory implements IUseCaseCallback {
    private SlideData slideData;
    private boolean value;

    public UseCaseCallbackFavoriteStory(SlideData slideData,
                                        boolean value) {
        this.slideData = slideData;
        this.value = value;
    }

    @Override
    public void invoke() {
        if (CallbackManager.getInstance().getFavoriteStoryCallback() != null) {
            CallbackManager.getInstance().getFavoriteStoryCallback().favoriteStory(slideData, value);
        }
    }
}
