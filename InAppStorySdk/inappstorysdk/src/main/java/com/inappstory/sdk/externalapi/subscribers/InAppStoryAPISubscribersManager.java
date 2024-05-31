package com.inappstory.sdk.externalapi.subscribers;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.externalapi.StoryAPIData;
import com.inappstory.sdk.externalapi.storylist.IASStoryListRequestData;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.LoadFavoritesCallback;
import com.inappstory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.StoryDownloadManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.statistic.GetOldStatisticManagerCallback;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.utils.RunnableCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InAppStoryAPISubscribersManager {

    private HashMap<String, IAPISubscriber> inAppStoryAPISubscribers = new HashMap<>();

    public void addAPISubscriber(IAPISubscriber subscriber) {
        inAppStoryAPISubscribers.put(subscriber.getUniqueId(), subscriber);
    }

    public void clearCache() {
        shownStories.clear();
    }

    public HashMap<String, IASStoryListRequestData> requestsData = new HashMap<>();

    public void refreshAllLists() {
        for (IASStoryListRequestData requestData : requestsData.values()) {
            getStoryList(requestData);
        }
    }

    public void getStoryList(final IASStoryListRequestData data) {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull final InAppStoryService service) throws Exception {
                final StoryDownloadManager downloadManager = service.getDownloadManager();
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

        OldStatisticManager.useInstance(
                sessionId,
                new GetOldStatisticManagerCallback() {
                    @Override
                    public void get(@NonNull OldStatisticManager manager) {
                        ArrayList<Integer> newIndexes = manager.newStatisticPreviews(indexes);
                        try {
                            if (StatisticManager.getInstance() != null && iasStoryListRequestData != null) {
                                StatisticManager.getInstance().sendViewStory(
                                        newIndexes,
                                        iasStoryListRequestData.isFavorite ?
                                                StatisticManager.FAVORITE : StatisticManager.LIST,
                                        iasStoryListRequestData.feed
                                );
                            }
                        } catch (Exception e) {

                        }
                        manager.previewStatisticEvent(indexes);
                    }
                }
        );

    }

    private void cacheStoryCover(final Integer storyId) {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull final InAppStoryService service) throws Exception {
                final Story story = service.getDownloadManager().getStoryById(storyId, Story.StoryType.COMMON);
                if (story != null) {
                    String image = story.getProperImage(AppearanceManager.getCommonInstance().csCoverQuality()).getUrl();
                    if (image != null && !image.isEmpty())
                        Downloader.downloadFileAndSendToInterface(image, new RunnableCallback() {
                            @Override
                            public void run(String path) {
                                updateStory(story, path, null);
                            }

                            @Override
                            public void error() {

                            }
                        });
                    String video = story.getVideoUrl();
                    if (video != null && !video.isEmpty()) {
                        Downloader.downloadFileAndSendToInterface(image, new RunnableCallback() {
                            @Override
                            public void run(String path) {
                                updateStory(story, null, path);
                            }

                            @Override
                            public void error() {

                            }
                        });
                    }
                }
            }
        });
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
            storyAPIData.add(new StoryAPIData(story, storyData, null, null));
        }
        IAPISubscriber subscriber = inAppStoryAPISubscribers.get(uniqueId);
        if (subscriber instanceof IStoryAPIDataHolder) {
            ((IStoryAPIDataHolder) subscriber).setStoryAPIData(storyAPIData);
            subscriber.updateStoriesData(storyAPIData);
        }
    }

    public void openStory(Story story, String uniqueId) {

    }

    public void closeReader() {

    }

    public void openReader() {

    }

    private void updateStory(Story story, String imagePath, String videoPath) {
        for (IAPISubscriber subscriber : inAppStoryAPISubscribers.values()) {
            if (subscriber instanceof IStoryAPIDataHolder) {
                StoryAPIData data = ((IStoryAPIDataHolder) subscriber).updateStoryAPIData(
                        story,
                        imagePath,
                        videoPath
                );
                if (data != null) {
                    subscriber.updateStoryData(data);
                }
            }
        }
    }

    public void clearAllFavorites() {

    }


    void updateFavorites(List<FavoriteImage> favoriteImages) {
        for (IAPISubscriber subscriber : inAppStoryAPISubscribers.values()) {
            if (subscriber instanceof InAppStoryAPIListSubscriber) {
                ((InAppStoryAPIListSubscriber) subscriber).updateFavoriteItemData(favoriteImages);
            }
        }
    }
}
