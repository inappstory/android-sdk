package com.inappstory.sdk.stories.outerevents;

public class CallToAction extends BaseOuterEvent {

    public int getIndex() {
        return index;
    }

    public String getLink() {
        return link;
    }
    public int getType() {
        return type;
    }

    int index;
    int type;
    String link;

    public static final int BUTTON = 0;
    public static final int SWIPE = 1;
    public static final int GAME = 2;
    public static final int DEEPLINK = 3;

    public CallToAction(int id, String title, String tags, int slidesCount, int index, String link, int type) {
        super(id, title, tags, slidesCount);
        this.index = index;
        this.link = link;
        this.type = type;
    }
}
