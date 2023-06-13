package com.inappstory.sdk.game.reader;


import com.inappstory.sdk.stories.api.models.Story;

public class GameStoryData {

    public int storyId;
    public String feedId;
    public int slideIndex;
    public int slidesCount;
    public String title;
    public String tags;
    public Story.StoryType type;

    public GameStoryData(
            int storyId,
            int slideIndex,
            int slidesCount,
            String title,
            String tags,
            String feedId,
            Story.StoryType type
    ) {
        this.storyId = storyId;
        this.slideIndex = slideIndex;
        this.slidesCount = slidesCount;
        this.title = title;
        this.tags = tags;
        this.feedId = feedId;
        this.type = type;
    }

}
