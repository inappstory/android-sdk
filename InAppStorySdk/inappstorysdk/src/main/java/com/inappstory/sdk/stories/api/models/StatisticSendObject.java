package com.inappstory.sdk.stories.api.models;


import com.inappstory.sdk.network.SerializedName;

import java.util.List;

/**
 * Created by paperrose on 21.02.2018.
 */

public class StatisticSendObject {
    public StatisticSendObject(String session, List<List<Object>> data) {
        this.sessionId = session;
        this.data = data;
    }

    @SerializedName("session_id")
    public String sessionId;
    public List<List<Object>> data;
}
