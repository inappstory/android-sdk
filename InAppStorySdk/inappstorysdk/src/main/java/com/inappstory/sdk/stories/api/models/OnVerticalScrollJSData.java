package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class OnVerticalScrollJSData {
    @SerializedName(ModelFields.OnVerticalScrollJSData.scrollY)
    public float scrollY;
    @SerializedName(ModelFields.OnVerticalScrollJSData.scrollPercent)
    public float scrollPercent;
    @SerializedName(ModelFields.OnVerticalScrollJSData.isScrollableLayer)
    public boolean isScrollableLayer;
    @SerializedName(ModelFields.OnVerticalScrollJSData.cardId)
    public int cardId;
    @SerializedName(ModelFields.OnVerticalScrollJSData.slideIndex)
    public int slideIndex;
    @SerializedName(ModelFields.OnVerticalScrollJSData.layerIndex)
    public int layerIndex;
}
