package com.inappstory.sdk.core.repository.session.interfaces;

import com.inappstory.sdk.stories.api.models.StoryPlaceholder;

import java.util.List;

public interface IPlaceholdersDtoHolder {
    List<StoryPlaceholder> getTextPlaceholders();
    List<StoryPlaceholder> getImagePlaceholders();
}
