package com.inappstory.sdk.stories.uidomain.list;

import android.content.Context;


import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.repository.statistic.IStatisticV1Repository;
import com.inappstory.sdk.core.repository.stories.IStoriesRepository;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetStoriesPreviewsCallback;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.core.models.api.Story.StoryType;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryData;
import com.inappstory.sdk.stories.outercallbacks.screen.IOpenGameReader;
import com.inappstory.sdk.stories.outercallbacks.screen.IOpenStoriesReader;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListCallback;
import com.inappstory.sdk.core.repository.statistic.ProfilingManager;
import com.inappstory.sdk.core.repository.statistic.StatisticV2Manager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.list.ShownStoriesListItem;
import com.inappstory.sdk.stories.uidomain.list.listnotify.IAllStoriesListsNotify;
import com.inappstory.sdk.stories.uidomain.list.listnotify.IStoriesListNotify;
import com.inappstory.sdk.stories.uidomain.list.utils.GetStoriesList;
import com.inappstory.sdk.usecase.callbacks.IUseCaseCallbackWithContext;
import com.inappstory.sdk.usecase.callbacks.UseCaseCallbackCallToAction;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.List;

public final class StoriesListPresenter implements IStoriesListPresenter {

    private final IStoriesListNotify storiesListNotify;
    private final IAllStoriesListsNotify allStoriesListsNotify;

    private AppearanceManager appearanceManager;

    private final String uniqueListId;
    private final String feed;

    private ListCallback listCallback;


    private final SourceType sourceType;
    private final StoryType storyType;

    public StoriesListPresenter(
            IStoriesListNotify storiesListNotify,
            IAllStoriesListsNotify allStoriesListsNotify,
            String feed,
            SourceType sourceType,
            StoryType storyType,
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
        IPreviewStoryDTO currentStory =
                IASCore.getInstance().getStoriesRepository(storyType).getStoryPreviewById(storyId);
        if (currentStory != null && currentPercentage > 0) {
            return new ShownStoriesListItem(
                    new StoryData(
                            currentStory.getId(),
                            StringsUtils.getNonNull(currentStory.getStatTitle()),
                            StringsUtils.getNonNull(currentStory.getTags()),
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
        IASCore.getInstance().statisticV1Repository.forceSend();
    }

    @Override
    public boolean hasUgcEditor() {
        return IASCore.getInstance().sessionRepository.getUgcEditor() != null;
    }

    private void notifyListCallback(IPreviewStoryDTO current, int index) {
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
    public void gameItemClick(
            IPreviewStoryDTO data,
            int index,
            Context context,
            IOpenGameReader callback
    ) {
        notifyListCallback(data, index);
        IASCore.getInstance().getStoriesRepository(storyType).openStory(data.getId());
        IASCore.getInstance().gameRepository.openGameReaderWithGC(
                context,
                new GameStoryData(
                        new SlideData(
                                new StoryData(
                                        data.getId(),
                                        StoryType.COMMON,
                                        StringsUtils.getNonNull(data.getStatTitle()),
                                        StringsUtils.getNonNull(data.getTags()),
                                        data.getSlidesCount(),
                                        feed,
                                        sourceType
                                ),
                                0
                        )
                ),
                data.getGameInstanceId()
        );
    }

    @Override
    public void deeplinkItemClick(IPreviewStoryDTO data, int index, Context context) {
        notifyListCallback(data, index);
        IASCore.getInstance().getStoriesRepository(storyType).openStory(data.getId());
        StatisticV2Manager.getInstance().sendDeeplinkStory(data.getId(), data.getDeeplink(), feed);
        IASCore.getInstance().statisticV1Repository.onDeeplinkClick(data.getId());
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
    public void commonItemClick(
            List<IPreviewStoryDTO> data,
            int index,
            Context context,
            IOpenStoriesReader callback
    ) {
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
                StoryType.COMMON
        );
    }

    private List<Integer> getIdsFromStoriesAdapterDataList(List<IPreviewStoryDTO> storyData) {
        List<Integer> ids = new ArrayList<>();
        for (IPreviewStoryDTO data : storyData) {
            ids.add(data.getId());
        }
        return ids;
    }

    private String getCacheId() {
        if (cacheId == null || cacheId.isEmpty()) return null;
        return cacheId;
    }

    @Override
    public void loadFeed(
            String feed,
            final boolean loadFavoriteCovers,
            final GetStoriesList getStoriesList
    ) {
        if (!iasManagerAndUserIdExists()) return;
        final IStoriesRepository storiesRepository =
                IASCore.getInstance().getStoriesRepository(storyType);
        final String listUid = ProfilingManager.getInstance().addTask("widget_init");
        storiesRepository.getStoriesPreviewsByListIdAsync(
                getCacheId(),
                feed,
                loadFavoriteCovers,
                new IGetStoriesPreviewsCallback() {
                    @Override
                    public void onSuccess(List<IPreviewStoryDTO> response) {
                        getStoriesList.onSuccess(response);
                        ProfilingManager.getInstance().setReady(listUid);
                    }

                    @Override
                    public void onError() {
                        getStoriesList.onError();
                        ProfilingManager.getInstance().setReady(listUid);
                    }
                }
        );
    }

    @Override
    public void loadFavoriteList(final GetStoriesList getStoriesList) {
        if (!iasManagerAndUserIdExists()) return;
        final IStoriesRepository storiesRepository =
                IASCore.getInstance().getStoriesRepository(storyType);
        final String listUid = ProfilingManager.getInstance().addTask("widget_init");
        storiesRepository.getFavoriteStoriesByListIdAsync(
                getCacheId(),
                new IGetStoriesPreviewsCallback() {
                    @Override
                    public void onSuccess(List<IPreviewStoryDTO> response) {
                        getStoriesList.onSuccess(response);
                        ProfilingManager.getInstance().setReady(listUid);
                    }

                    @Override
                    public void onError() {
                        getStoriesList.onError();
                        ProfilingManager.getInstance().setReady(listUid);
                    }
                }
        );
    }

    @Override
    public void sendPreviewsToStatistic(List<Integer> ids, String feed,
                                        boolean isFavoriteList) {
        IStatisticV1Repository statisticV1Repository = IASCore.getInstance().statisticV1Repository;
        List<Integer> newIds = statisticV1Repository.getNonViewedStoryIds(ids);
        try {
            if (StatisticV2Manager.getInstance() != null) {
                StatisticV2Manager.getInstance().sendViewStory(newIds,
                        isFavoriteList ? StatisticV2Manager.FAVORITE : StatisticV2Manager.LIST, feed);
            }
        } catch (Exception e) {

        }
        statisticV1Repository.setViewedStoryIds(newIds);
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