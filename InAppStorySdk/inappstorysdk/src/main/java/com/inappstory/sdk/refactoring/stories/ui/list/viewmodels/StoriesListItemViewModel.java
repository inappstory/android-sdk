package com.inappstory.sdk.refactoring.stories.ui.list.viewmodels;

import android.content.Context;
import android.util.Log;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenData;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenStrategy;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.refactoring.core.utils.observers.Observable;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.IStoriesListItemChangeSubscriber;
import com.inappstory.sdk.refactoring.stories.data.local.StoriesListItemDTO;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListItemClickType;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListItemCoverState;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListItemState;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListState;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoryListItemCoordinates;
import com.inappstory.sdk.refactoring.stories.usecases.StoriesFeedParameters;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.cache.usecases.IGetStoryCoverCallback;
import com.inappstory.sdk.stories.cache.usecases.StoryCoverUseCase;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outerevents.ShowStory;

import java.util.ArrayList;

public class StoriesListItemViewModel implements IStoriesListItemChangeSubscriber {
    private final Observable<StoriesListItemState> storiesListItemStateObservable =
            new Observable<>(null);

    private StoriesListItemDTO listItemDTO;
    private BaseStoriesListViewModel storiesListViewModel;

    private final IASCore core;
    private final String storyId;

    public StoriesListItemViewModel(
            IASCore core,
            String storyId,
            BaseStoriesListViewModel storiesListViewModel
    ) {
        this.core = core;
        this.storyId = storyId;
        this.storiesListViewModel = storiesListViewModel;
        onChange(core.storyRepository().getLocalStoryListItem(storyId));
        core.storyChangesSubscribers().addStoryChangeSubscriber(this);

    }

    public void clickOnStory(
            Context context,
            AppearanceManager manager,
            StoryListItemCoordinates coordinates
    ) {
        StoriesListItemState state = storiesListItemStateObservable.getValue();
        StoriesListState listState = storiesListViewModel.storiesListStateObservable.getValue();

        ArrayList<String> tempStories = new ArrayList<>();
        for (String storyId : listState.storiesIds()) {
            Boolean hidden = listState.hideInReader().get(storyId);
            if (Boolean.FALSE.equals(hidden)) {
                tempStories.add(storyId);
            }

        }
        StoriesFeedParameters feedParameters = storiesListViewModel.feedParameters;
        RequestLocalParameters requestLocalParameters = storiesListViewModel.requestLocalParameters;
        LaunchStoryScreenData launchData = new LaunchStoryScreenData()
                .listUniqueId(feedParameters.feed())
                .feed(feedParameters.feed())
                .requestLocalParameters(requestLocalParameters)
                .options(feedParameters.options())
                .sessionId(requestLocalParameters.sessionId())
                .storiesIds(new ArrayList<>(tempStories))
                .listIndex(tempStories.indexOf(storyId))
                .firstAction(ShowStory.ACTION_OPEN)
                .sourceType(
                        storiesListViewModel instanceof StoriesFavoriteListViewModel
                                ? SourceType.FAVORITE : SourceType.LIST
                )
                .type(ContentType.STORY)
                .initCoordinates(coordinates);
        boolean nonAnonymous = !((IASDataSettingsHolder) core.settingsAPI()).anonymous();
        switch (state.clickType()) {
            case STORY:
                core.screensManager().openScreen(
                        context,
                        new LaunchStoryScreenStrategy(core, false).
                                launchStoryScreenData(launchData).
                                readerAppearanceSettings(
                                        new LaunchStoryScreenAppearance(
                                                AppearanceManager.checkOrCreateAppearanceManager(manager),
                                                context,
                                                nonAnonymous
                                        )
                                )
                );
                break;
            default:
                break;
        }
    }

    public void addSubscriber(Observer<StoriesListItemState> observer) {
        storiesListItemStateObservable.subscribeAndGetValue(observer);
    }

    public void removeSubscriber(Observer<StoriesListItemState> observer) {
        storiesListItemStateObservable.unsubscribe(observer);
    }

