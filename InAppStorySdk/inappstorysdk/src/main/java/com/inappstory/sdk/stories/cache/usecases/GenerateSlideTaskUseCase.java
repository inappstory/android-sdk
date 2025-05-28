package com.inappstory.sdk.stories.cache.usecases;

import android.util.Pair;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderType;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.cache.SlideTask;
import com.inappstory.sdk.stories.cache.UrlWithAlter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateSlideTaskUseCase {
    private final IASCore core;
    private final IReaderContent readerContent;
    private final int index;


    public GenerateSlideTaskUseCase(IASCore core, IReaderContent readerContent, int index) {
        this.core = core;
        this.readerContent = readerContent;
        this.index = index;
    }

    public SlideTask generate() {
        Map<String, Pair<ImagePlaceholderValue, ImagePlaceholderValue>> imgPlaceholders = new HashMap<>(
                ((IASDataSettingsHolder) core.settingsAPI()).imagePlaceholdersWithSessionDefaults()
        );
        List<UrlWithAlter> urlWithAlters = new ArrayList<>();
        List<String> plNames = readerContent.placeholdersNames(index);
        for (String plName : plNames) {
            Pair<ImagePlaceholderValue, ImagePlaceholderValue> value =
                    imgPlaceholders.get(plName);
            if (value != null
                    && value.first != null
                    && value.first.getType() == ImagePlaceholderType.URL) {
                if (value.second != null
                        && value.second.getType() == ImagePlaceholderType.URL) {
                    urlWithAlters.add(
                            new UrlWithAlter(
                                    value.first.getUrl(),
                                    value.second.getUrl()
                            )
                    );
                } else {
                    urlWithAlters.add(
                            new UrlWithAlter(
                                    value.first.getUrl()
                            )
                    );
                }
            }
        }
        return new SlideTask(
                readerContent.staticResources(index),
                readerContent.vodResources(index),
                urlWithAlters
        );
    }
}
