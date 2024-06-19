package com.inappstory.sdk.iasapimodules.settings;


import androidx.annotation.NonNull;

import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface ISettingsApi {
    void setLang(@NonNull Locale lang);

    void setUserId(@NonNull String userId);

    void setTags(ArrayList<String> tags);

    void addTags(ArrayList<String> tagsToAdd);

    void removeTags(ArrayList<String> tagsToRemove);

    void setPlaceholders(@NonNull Map<String, String> newPlaceholders);

    void setPlaceholder(String key, String value);

    void setImagePlaceholders(@NonNull Map<String, ImagePlaceholderValue> placeholders);

    void setImagePlaceholder(@NonNull String key, ImagePlaceholderValue value);
}
