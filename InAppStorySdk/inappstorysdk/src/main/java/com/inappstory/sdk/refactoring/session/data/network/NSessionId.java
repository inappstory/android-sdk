package com.inappstory.sdk.refactoring.session.data.network;

import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;


public class NSessionId {
    @Required
    @SerializedName(NSessionResponseFields.sessionId)
    public String id;

}
