package com.inappstory.sdk.core.network.content.models;

import com.inappstory.sdk.core.data.IInAppMessageLimit;
import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class InAppMessageLimit implements IInAppMessageLimit {
    @Required
    @SerializedName(ModelFields.InAppMessageLimit.messageId)
    public int messageId;

    @SerializedName(ModelFields.InAppMessageLimit.expire)
    public long expire;

    @SerializedName(ModelFields.InAppMessageLimit.canOpen)
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
