package com.inappstory.sdk.inappmessage.stedata;

import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;

public class CallToActionData implements STEData {
    public SlideData slideData() {
        return slideData;
    }

    public String link() {
        return link;
    }

    public ClickAction clickAction() {
        return clickAction;
    }

    public CallToActionData slideData(SlideData slideData) {
        this.slideData = slideData;
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

    private SlideData slideData;
    private String link;
    private ClickAction clickAction;
}
