package com.inappstory.sdk.core.repository.session.dto;

import com.inappstory.sdk.core.models.StoryPlaceholder;

public class StoryPlaceholderDTO {
    public String getName() {
        return name;
    }

    public String getDefaultVal() {
        return defaultVal;
    }

    private String name;
    private String defaultVal;

    public StoryPlaceholderDTO(StoryPlaceholder storyPlaceholder) {
        this.name = storyPlaceholder.name;
        this.defaultVal = storyPlaceholder.defaultVal;
    }
}