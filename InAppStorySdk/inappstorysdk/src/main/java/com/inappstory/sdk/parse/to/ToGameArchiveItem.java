package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.GameArchiveItem;
import com.inappstory.sdk.stories.api.models.GameLaunchConditions;

import org.json.JSONObject;


public final class ToGameArchiveItem extends ToModelParser<GameArchiveItem> {
    public ToGameArchiveItem(JSONObject value) {
        super(value);
    }

    @Override
    protected GameArchiveItem parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            GameArchiveItem gameArchiveItem = new GameArchiveItem();
            gameArchiveItem.item = jsonObj.getString("item");
            gameArchiveItem.sha1 = jsonObj.optStringNullable("sha1");
            return gameArchiveItem;
        } catch (Exception e) {
            return null;
        }
    }
}
