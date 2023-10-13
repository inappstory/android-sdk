package com.inappstory.sdk.usecase.callbacks;

import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.CloseReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.ShowStoryAction;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryData;

public class UseCaseCallbackShowStory implements IUseCaseCallback {
    private StoryData storyData;
    private ShowStoryAction action;

    public UseCaseCallbackShowStory(StoryData storyData,
                                    ShowStoryAction action) {
        this.storyData = storyData;
        this.action = action;
    }

    @Override
    public void invoke() {
        if (CallbackManager.getInstance().getShowStoryCallback() != null) {
            CallbackManager.getInstance().getShowStoryCallback().showStory(storyData, action);
        }
    }
}
