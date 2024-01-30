package com.inappstory.sdk.stories.stackfeed;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.utils.RunnableCallback;

import java.util.ArrayList;
import java.util.List;

public class StackStoryObserver implements IStackFeedActions {
    public StackStoryObserver(
            List<Story> stories,
            AppearanceManager appearanceManager,
            String feed,
            String listId,
            StackStoryUpdatedCallback stackStoryUpdated
    ) {
        this.stories = stories;
        this.appearanceManager = appearanceManager;
        this.feed = feed;
        this.listId = listId;
        this.stackStoryUpdated = stackStoryUpdated;
    }

    private final String listId;
    private final AppearanceManager appearanceManager;

    public String listId() {
        return listId;
    }

    private final String feed;
    private final List<Story> stories;
    private final StackStoryUpdatedCallback stackStoryUpdated;
    private IStackStoryData currentStackStoryData;

    int oldIndex = -1;

    private void loadCovers(
            final StackStoryData localStackStoryData,
            String image,
            String video,
            final StackStoryUpdatedCallback stackStoryUpdated
    ) {
        if (image == null && video == null) {
            currentStackStoryData = localStackStoryData;
            stackStoryUpdated.onUpdate(localStackStoryData);
        } else {
            final StackStoryCoverCompleteCallback coverCompleteCallback =
                    new StackStoryCoverCompleteCallback() {
                        @Override
                        public void onComplete() {
                            currentStackStoryData = localStackStoryData;
                            stackStoryUpdated.onUpdate(localStackStoryData);
                        }
                    };
            if (image != null) {
                Downloader.downloadFileAndSendToInterface(image,
                        new RunnableCallback() {
                            @Override
                            public void run(String imagePath) {
                                localStackStoryData.updateStoryDataCover(
                                        imagePath,
                                        StackStoryCoverLoadType.IMAGE,
                                        coverCompleteCallback
                                );
                            }

                            @Override
                            public void error() {
                                localStackStoryData.updateStoryDataCover(
                                        null,
                                        StackStoryCoverLoadType.IMAGE,
                                        coverCompleteCallback
                                );
                            }
                        });
            }
            if (video != null) {
                Downloader.downloadFileAndSendToInterface(video, new RunnableCallback() {
                    @Override
                    public void run(String videoPath) {
                        localStackStoryData.updateStoryDataCover(
                                videoPath,
                                StackStoryCoverLoadType.VIDEO,
                                coverCompleteCallback
                        );
                    }

                    @Override
                    public void error() {
                        localStackStoryData.updateStoryDataCover(
                                null,
                                StackStoryCoverLoadType.VIDEO,
                                coverCompleteCallback
                        );
                    }
                });
            }
        }
    }

    private void generateNewStackStoryData(
            int newIndex,
            StackStoryUpdatedCallback stackStoryUpdated
    ) {
        final Story currentStory = stories.get(newIndex);
        String image = currentStory.getProperImage(appearanceManager.csCoverQuality()).getUrl();
        final String video = currentStory.getVideoUrl();
        boolean[] statuses = new boolean[stories.size()];
        StoryData[] storiesData = new StoryData[stories.size()];
        for (int i = 0; i < stories.size(); i++) {
            statuses[i] = stories.get(i).isOpened();
            storiesData[i] = new StoryData(stories.get(i), feed, SourceType.STACK);
        }
        final int backgroundColor = Color.parseColor(
                currentStory.getBackgroundColor()
        );
        StackStoryData localStackStoryData = new StackStoryData(
                currentStory.getTitle(),
                Color.parseColor(currentStory.getTitleColor()),
                currentStory.hasAudio(),
                backgroundColor,
                image != null,
                video != null,
                statuses,
                storiesData,
                newIndex
        );
        loadCovers(localStackStoryData, image, video, stackStoryUpdated);
    }

    public void onLoad(StackStoryUpdatedCallback stackStoryUpdated) {
        checkLastIndex(stackStoryUpdated);
    }

    private void checkLastIndex(StackStoryUpdatedCallback stackStoryUpdated) {
        int newIndex = stories.size() - 1;
        for (int i = 0; i < stories.size(); i++) {
            if (!stories.get(i).isOpened) {
                newIndex = i;
                break;
            }
        }
        if (stackStoryUpdated == null) return;
        if (newIndex != oldIndex) {
            oldIndex = newIndex;
            generateNewStackStoryData(newIndex, stackStoryUpdated);
        }
    }

    public void onUpdate(int storyId) {
        int openedIndex = -1;
        for (int i = 0; i < stories.size(); i++) {
            if (storyId == stories.get(i).id) {
                openedIndex = i;
                stories.get(i).isOpened = true;
                break;
            }
        }
        if (openedIndex < 0) return;
        checkLastIndex(stackStoryUpdated);
    }

    public void subscribe() {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                service.subscribeStackStoryObserver(listId, StackStoryObserver.this);
            }
        });
    }

    @Override
    public void unsubscribe() {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                unsubscribe(service);
            }
        });
    }

    public void unsubscribe(@NonNull InAppStoryService service) {
        service.unsubscribeStackStoryObserver(StackStoryObserver.this);
    }

    @Override
    public void openReader(Context context) {
        Story currentStory = stories.get(oldIndex);
        if (currentStory.getDeeplink() != null && !currentStory.getDeeplink().isEmpty()) {
        } else if (currentStory.getGameInstanceId() != null && !currentStory.getGameInstanceId().isEmpty()) {
        } else if (currentStory.isHideInReader()) {
        } else {
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
            StoriesReaderLaunchData launchData = new StoriesReaderLaunchData(
                    listId,
                    feed,
                    readerStories,
                    correctedIndex,
                    ShowStory.ACTION_OPEN,
                    SourceType.STACK,
                    0,
                    Story.StoryType.COMMON,
                    null
            );
            ScreensManager.getInstance().openStoriesReader(
                    context,
                    appearanceManager,
                    launchData
            );
            return;
        }
        onUpdate(currentStory.id);
    }
}
