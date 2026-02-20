package com.inappstory.sdk.refactoring.stories.usecases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StoryFeedParameters {
    public StoryFeedParameters(String feed, List<String> tags, Map<String, String> options) {
        this.feed = feed;
        this.options = new HashMap<>(options);
        this.tags = new ArrayList<>(tags);
    }

    final String feed;

    final Map<String, String> options;

    public String feed() {
        return feed;
    }

    public Map<String, String> options() {
        return options;
    }

    public List<String> tags() {
        return tags;
    }

    final List<String> tags;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoryFeedParameters)) return false;
        StoryFeedParameters that = (StoryFeedParameters) o;
        return Objects.equals(feed, that.feed) &&
                Objects.equals(options, that.options) &&
                Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feed, options, tags);
    }
}
