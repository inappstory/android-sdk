package com.inappstory.sdk.stories.cache;

import android.util.Pair;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.utils.AssetUrlsExtractor;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderType;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlideTaskCreator {
    private final IASDataSettingsHolder settingsHolder;
    private final AssetUrlsExtractor urlsExtractor;
    private final IReaderContent readerContent;


    public SlideTaskCreator(
            IASDataSettingsHolder settingsHolder,
            AssetUrlsExtractor urlsExtractor,
            IReaderContent readerContent
    ) {
        this.readerContent = readerContent;
        this.urlsExtractor = urlsExtractor;
        this.settingsHolder = settingsHolder;
    }


    public SlideTask createForSlideIndex(int index) {
        Map<String, Pair<ImagePlaceholderValue, ImagePlaceholderValue>> imgPlaceholders =
                new HashMap<>(settingsHolder.imagePlaceholdersWithSessionDefaults());
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
                urlWithAlters,
                urlsExtractor.extract(readerContent)
        );
    }
}
