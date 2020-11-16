package io.casestory.sdk.stories.utils;

import android.content.Context;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import io.casestory.sdk.CaseStoryManager;
import io.casestory.sdk.eventbus.CsEventBus;
import io.casestory.sdk.stories.cache.FileCache;
import io.casestory.sdk.stories.cache.FileType;
import io.casestory.sdk.stories.cache.StoryDownloader;
import io.casestory.sdk.stories.serviceevents.GeneratedWebPageEvent;

public class WebPageConverter {
    public static void replaceImagesAndLoad(String innerWebData, final int storyId, final int index, String layout) {
        boolean exists = false;
        List<String> imgs = StoryDownloader.getInstance().getStoryById(storyId).getSrcListUrls(index, null);
        List<String> imgKeys = StoryDownloader.getInstance().getStoryById(storyId).getSrcListKeys(index, null);
        for (int i = 0; i < imgs.size(); i++) {
            String img = imgs.get(i);
            String imgKey = imgKeys.get(i);
            Context con = CaseStoryManager.getInstance().getContext();
            FileCache cache = FileCache.INSTANCE;
            File file = cache.getStoredFile(con, img, FileType.STORY_IMAGE, storyId, null);
            if (file.exists()) {
                exists = true;
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
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String webData = layout
                .replace("//_ratio = 0.66666666666,", "")
                .replace("{{%content}}", innerWebData);
        CsEventBus.getDefault().post(new GeneratedWebPageEvent(webData, storyId));
        return;
    }

    public static void replaceVideoAndLoad(String innerWebData, final int storyId, final int index, String layout) {
        List<String> imgs = StoryDownloader.getInstance().getStoryById(storyId).getSrcListUrls(index, "video");
        List<String> imgKeys = StoryDownloader.getInstance().getStoryById(storyId).getSrcListKeys(index, "video");
        for (int i = 0; i < imgs.size(); i++) {
            String img = imgs.get(i);
            String imgKey = imgKeys.get(i);
            innerWebData = innerWebData.replace(imgKey, img);
        }
        String webData = layout
                .replace("//_ratio = 0.66666666666,", "")
                .replace("{{%content}}", innerWebData);
        CsEventBus.getDefault().post(new GeneratedWebPageEvent(webData, storyId));
        return;
    }

    public static void replaceEmptyAndLoad(String innerWebData, final int storyId, final int index, String layout) {
        String webData = layout
                .replace("//_ratio = 0.66666666666,", "")
                .replace("{{%content}}", innerWebData)
                .replace("window.Android.storyLoaded", "window.Android.emptyLoaded");
        CsEventBus.getDefault().post(new GeneratedWebPageEvent(webData, storyId));
        return;
    }

}
