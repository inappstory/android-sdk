package com.inappstory.sdk.stories.api.models;

public class StoryLinkObject {
    public StoryLinkObject(String type, StoryLink link) {
        this.type = type;
        this.link = link;
    }


    public StoryLinkObject(String type, StoryLink link,
                           String elementId) {
        this.type = type;
        this.link = link;
        this.elementId = elementId;
    }

    public StoryLinkObject() {

    }

    public String getType() {
        return type;
    }

    public StoryLink getLink() {
        return link;
    }

    public String getElementId() {
        return elementId;
    }
    String elementId;

    String type;
    StoryLink link;

}
