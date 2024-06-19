package com.inappstory.sdk.iasapimodules.settings;


import androidx.annotation.NonNull;

import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface ISettingsProviderApi {

    boolean userIdOrDeviceIdIsCorrect();

    boolean tagsIsCorrect(List<String> tags);

    List<String> getTags();

    Map<String, String> getPlaceholders();

    Map<String, ImagePlaceholderValue> getImagePlaceholders();
}
