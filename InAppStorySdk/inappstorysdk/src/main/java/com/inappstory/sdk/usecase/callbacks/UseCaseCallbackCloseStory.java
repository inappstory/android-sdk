package com.inappstory.sdk.usecase.callbacks;

import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.CloseReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;

public class UseCaseCallbackCloseStory implements IUseCaseCallback {
    private SlideData slideData;
    private CloseReader action;

    public UseCaseCallbackCloseStory(SlideData slideData,
                                     CloseReader action) {
        this.slideData = slideData;
        this.action = action;
    }

    @Override
    public void invoke() {
        if (CallbackManager.getInstance().getCloseStoryCallback() != null) {
            CallbackManager.getInstance().getCloseStoryCallback().closeStory(slideData, action);
        }
    }
}
