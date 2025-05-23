package com.inappstory.sdk.core.data.models;

import com.inappstory.sdk.core.data.IInAppStoryUserSettings;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InAppStoryUserSettings implements IInAppStoryUserSettings {
    private String userId;
    private String userSign;
    private List<String> tags;
    private Map<String, String> placeholders;
    private Map<String, ImagePlaceholderValue> imagePlaceholders;
    private Locale lang;
    private boolean changeLayoutDirection;

    public InAppStoryUserSettings userId(String userId) {
        this.userId = userId;
        return this;
    }

    public InAppStoryUserSettings userId(String userId, String userSign) {
        this.userId = userId;
        this.userSign = userSign;
        return this;
    }

    public InAppStoryUserSettings tags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public InAppStoryUserSettings placeholders(Map<String, String> placeholders) {
        this.placeholders = placeholders;
        return this;
    }

    public InAppStoryUserSettings imagePlaceholders(Map<String, ImagePlaceholderValue> imagePlaceholders) {
        this.imagePlaceholders = imagePlaceholders;
        return this;
    }

    public InAppStoryUserSettings lang(Locale lang) {
        this.lang = lang;
        return this;
    }

    public InAppStoryUserSettings lang(Locale lang, boolean changeLayoutDirection) {
        this.lang = lang;
        this.changeLayoutDirection = changeLayoutDirection;
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

    @Override
    public boolean changeLayoutDirection() {
        return changeLayoutDirection;
    }
}
