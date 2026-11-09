package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.Feed;
import com.inappstory.sdk.core.network.content.models.InAppMessage;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;


public final class ToFeed extends ToModelParser<Feed> {
    public ToFeed(String value) {
        super(value);
    }

    @Override
    protected Feed parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            Feed feed = new Feed();
            feed.feedId = jsonObj.optIntNullable(
                    ModelFields.Feed.feedId
            );
            feed.hasFavorite = jsonObj.optBooleanNullable(
                    ModelFields.Feed.hasFavorite
            );
            feed.stories = new ToStoryList(jsonObj.optJSONArray(
                    ModelFields.Feed.stories
            )).parse();
            feed.feedCover = new ToImageList(jsonObj.optJSONArray(
                    ModelFields.Feed.feedCover
            )).parse();
            return feed;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "Feed";
    }
}
