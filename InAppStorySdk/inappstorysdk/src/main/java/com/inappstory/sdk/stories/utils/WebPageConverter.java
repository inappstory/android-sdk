package com.inappstory.sdk.stories.utils;


import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.core.data.IResource;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderType;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.stories.cache.usecases.SessionAssetLocalUseCase;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebPageConverter {
    public Spanned fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    private String replaceStaticResources(
            IASCore core,
            String innerWebData,
            IReaderContent story,
            final int index
    ) {
        List<IResource> resources = new ArrayList<>();
        resources.addAll(story.staticResources(index));
        for (IResource object : resources) {
            String resource = object.getUrl();
            String resourceKey = object.getKey();
            String key = StringsUtils.md5(resource);
            File file = core.contentLoader().getCommonCache().getFullFile(key);
            if (file != null && file.exists() && file.length() > 0) {
                resource = "file://" + file.getAbsolutePath();
            }
            innerWebData = innerWebData.replace(resourceKey, resource);
        }
        return innerWebData;
    }

    private String replaceLayoutAssets(IASCore core, String layout) {
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

    private String replaceImagePlaceholders(IASCore core,
                                            String innerWebData,
                                            final IReaderContent readerContent,
                                            final int index
    ) {
        final String[] newData = {innerWebData};
        Map<String, Pair<ImagePlaceholderValue, ImagePlaceholderValue>> imgPlaceholders =
                ((IASDataSettingsHolder) core.settingsAPI()).imagePlaceholdersWithSessionDefaults();
        Map<String, String> imgPlaceholderKeys = readerContent.placeholdersMap(index);
        for (Map.Entry<String, String> entry : imgPlaceholderKeys.entrySet()) {
            String placeholderKey = entry.getKey();
            String placeholderName = entry.getValue();
            if (placeholderKey != null && placeholderName != null) {
                Pair<ImagePlaceholderValue, ImagePlaceholderValue> placeholderValue
                        = imgPlaceholders.get(placeholderName);
                if (placeholderValue != null) {
                    String path = "";
                    if (placeholderValue.first != null && placeholderValue.first.getType() == ImagePlaceholderType.URL) {
                        String uniqueKey = StringsUtils.md5(placeholderValue.first.getUrl());
                        File file = core.contentLoader().getCommonCache().getFullFile(uniqueKey);
                        if (file != null && file.exists() && file.length() > 0) {
                            path = "file://" + file.getAbsolutePath();
                        }
                    }
                    if (path.isEmpty()) {
                        if (placeholderValue.second != null && placeholderValue.second.getType() == ImagePlaceholderType.URL) {
                            String uniqueKey = StringsUtils.md5(placeholderValue.second.getUrl());
                            File file = core.contentLoader().getCommonCache().getFullFile(uniqueKey);
                            if (file != null && file.exists() && file.length() > 0) {
                                path = "file://" + file.getAbsolutePath();
                            }
                        }
                    }
                    newData[0] = newData[0].replace(placeholderKey, path);
                }
            }
        }

        return newData[0];
    }


    private Pair<String, String> replacePlaceholders(
            IASCore core,
            String outerData,
            String outerLayout
    ) {
        String tmpData = outerData;
        String tmpLayout = outerLayout;
        Map<String, String> localPlaceholders =
                ((IASDataSettingsHolder) core.settingsAPI()).placeholders();
        for (String key : localPlaceholders.keySet()) {
            String modifiedKey = "%" + key + "%";
            String value = localPlaceholders.get(key);
            if (value != null) {
                tmpData = tmpData.replace(modifiedKey, value);
                tmpLayout = tmpLayout.replace(modifiedKey, value);
            }
        }
        return new Pair<>(tmpData, tmpLayout);
    }

    public void replaceDataAndLoad(
            final String innerWebData,
            final IReaderContent readerContent,
            final int index,
            final WebPageConvertCallback callback
    ) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                String localData = innerWebData;
                String newLayout = readerContent.layout();
                localData = replaceStaticResources(core, localData, readerContent, index);
                core.contentLoader().addVODResources(readerContent, index);
                localData = replaceImagePlaceholders(core, localData, readerContent, index);
                newLayout = replaceLayoutAssets(core, newLayout);
                Pair<String, String> replaced = replacePlaceholders(core, localData, newLayout);
                newLayout = replaced.second;
                localData = replaced.first;
                try {
                    String wData = newLayout
                            .replace("//_ratio = 0.66666666666,", "")
                            .replace("{{%content}}", localData);
                    callback.onConvert(localData, wData, index);
                } catch (Exception e) {
                    core.exceptionManager().createExceptionLog(e);
                }
            }
        });

    }
}
