package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.InAppMessageFeed;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.GameCenterData;

import org.json.JSONObject;


public final class ToGameCenterData extends ToModelParser<GameCenterData> {
    public ToGameCenterData(String value) {
        super(value);
    }

    @Override
    protected GameCenterData parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            GameCenterData gameCenterData = new GameCenterData();
            story.id = jsonObj.getInt("id");
            return gameCenterData;
        } catch (Exception e) {
            return null;
        }
    }
}
