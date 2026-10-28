package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public final class ToStory extends ToModelParser<Story> {
    public ToStory(String value) {
        super(value);
    }

    public ToStory(JSONObject value) {
        super(value);
    }

    private Story parseWithException(ExtendedJsonObject jsonObj) throws Exception {
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

        story.image = new ToImageList(jsonObj.optJSONArray("images")).parse();
        story.videoUrl = new ToImageList(jsonObj.optJSONArray("video_cover")).parse();
        story.slides = new ToStorySlideList(jsonObj.optJSONArray("slides")).parse();
        story.gameInstance = new ToGameInstance(jsonObj.optJSONObject("game_instance")).parse();

        story.customAttributes = JsonParser.toMap(jsonObj.optJSONObject("custom_attributes"));
        story.layoutTemplateVariables = JsonParser.toMap(jsonObj.optJSONObject("layout_template_variables"));

        return story;
    }

    @Override
    protected Story parseObject(JSONObject jsonObject) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    jsonObject
            );
            return parseWithException(jsonObj);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected Story parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            return parseWithException(jsonObj);
        } catch (Exception e) {
            return null;
        }
    }
}
