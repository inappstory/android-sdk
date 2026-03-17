package com.inappstory.sdk.refactoring.stories.ui.list.states;


import java.util.Objects;

public class StoriesListItemState {
    private int id;
    private String title;
    private String titleColor;
    private StoriesListItemCoverState coverState;
    private boolean hasVideoUrl;
    private boolean hasAudio;
    private boolean isOpened;
    private StoriesListItemClickType clickType = StoriesListItemClickType.STORY;
    private String clickTypePayload;

    public StoriesListItemState() {
    }

    public StoriesListItemState(
            int id,
            String title,
            String titleColor,
            boolean isOpened,
            boolean hasVideoUrl,
            boolean hasAudio,
            StoriesListItemClickType clickType,
            String clickTypePayload
    ) {
        this.id = id;
        this.title = title;
        this.titleColor = titleColor;
        this.hasAudio = hasAudio;
        this.hasVideoUrl = hasVideoUrl;
        this.clickType = clickType;
        this.isOpened = isOpened;
        this.clickTypePayload = clickTypePayload;
    }


    public StoriesListItemCoverState coverState() {
        return coverState;
    }

    public StoriesListItemState coverState(StoriesListItemCoverState coverState) {
        this.coverState = coverState;
        return this;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public StoriesListItemState isOpened(boolean opened) {
        isOpened = opened;
        return this;
    }

    public int id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String titleColor() {
        return titleColor;
    }

    public boolean hasAudio() {
        return hasAudio;
    }

    public boolean hasVideoUrl() {
        return hasVideoUrl;
    }

    public StoriesListItemClickType clickType() {
        return clickType;
    }

    public String clickTypePayload() {
        return clickTypePayload;
    }


    public StoriesListItemState copy() {
        StoriesListItemState state = new StoriesListItemState()
                .coverState(this.coverState)
                .isOpened(this.isOpened);
        state.id = this.id;
        state.title = this.title;
        state.titleColor = this.titleColor;
        state.hasVideoUrl = this.hasVideoUrl;
        state.hasAudio = this.hasAudio;
        state.clickType = this.clickType;
        state.clickTypePayload = this.clickTypePayload;
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoriesListItemState)) return false;
        StoriesListItemState state = (StoriesListItemState) o;
        return id == state.id &&
                hasAudio == state.hasAudio &&
                hasVideoUrl == state.hasVideoUrl &&
                isOpened == state.isOpened &&
                Objects.equals(title, state.title) &&
                Objects.equals(titleColor, state.titleColor) &&
                Objects.equals(coverState, state.coverState) &&
                clickType == state.clickType &&
                Objects.equals(clickTypePayload, state.clickTypePayload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                title,
                titleColor,
                coverState,
                hasVideoUrl,
                hasAudio,
                isOpened,
                clickType,
                clickTypePayload
        );
    }
}
