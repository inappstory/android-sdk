package com.inappstory.sdk.stories.cache;


import androidx.annotation.WorkerThread;


import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import com.inappstory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;

import java.util.ArrayList;
import java.util.List;

public class FakeStoryDownloadManager extends StoryDownloadManager {
    public List<Story> getStories(Story.StoryType type) {
        return new ArrayList<>();
    }

    public void clearLocalData() {

    }

    @WorkerThread
    public void uploadingAdditional(List<Story> newStories, Story.StoryType type) {

    }

    public void getFullStoryById(final GetStoryByIdCallback storyByIdCallback,
                                 final int id,
                                 Story.StoryType type) {

    }

    public void getFullStoryByStringId(
            final GetStoryByIdCallback storyByIdCallback,
            final String id,
            final Story.StoryType type,
            final int readerSource
    ) {

    }

    public void changePriority(int storyId, List<Integer> adjacent, Story.StoryType type) {

    }

    public void changePriorityForSingle(int storyId, Story.StoryType type) {

    }

    public void initDownloaders() {

    }

    public void destroy() {

    }

    public void cleanTasks() {

    }

    public void cleanTasks(boolean cleanStories) {

    }

    public void clearCache() {

    }


    public void addSubscriber(ReaderPageManager manager) {

    }

    public void removeSubscriber(ReaderPageManager manager) {

    }

    void slideLoaded(SlideTaskData key) {

    }


    void storyError(StoryTaskData storyTaskData) {

    }

    void slideError(SlideTaskData slideTaskData) {

    }

    void storyLoaded(int storyId, Story.StoryType type) {

    }

    public void addStories(List<Story> storiesToAdd, Story.StoryType type) {

    }

    public List<Story> getStoriesListByType(Story.StoryType type) {
        return new ArrayList<>();
    }

    public void putStories(List<Story> storiesToPut, Story.StoryType type) {

    }

    public int checkIfPageLoaded(int storyId, int index, Story.StoryType type) {
        return 0;
    }


    public FakeStoryDownloadManager() {
        super();
    }

    public void addStoryTask(int storyId, ArrayList<Integer> addIds, Story.StoryType type) {

    }


    public void reloadStory(int storyId, Story.StoryType type) {

    }


    public void clearAllFavoriteStatus(Story.StoryType type) {

    }

    public Story getStoryById(int id, Story.StoryType type) {
        return null;
    }

    public void setStory(final Story story, int id, Story.StoryType type) {

    }



    public void cleanStoriesIndex(Story.StoryType type) {

    }

    public void addCompletedStoryTask(Story story, Story.StoryType type) {

    }


    public void loadUgcStories(final LoadStoriesCallback callback, final String payload) {

    }

    public void loadStories(String feed, final LoadStoriesCallback callback,
                            boolean isFavorite, boolean hasFavorite) {

    }


    public void refreshLocals(Story.StoryType type) {

    }

    void setLocalsOpened(final List<Story> response, final Story.StoryType type) {

    }
}
