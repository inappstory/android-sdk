package com.inappstory.sdk.core.repository.session;

import com.inappstory.sdk.core.repository.session.interfaces.IPlaceholdersDtoHolder;
import com.inappstory.sdk.stories.api.models.StoryPlaceholder;

import java.util.ArrayList;
import java.util.List;

final class PlaceholdersDtoHolder implements IPlaceholdersDtoHolder {

    public PlaceholdersDtoHolder(
            List<StoryPlaceholder> textPlaceholders,
            List<StoryPlaceholder> imagePlaceholders
    ) {
        if (imagePlaceholders == null) {
            this.imagePlaceholders = new ArrayList<>();
        } else {
            this.imagePlaceholders = new ArrayList<>(imagePlaceholders);
        }
        if (textPlaceholders == null) {
            this.textPlaceholders = new ArrayList<>();
        } else {
            this.textPlaceholders = new ArrayList<>(textPlaceholders);
        }
    }

    private final List<StoryPlaceholder> imagePlaceholders;
    private final List<StoryPlaceholder> textPlaceholders;

    @Override
    public List<StoryPlaceholder> getImagePlaceholders() {
        return imagePlaceholders;
    }

    @Override
    public List<StoryPlaceholder> getTextPlaceholders() {
        return textPlaceholders;
    }
}
