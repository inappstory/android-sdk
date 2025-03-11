package com.inappstory.sdk.inappmessage.domain.stedata;

public class JsSendApiRequestData implements STEData {
    public String data() {
        return data;
    }

    public JsSendApiRequestData data(String data) {
        this.data = data;
        return this;
    }

    private String data;
}
