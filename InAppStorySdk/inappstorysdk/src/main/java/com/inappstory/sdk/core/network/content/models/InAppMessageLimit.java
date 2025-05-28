package com.inappstory.sdk.core.network.content.models;

import com.inappstory.sdk.core.data.IInAppMessageLimit;
import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;

public class InAppMessageLimit implements IInAppMessageLimit {
    @Required
    @SerializedName("messageId")
    public int messageId;

    @SerializedName("expire")
    public long expire;

    @SerializedName("canOpen")
    public boolean canOpen;


    @Override
    public boolean canOpen() {
        return canOpen;
    }

    @Override
    public long expireInSeconds() {
        return expire;
    }

    @Override
    public int messageId() {
        return messageId;
    }
}
