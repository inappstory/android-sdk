package com.inappstory.sdk.stories.ui.reader;

public interface BaseReaderScreen {
    void closeStoryReader(int action);
    void forceFinish();
    void observeGameReader(String observableUID);
    void shareComplete();
    void removeStoryFromFavorite(int id);
    void removeAllStoriesFromFavorite();
}
