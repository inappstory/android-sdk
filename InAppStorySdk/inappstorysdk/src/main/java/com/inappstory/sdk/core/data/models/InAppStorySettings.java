package com.inappstory.sdk.core.data.models;

import com.inappstory.sdk.core.data.IInAppStorySettings;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InAppStorySettings implements IInAppStorySettings {
    private String userId;
    private String userSign;
    private List<String> tags;
    private Map<String, String> placeholders;
    private Map<String, ImagePlaceholderValue> imagePlaceholders;
    private Locale lang;

    public InAppStorySettings userId(String userId) {
        this.userId = userId;
        return this;
    }

    public InAppStorySettings userId(String userId, String userSign) {
        this.userId = userId;
        this.userSign = userSign;
        return this;
    }

    public InAppStorySettings tags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public InAppStorySettings placeholders(Map<String, String> placeholders) {
        this.placeholders = placeholders;
        return this;
    }

    public InAppStorySettings imagePlaceholders(Map<String, ImagePlaceholderValue> imagePlaceholders) {
        this.imagePlaceholders = imagePlaceholders;
        return this;
    }

    public InAppStorySettings lang(Locale lang) {
        this.lang = lang;
        return this;
    }

    @Override
    public String userId() {
        return userId;
    }

    @Override
    public String userSign() {
        return userSign;
    }

    @Override
    public List<String> tags() {
        return tags;
    }

    @Override
    public Map<String, String> placeholders() {
        return placeholders;
    }

    @Override
    public Map<String, ImagePlaceholderValue> imagePlaceholders() {
        return imagePlaceholders;
    }

    @Override
    public Locale lang() {
        return lang;
    }
}
