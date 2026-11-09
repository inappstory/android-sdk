package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.core.data.IGameArchiveItem;
import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class GameArchiveItem implements IGameArchiveItem {

    @Required
    @SerializedName(ModelFields.GameArchiveItem.item)
    public String item;
    @SerializedName(ModelFields.GameArchiveItem.sha1)
    public String sha1;

    @Override
    public String item() {
        return item;
    }

    @Override
    public String sha1() {
        return sha1;
    }
}
