package com.inappstory.sdk.refactoring.stories.ui.list.viewmodels;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.refactoring.core.utils.observers.Observable;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.IStoriesListItemChangeSubscriber;
import com.inappstory.sdk.refactoring.stories.data.local.StoriesListItemDTO;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListItemClickType;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListItemCoverState;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListItemState;
import com.inappstory.sdk.stories.cache.usecases.IGetStoryCoverCallback;
import com.inappstory.sdk.stories.cache.usecases.StoryCoverUseCase;

public class StoriesListItemViewModel implements IStoriesListItemChangeSubscriber {
    private final Observable<StoriesListItemState> storiesListItemStateObservable =
            new Observable<>(null);

    private StoriesListItemDTO listItemDTO;

    private final IASCore core;
    private final String storyId;

    public StoriesListItemViewModel(
            IASCore core,
            String storyId
    ) {
        this.core = core;
        this.storyId = storyId;
        core.storyChangesSubscribers().addStoryChangeSubscriber(this);
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
        String path = core.contentHolder().listsContent().getPathByUrl(
                dto.imageCoverByQuality(imageQuality)
        );
        storiesListItemStateObservable.updateValue(
                state.copy().coverState(
                        new StoriesListItemCoverState()
                                .backgroundColor(dto.backgroundColor())
                                .imagePath(path)
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
        storiesListItemStateObservable.updateValue(
                new StoriesListItemState(
                        dto.id(),
                        dto.title(),
                        dto.titleColor(),
                        dto.isOpened(),
                        dto.videoCover() != null && !dto.videoCover().isEmpty(),
                        dto.hasAudio(),
                        clickType,
                        payload
                )
        );
    }

    @Override
    public String getStoryId() {
        return storyId;
    }
}