    public void initCover(int imageQuality) {
        StoriesListItemDTO dto = listItemDTO;
        StoriesListItemState state = storiesListItemStateObservable.getValue();
        if (dto == null || state == null) return;
        String imagePath = core.contentHolder().listsContent().getPathByUrl(
                dto.imageCoverByQuality(imageQuality)
        );
        String videoPath = core.contentHolder().listsContent().getPathByUrl(
                dto.videoCover()
        );
        storiesListItemStateObservable.updateValue(
                state.copy().coverState(
                        new StoriesListItemCoverState()
                                .backgroundColor(dto.backgroundColor())
                                .imagePath(imagePath)
                                .videoPath(videoPath)
                )
        );
    }

    public void loadCoverResources(int imageQuality) {
        StoriesListItemDTO dto = listItemDTO;
        if (dto == null) return;
        final String imageUrl = dto.imageCoverByQuality(imageQuality);
        final String videoUrl = dto.videoCover();
        loadImage(imageUrl);
        loadVideo(videoUrl);
    }

    private void loadImage(
            final String imageUrl
    ) {
        if (imageUrl == null) return;
        new StoryCoverUseCase(
                core,
                imageUrl,
                new IGetStoryCoverCallback() {
                    @Override
                    public void success(final String file) {
                        StoriesListItemState itemState = storiesListItemStateObservable.getValue();
                        if (itemState == null) return;
                        final StoriesListItemCoverState coverState;
                        if (itemState.coverState() != null) {
                            coverState = itemState.coverState().copy();
                        } else {
                            coverState = new StoriesListItemCoverState();
                        }
                        storiesListItemStateObservable.updateValue(
                                itemState.copy().coverState(
                                        coverState.imagePath(file)
                                )
                        );
                    }

                    @Override
                    public void error() {

                    }
                }
        ).getFile();
    }

    public void loadVideo(final String videoUrl) {
        if (videoUrl == null) return;
        new StoryCoverUseCase(
                core,
                videoUrl,
                new IGetStoryCoverCallback() {
                    @Override
                    public void success(final String file) {
                        StoriesListItemState itemState = storiesListItemStateObservable.getValue();
                        if (itemState == null) return;
                        final StoriesListItemCoverState coverState;
                        if (itemState.coverState() != null) {
                            coverState = itemState.coverState().copy();
                        } else {
                            coverState = new StoriesListItemCoverState();
                        }
                        storiesListItemStateObservable.updateValue(
                                itemState.copy().coverState(
                                        coverState.videoPath(file)
                                )
                        );
                    }

                    @Override
                    public void error() {

                    }
                }
        ).getFile();
    }

    public void clear() {
        core.storyChangesSubscribers().removeStoryChangeSubscriber(this);
        listItemDTO = null;
        storiesListItemStateObservable.updateValue(null);
    }

    @Override
    public void onChange(StoriesListItemDTO dto) {
        if (dto == null) return;
        Log.e("StoriesListItemDTO", dto.toString());
        listItemDTO = dto;
        StoriesListItemClickType clickType = StoriesListItemClickType.STORY;
        String payload = Integer.toString(dto.id());
        if (dto.gameInstanceId() != null) {
            clickType = StoriesListItemClickType.GAME;
            payload = dto.gameInstanceId();
        } else if (dto.deeplink() != null) {
            clickType = StoriesListItemClickType.DEEPLINK;
            payload = dto.deeplink();
        } else if (dto.hideInReader()) {
            clickType = StoriesListItemClickType.UNKNOWN;
            payload = null;
        }
        StoriesListItemState currentValue = storiesListItemStateObservable.getValue();
        StoriesListItemState newValue = new StoriesListItemState(
                dto.id(),
                dto.title(),
                dto.titleColor(),
                dto.opened(),
                dto.videoCover() != null && !dto.videoCover().isEmpty(),
                dto.hasAudio(),
                clickType,
                payload
        );
        if (currentValue != null) {
            newValue.coverState(currentValue.coverState());
            newValue.isOpened(currentValue.isOpened());
            newValue.listIndex(currentValue.listIndex());
        }
        storiesListItemStateObservable.updateValue(
                newValue
        );
    }

    @Override
    public String getStoryId() {
        return storyId;
    }
}
