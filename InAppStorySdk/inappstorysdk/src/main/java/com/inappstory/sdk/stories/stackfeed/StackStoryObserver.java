package com.inappstory.sdk.stories.stackfeed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenData;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenStrategy;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenData;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenStrategy;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.stories.api.models.Image;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.GetOldStatisticManagerCallback;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.utils.RunnableCallback;

import java.util.ArrayList;
import java.util.List;

public class StackStoryObserver implements IStackFeedActions {
    private final IASCore core;

    public StackStoryObserver(
            IASCore core,
            List<Story> stories,
            String sessionId,
            AppearanceManager appearanceManager,
            String feed,
            String listId,
            StackStoryUpdatedCallback stackStoryUpdated
    ) {
        this.core = core;
        if (stories == null)
            this.stories = new ArrayList<>();
        else
            this.stories = new ArrayList<>(stories);
        this.appearanceManager = appearanceManager;
        this.sessionId = sessionId;
        this.feed = feed;
        this.listId = listId;
        this.stackStoryUpdated = stackStoryUpdated;
    }

    private final String listId;
    String sessionId;
    private final AppearanceManager appearanceManager;

    public String listId() {
        return listId;
    }

    private final String feed;
    private final List<Story> stories;
    private final StackStoryUpdatedCallback stackStoryUpdated;
    private IStackStoryData currentStackStoryData;

    public String feedCover;

    int oldIndex = -1;

    private void loadCovers(
            final StackStoryData localStackStoryData,
            String image,
            String video,
            final StackStoryUpdatedCallback stackStoryUpdated
    ) {
        final StackStoryCoverCompleteCallback coverCompleteCallback =
                new StackStoryCoverCompleteCallback() {
                    @Override
                    public void onComplete() {
                        localStackStoryData.cover.feedCoverPath(feedCover);
                        currentStackStoryData = localStackStoryData;
                        stackStoryUpdated.onUpdate(localStackStoryData);
                    }
                };
        if (image == null && video == null) {
            coverCompleteCallback.onComplete();
        } else {
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
        if (stories.size() <= newIndex) {
            stackStoryUpdated.onUpdate(null);
        }
        final Story currentStory = stories.get(newIndex);
        Log.e("NewStackStoryData", newIndex + " " + currentStory.isOpened());
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
        localStackStoryData.cover = new StackStoryCover(backgroundColor, image, video);
        localStackStoryData.cover.feedCoverPath(feedCover);
        currentStackStoryData = localStackStoryData;
        stackStoryUpdated.onUpdate(localStackStoryData);
        //loadCovers(localStackStoryData, image, video, stackStoryUpdated);
    }

    public void onLoad(StackStoryUpdatedCallback stackStoryUpdated) {
        int newIndex = 0;
        checkLastIndex(newIndex, stackStoryUpdated);
    }

    private void checkLastIndex(int newIndex, StackStoryUpdatedCallback stackStoryUpdated) {

        if (stackStoryUpdated == null) return;
        if (newIndex != oldIndex) {
            oldIndex = newIndex;
            generateNewStackStoryData(newIndex, stackStoryUpdated);
        }
    }

    public void onUpdate(int storyId, String listId, boolean getNextNonOpened) {
        int openedIndex = -1;
        for (int i = 0; i < stories.size(); i++) {
            if (storyId == stories.get(i).id) {
                openedIndex = i;
                stories.get(i).isOpened = true;
                break;
            }
        }
        boolean useOldIndex = false;
        if (openedIndex < 0) return;
        if (listId == null || !listId.equals(this.listId()))
            useOldIndex = true;
        if (useOldIndex) {
            generateNewStackStoryData(oldIndex, stackStoryUpdated);
            return;
        }

        int newIndex = (openedIndex + 1) % stories.size();
        if (getNextNonOpened) {
            for (int i = newIndex; i < stories.size(); i++) {
                if (!stories.get(i).isOpened()) {
                    newIndex = i;
                    break;
                }
            }
        }
        checkLastIndex(newIndex, stackStoryUpdated);
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

    private void openReader(final Context context, boolean showNewStories) {
        InAppStoryService service = InAppStoryService.getInstance();
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager == null) return;
        if (service == null) return;
        final Story currentStory = stories.get(oldIndex);
        Story current = service.getStoryDownloadManager().getStoryById(currentStory.id, Story.StoryType.COMMON);
        boolean currentStoryIsOpened = true;
        if (current != null) {
            currentStoryIsOpened = current.isOpened;
        }
        boolean showOnlyNewStories = !currentStoryIsOpened && showNewStories;
        if (currentStory.getDeeplink() != null && !currentStory.getDeeplink().isEmpty()) {
            service.getListReaderConnector().changeStory(currentStory.id, listId, showOnlyNewStories);
            StatisticManager.getInstance().sendDeeplinkStory(
                    currentStory.id,
                    currentStory.getDeeplink(),
                    feed
            );
            OldStatisticManager.useInstance(sessionId, new GetOldStatisticManagerCallback() {
                @Override
                public void get(@NonNull OldStatisticManager manager) {
                    manager.addDeeplinkClickStatistic(currentStory.id);
                }
            });
            core.callbacksAPI().useCallback(
                    IASCallbackType.CALL_TO_ACTION,
                    new UseIASCallback<CallToActionCallback>() {
                        @Override
                        public void use(@NonNull CallToActionCallback callback) {
                            callback.callToAction(
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
                        }

                        @Override
                        public void onDefault() {
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
                    }
            );
            if (current != null) {
                current.isOpened = true;
                current.saveStoryOpened(Story.StoryType.COMMON);
            }
        } else if (currentStory.getGameInstanceId() != null && !currentStory.getGameInstanceId().isEmpty()) {
            service.getListReaderConnector().changeStory(currentStory.id, listId, showOnlyNewStories);
            OldStatisticManager.useInstance(
                    sessionId,
                    new GetOldStatisticManagerCallback() {
                        @Override
                        public void get(@NonNull OldStatisticManager manager) {
                            manager.addGameClickStatistic(currentStory.id);
                        }
                    }
            );
            core.screensManager().openScreen(context,
                    new LaunchGameScreenStrategy(core, false)
                            .data(new LaunchGameScreenData(
                                    null,
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
                                    currentStory.getGameInstanceId()
                            ))
            );
            if (current != null) {
                current.isOpened = true;
                current.saveStoryOpened(Story.StoryType.COMMON);
            }
        } else if (!currentStory.isHideInReader()) {
            List<Integer> readerStories = new ArrayList<>();
            int j = 0;
            int openIndex = 0;
            for (Story story : stories) {
                if (showOnlyNewStories && story.isOpened()) continue;
                if (!story.isHideInReader()) {
                    if (currentStory == story) {
                        openIndex = j;
                    }
                    readerStories.add(story.id);
                    j++;
                }
            }
            LaunchStoryScreenData launchData = new LaunchStoryScreenData(
                    listId,
                    feed,
                    sessionId,
                    readerStories,
                    openIndex,
                    showOnlyNewStories,
                    ShowStory.ACTION_OPEN,
                    SourceType.STACK,
                    0,
                    Story.StoryType.COMMON,
                    null
            );
            core.screensManager().openScreen(context,
                    new LaunchStoryScreenStrategy(false).
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

    @Override
    public void openReader(Context context) {
        openReader(context, true);
    }
}
