package com.inappstory.sdk.usecase.callbacks;

import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryData;
import com.inappstory.sdk.utils.StringsUtils;

public class UseCaseCallbackShareClick implements IUseCaseCallback {
    private SlideData slideData;

    public UseCaseCallbackShareClick(SlideData slideData) {
        this.slideData = slideData;
    }

    @Override
    public void invoke() {
        if (CallbackManager.getInstance().getClickOnShareStoryCallback() != null) {
            CallbackManager.getInstance().getClickOnShareStoryCallback().shareClick(slideData);
        }
    }
}
