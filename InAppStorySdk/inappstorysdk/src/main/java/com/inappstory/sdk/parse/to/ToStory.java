package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.parse.ExtendedJsonObject;


public final class ToStory extends ToModelParser<Story> {
    public ToStory(String value) {
        super(value);
    }

    @Override
    protected Story parseString(String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(value);
            Story story = new Story();
            story.id = jsonObj.getInt("id");
            story.title = jsonObj.optStringNullable("title");
            story.titleColor = jsonObj.optStringNullable("title_color");
            story.statTitle = jsonObj.optStringNullable("stat_title");
            story.isFullscreen = jsonObj.optBoolean("is_full_screen");
            story.backgroundColor = jsonObj.optStringNullable("background_color");
            story.hasSwipeUp = jsonObj.optBooleanNullable("has_swipe_up");
            story.like = jsonObj.optIntNullable("like");
            story.slidesCount = jsonObj.optInt("slides_count");
            story.favorite = jsonObj.optBoolean("favorite");
            story.hideInReader = jsonObj.optBooleanNullable("hide_in_reader");
            story.deeplink = jsonObj.optStringNullable("deeplink");
            story.isOpened = jsonObj.optBoolean("is_opened");
            story.disableClose = jsonObj.optBoolean("disable_close");
            story.hasLike = jsonObj.optBooleanNullable("like_functional");
            story.hasAudio = jsonObj.optBooleanNullable("has_audio");
            story.hasFavorite = jsonObj.optBooleanNullable("favorite_functional");
            story.hasShare = jsonObj.optBooleanNullable("share_functional");
            story.timelineIsHidden = jsonObj.optBoolean("hide_timeline");
            return story;
        } catch (Exception e) {
            return null;
        }
    }
}
