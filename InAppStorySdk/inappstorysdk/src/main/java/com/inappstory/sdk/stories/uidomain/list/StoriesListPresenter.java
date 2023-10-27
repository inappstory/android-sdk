package com.inappstory.sdk.stories.uidomain.list;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCoreManager;
import com.inappstory.sdk.core.repository.stories.dto.PreviewStoryDTO;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryData;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListCallback;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.list.ShownStoriesListItem;
import com.inappstory.sdk.stories.uidomain.list.listnotify.IAllStoriesListsNotify;
import com.inappstory.sdk.stories.uidomain.list.listnotify.IStoriesListNotify;
import com.inappstory.sdk.stories.uidomain.list.utils.CheckIASServiceSuccess;
import com.inappstory.sdk.stories.uidomain.list.utils.CheckIASServiceWithRetry;
import com.inappstory.sdk.stories.uidomain.list.utils.GetStoriesList;
import com.inappstory.sdk.usecase.callbacks.IUseCaseCallbackWithContext;
import com.inappstory.sdk.usecase.callbacks.UseCaseCallbackCallToAction;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.List;

public class StoriesListPresenter implements IStoriesListPresenter {

    private final IStoriesListNotify storiesListNotify;
    private final IAllStoriesListsNotify allStoriesListsNotify;

    private AppearanceManager appearanceManager;

    private final String uniqueListId;
    private final String feed;

    private ListCallback listCallback;


    private final SourceType sourceType;
    private final Story.StoryType storyType;

    public StoriesListPresenter(
            IStoriesListNotify storiesListNotify,
            IAllStoriesListsNotify allStoriesListsNotify,
            String feed,
            SourceType sourceType,
            Story.StoryType storyType,
            String uniqueListId
    ) {
        this.storiesListNotify = storiesListNotify;
        this.allStoriesListsNotify = allStoriesListsNotify;
        this.feed = feed;
        this.uniqueListId = uniqueListId;
        this.sourceType = sourceType;
        this.storyType = storyType;
    }

    @Override
    public void updateAppearanceManager(AppearanceManager appearanceManager) {
        this.appearanceManager = appearanceManager;
    }


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
                .getStoryById(storyId, storyType);
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
    public void setListCallback(ListCallback listCallback) {
        this.listCallback = listCallback;
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
        return IASCoreManager.getInstance().sessionRepository.getUgcEditor() != null;
    }

    private void notifyListCallback(PreviewStoryDTO current, int index) {
        if (listCallback != null) {
            listCallback.itemClick(
                    new StoryData(
                            current.getId(),
                            StringsUtils.getNonNull(current.getStatTitle()),
                            StringsUtils.getNonNull(current.getTags()),
                            current.getSlidesCount(),
                            feed,
                            sourceType
                    ),
                    index
            );
        }
    }

    @Override
    public void gameItemClick(PreviewStoryDTO data, int index, Context context) {
        notifyListCallback(data, index);
        allStoriesListsNotify.openStory(data.getId(), storyType);
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        service.openGameReaderWithGC(
                context,
                new GameStoryData(
                        new SlideData(
                                new StoryData(
                                        data.getId(),
                                        Story.StoryType.COMMON,
                                        StringsUtils.getNonNull(data.getStatTitle()),
                                        StringsUtils.getNonNull(data.getTags()),
                                        data.getSlidesCount(),
                                        feed,
                                        sourceType
                                ),
                                0
                        )

                ),
                data.getGameInstanceId());
    }

