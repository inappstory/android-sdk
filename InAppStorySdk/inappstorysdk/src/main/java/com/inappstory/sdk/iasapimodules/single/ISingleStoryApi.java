package com.inappstory.sdk.iasapimodules.single;


import android.content.Context;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryOnceCallback;

public interface ISingleStoryApi {
    void showStory(
            String storyId,
            Context context,
            AppearanceManager manager
    );

    void showStory(
            String storyId,
            Context context,
            AppearanceManager manager,
            IShowStoryCallback callback
    );

    void showStory(
            String storyId,
            Context context,
            AppearanceManager manager,
            IShowStoryCallback callback,
            Integer slide
    );

    void showStoryOnce(
            String storyId,
            Context context,
            AppearanceManager manager,
            IShowStoryOnceCallback callback
    );
}
