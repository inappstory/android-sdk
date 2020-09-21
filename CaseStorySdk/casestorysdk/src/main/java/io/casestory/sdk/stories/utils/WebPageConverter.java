package io.casestory.sdk.stories.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import io.casestory.sdk.CaseStoryManager;
import io.casestory.sdk.eventbus.EventBus;
import io.casestory.sdk.stories.cache.FileCache;
import io.casestory.sdk.stories.cache.FileType;
import io.casestory.sdk.stories.cache.HtmlParser;
import io.casestory.sdk.stories.serviceevents.GeneratedWebPageEvent;

public class WebPageConverter {
    public static void replaceImagesAndLoad(String innerWebData, final int storyId, String layout) {
        boolean exists = false;
        List<String> imgs = HtmlParser.getSrcUrls(innerWebData);

        for (String img : imgs) {
            Context con = CaseStoryManager.getInstance().getContext();
            FileCache cache = FileCache.INSTANCE;
            File file = cache.getStoredFile(con, img, FileType.STORY_IMAGE, storyId, null);

            Log.d("LoadEvents", "loadImage " + img);
            if (file.exists()) {
                exists = true;
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    byte[] imageRaw = new byte[(int) file.length()];
                    fis.read(imageRaw);
                    String image64 = "data:image/jpeg;base64," + Base64.encodeToString(imageRaw, Base64.DEFAULT);
                    fis.close();
                    innerWebData = innerWebData.replace(img.replace("&", "&amp;"), image64);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (exists) {
            String webData = layout
                    .replace("//_ratio = 0.66666666666,", "")
                    .replace("{{%content}}", innerWebData);
            EventBus.getDefault().post(new GeneratedWebPageEvent(webData, storyId));
            return;
        }
    }

    public static void replaceVideoAndLoad(String innerWebData, final int storyId, String layout) {
        boolean exists = false;
        List<String> imgs = HtmlParser.getSrcUrls(innerWebData);
        for (String img : imgs) {
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
                    String image64 = "data:image/jpeg;base64," + Base64.encodeToString(imageRaw, Base64.DEFAULT);
                    fis.close();
                    innerWebData = innerWebData.replace(img.replace("&", "&amp;"), image64);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (exists) {
            String webData = layout
                    .replace("//_ratio = 0.66666666666,", "")
                    .replace("{{%content}}", innerWebData);
            EventBus.getDefault().post(new GeneratedWebPageEvent(webData, storyId));
            return;
        }
    }

}
