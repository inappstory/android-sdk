package com.inappstory.sdk.stories.api.models;

public class StoryLink {
    public StoryLink(String type, String target) {
        this.type = type;
        this.target = target;
    }

    public StoryLink() {

    }

    public String getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    String type;
    String target;
}