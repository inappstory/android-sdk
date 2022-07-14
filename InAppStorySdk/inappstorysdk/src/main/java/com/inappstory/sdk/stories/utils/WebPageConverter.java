package com.inappstory.sdk.stories.utils;


import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.api.models.Story;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    public void replaceImagesAndLoad(String innerWebData, Story story, final int index, String layout,
                                     WebPageConvertCallback callback) throws IOException {
        List<String> imgs = story.getSrcListUrls(index, null);
        List<String> imgKeys = story.getSrcListKeys(index, null);
        for (int i = 0; i < imgs.size(); i++) {
            String img = imgs.get(i);
            String imgKey = imgKeys.get(i);
            File file = InAppStoryService.getInstance().getCommonCache().get(img);
            if (file != null) {
                FileInputStream fis = null;
                try {

                    if (file.length() > 0) {
                        fis = new FileInputStream(file);
                        byte[] imageRaw = new byte[(int) file.length()];
                        fis.read(imageRaw);
                        String cType = KeyValueStorage.getString(file.getName());
                        String image64;
                        if (cType != null)
                            image64 = "data:" + cType + ";base64," + Base64.encodeToString(imageRaw, Base64.DEFAULT);
                        else
                            image64 = "data:image/jpeg;base64," + Base64.encodeToString(imageRaw, Base64.DEFAULT);
                        fis.close();
                        innerWebData = innerWebData.replace(imgKey, image64);
                    } else {
                        innerWebData = innerWebData.replace(imgKey, img);
                    }
                } catch (Exception e) {
                    InAppStoryService.createExceptionLog(e);
                }
            }
        }
        try {
            String wData = layout
                    .replace("//_ratio = 0.66666666666,", "")
                    .replace("{{%content}}", innerWebData);
            callback.onConvert(innerWebData, wData, index);
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
        }
    }


    public void replaceVideoAndLoad(String innerWebData, Story story, final int index, String layout,
                                    WebPageConvertCallback callback) throws IOException {
        List<String> videos = story.getSrcListUrls(index, "video");
        List<String> videosKeys = story.getSrcListKeys(index, "video");
        LruDiskCache cache = InAppStoryService.getInstance().getCommonCache();
        for (int i = 0; i < videos.size(); i++) {
            String video = videos.get(i);
            String videoKey = videosKeys.get(i);
            File file = cache.get(video);
            if (file != null && file.exists() && file.length() > 0) {
                video = "file://" + file.getAbsolutePath();
            }
            innerWebData = innerWebData.replace(videoKey, video);
        }

        List<String> imgs = story.getSrcListUrls(index, null);
        List<String> imgKeys = story.getSrcListKeys(index, null);

        for (int i = 0; i < imgs.size(); i++) {
            String video = imgs.get(i);
            String videoKey = imgKeys.get(i);
            File file = cache.get(video);
            if (file != null && file.exists() && file.length() > 0) {
                video = "file://" + file.getAbsolutePath();
            }
            innerWebData = innerWebData.replace(videoKey, video);
        }
        if (InAppStoryService.isNotNull()) {
            Map<String, ImagePlaceholderValue> imgPlaceholders =
                    InAppStoryService.getInstance().getImagePlaceholders();
            List<String> imgPlaceholderKeys = story.getSrcListKeys(index, "image-placeholder");

            for (int i = 0; i < imgPlaceholderKeys.size(); i++) {
                String placeholderKey = imgPlaceholderKeys.get(i);
                String placeholderName = PlaceholderKeyConverter.getPlaceholderNameFromKey(placeholderKey);
                ImagePlaceholderValue placeholderValue = imgPlaceholders.get(placeholderName);
                if (placeholderValue != null) {
                    String path = "";
                    switch (placeholderValue.getType()) {
                        case URL:
                            File file = cache.get(placeholderValue.getUrl());
                            if (file != null && file.exists() && file.length() > 0) {
                                path = "file://" + file.getAbsolutePath();
                            }
                            break;
                        default:
                            break;
                    }
                    innerWebData = innerWebData.replace(placeholderKey, path);
                }

            }
        }

        /*for (int i = 0; i < imgs.size(); i++) {
            String img = imgs.get(i);
            String imgKey = imgKeys.get(i);
            File file = cache.get(img);
            if (file != null && file.exists() && file.length() > 0) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    byte[] imageRaw = new byte[(int) file.length()];
                    fis.read(imageRaw);
                    String cType = KeyValueStorage.getString(file.getName());
                    String image64;
                    if (cType != null)
                        image64 = "data:" + cType + ";base64," + Base64.encodeToString(imageRaw, Base64.DEFAULT);
                    else
                        image64 = "data:image/jpeg;base64," + Base64.encodeToString(imageRaw, Base64.DEFAULT);
                    fis.close();
                    innerWebData = innerWebData.replace(imgKey, image64);
                } catch (Exception e) {
                    InAppStoryService.createExceptionLog(e);
                }
            }
        }*/
        try {
            String wData = layout
                    .replace("//_ratio = 0.66666666666,", "")
                    .replace("{{%content}}", innerWebData);
            callback.onConvert(innerWebData, wData, index);
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
        }
    }
}
