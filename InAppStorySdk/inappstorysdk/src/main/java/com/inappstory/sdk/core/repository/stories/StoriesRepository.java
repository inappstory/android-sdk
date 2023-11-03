package com.inappstory.sdk.core.repository.stories;

import android.util.Pair;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.repository.stories.dto.FavoritePreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.IFavoritePreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.core.repository.stories.interfaces.IChangeFavoriteStatusCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IChangeLikeStatusCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IFavoriteCellUpdatedCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IFavoriteListUpdatedCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetFavoritePreviewsCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetFeedCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetStoriesPreviewsCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetStoryCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IRemoveAllStoriesFromFavoritesCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IStoryUpdatedCallback;
import com.inappstory.sdk.core.repository.stories.usecase.ChangeStoryFavoriteStatus;
import com.inappstory.sdk.core.repository.stories.usecase.ChangeStoryLikeStatus;
import com.inappstory.sdk.core.repository.stories.usecase.GetFavoriteStoryList;
import com.inappstory.sdk.core.repository.stories.usecase.GetFavoriteStoryPreviews;
import com.inappstory.sdk.core.repository.stories.usecase.GetOnboardingStoryListByFeed;
import com.inappstory.sdk.core.repository.stories.usecase.GetStoryById;
import com.inappstory.sdk.core.repository.stories.usecase.GetStoryListByFeed;
import com.inappstory.sdk.core.repository.stories.usecase.RemoveAllStoriesFromFavorites;
import com.inappstory.sdk.core.repository.stories.utils.FavoriteCallbackKey;
import com.inappstory.sdk.core.repository.stories.utils.FeedCallbackKey;
import com.inappstory.sdk.core.repository.stories.utils.IFeedCallbackKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoriesRepository implements IStoriesRepository {
    private final Object storyPreviewsLock = new Object();
    Map<Integer, IPreviewStoryDTO> storyPreviews = new HashMap<>();

    private final Object cachedStoriesLock = new Object();
    private final Map<Integer, IStoryDTO> cachedStories = new HashMap<>();

    private final Object cachedListIdsLock = new Object();
    private final Map<String, List<Integer>> cachedListIds = new HashMap<>();

    private final Object storyLastIndexesLock = new Object();
    private final Map<Integer, Integer> storyLastIndexes = new HashMap<>();

    private final Object storyUpdatedCallbacksLock = new Object();
    private final List<IStoryUpdatedCallback> storyUpdatedCallbacks = new ArrayList<>();

    private final Object favoriteCellUpdatedCallbacksLock = new Object();
    private final List<IFavoriteCellUpdatedCallback> favoriteCellUpdatedCallbacks = new ArrayList<>();

    private final Object favoriteListUpdatedCallbacksLock = new Object();
    List<IFavoriteListUpdatedCallback> favoriteListUpdatedCallbacks = new ArrayList<>();

    @Override
    public void addFavoriteCellUpdatedCallback(IFavoriteCellUpdatedCallback callback) {
        synchronized (favoriteCellUpdatedCallbacksLock) {
            favoriteCellUpdatedCallbacks.add(callback);
        }
    }

    @Override
    public void removeFavoriteCellUpdatedCallback(IFavoriteCellUpdatedCallback callback) {
        synchronized (favoriteCellUpdatedCallbacksLock) {
            favoriteCellUpdatedCallbacks.remove(callback);
        }
    }

    @Override
    public void addFavoriteListUpdatedCallback(IFavoriteListUpdatedCallback callback) {
        synchronized (favoriteListUpdatedCallbacksLock) {
            favoriteListUpdatedCallbacks.add(callback);
        }
    }

    @Override
    public void removeFavoriteListUpdatedCallback(IFavoriteListUpdatedCallback callback) {
        synchronized (favoriteListUpdatedCallbacksLock) {
            favoriteListUpdatedCallbacks.remove(callback);
        }
    }

    private final Object storyFavoritesLock = new Object();
    private final List<IFavoritePreviewStoryDTO> storyFavorites = new ArrayList<>();

    private final Object feedCallbacksLock = new Object();
    private final Object storyIdCallbacksLock = new Object();
    private final Map<Integer, List<IGetStoriesPreviewsCallback>> feedCallbacks = new HashMap<>();
    private final Map<String, List<IGetStoryCallback<IStoryDTO>>> storyByIdCallbacks = new HashMap<>();

    private List<IGetStoriesPreviewsCallback> getFeedCallbacksListCopy(
            IFeedCallbackKey feedKey
    ) {
        List<IGetStoriesPreviewsCallback> localList;
        synchronized (feedCallbacksLock) {
            localList = feedCallbacks.get(feedKey.getKey());
            feedCallbacks.remove(feedKey.getKey());
        }
        return localList != null ? localList : new ArrayList<IGetStoriesPreviewsCallback>();
    }

    private boolean addToFeedCallbacksList(
            IFeedCallbackKey feedKey,
            IGetStoriesPreviewsCallback callback
    ) {
        boolean loading = true;
        synchronized (feedCallbacksLock) {
            if (feedCallbacks.get(feedKey.getKey()) == null) {
                feedCallbacks.put(feedKey.getKey(), new ArrayList<IGetStoriesPreviewsCallback>());
                loading = false;
            }
            feedCallbacks.get(feedKey.getKey()).add(callback);
        }
        return loading;
    }

    private void feedCallbacksListSuccess(
            IFeedCallbackKey feedKey,
            List<IPreviewStoryDTO> previews
    ) {
        List<IGetStoriesPreviewsCallback> localList = getFeedCallbacksListCopy(feedKey);
        for (IGetStoriesPreviewsCallback feedCallback : localList) {
            feedCallback.onSuccess(previews);
        }
        for (IPreviewStoryDTO previewStoryDTO : previews) {
            updateItem(previewStoryDTO);
        }
    }

    private void feedCallbacksListError(IFeedCallbackKey feedKey) {
        List<IGetStoriesPreviewsCallback> localList = getFeedCallbacksListCopy(feedKey);
        for (IGetStoriesPreviewsCallback feedCallback : localList) {
            feedCallback.onError();
        }
    }

    private List<IGetStoryCallback<IStoryDTO>> getStoryByIdCallbacksCopy(
            String storyId
    ) {
        List<IGetStoryCallback<IStoryDTO>> localList;
        synchronized (storyIdCallbacksLock) {
            localList = storyByIdCallbacks.get(storyId);
            storyByIdCallbacks.remove(storyId);
        }
        return localList != null ? localList : new ArrayList<IGetStoryCallback<IStoryDTO>>();
    }

    private boolean addToStoryByIdCallbacks(
            String storyId,
            IGetStoryCallback<IStoryDTO> callback
    ) {
        boolean loading = true;
        synchronized (storyIdCallbacksLock) {
            if (storyByIdCallbacks.get(storyId) == null) {
                storyByIdCallbacks.put(storyId, new ArrayList<IGetStoryCallback<IStoryDTO>>());
                loading = false;
            }
            storyByIdCallbacks.get(storyId).add(callback);
        }
        return loading;
    }

    private void storyByIdCallbacksSuccess(String storyId, IStoryDTO storyDTO) {
        List<IGetStoryCallback<IStoryDTO>> localList = getStoryByIdCallbacksCopy(storyId);
        for (IGetStoryCallback<IStoryDTO> feedCallback : localList) {
            feedCallback.onSuccess(storyDTO);
        }

    }

    private void storyByIdCallbacksError(String storyId) {
        List<IGetStoryCallback<IStoryDTO>> localList = getStoryByIdCallbacksCopy(storyId);
        for (IGetStoryCallback<IStoryDTO> feedCallback : localList) {
            feedCallback.onError();
        }
    }

    @Override
    public void getStoryByIdAsync(final int storyId, final IGetStoryCallback<IStoryDTO> callback) {
        getStoryByStringId(Integer.toString(storyId), callback);
    }

    @Override
    public IStoryDTO getStoryById(int storyId) {
        synchronized (cachedStoriesLock) {
            return cachedStories.get(storyId);
        }
    }

    @Override
    public IPreviewStoryDTO getCurrentStory() {
        synchronized (storyPreviewsLock) {
            return currentStory;
        }
    }

    @Override
    public void clear() {
        synchronized (cachedStoriesLock) {
            cachedStories.clear();
        }
        synchronized (storyLastIndexesLock) {
            storyLastIndexes.clear();
        }
        setCurrentStory(null);
    }

    private IPreviewStoryDTO currentStory = null;

    @Override
    public void setCurrentStory(Integer storyId) {
        synchronized (storyPreviewsLock) {
            if (storyId == null) currentStory = null;
            else currentStory = storyPreviews.get(storyId);
        }
    }

    @Override
    public void getStoryByStringId(final String storyId, final IGetStoryCallback<IStoryDTO> callback) {
        if (addToStoryByIdCallbacks(storyId, callback)) return;
        synchronized (cachedStoriesLock) {
            IStoryDTO storyDTO = cachedStories.get(storyId);
            if (storyDTO != null) {
                storyByIdCallbacksSuccess(storyId, storyDTO);
                return;
            }
        }
        new GetStoryById(storyId).get(new IGetStoryCallback<Pair<IStoryDTO, IPreviewStoryDTO>>() {
            @Override
            public void onSuccess(Pair<IStoryDTO, IPreviewStoryDTO> response) {
                synchronized (cachedStoriesLock) {
                    cachedStories.put(response.first.getId(), response.first);
                }
                synchronized (storyPreviewsLock) {
                    storyPreviews.put(response.second.getId(), response.second);
                }
                storyByIdCallbacksSuccess(storyId, response.first);
            }

            @Override
            public void onError() {
                storyByIdCallbacksError(storyId);
            }
        });
    }

    @Override
    public IPreviewStoryDTO getStoryPreviewById(int storyId) {
        synchronized (storyPreviewsLock) {
            return storyPreviews.get(storyId);
        }
    }

    private void setLocalPreviews(String listID, List<IPreviewStoryDTO> previews) {
        synchronized (storyPreviewsLock) {
            for (IPreviewStoryDTO previewStory : previews) {
                storyPreviews.put(previewStory.getId(), previewStory);
            }
        }
        if (listID == null) return;
        List<Integer> cached = new ArrayList<>();
        for (IPreviewStoryDTO previewStory : previews) {
            cached.add(previewStory.getId());
        }
        synchronized (cachedListIdsLock) {
            cachedListIds.put(listID, cached);
        }
    }

    private List<IPreviewStoryDTO> getLocalPreviews(String listID) {
        if (listID == null) return null;
        List<IPreviewStoryDTO> previews = null;
        List<Integer> cached = null;
        synchronized (cachedListIdsLock) {
            cached = cachedListIds.get(listID);
        }
        if (cached != null) {
            previews = new ArrayList<>();
            synchronized (storyPreviewsLock) {
                for (int id : cached) {
                    previews.add(storyPreviews.get(id));
                }

                return previews;
            }
        }
        return null;
    }

    @Override
    public void getStoriesPreviewsByListIdAsync(
            final String listID,
            final String uncheckedFeed,
            final boolean loadFavorites,
            final IGetStoriesPreviewsCallback callback
    ) {
        @NonNull String feed =
                (uncheckedFeed != null && !uncheckedFeed.isEmpty()) ? uncheckedFeed : "default";
        final IFeedCallbackKey feedKey = new FeedCallbackKey(feed);
        if (addToFeedCallbacksList(feedKey, callback)) return;
        List<IPreviewStoryDTO> previews = getLocalPreviews(listID);
        if (previews == null) {
            new GetStoryListByFeed(
                    feed,
                    InAppStoryManager.getInstance().getTagsString()
            ).get(new IGetFeedCallback() {
                @Override
                public void onSuccess(final Pair<List<IPreviewStoryDTO>, Boolean> feedResponse) {
                    setLocalPreviews(listID, feedResponse.first);
                    if (feedResponse.second && loadFavorites) {
                        new GetFavoriteStoryPreviews().get(new IGetFavoritePreviewsCallback() {
                            @Override
                            public void onSuccess(List<IFavoritePreviewStoryDTO> response) {
                                synchronized (storyFavoritesLock) {
                                    storyFavorites.clear();
                                    storyFavorites.addAll(response);
                                }
                                feedCallbacksListSuccess(feedKey, feedResponse.first);
                                updateFavoriteCell();
                            }

                            @Override
                            public void onError() {
                                feedCallbacksListSuccess(feedKey, feedResponse.first);
                            }
                        });
                    } else {
                        feedCallbacksListSuccess(feedKey, feedResponse.first);
                    }
                }

                @Override
                public void onError() {
                    feedCallbacksListError(feedKey);
                }
            });
        } else {
            feedCallbacksListSuccess(feedKey, previews);
        }
    }

    @Override
    public int getStoryLastIndex(int storyId) {
        synchronized (storyLastIndexesLock) {
            Integer index = storyLastIndexes.get(storyId);
            if (index != null) return index;
        }
        return 0;
    }

    @Override
    public void setStoryLastIndex(int storyId, int index) {
        synchronized (storyLastIndexesLock) {
            storyLastIndexes.put(storyId, index);
        }
    }

    @Override
    public void setOpenedStories(List<Integer> ids) {
        synchronized (storyPreviewsLock) {
            for (Integer id : ids) {
                IPreviewStoryDTO previewStoryDTO = storyPreviews.get(id);
                if (previewStoryDTO != null) previewStoryDTO.setOpened(true);
            }
        }

        synchronized (cachedStoriesLock) {
            for (Integer id : ids) {
                IStoryDTO storyDTO = cachedStories.get(id);
                if (storyDTO != null) storyDTO.setOpened(true);
            }
        }
    }

    @Override
    public void getOpenedStories(List<Integer> ids) {

    }

    @Override
    public void getOnboardingStoriesAsync(String uncheckedFeed, Integer limit, IGetStoriesPreviewsCallback callback) {
        @NonNull String feed =
                (uncheckedFeed != null && !uncheckedFeed.isEmpty()) ? uncheckedFeed : "onboarding";
        final IFeedCallbackKey feedKey = new FeedCallbackKey(feed);
        if (addToFeedCallbacksList(feedKey, callback)) return;
        new GetOnboardingStoryListByFeed(
                feed,
                limit,
                InAppStoryManager.getInstance().getTagsString()
        ).get(new IGetFeedCallback() {
            @Override
            public void onSuccess(Pair<List<IPreviewStoryDTO>, Boolean> response) {
                feedCallbacksListSuccess(feedKey, response.first);
            }

            @Override
            public void onError() {
                feedCallbacksListError(feedKey);
            }
        });
    }

    @Override
    public void getFavoriteStoriesByListIdAsync(final String listID, IGetStoriesPreviewsCallback callback) {
        final IFeedCallbackKey feedKey = new FavoriteCallbackKey();
        if (addToFeedCallbacksList(feedKey, callback)) return;
        List<IPreviewStoryDTO> previews = getLocalPreviews(listID);
        if (previews == null) {
            new GetFavoriteStoryList().get(new IGetFeedCallback() {
                @Override
                public void onSuccess(Pair<List<IPreviewStoryDTO>, Boolean> response) {
                    setLocalPreviews(listID, response.first);
                    feedCallbacksListSuccess(feedKey, response.first);
                    List<IPreviewStoryDTO> newFavorites = response.first;
                    synchronized (storyFavoritesLock) {
                        storyFavorites.clear();
                        for (IPreviewStoryDTO previewStoryDTO : newFavorites) {
                            storyFavorites.add(new FavoritePreviewStoryDTO(previewStoryDTO));
                        }
                    }
                    updateFavoriteCell();
                    updateFavoriteList();
                }

                @Override
                public void onError() {
                    feedCallbacksListError(feedKey);
                }
            });
        } else {
            feedCallbacksListSuccess(feedKey, previews);
        }
    }

    @Override
    public List<IPreviewStoryDTO> getCachedFavorites() {
        synchronized (storyFavoritesLock) {
            List<IPreviewStoryDTO> previews = new ArrayList<>();
            for (IFavoritePreviewStoryDTO favoritePreviewStory : storyFavorites) {
                previews.add(storyPreviews.get(favoritePreviewStory.getId()));
            }
            return previews;
        }
    }

    @Override
    public List<IFavoritePreviewStoryDTO> getCachedFavoriteCell() {
        synchronized (storyFavoritesLock) {
            return new ArrayList<>(storyFavorites);
        }
    }


    @Override
    public void clearCachedList(String listID) {
        synchronized (cachedListIdsLock) {
            cachedListIds.remove(listID);
        }
    }

    @Override
    public void clearCachedLists() {
        synchronized (cachedListIdsLock) {
            cachedListIds.clear();
        }
    }

    @Override
    public void openStory(int storyId) {
        IPreviewStoryDTO previewStoryDTO;
        IStoryDTO cachedStoryDTO;
        synchronized (cachedStoriesLock) {
            cachedStoryDTO = cachedStories.get(storyId);
            if (cachedStoryDTO != null) cachedStoryDTO.setOpened(true);
        }
        synchronized (storyPreviewsLock) {
            previewStoryDTO = storyPreviews.get(storyId);
            if (previewStoryDTO == null) return;
            previewStoryDTO.setOpened(true);
        }
        updateItem(previewStoryDTO);
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


    private void updateFavoriteCell() {
        synchronized (favoriteCellUpdatedCallbacksLock) {
            for (IFavoriteCellUpdatedCallback callback : favoriteCellUpdatedCallbacks) {
                callback.onUpdate();
            }
        }
    }

    private void updateItem(IPreviewStoryDTO previewStoryDTO) {
        synchronized (storyUpdatedCallbacksLock) {
            for (IStoryUpdatedCallback callback : storyUpdatedCallbacks) {
                callback.onUpdate(previewStoryDTO);
            }
        }
    }

    private void updateFavoriteList() {
        synchronized (favoriteListUpdatedCallbacksLock) {
            for (IFavoriteListUpdatedCallback callback : favoriteListUpdatedCallbacks) {
                callback.onUpdate();
            }
        }
    }

    private final IChangeLikeStatusCallback changeLikeStatusCallback = new IChangeLikeStatusCallback() {
        @Override
        public void like(int storyId) {
            changeStatus(1, storyId);
        }

        @Override
        public void dislike(int storyId) {

            changeStatus(-1, storyId);
        }

        @Override
        public void clear(int storyId) {
            changeStatus(0, storyId);
        }

        @Override
        public void onError() {
            //TODO update buttons status in reader
        }

        private void changeStatus(int status, int storyId) {
            synchronized (cachedStoriesLock) {
                IStoryDTO cachedStoryDTO = cachedStories.get(storyId);
                if (cachedStoryDTO != null)
                    cachedStoryDTO.setLike(status);
                //TODO update buttons status in reader
            }
        }
    };

    private final IChangeFavoriteStatusCallback changeFavoriteStatusCallback =
            new IChangeFavoriteStatusCallback() {
                @Override
                public void addedToFavorite(int storyId) {
                    synchronized (storyFavoritesLock) {
                        if (!storyFavorites.contains(new FavoritePreviewStoryDTO(storyId))) {
                            synchronized (storyPreviewsLock) {
                                IPreviewStoryDTO previewStoryDTO = storyPreviews.get(storyId);
                                if (previewStoryDTO != null)
                                    storyFavorites.add(new FavoritePreviewStoryDTO(previewStoryDTO));
                            }
                        }
                    }
                    synchronized (cachedStoriesLock) {
                        IStoryDTO cachedStoryDTO = cachedStories.get(storyId);
                        if (cachedStoryDTO != null)
                            cachedStoryDTO.setFavorite(true);
                        //TODO update buttons status in reader
                    }
                    updateFavoriteCell();
                    updateFavoriteList();
                }

                @Override
                public void removedFromFavorite(int storyId) {
                    synchronized (storyFavoritesLock) {
                        IFavoritePreviewStoryDTO favoritePreviewStoryDTO =
                                new FavoritePreviewStoryDTO(storyId);
                        storyFavorites.remove(favoritePreviewStoryDTO);
                    }
                    synchronized (cachedStoriesLock) {
                        IStoryDTO cachedStoryDTO = cachedStories.get(storyId);
                        if (cachedStoryDTO != null)
                            cachedStoryDTO.setFavorite(false);
                        //TODO update buttons status in reader
                    }
                    updateFavoriteCell();
                    updateFavoriteList();
                }

                @Override
                public void onError() {
                    //TODO update buttons status in reader
                }
            };


    @Override
    public void addToFavorite(int storyId) {
        new ChangeStoryFavoriteStatus(storyId, false).changeStatus(
                changeFavoriteStatusCallback
        );

    }

    @Override
    public void removeFromFavorite(int storyId) {
        new ChangeStoryFavoriteStatus(storyId, true).changeStatus(
                changeFavoriteStatusCallback
        );
    }

    @Override
    public void removeAllFavorites() {
        new RemoveAllStoriesFromFavorites().remove(new IRemoveAllStoriesFromFavoritesCallback() {
            @Override
            public void onRemove() {
                synchronized (storyFavoritesLock) {
                    storyFavorites.clear();
                }
                synchronized (cachedStoriesLock) {
                    for (IStoryDTO storyDTO : cachedStories.values()) {
                        storyDTO.setFavorite(false);
                    }
                    //TODO update buttons status in reader
                }
                updateFavoriteCell();
                updateFavoriteList();
            }

            @Override
            public void onError() {
            }
        });

    }

    @Override
    public void addStoryUpdatedCallback(IStoryUpdatedCallback callback) {
        synchronized (storyUpdatedCallbacksLock) {
            storyUpdatedCallbacks.add(callback);
        }
    }

    @Override
    public void removeStoryUpdatedCallback(IStoryUpdatedCallback callback) {
        synchronized (storyUpdatedCallbacksLock) {
            storyUpdatedCallbacks.remove(callback);
        }
    }

    @Override
    public void likeStory(int storyId) {
        new ChangeStoryLikeStatus(storyId, true, false)
                .changeStatus(changeLikeStatusCallback);
    }

    @Override
    public void dislikeStory(int storyId) {
        new ChangeStoryLikeStatus(storyId, false, false)
                .changeStatus(changeLikeStatusCallback);
    }

    @Override
    public void clearLikeDislikeStoryStatus(int storyId) {
        new ChangeStoryLikeStatus(storyId, true, true)
                .changeStatus(changeLikeStatusCallback);
    }
}
