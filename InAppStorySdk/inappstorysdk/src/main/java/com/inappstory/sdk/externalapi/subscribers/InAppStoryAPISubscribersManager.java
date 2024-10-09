package com.inappstory.sdk.externalapi.subscribers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASStatisticV1;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenData;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenStrategy;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenData;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenStrategy;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.externalapi.StoryAPIData;
import com.inappstory.sdk.externalapi.StoryFavoriteItemAPIData;
import com.inappstory.sdk.externalapi.storylist.IASStoryListRequestData;
import com.inappstory.sdk.game.cache.SuccessUseCaseCallback;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.imageloader.CustomFileLoader;
import com.inappstory.sdk.stories.api.models.Image;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.LoadFavoritesCallback;
import com.inappstory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.cache.StoryDownloadManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.statistic.IASStatisticV2Impl;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InAppStoryAPISubscribersManager {

    private HashMap<String, IAPISubscriber> inAppStoryAPISubscribers = new HashMap<>();
    private final IASCore core;

    public InAppStoryAPISubscribersManager(IASCore core) {
        this.core = core;
    }

    public final List<StoryFavoriteItemAPIData> getStoryFavoriteItemAPIData() {
        return storyFavoriteItemAPIData;
    }

    private final List<StoryFavoriteItemAPIData> storyFavoriteItemAPIData = new ArrayList<>();

    public void addAPISubscriber(IAPISubscriber subscriber) {
        inAppStoryAPISubscribers.put(subscriber.getUniqueId(), subscriber);
    }

    public final Map<String, String> urlLocalPath = new HashMap<>();

    public void clearCache() {
        shownStories.clear();
        storyFavoriteItemAPIData.clear();
        urlLocalPath.clear();
    }


    public HashMap<String, IASStoryListRequestData> requestsData = new HashMap<>();

    public void refreshAllLists() {
        for (IASStoryListRequestData requestData : requestsData.values()) {
            getStoryList(requestData);
        }
    }

    public void openStoryReader(
            final Context context,
            final String uniqueKey,
            final int storyId,
            final AppearanceManager appearanceManager
    ) {
        final IAPISubscriber subscriber = inAppStoryAPISubscribers.get(uniqueKey);
        if (subscriber instanceof IStoryAPIDataHolder) {
            InAppStoryService.useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) throws Exception {
                    List<StoryAPIData> dataList = ((IStoryAPIDataHolder) subscriber).getStoryAPIData();
                    readerIsOpened(context, storyId, uniqueKey, dataList, appearanceManager);
                }
            });
        }
    }

    private void readerIsOpened(
            final Context context,
            final int storyId,
            final String uniqueKey,
            List<StoryAPIData> storyAPIDataList,
            final AppearanceManager appearanceManager
    ) {
        InAppStoryService service = InAppStoryService.getInstance();
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager == null) return;
        final Story currentStory = core.contentLoader().storyDownloadManager().getStoryById(storyId, Story.StoryType.COMMON);
        if (currentStory == null)
            return;
        final IASStoryListRequestData requestData = requestsData.get(uniqueKey);
        if (requestData == null)
            return;
        List<Story> stories = new ArrayList<>();
        for (StoryAPIData apiData : storyAPIDataList) {
            Story story = core.contentLoader().storyDownloadManager().getStoryById(apiData.id, Story.StoryType.COMMON);
            if (story == null) return;
            stories.add(story);
        }
        currentStory.isOpened = true;
        core.storyListCache().saveStoryOpened(currentStory.id, Story.StoryType.COMMON);
        String sessionId = core.sessionManager().getSession().getSessionId();
        if (currentStory.getDeeplink() != null && !currentStory.getDeeplink().isEmpty()) {
            service.getListReaderConnector().changeStory(currentStory.id, uniqueKey, false);

            core.statistic().v2().sendDeeplinkStory(
                    currentStory.id,
                    currentStory.getDeeplink(),
                    requestData.feed
            );
            core.statistic().v1(sessionId, new GetStatisticV1Callback() {
                @Override
                public void get(@NonNull IASStatisticV1 manager) {
                    manager.addDeeplinkClickStatistic(currentStory.id);
                }
            });
            core.callbacksAPI().useCallback(IASCallbackType.CALL_TO_ACTION,
                    new UseIASCallback<CallToActionCallback>() {
                        @Override
                        public void use(@NonNull CallToActionCallback callback) {
                            callback.callToAction(
                                    context,
                                    new SlideData(
                                            new StoryData(
                                                    currentStory,
                                                    requestData.feed,
                                                    SourceType.LIST
                                            ),
                                            0,
                                            null
                                    ),
                                    currentStory.getDeeplink(),
                                    ClickAction.DEEPLINK
                            );
                        }

                        @Override
                        public void onDefault() {
                            InAppStoryManager.useCore(new UseIASCoreCallback() {
                                @Override
                                public void use(@NonNull IASCore core) {
                                    new ConnectionCheck().check(
                                            context,
                                            new ConnectionCheckCallback(core) {
                                                @Override
                                                public void success() {
                                                    try {
                                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                                        i.setData(Uri.parse(currentStory.getDeeplink()));
                                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        context.startActivity(i);
                                                    } catch (Exception ignored) {
                                                        InAppStoryService.createExceptionLog(ignored);
                                                    }
                                                }
                                            }
                                    );
                                }
                            });
                        }
                    }
            );
        } else if (currentStory.getGameInstanceId() != null && !currentStory.getGameInstanceId().isEmpty()) {
            service.getListReaderConnector().changeStory(currentStory.id, uniqueKey, false);
            core.statistic().v1(
                    sessionId,
                    new GetStatisticV1Callback() {
                        @Override
                        public void get(@NonNull IASStatisticV1 manager) {
                            manager.addGameClickStatistic(currentStory.id);
                        }
                    }
            );
            core.screensManager().openScreen(
                    context,
                    new LaunchGameScreenStrategy(core, false)
                            .data(new LaunchGameScreenData(
                                    null,
                                    new GameStoryData(
                                            new SlideData(
                                                    new StoryData(
                                                            currentStory,
                                                            requestData.feed,
                                                            SourceType.LIST
                                                    ),
                                                    0,
                                                    null
                                            )

                                    ),
                                    currentStory.getGameInstanceId()
                            ))
            );
        } else if (!currentStory.isHideInReader()) {
            List<Integer> readerStories = new ArrayList<>();
            int j = 0;
            int correctedIndex = 0;
            for (Story story : stories) {
                if (!story.isHideInReader()) {
                    if (currentStory == story) {
                        correctedIndex = j;
                    }
                    readerStories.add(story.id);
                    j++;
                }
            }
            LaunchStoryScreenData launchData = new LaunchStoryScreenData(
                    uniqueKey,
                    requestData.feed,
                    sessionId,
                    readerStories,
                    correctedIndex,
                    false,
                    ShowStory.ACTION_OPEN,
                    SourceType.LIST,
                    0,
                    Story.StoryType.COMMON,
                    null
            );
            core.screensManager().openScreen(context,
                    new LaunchStoryScreenStrategy(core, false).
                            launchStoryScreenData(launchData).
                            readerAppearanceSettings(
                                    new LaunchStoryScreenAppearance(
                                            AppearanceManager.checkOrCreateAppearanceManager(appearanceManager),
                                            context
                                    )
                            )
            );
        }
    }

    public void getStoryList(final IASStoryListRequestData data) {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull final InAppStoryService service) throws Exception {
                final StoryDownloadManager downloadManager = core.contentLoader().storyDownloadManager();
                downloadManager.loadStories(
                        data.feed,
                        new LoadStoriesCallback() {
                            @Override
                            public void storiesLoaded(List<Integer> storiesIds) {
                                List<Story> stories = new ArrayList<>();
                                for (Integer storyId : storiesIds) {
                                    Story story = downloadManager.getStoryById(storyId, Story.StoryType.COMMON);
                                    if (story == null) return;
                                    stories.add(story);
                                }
                                updateStoryList(data.uniqueId, data.feed, stories);
                                for (Story story : stories) {
                                    updateStory(story, null, null);
                                }
                            }

                            @Override
                            public void setFeedId(String feedId) {

                            }

                            @Override
                            public void onError() {

                            }
                        },
                        data.hasFavorite ? new LoadFavoritesCallback() {
                            @Override
                            public void success(List<FavoriteImage> favoriteImages) {
                                updateFavorites(favoriteImages);
                            }
                        } : null,
                        data.isFavorite,
                        data.hasFavorite
                );
            }
        });
    }

    public void showFavoriteItem(
            final String uniqueId
    ) {
        List<FavoriteImage> actualFavoriteImages = new ArrayList<>(
                core.contentLoader().storyDownloadManager().favoriteImages()
        );
        cacheFavoriteCellImage(
                actualFavoriteImages.iterator(),
                new ArrayList<StoryFavoriteItemAPIData>(),
                uniqueId
        );
    }

    private void cacheFavoriteCellImage(
            final Iterator<FavoriteImage> iterator,
            final List<StoryFavoriteItemAPIData> favoriteItemAPIData,
            final String uniqueId
    ) {
        if (iterator.hasNext()) {
            final FavoriteImage favoriteImage = iterator.next();
            String image = favoriteImage.getUrl();
            if (image != null && !image.isEmpty())
                new CustomFileLoader().getFileLinkFromUrl(image, new SuccessUseCaseCallback<String>() {
                    @Override
                    public void onSuccess(String path) {
                        favoriteItemAPIData.add(new StoryFavoriteItemAPIData(favoriteImage, path));
                        cacheFavoriteCellImage(iterator, favoriteItemAPIData, uniqueId);
                    }

                    @Override
                    public void onError(String message) {
                        favoriteItemAPIData.add(new StoryFavoriteItemAPIData(favoriteImage, null));
                        cacheFavoriteCellImage(iterator, favoriteItemAPIData, uniqueId);
                    }
                });
            else {
                favoriteItemAPIData.add(new StoryFavoriteItemAPIData(favoriteImage, null));
                cacheFavoriteCellImage(iterator, favoriteItemAPIData, uniqueId);
            }
        } else {
            updateStoryFavoriteItemAPIData(favoriteItemAPIData);
        }
    }

    public void updateVisiblePreviews(
            String sessionId,
            List<Integer> storyIds,
            final String uniqueId
    ) {
        final ArrayList<Integer> indexes = new ArrayList<>();
        for (Integer storyId : storyIds) {
            if (shownStories.contains(storyId)) continue;
            shownStories.add(storyId);
            indexes.add(storyId);
            cacheStoryCover(storyId);
        }
        final IASStoryListRequestData iasStoryListRequestData = requestsData.get(uniqueId);
        final List<Integer> newIndexes = core.statistic().newStatisticPreviews(indexes);
        if (iasStoryListRequestData != null) {
            core.statistic().v2().sendViewStory(
                    newIndexes,
                    iasStoryListRequestData.isFavorite ?
                            IASStatisticV2Impl.FAVORITE : IASStatisticV2Impl.LIST,
                    iasStoryListRequestData.feed
            );
        }
        core.statistic().v1(
                sessionId,
                new GetStatisticV1Callback() {
                    @Override
                    public void get(@NonNull IASStatisticV1 manager) {
                        manager.previewStatisticEvent(newIndexes);
                    }
                }
        );

    }


    private StoryFavoriteItemAPIData updateStoryFavoriteItemAPIDataItem(StoryFavoriteItemAPIData favoriteItemAPIData) {
        boolean newItem = true;
        for (StoryFavoriteItemAPIData storyFavoriteItemAPIDataItem : storyFavoriteItemAPIData) {
            if (favoriteItemAPIData.id == storyFavoriteItemAPIDataItem.id) {
                if (
                        (
                                storyFavoriteItemAPIDataItem.imageFilePath == null
                                        && favoriteItemAPIData.imageFilePath != null
                        )
                                ||
                                storyFavoriteItemAPIDataItem.imageFilePath != null
                                        && favoriteItemAPIData.imageFilePath != null &&
                                        !favoriteItemAPIData.imageFilePath.equals(
                                                storyFavoriteItemAPIDataItem.imageFilePath
                                        )
                ) {
                    storyFavoriteItemAPIDataItem.imageFilePath = favoriteItemAPIData.imageFilePath;
                }
                return storyFavoriteItemAPIDataItem;
            }
        }
        storyFavoriteItemAPIData.add(favoriteItemAPIData);
        return favoriteItemAPIData;
    }

    public final void updateStoryFavoriteItemAPIData(List<StoryFavoriteItemAPIData> favorites) {
        List<StoryFavoriteItemAPIData> currentFavorites = new ArrayList<>();
        for (StoryFavoriteItemAPIData favorite : favorites) {
            currentFavorites.add(updateStoryFavoriteItemAPIDataItem(favorite));
        }
        for (IAPISubscriber subscriber : inAppStoryAPISubscribers.values()) {
            if (subscriber instanceof InAppStoryAPIListSubscriber) {
                ((InAppStoryAPIListSubscriber) subscriber).updateFavoriteItemData(currentFavorites);
            }
        }
    }


    private void cacheStoryCover(final Integer storyId) {
        final Story story = core.contentLoader().storyDownloadManager()
                .getStoryById(storyId, Story.StoryType.COMMON);
        if (story != null) {
            Image storyImage = story.getProperImage(AppearanceManager.getCommonInstance().csCoverQuality());
            final String image;
            if (storyImage != null)
                image = storyImage.getUrl();
            else
                image = null;
            String localImage = null;
            String localVideo = null;
            if (image != null && !image.isEmpty()) {
                localImage = urlLocalPath.get(image);
                if (localImage == null) {
                    new CustomFileLoader()
                            .getFileLinkFromUrl(image, new SuccessUseCaseCallback<String>() {
                                        @Override
                                        public void onSuccess(String path) {
                                            urlLocalPath.put(image, path);
                                            updateStory(story, path, null);
                                        }
                                    }
                            );
                }
            }
            final String video = story.getVideoUrl();
            if (video != null && !video.isEmpty()) {
                localVideo = urlLocalPath.get(video);
                if (localVideo == null) {
                    new CustomFileLoader()
                            .getFileLinkFromUrl(video, new SuccessUseCaseCallback<String>() {
                                        @Override
                                        public void onSuccess(String path) {
                                            urlLocalPath.put(video, path);
                                            updateStory(story, null, path);
                                        }
                                    }
                            );
                }
            }
            if (localImage != null && localVideo != null) {
                updateStory(story, localImage, localVideo);
            }
        }
    }

    private final List<Integer> shownStories = new ArrayList<>();

    public void removeAPISubscriber(String uniqueKey) {
        inAppStoryAPISubscribers.remove(uniqueKey);
    }

    public void removeAPISubscriber(IAPISubscriber subscriber) {
        inAppStoryAPISubscribers.remove(subscriber.getUniqueId());
    }

    public void updateStoryList(
            String uniqueId,
            String feed,
            List<Story> stories
    ) {
        List<StoryAPIData> storyAPIData = new ArrayList<>();
        for (Story story : stories) {
            StoryData storyData = new StoryData(story, feed, SourceType.LIST);
            String imagePath = null;
            String videoPath = null;
            Image storyImage = story.getProperImage(AppearanceManager.getCommonInstance().csCoverQuality());
            String imageUrl = null;
            if (storyImage != null)
                imageUrl = storyImage.getUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                imagePath = urlLocalPath.get(imageUrl);
            }
            String videoUrl = story.getVideoUrl();
            if (videoUrl != null && !videoUrl.isEmpty()) {
                videoPath = urlLocalPath.get(videoUrl);
            }

            storyAPIData.add(new StoryAPIData(story, storyData, imagePath, videoPath));
        }
        IAPISubscriber subscriber = inAppStoryAPISubscribers.get(uniqueId);
        if (subscriber instanceof IStoryAPIDataHolder) {
            ((IStoryAPIDataHolder) subscriber).setStoryAPIData(storyAPIData);
            subscriber.updateStoriesData(storyAPIData);
        }
    }

    public void openStory(final int storyId, final String uniqueId) {
        Story story = core.contentLoader().storyDownloadManager().getStoryById(storyId, Story.StoryType.COMMON);
        if (story != null) {
            story.isOpened = true;
            updateStory(story, null, null);
        }
        IAPISubscriber currentSubscriber = inAppStoryAPISubscribers.get(uniqueId);
        if (currentSubscriber instanceof InAppStoryAPIListSubscriber) {
            currentSubscriber.storyIsOpened(storyId);
        }
    }

    public void readerIsClosed() {
        for (IAPISubscriber subscriber : inAppStoryAPISubscribers.values()) {
            subscriber.readerIsClosed();
        }
    }

    public void readerIsOpened() {
        for (IAPISubscriber subscriber : inAppStoryAPISubscribers.values()) {
            subscriber.readerIsOpened();
        }
    }

    private void updateStory(Story story, String image, String video) {
        String imagePath = image;
        String videoPath = video;
        if (imagePath == null) {
            Image storyImage = story.getProperImage(AppearanceManager.getCommonInstance().csCoverQuality());
            String imageUrl = null;
            if (storyImage != null)
                imageUrl = storyImage.getUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                imagePath = urlLocalPath.get(imageUrl);
            }
        }
        if (videoPath == null) {
            String videoUrl = story.getVideoUrl();
            if (videoUrl != null && !videoUrl.isEmpty()) {
                videoPath = urlLocalPath.get(videoUrl);
            }
        }

        Log.e("IAS_SDK_API", "update: " + story.id + " " + image + " " + video);
        for (IAPISubscriber subscriber : inAppStoryAPISubscribers.values()) {
            if (subscriber instanceof IStoryAPIDataHolder) {
                StoryAPIData data = ((IStoryAPIDataHolder) subscriber).updateStoryAPIData(
                        story,
                        imagePath,
                        videoPath
                );
                if (data != null) {
                    Log.e("IAS_SDK_API", "update data: " + data);
                    subscriber.updateStoryData(data);
                }
            }
        }
    }

    public void clearAllFavorites() {
        updateFavorites(new ArrayList<FavoriteImage>());
    }

    public void storyFavorite() {
        List<FavoriteImage> actualFavoriteImages = new ArrayList<>(
                core.contentLoader().storyDownloadManager().favoriteImages()
        );
        updateFavorites(actualFavoriteImages);
    }


    private void updateFavorites(List<FavoriteImage> favoriteImages) {
        List<StoryFavoriteItemAPIData> favoriteItemAPIData = new ArrayList<>();
        for (FavoriteImage favoriteImage : favoriteImages) {
            favoriteItemAPIData.add(new StoryFavoriteItemAPIData(favoriteImage, null));
        }
        updateStoryFavoriteItemAPIData(favoriteItemAPIData);
        for (IAPISubscriber subscriber : inAppStoryAPISubscribers.values()) {
            if (subscriber instanceof InAppStoryAPIFavoriteListSubscriber) {
                List<StoryAPIData> newData = new ArrayList<>();
                List<StoryAPIData> currentData = ((InAppStoryAPIFavoriteListSubscriber) subscriber).getStoryAPIData();
                for (FavoriteImage favoriteImage : favoriteImages) {
                    boolean addNew = true;
                    for (StoryAPIData storyAPIData : currentData) {
                        if (storyAPIData.id == favoriteImage.getId()) {
                            addNew = false;
                            newData.add(storyAPIData);
                            break;
                        }
                    }
                    if (addNew) {
                        Story story = core.contentLoader().storyDownloadManager()
                                .getStoryById(favoriteImage.getId(), Story.StoryType.COMMON);
                        if (story == null) return;
                        String imagePath = null;
                        Image storyImage = story.getProperImage(AppearanceManager.getCommonInstance().csCoverQuality());
                        String imageUrl = null;
                        if (storyImage != null)
                            imageUrl = storyImage.getUrl();
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            imagePath = urlLocalPath.get(imageUrl);
                        }
                        String videoPath = null;
                        String videoUrl = story.getVideoUrl();
                        if (videoUrl != null && !videoUrl.isEmpty()) {
                            videoPath = urlLocalPath.get(videoUrl);
                        }
                        newData.add(
                                0,
                                new StoryAPIData(
                                        story,
                                        new StoryData(story, null, SourceType.FAVORITE),
                                        imagePath,
                                        videoPath
                                )
                        );
                    }

                }
                ((InAppStoryAPIFavoriteListSubscriber) subscriber).setStoryAPIData(newData);
                subscriber.updateStoriesData(newData);
            }
        }
    }
}
