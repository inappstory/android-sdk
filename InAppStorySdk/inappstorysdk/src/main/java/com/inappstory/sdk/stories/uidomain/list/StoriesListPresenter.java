package com.inappstory.sdk.stories.uidomain.list;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.list.ShownStoriesListItem;
import com.inappstory.sdk.stories.uidomain.list.utils.CheckIASServiceSuccess;
import com.inappstory.sdk.stories.uidomain.list.utils.CheckIASServiceWithRetry;
import com.inappstory.sdk.stories.uidomain.list.utils.GetStoriesListIds;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.List;

public class StoriesListPresenter implements IStoriesListPresenter {
    @Override
    public ShownStoriesListItem getShownStoriesListItemByStoryId(
            int storyId,
            int listIndex,
            float currentPercentage,
            String feed,
            SourceType sourceType
    ) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return null;
        Story currentStory = service.getDownloadManager()
                .getStoryById(storyId, Story.StoryType.COMMON);
        if (currentStory != null && currentPercentage > 0) {
            return new ShownStoriesListItem(
                    new StoryData(
                            currentStory.id,
                            StringsUtils.getNonNull(currentStory.statTitle),
                            StringsUtils.getNonNull(currentStory.tags),
                            currentStory.getSlidesCount(),
                            feed,
                            sourceType
                    ),
                    listIndex,
                    currentPercentage
            );
        }
        return null;
    }

    @Override
    public void setCacheId(String cacheId) {
        this.cacheId = cacheId;
    }

    @Override
    public void clearCachedList() {
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager != null && cacheId != null)
            manager.clearCachedList(cacheId);
    }

    @Override
    public void onWindowFocusChanged() {
        OldStatisticManager.getInstance().sendStatistic();
    }

    @Override
    public boolean hasUgcEditor() {
        return Session.hasUgcEditor();
    }

    private List<Integer> getCachedStoriesPreviewIds(String cacheId) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return null;
        return service.listStoriesIds.get(cacheId);
    }

    public void cacheStoriesPreviewIds(String cacheId, List<Integer> ids) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        service.listStoriesIds.put(cacheId, ids);
    }

    @Override
    public void loadFeed(String feed, boolean loadFavoriteCovers, GetStoriesListIds getStoriesListIds) {
        loadList(feed, false, loadFavoriteCovers, getStoriesListIds);
    }

    @Override
    public void loadFavoriteList(final GetStoriesListIds getStoriesListIds) {
        loadList(null, true, false, getStoriesListIds);
    }

    private void loadList(
            final String feed,
            final boolean isFavorite,
            final boolean hasFavorite,
            final GetStoriesListIds getStoriesListIds
    ) {
        InAppStoryManager.debugSDKCalls("StoriesList_loadStories", "");
        if (iasManagerAndUserIdExists()) {
            if (!tryToLoadCached(getStoriesListIds)) {
                final String listUid = ProfilingManager.getInstance().addTask("widget_init");
                new CheckIASServiceWithRetry().check(new CheckIASServiceSuccess() {
                    @Override
                    public void onSuccess(@NonNull InAppStoryService service) {
                        service.getDownloadManager().loadStories(
                                feed,
                                generateLoadStoriesCallback(getStoriesListIds, listUid),
                                isFavorite,
                                hasFavorite
                        );
                    }
                });
            }
        }
    }

    @Override
    public void sendPreviewsToStatistic(List<Integer> indexes, String feed, boolean isFavoriteList) {
        List<Integer> newIndexes =
                OldStatisticManager.getInstance().newStatisticPreviews(indexes);
        try {
            if (StatisticManager.getInstance() != null) {
                StatisticManager.getInstance().sendViewStory(newIndexes,
                        isFavoriteList ? StatisticManager.FAVORITE : StatisticManager.LIST, feed);
            }
        } catch (Exception e) {

        }
        OldStatisticManager.getInstance().previewStatisticEvent(indexes);
    }

    private LoadStoriesCallback generateLoadStoriesCallback(
            final GetStoriesListIds getStoriesListIds,
            final String listUid
    ) {
        return new LoadStoriesCallback() {
            @Override
            public void storiesLoaded(List<Integer> storiesIds) {
                if (cacheId != null && !cacheId.isEmpty()) {
                    cacheStoriesPreviewIds(cacheId, storiesIds);
                }
                ProfilingManager.getInstance().setReady(listUid);
                getStoriesListIds.onSuccess(storiesIds);
            }

            @Override
            public void setFeedId(String feedId) {

            }

            @Override
            public void onError() {
                getStoriesListIds.onError();
            }
        };
    }

    private boolean tryToLoadCached(GetStoriesListIds getStoriesListIds) {
        if (cacheId == null || cacheId.isEmpty()) return false;
        List<Integer> storiesIds = getCachedStoriesPreviewIds(cacheId);
        if (storiesIds == null) {
            return false;
        } else {
            getStoriesListIds.onSuccess(storiesIds);
        }
        return true;
    }

    private String cacheId;

    private boolean iasManagerAndUserIdExists() {
        if (InAppStoryManager.getInstance() == null) {
            InAppStoryManager.showELog(InAppStoryManager.IAS_ERROR_TAG, "'InAppStoryManager' cannot be null");
            return false;
        }
        if (InAppStoryManager.getInstance().getUserId() == null) {
            InAppStoryManager.showELog(InAppStoryManager.IAS_ERROR_TAG, "Parameter 'userId' cannot be null");
            return false;
        }
        return true;
    }
}