    @Override
    public void deeplinkItemClick(PreviewStoryDTO data, int index, Context context) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        notifyListCallback(data, index);
        allStoriesListsNotify.openStory(data.getId(), storyType);
        StatisticManager.getInstance().sendDeeplinkStory(data.getId(), data.getDeeplink(), feed);
        OldStatisticManager.getInstance().addDeeplinkClickStatistic(data.getId());
        IUseCaseCallbackWithContext callbackWithContext = new UseCaseCallbackCallToAction(
                data.getDeeplink(),
                new SlideData(
                        new StoryData(
                                data.getId(),
                                StringsUtils.getNonNull(data.getStatTitle()),
                                StringsUtils.getNonNull(data.getTags()),
                                data.getSlidesCount(),
                                feed,
                                sourceType
                        ),
                        0
                ),
                ClickAction.DEEPLINK
        );
        callbackWithContext.invoke(context);
    }

    @Override
    public void commonItemClick(List<PreviewStoryDTO> data, int index, Context context) {
        if (index == -1) {
            if (CallbackManager.getInstance().getErrorCallback() != null) {
                CallbackManager.getInstance().getErrorCallback().emptyLinkError();
            }
            return;
        }
        notifyListCallback(data.get(index), index);
        ScreensManager.getInstance().openStoriesReader(
                context,
                uniqueListId,
                appearanceManager,
                getIdsFromStoriesAdapterDataList(data),
                index,
                sourceType,
                feed,
                Story.StoryType.COMMON
        );
    }

    private List<Integer> getIdsFromStoriesAdapterDataList(List<PreviewStoryDTO> storyData) {
        List<Integer> ids = new ArrayList<>();
        for (PreviewStoryDTO data : storyData) {
            ids.add(data.getId());
        }
        return ids;
    }

    private List<PreviewStoryDTO> getCachedStoriesPreviews(String cacheId) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return null;
        return service.cachedListStories.get(cacheId);
    }

    public void cacheStoriesPreviewIds(String cacheId, List<PreviewStoryDTO> storyDataList) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        service.cachedListStories.put(cacheId, storyDataList);
    }

    @Override
    public void loadFeed(String feed, final boolean loadFavoriteCovers, final GetStoriesList
            getStoriesList) {
        loadList(feed, false, loadFavoriteCovers, new GetStoriesList() {
            @Override
            public void onSuccess(List<PreviewStoryDTO> stories) {
                getStoriesList.onSuccess(stories);
                InAppStoryService service = InAppStoryService.getInstance();
                if (loadFavoriteCovers && service != null)
                    allStoriesListsNotify.storyFavoriteCellNotify(
                            service.getFavoriteImages(),
                            Story.StoryType.COMMON,
                            true
                    );
            }

            @Override
            public void onError() {
                getStoriesList.onError();
            }
        });
    }

    @Override
    public void loadFavoriteList(final GetStoriesList getStoriesList) {
        loadList(null, true, false, getStoriesList);
    }

    private void loadList(
            final String feed,
            final boolean isFavorite,
            final boolean hasFavorite,
            final GetStoriesList getStoriesList
    ) {
        InAppStoryManager.debugSDKCalls("StoriesList_loadStories", "");
        if (iasManagerAndUserIdExists()) {
            if (!tryToLoadCached(getStoriesList)) {
                final String listUid = ProfilingManager.getInstance().addTask("widget_init");
                new CheckIASServiceWithRetry().check(new CheckIASServiceSuccess() {
                    @Override
                    public void onSuccess(@NonNull InAppStoryService service) {
                        service.getDownloadManager().loadStories(
                                feed,
                                generateLoadStoriesCallback(getStoriesList, listUid),
                                isFavorite,
                                hasFavorite
                        );
                    }
                });
            }
        }
    }

    @Override
    public void sendPreviewsToStatistic(List<Integer> indexes, String feed,
                                        boolean isFavoriteList) {
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
            final GetStoriesList getStoriesList,
            final String listUid
    ) {
        return new LoadStoriesCallback() {
            @Override
            public void storiesLoaded(List<Story> stories) {
                List<PreviewStoryDTO> adapterStoryData = new ArrayList<>();
                for (Story story : stories) {
                    adapterStoryData.add(new PreviewStoryDTO(story));
                }
                if (cacheId != null && !cacheId.isEmpty()) {
                    cacheStoriesPreviewIds(cacheId, adapterStoryData);
                }
                ProfilingManager.getInstance().setReady(listUid);
                getStoriesList.onSuccess(adapterStoryData);
            }

            @Override
            public void setFeedId(String feedId) {

            }

            @Override
            public void onError() {
                getStoriesList.onError();
            }
        };
    }

    private boolean tryToLoadCached(GetStoriesList getStoriesList) {
        if (cacheId == null || cacheId.isEmpty()) return false;
        List<PreviewStoryDTO> stories = getCachedStoriesPreviews(cacheId);
        if (stories == null) {
            return false;
        } else {
            getStoriesList.onSuccess(stories);
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
