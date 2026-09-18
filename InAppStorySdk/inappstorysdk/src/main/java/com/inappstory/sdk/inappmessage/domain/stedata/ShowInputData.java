package com.inappstory.sdk.inappmessage.domain.stedata;


import com.inappstory.sdk.network.annotations.models.SerializedName;

public class ShowInputData implements STEData {
    public String id() {
        return id;
    }

    public String data() {
        return data;
    }


    public ShowInputData id(String id) {
        this.id = id;
        return this;
    }

    public ShowInputData data(String data) {
        this.data = data;
        return this;
    }

    public ShowInputData() {
    }

    @SerializedName("id")
    public String id;
    @SerializedName("data")
    public String data;
}
