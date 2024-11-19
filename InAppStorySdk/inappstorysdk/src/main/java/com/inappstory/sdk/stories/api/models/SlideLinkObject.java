package com.inappstory.sdk.stories.api.models;

public class SlideLinkObject {
    public SlideLinkObject(String type, StoryLink link) {
        this.type = type;
        this.link = link;
    }


    public SlideLinkObject(String type, StoryLink link,
                           String elementId) {
        this.type = type;
        this.link = link;
        this.elementId = elementId;
    }

    public SlideLinkObject() {

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
