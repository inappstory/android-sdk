package com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents;

import com.inappstory.sdk.inappmessage.domain.stedata.STEData;

public class JsSendApiRequestResponse implements STEData {
    public String cb() {
        return cb;
    }

    public String result() {
        return result;
    }

    private String result;
    private String cb;

    public JsSendApiRequestResponse result(String result) {
        this.result = result;
        return this;
    }

    public JsSendApiRequestResponse cb(String cb) {
        this.cb = cb;
        return this;
    }
}
