package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TargetingBodyOptionsObject {
    @SerializedName("pos")
    String pointOfSale;

    public TargetingBodyOptionsObject(String pointOfSale) {
        this.pointOfSale = pointOfSale;
    }
}
