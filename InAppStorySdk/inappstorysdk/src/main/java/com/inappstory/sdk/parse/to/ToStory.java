package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

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
        story.id = jsonObj.getInt(
                ModelFields.Story.id
        );
        story.title = jsonObj.optStringNullable(
                ModelFields.Story.title
        );
        story.titleColor = jsonObj.optStringNullable(
                ModelFields.Story.titleColor
        );
        story.statTitle = jsonObj.optStringNullable(
                ModelFields.Story.statTitle
        );
        story.isFullscreen = jsonObj.optBoolean(
                ModelFields.Story.isFullscreen
        );
        story.backgroundColor = jsonObj.optStringNullable(
                ModelFields.Story.backgroundColor
        );
        story.hasSwipeUp = jsonObj.optBooleanNullable(
                ModelFields.Story.hasSwipeUp
        );
        story.like = jsonObj.optIntNullable(
                ModelFields.Story.like
        );
        story.slidesCount = jsonObj.optInt(
                ModelFields.Story.slidesCount
        );
        story.favorite = jsonObj.optBoolean(
                ModelFields.Story.favorite
        );
        story.hideInReader = jsonObj.optBooleanNullable(
                ModelFields.Story.hideInReader
        );
        story.deeplink = jsonObj.optStringNullable(
                ModelFields.Story.deeplink
        );
        story.isOpened = jsonObj.optBoolean(
                ModelFields.Story.isOpened
        );
        story.disableClose = jsonObj.optBoolean(
                ModelFields.Story.disableClose
        );
        story.hasLike = jsonObj.optBooleanNullable(
                ModelFields.Story.hasLike
        );
        story.hasAudio = jsonObj.optBooleanNullable(
                ModelFields.Story.hasAudio
        );
        story.hasFavorite = jsonObj.optBooleanNullable(
                ModelFields.Story.hasFavorite
        );
        story.hasShare = jsonObj.optBooleanNullable(
                ModelFields.Story.hasShare
        );
        story.timelineIsHidden = jsonObj.optBoolean(
                ModelFields.Story.timelineIsHidden
        );

        story.image = new ToImageList(jsonObj.optJSONArray(
                ModelFields.Story.image
        )).parse();
        story.videoUrl = new ToImageList(jsonObj.optJSONArray(
                ModelFields.Story.videoUrl
        )).parse();
        story.slides = new ToStorySlideList(jsonObj.optJSONArray(
                ModelFields.Story.slides
        )).parse();
        story.gameInstance = new ToGameInstance(jsonObj.optJSONObject(
                ModelFields.Story.gameInstance
        )).parse();

        story.customAttributes = JsonParser.toMap(jsonObj.optJSONObject(
                ModelFields.Story.customAttributes
        ));
        story.layoutTemplateVariables = JsonParser.toMap(jsonObj.optJSONObject(
                ModelFields.Story.layoutTemplateVariables
        ));

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

    @Override
    protected String getModelClassName() {
        return "Story";
    }
}
