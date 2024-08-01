package com.inappstory.sdk.iasapimodules.settings;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.iasapimodules.NotImplementedYetException;
import com.inappstory.sdk.packages.core.base.network.IContextDependentSettings;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SettingsApi implements ISettingsApi, ISettingsProviderApi {
    private IContextDependentSettings contextDependentSettings;
    private boolean isUserIdEnabled;

    public SettingsApi(boolean isUserIdEnabled) {
        this.isUserIdEnabled = isUserIdEnabled;
    }

    @Override
    public void updateContextDependentSettings(@NonNull Context context) {

    }

    @Override
    public void setLang(@NonNull Locale lang) {
        throw new NotImplementedYetException();
    }

    @Override
    public void setUserId(@NonNull String userId) {
        throw new NotImplementedYetException();
    }

    @Override
    public void setTags(ArrayList<String> tags) {
        throw new NotImplementedYetException();
    }

    @Override
    public void addTags(ArrayList<String> tagsToAdd) {
        throw new NotImplementedYetException();
    }

    @Override
    public void removeTags(ArrayList<String> tagsToRemove) {
        throw new NotImplementedYetException();
    }

    @Override
    public void setPlaceholders(@NonNull Map<String, String> newPlaceholders) {
        throw new NotImplementedYetException();
    }

    @Override
    public void setPlaceholder(String key, String value) {
        throw new NotImplementedYetException();
    }

    @Override
    public void setImagePlaceholders(@NonNull Map<String, ImagePlaceholderValue> placeholders) {
        throw new NotImplementedYetException();
    }

    @Override
    public void setImagePlaceholder(@NonNull String key, ImagePlaceholderValue value) {
        throw new NotImplementedYetException();
    }

    @Override
    public boolean userIdOrDeviceIdIsCorrect() {
        throw new NotImplementedYetException();
    }

    @Override
    public boolean tagsIsCorrect(List<String> tags) {
        throw new NotImplementedYetException();
    }

    @Override
    public List<String> getTags() {
        throw new NotImplementedYetException();
    }

    @Override
    public Map<String, String> getPlaceholders() {
        throw new NotImplementedYetException();
    }

    @Override
    public Map<String, ImagePlaceholderValue> getImagePlaceholders() {
        throw new NotImplementedYetException();
    }
}
