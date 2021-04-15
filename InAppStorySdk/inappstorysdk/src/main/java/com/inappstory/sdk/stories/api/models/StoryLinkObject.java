package com.inappstory.sdk.stories.api.models;

public class StoryLinkObject {
    public StoryLinkObject(String type, StoryLink link) {
        this.type = type;
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public StoryLink getLink() {
        return link;
    }

    String type;
    StoryLink link;

}
