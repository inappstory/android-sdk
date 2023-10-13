package com.inappstory.sdk.usecase.callbacks;

import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.CloseReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.ShowStoryAction;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryData;

public class UseCaseCallbackShowSlide implements IUseCaseCallback {
    private SlideData slideData;

    public UseCaseCallbackShowSlide(SlideData slideData) {
        this.slideData = slideData;
    }

    @Override
    public void invoke() {
        if (CallbackManager.getInstance().getShowSlideCallback() != null) {
            CallbackManager.getInstance().getShowSlideCallback().showSlide(slideData);
        }
    }
}
