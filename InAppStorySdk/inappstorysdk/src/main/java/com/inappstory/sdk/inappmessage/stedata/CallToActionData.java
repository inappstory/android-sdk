package com.inappstory.sdk.inappmessage.stedata;

import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ContentData;

public class CallToActionData implements STEData {
    public ContentData contentData() {
        return contentData;
    }

    public String link() {
        return link;
    }

    public ClickAction clickAction() {
        return clickAction;
    }

    public CallToActionData contentData(ContentData contentData) {
        this.contentData = contentData;
        return this;
    }

    public CallToActionData link(String link) {
        this.link = link;
        return this;
    }

    public CallToActionData clickAction(ClickAction clickAction) {
        this.clickAction = clickAction;
        return this;
    }

    public CallToActionData() {
    }

    private ContentData contentData;
    private String link;
    private ClickAction clickAction;
}
