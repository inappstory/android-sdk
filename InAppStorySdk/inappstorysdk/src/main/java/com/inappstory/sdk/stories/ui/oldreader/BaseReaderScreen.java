package com.inappstory.sdk.stories.ui.oldreader;

import com.inappstory.sdk.stories.outercallbacks.common.objects.CloseReader;

public interface BaseReaderScreen {
    void closeStoryReader(CloseReader action, String cause);
    void forceFinish();
    void observeGameReader(String observableUID);
    void shareComplete(boolean shared);
    void removeStoryFromFavorite(int id);
    void removeAllStoriesFromFavorite();
    void storyIsOpened(int currentStoryId);
}
