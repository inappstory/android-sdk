package com.inappstory.sdk.core.network.content.models;

import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class InAppMessageEvent {
    @SerializedName(ModelFields.InAppMessageEvent.id)
    public int id;
    @Required
    @SerializedName(ModelFields.InAppMessageEvent.name)
    public String name;
    @SerializedName(ModelFields.InAppMessageEvent.priority)
    public int priority;
}
