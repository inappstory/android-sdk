package com.inappstory.sdk.stories.stackfeed;

import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;

public class StackStoryData implements IStackStoryData {
    public StackStoryData(
            String title,
            int titleColor,
            boolean hasAudio,
            int backgroundColor,
            boolean hasImageCover,
            boolean hasVideoCover,
            boolean[] stackFeedOpenedStatuses,
            StoryData[] stackFeedStories,
            int stackFeedIndex
    ) {
        this.title = title;
        this.titleColor = titleColor;
        this.hasAudio = hasAudio;
        this.cover = new StackStoryCover(backgroundColor, null, null);
        if (hasImageCover && hasVideoCover) {
            stackStoryCoverLoadType = StackStoryCoverLoadType.VIDEO_IMAGE;
        } else if (hasImageCover) {
            stackStoryCoverLoadType = StackStoryCoverLoadType.IMAGE;
        } else if (hasVideoCover) {
            stackStoryCoverLoadType = StackStoryCoverLoadType.VIDEO;
        }
        this.stackFeedOpenedStatuses = stackFeedOpenedStatuses;
        this.stackFeedStories = stackFeedStories;
        this.stackFeedIndex = stackFeedIndex;
    }

    String title;
    boolean hasAudio;
    StackStoryCover cover;
    boolean[] stackFeedOpenedStatuses;
    StoryData[] stackFeedStories;
    int stackFeedIndex;
    int titleColor;

    public void stackStoryCoverLoadType(StackStoryCoverLoadType stackStoryCoverLoadType) {
        synchronized (this) {
            this.stackStoryCoverLoadType = stackStoryCoverLoadType;
        }
    }

    StackStoryCoverLoadType stackStoryCoverLoadType = StackStoryCoverLoadType.NOTHING;

    public void updateStoryDataCover(String cover,
                                     StackStoryCoverLoadType type,
                                     StackStoryCoverCompleteCallback loadCompleteCallback
    ) {
        synchronized (this) {
            if (type == StackStoryCoverLoadType.VIDEO) {
                if (stackStoryCoverLoadType == StackStoryCoverLoadType.VIDEO_IMAGE)
                    stackStoryCoverLoadType = StackStoryCoverLoadType.IMAGE;
                else if (stackStoryCoverLoadType == StackStoryCoverLoadType.VIDEO) {
                    stackStoryCoverLoadType = StackStoryCoverLoadType.NOTHING;
                }
                this.cover.videoCoverPath(cover);
            } else if (type == StackStoryCoverLoadType.IMAGE) {
                if (stackStoryCoverLoadType == StackStoryCoverLoadType.VIDEO_IMAGE)
                    stackStoryCoverLoadType = StackStoryCoverLoadType.VIDEO;
                else if (stackStoryCoverLoadType == StackStoryCoverLoadType.IMAGE) {
                    stackStoryCoverLoadType = StackStoryCoverLoadType.NOTHING;
                }
                this.cover.imageCoverPath(cover);
            }
            if (stackStoryCoverLoadType == StackStoryCoverLoadType.NOTHING) {
                loadCompleteCallback.onComplete();
            }
        }
    }

    public String title() {
        return title;
    }

    @Override
    public int titleColor() {
        return titleColor;
    }

    public boolean hasAudio() {
        return hasAudio;
    }

    public IStackStoryCover cover() {
        return cover;
    }

    public boolean[] stackFeedOpenedStatuses() {
        return stackFeedOpenedStatuses;
    }

    public StoryData[] stackFeedStories() {
        return stackFeedStories;
    }

    public int stackFeedIndex() {
        return stackFeedIndex;
    }
}
