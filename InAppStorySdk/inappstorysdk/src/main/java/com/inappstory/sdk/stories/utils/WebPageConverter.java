package com.inappstory.sdk.stories.utils;


import android.util.Log;
import android.util.Pair;


import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.ImagePlaceholderMappingObjectDTO;
import com.inappstory.sdk.core.repository.stories.dto.ResourceMappingObjectDTO;
import com.inappstory.sdk.core.models.ImagePlaceholderType;
import com.inappstory.sdk.core.models.ImagePlaceholderValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebPageConverter {

    private String replaceResources(String innerWebData, IStoryDTO story, final int index) {
        List<ResourceMappingObjectDTO> resources = new ArrayList<>(story.getSrcList(index));
        for (ResourceMappingObjectDTO resourceDTO : resources) {
            String resource = resourceDTO.getUrl();
            String resourceKey = resourceDTO.getKey();
            String path = IASCore.getInstance().filesRepository.getLocalStoryFile(resourceDTO.getUrl());
            if (path != null) {
                resource = "file://" + path;
            }
            innerWebData = innerWebData.replace(resourceKey, resource);
        }
        return innerWebData;
    }

    private String replaceImagePlaceholders(String innerWebData, IStoryDTO story, final int index) {
        Map<String, Pair<ImagePlaceholderValue, ImagePlaceholderValue>> imgPlaceholders =
                new HashMap<>(IASCore.getInstance().getImagePlaceholdersValuesWithDefaults());
        List<ImagePlaceholderMappingObjectDTO> replaceable = story.getImagePlaceholdersList(index);
        for (ImagePlaceholderMappingObjectDTO entry : replaceable) {
            String placeholderKey = entry.getKey();
            String placeholderName = entry.getUrl();
            if (placeholderKey != null && placeholderName != null) {
                Pair<ImagePlaceholderValue, ImagePlaceholderValue> placeholderValue
                        = imgPlaceholders.get(placeholderName);
                if (placeholderValue != null) {
                    String path = "";
                    if (placeholderValue.first.getType() == ImagePlaceholderType.URL) {
                        String filePath =
                                IASCore.getInstance().filesRepository.getLocalStoryFile(
                                        placeholderValue.first.getUrl()
                                );
                        if (filePath != null) {
                            path = "file://" + filePath;
                        } else {
                            if (placeholderValue.second.getType() == ImagePlaceholderType.URL) {
                                filePath =
                                        IASCore.getInstance().filesRepository.getLocalStoryFile(
                                                placeholderValue.second.getUrl()
                                        );
                                if (filePath != null) {
                                    path = "file://" + filePath;
                                }
                            }
                        }
                    }
                    innerWebData = innerWebData.replace(placeholderKey, path);
                }
            }
        }
        Log.e("WebData", innerWebData);
        return innerWebData;
    }

    private Pair<String, String> replacePlaceholders(String outerData, String outerLayout) {
        String tmpData = outerData;
        String tmpLayout = outerLayout;
        Map<String, String> localPlaceholders = IASCore.getInstance().getPlaceholders();
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

    public void replaceDataAndLoad(IStoryDTO story, int index,
                                   WebPageConvertCallback callback) {

        String content = story.getPages().get(index);
        String layout = story.getLayout();
        content = replaceResources(content, story, index);
        content = replaceImagePlaceholders(content, story, index);
        Pair<String, String> replaced = replacePlaceholders(content, layout);
        layout = replaced.second;
        content = replaced.first;

        try {
            String wData = layout
                    .replace("//_ratio = 0.66666666666,", "")
                    .replace("{{%content}}", content);
            callback.onConvert(content, wData, index);
        } catch (Exception e) {
        }
    }
}
