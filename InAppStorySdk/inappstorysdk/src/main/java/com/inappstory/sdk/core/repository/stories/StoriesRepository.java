package com.inappstory.sdk.core.repository.stories;

import android.util.Pair;

import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.PreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.StoryDTO;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetFeedCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetStoriesPreviewsCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetStoryCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IStoryUpdatedCallback;
import com.inappstory.sdk.core.repository.stories.usecase.GetStoryById;
import com.inappstory.sdk.core.repository.stories.usecase.GetStoryListByFeed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoriesRepository implements IStoriesRepository {
    private final Object storyPreviewsLock = new Object();
    HashMap<Integer, IPreviewStoryDTO> storyPreviews = new HashMap<>();

    private final Object cachedStoriesLock = new Object();
    HashMap<Integer, IStoryDTO> cachedStories = new HashMap<>();

    private final Object cachedListIdsLock = new Object();
    HashMap<String, List<Integer>> cachedListIds = new HashMap<>();

    private final Object storyUpdatedCallbacksLock = new Object();
    List<IStoryUpdatedCallback> storyUpdatedCallbacks = new ArrayList<>();

    @Override
    public void getStoryById(final int storyId, final IGetStoryCallback<IStoryDTO> callback) {
        synchronized (cachedStoriesLock) {
            IStoryDTO storyDTO = cachedStories.get(storyId);
            if (storyDTO != null) {
                callback.onSuccess(storyDTO);
                return;
            }
        }
        new GetStoryById(storyId).get(new IGetStoryCallback<StoryDTO>() {
            @Override
            public void onSuccess(StoryDTO response) {
                synchronized (cachedStoriesLock) {
                    cachedStories.put(response.getId(), response);
                }
                callback.onSuccess(response);
            }

            @Override
            public void onError() {
                callback.onError();
            }
        });
    }

    @Override
    public void getStoryPreviewById(int storyId, IGetStoryCallback<IPreviewStoryDTO> callback) {
        synchronized (storyPreviewsLock) {
            IPreviewStoryDTO storyDTO = storyPreviews.get(storyId);
            if (storyDTO != null) {
                callback.onSuccess(storyDTO);
                return;
            }
        }
        callback.onError();
    }


    @Override
    public void getStoriesPreviewsByListId(
            final String listID,
            final String feed,
            final IGetStoriesPreviewsCallback callback
    ) {
        if (listID != null) {
            List<Integer> cached = null;
            synchronized (cachedListIdsLock) {
                cached = cachedListIds.get(listID);
            }
            if (cached != null) {
                List<IPreviewStoryDTO> previews = new ArrayList<>();
                synchronized (storyPreviewsLock) {
                    for (int id : cached) {
                        previews.add(storyPreviews.get(id));
                    }
                    callback.onSuccess(previews);
                    return;
                }
            }
        }
        new GetStoryListByFeed(feed).get(new IGetFeedCallback() {
            @Override
            public void onSuccess(Pair<List<IPreviewStoryDTO>, Boolean> response) {

            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void getStoriesPreviewsFavoriteList(String listID) {
        synchronized (cachedListIdsLock) {
            cachedListIds.remove(listID);
        }
    }

    @Override
    public void getStoriesPreviewsFavoriteItem() {

    }

    @Override
    public void removeCachedList(String listID) {
        synchronized (cachedListIdsLock) {
            cachedListIds.remove(listID);
        }
    }

    @Override
    public void removeCachedLists() {

    }


    @Override
    public void getOnboardingStories(String userId) {

    }

    @Override
    public void openStory(int storyId) {
        int index = storyPreviews.indexOf(new PreviewStoryDTO(storyId));
        if (index >= 0) {
            storyPreviews.get(index).open();
            synchronized (storyUpdatedCallbacksLock) {
                for (IStoryUpdatedCallback callback : storyUpdatedCallbacks) {
                    callback.update();
                }
            }
        }
    }


    @Override
    public void addStoryUpdateCallback(IStoryUpdatedCallback callback) {
        synchronized (storyUpdatedCallbacksLock) {
            storyUpdatedCallbacks.add(callback);
        }
    }

    @Override
    public void removeStoryUpdateCallback(IStoryUpdatedCallback callback) {
        synchronized (storyUpdatedCallbacksLock) {
            storyUpdatedCallbacks.remove(callback);
        }
    }
}
