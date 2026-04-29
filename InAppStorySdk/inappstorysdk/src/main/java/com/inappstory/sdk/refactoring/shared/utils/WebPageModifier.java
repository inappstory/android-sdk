package com.inappstory.sdk.refactoring.shared.utils;

import android.util.Pair;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.data.IResource;
import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.refactoring.shared.data.contracts.ISlidesContent;
import com.inappstory.sdk.refactoring.stories.data.contracts.IStoryReaderItem;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderType;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.cache.usecases.SessionAssetLocalUseCase;
import com.inappstory.sdk.utils.FilePathCacheGenerator;
import com.inappstory.sdk.utils.FilePathCacheType;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

public class WebPageModifier {
    private final IASCore core;

    public WebPageModifier(IASCore core) {
        this.core = core;
    }

    public String[] modifyForStory(IStoryReaderItem story, int index) {
        WebPageModifier modifier = new WebPageModifier(core);
        String slide = story.slideByIndex(index);
        String layout = story.layout();
        slide = modifier.replaceStaticResources(
                story.staticResources(index), false, slide)[0];
        slide = modifier.replaceImagePlaceholders(
                story.placeholdersMap(index), false, slide)[0];
        layout = modifier.replaceLayoutAssets(layout);
        return modifier.replacePlaceholders(layout, slide);
    }

    private String[] replaceStaticResources(
            List<IResource> resources,
            boolean forced,
            String... replaceStrings
    ) {
        String[] result = new String[replaceStrings.length];
        System.arraycopy(replaceStrings, 0, result, 0, replaceStrings.length);
        for (IResource object : resources) {
            String resource = object.getUrl();
            String resourceKey = object.getKey();
            String key = StringsUtils.md5(resource);
            File file = core.contentLoader().getCommonCache().getFullFile(key);
            if (file != null && file.exists() && file.length() > 0) {
                resource = "file://" + file.getAbsolutePath();
            } else if (forced) {
                resource = "file://" +
                        new FilePathCacheGenerator(
                                object.getUrl(),
                                core,
                                FilePathCacheType.STORY_RESOURCE
                        ).generate();
            }
            for (int i = 0; i < result.length; i++) {
                result[i] = result[i].replace(resourceKey, resource);
            }
        }
        return result;
    }

    private String[] replaceImagePlaceholders(
            Map<String, String> imgPlaceholderKeys,
            boolean forced,
            String... replaceStrings
    ) {
        String[] result = new String[replaceStrings.length];
        System.arraycopy(replaceStrings, 0, result, 0, replaceStrings.length);
        Map<String, Pair<ImagePlaceholderValue, ImagePlaceholderValue>> imgPlaceholders =
                ((IASDataSettingsHolder) core.settingsAPI()).imagePlaceholdersWithSessionDefaults();
        for (Map.Entry<String, String> entry : imgPlaceholderKeys.entrySet()) {
            String placeholderKey = entry.getKey();
            String placeholderName = entry.getValue();
            if (placeholderKey != null && placeholderName != null) {
                Pair<ImagePlaceholderValue, ImagePlaceholderValue> placeholderValue
                        = imgPlaceholders.get(placeholderName);
                if (placeholderValue != null) {
                    String path = "";
                    if (placeholderValue.first != null &&
                            placeholderValue.first.getType() == ImagePlaceholderType.URL) {
                        String uniqueKey = StringsUtils.md5(placeholderValue.first.getUrl());
                        File file = core.contentLoader().getCommonCache().getFullFile(uniqueKey);
                        if (file != null && file.exists() && file.length() > 0) {
                            path = "file://" + file.getAbsolutePath();
                        } else if (forced) {
                            path = "file://" + new FilePathCacheGenerator(
                                    placeholderValue.first.getUrl(),
                                    core,
                                    FilePathCacheType.STORY_RESOURCE
                            ).generate();
                        }
                    }
                    if (path.isEmpty()) {
                        if (placeholderValue.second != null &&
                                placeholderValue.second.getType() == ImagePlaceholderType.URL) {
                            String uniqueKey = StringsUtils.md5(placeholderValue.second.getUrl());
                            File file = core.contentLoader().getCommonCache().getFullFile(uniqueKey);
                            if (file != null && file.exists() && file.length() > 0) {
                                path = "file://" + file.getAbsolutePath();
                            } else if (forced) {
                                path = "file://" + new FilePathCacheGenerator(
                                        placeholderValue.second.getUrl(),
                                        core,
                                        FilePathCacheType.STORY_RESOURCE
                                ).generate();
                            }
                        }
                    }
                    for (int i = 0; i < result.length; i++) {
                        result[i] = result[i].replace(placeholderKey, path);
                    }
                }
            }
        }
        return result;
    }

    private String replaceLayoutAssets(String layout) {
        final String[] newLayout = {layout};
        List<SessionAsset> assets = core.assetsHolder().assets();
        for (final SessionAsset asset : assets) {
            if (newLayout[0].contains(asset.replaceKey)) {
                new SessionAssetLocalUseCase(
                        core,
                        new UseCaseCallback<File>() {
                            @Override
                            public void onError(String message) {

                            }

                            @Override
                            public void onSuccess(File result) {
                                newLayout[0] = newLayout[0].replace(asset.replaceKey,
                                        "file://" + result.getAbsolutePath());
                            }
                        },
                        asset
                ).getFile();
            }
        }
        return newLayout[0];
    }

    private String[] replacePlaceholders(
            String... replaceStrings
    ) {
        String[] result = new String[replaceStrings.length];
        System.arraycopy(replaceStrings, 0, result, 0, replaceStrings.length);
        Map<String, String> localPlaceholders =
                ((IASDataSettingsHolder) core.settingsAPI()).placeholders();
        for (String key : localPlaceholders.keySet()) {
            String modifiedKey = "%" + key + "%";
            String value = localPlaceholders.get(key);
            if (value != null) {
                for (int i = 0; i < result.length; i++) {
                    result[i] = result[i].replace(modifiedKey, value);
                }
            }
        }
        return result;
    }
}
