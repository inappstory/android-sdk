package com.inappstory.sdk.core.models.api;


import com.inappstory.sdk.core.utils.network.annotations.models.Required;
import com.inappstory.sdk.core.utils.network.annotations.models.SerializedName;

public class Session {
    @Required
    public String id;
    @SerializedName("expire_in")
    public int expireIn;
    public long updatedAt;
}
