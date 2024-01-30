package com.inappstory.sdk.stories.stackfeed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.stories.api.models.Image;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
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
        Image imageObject = currentStory.getProperImage(appearanceManager.csCoverQuality());
        final String image;
        if (imageObject != null) image = imageObject.getUrl();
        else image = null;
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
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        Story currentStory = stories.get(oldIndex);
        Story current = service.getDownloadManager().getStoryById(currentStory.id, Story.StoryType.COMMON);
        if (current != null) {
            current.isOpened = true;
            current.saveStoryOpened(Story.StoryType.COMMON);
        }
        if (currentStory.getDeeplink() != null && !currentStory.getDeeplink().isEmpty()) {
            StatisticManager.getInstance().sendDeeplinkStory(
                    currentStory.id,
                    currentStory.getDeeplink(),
                    feed
            );
            OldStatisticManager.getInstance().addDeeplinkClickStatistic(currentStory.id);
            if (CallbackManager.getInstance().getCallToActionCallback() != null) {
                CallbackManager.getInstance().getCallToActionCallback().callToAction(
                        context,
                        new SlideData(
                                new StoryData(
                                        currentStory,
                                        feed,
                                        SourceType.STACK
                                ),
                                0,
                                null
                        ),
                        currentStory.getDeeplink(),
                        ClickAction.DEEPLINK
                );
            } else if (CallbackManager.getInstance().getUrlClickCallback() != null) {
                CallbackManager.getInstance().getUrlClickCallback().onUrlClick(
                        currentStory.getDeeplink()
                );
            } else {
                if (!InAppStoryService.isServiceConnected()) {
                    if (CallbackManager.getInstance().getErrorCallback() != null) {
                        CallbackManager.getInstance().getErrorCallback().noConnection();
                    }
                    return;
                }
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(currentStory.getDeeplink()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                } catch (Exception ignored) {
                    InAppStoryService.createExceptionLog(ignored);
                }
            }
        } else if (currentStory.getGameInstanceId() != null && !currentStory.getGameInstanceId().isEmpty()) {
            service.openGameReaderWithGC(
                    context,
                    new GameStoryData(
                            new SlideData(
                                    new StoryData(
                                            currentStory,
                                            feed,
                                            SourceType.STACK
                                    ),
                                    0,
                                    null
                            )

                    ),
                    currentStory.getGameInstanceId(),
                    null
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
