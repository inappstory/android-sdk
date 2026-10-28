package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TargetingBodyOptionsObject {
    @SerializedName("pos")
    String pointOfSale;

    public TargetingBodyOptionsObject(Map<String, String> options) {
        this.pointOfSale = pointOfSale;
    }
}
