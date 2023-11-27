package com.inappstory.sdk.stories.utils;


import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.Pair;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderType;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.cache.Downloader;

import java.io.File;
import java.io.IOException;
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

    /*  public void replaceImagesAndLoad(String innerWebData, Story story, final int index, String layout,
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
  */

    private String replaceResources(String innerWebData, Story story, final int index, LruDiskCache cache) throws IOException {
        List<String> resourceKeys = new ArrayList<>();
        resourceKeys.addAll(story.getSrcListKeys(index, null));
        resourceKeys.addAll(story.getSrcListKeys(index, "video"));
        List<String> resourceUrls = new ArrayList<>();
        resourceUrls.addAll(story.getSrcListUrls(index, null));
        resourceUrls.addAll(story.getSrcListUrls(index, "video"));
        for (int i = 0; i < resourceKeys.size(); i++) {
            String resource = resourceUrls.get(i);
            String resourceKey = resourceKeys.get(i);
            String key = Downloader.deleteQueryArgumentsFromUrlOld(resource, true);
            File file = Downloader.updateFile(cache.getFullFile(key), resource, cache, key);
            if (file != null && file.exists() && file.length() > 0) {
                resource = "file://" + file.getAbsolutePath();
            }
            innerWebData = innerWebData.replace(resourceKey, resource);
        }
        return innerWebData;
    }

    private String replaceImagePlaceholders(String innerWebData, Story story, final int index, LruDiskCache cache) throws IOException {
        Map<String, Pair<ImagePlaceholderValue, ImagePlaceholderValue>> imgPlaceholders =
                InAppStoryService.getInstance().getImagePlaceholdersValuesWithDefaults();
        Map<String, String> imgPlaceholderKeys = story.getPlaceholdersList(index, "image-placeholder");
        for (Map.Entry<String, String> entry : imgPlaceholderKeys.entrySet()) {
            String placeholderKey = entry.getKey();
            String placeholderName = entry.getValue();
            if (placeholderKey != null && placeholderName != null) {
                Pair<ImagePlaceholderValue, ImagePlaceholderValue> placeholderValue
                        = imgPlaceholders.get(placeholderName);
                if (placeholderValue != null) {
                    String path = "";
                    if (placeholderValue.first.getType() == ImagePlaceholderType.URL) {
                        File file = cache.getFullFile(
                                Downloader.deleteQueryArgumentsFromUrlOld(placeholderValue.first.getUrl(), true)
                        );
                        if (file != null && file.exists() && file.length() > 0) {
                            path = "file://" + file.getAbsolutePath();
                        } else {
                            if (placeholderValue.second.getType() == ImagePlaceholderType.URL) {
                                file = cache.getFullFile(
                                        Downloader.deleteQueryArgumentsFromUrlOld(placeholderValue.second.getUrl(), true)
                                );
                                if (file != null && file.exists() && file.length() > 0) {
                                    path = "file://" + file.getAbsolutePath();
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

    public void replaceEmptyAndLoad(int index, String layout,
                                    WebPageConvertCallback callback) {
        try {
            String wData = layout
                    .replace("//_ratio = 0.66666666666,", "")
                    .replace("{{%content}}", "");
            callback.onConvert("", wData, index);
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
        }
    }

    private Pair<String, String> replacePlaceholders(String outerData, String outerLayout) {
        String tmpData = outerData;
        String tmpLayout = outerLayout;
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null) {
            Map<String, String> localPlaceholders = service.getPlaceholders();
            for (String key : localPlaceholders.keySet()) {
                String modifiedKey = "%" + key + "%";
                String value = localPlaceholders.get(key);
                if (value != null) {
                    tmpData = tmpData.replace(modifiedKey, value);
                    tmpLayout = tmpLayout.replace(modifiedKey, value);
                }
            }
        }
        return new Pair<>(tmpData, tmpLayout);
    }

    public void replaceDataAndLoad(String innerWebData, Story story, int index, String layout,
                                   WebPageConvertCallback callback) throws IOException {
        String localData = innerWebData;
        String newLayout = layout;
        if (InAppStoryService.isNotNull()) {
            LruDiskCache cache = InAppStoryService.getInstance().getCommonCache();
            localData = replaceResources(localData, story, index, cache);
            localData = replaceImagePlaceholders(localData, story, index, cache);
            Pair<String, String> replaced = replacePlaceholders(localData, newLayout);
            newLayout = replaced.second;
            localData = replaced.first;
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
            String wData = newLayout
                    .replace("//_ratio = 0.66666666666,", "")
                    .replace("{{%content}}", localData);
            callback.onConvert(localData, wData, index);
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
        }
    }
}
