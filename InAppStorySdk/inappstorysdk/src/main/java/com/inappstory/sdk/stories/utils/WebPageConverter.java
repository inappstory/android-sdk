package com.inappstory.sdk.stories.utils;


import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderType;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.api.models.ResourceMappingObject;
import com.inappstory.sdk.stories.api.models.SessionAsset;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.cache.usecases.SessionAssetLocalUseCase;
import com.inappstory.sdk.stories.cache.vod.VODCacheJournal;
import com.inappstory.sdk.utils.StringsUtils;

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

    private String replaceStaticResources(String innerWebData, Story story, final int index, LruDiskCache cache) throws IOException {
        List<ResourceMappingObject> resources = new ArrayList<>();
        resources.addAll(story.staticResources(index));
        for (ResourceMappingObject object : resources) {
            String resource = object.getUrl();
            String resourceKey = object.getKey();
            String key = StringsUtils.md5(resource);
            File file = cache.getFullFile(key);
            if (file != null && file.exists() && file.length() > 0) {
                resource = "file://" + file.getAbsolutePath();
            }
            innerWebData = innerWebData.replace(resourceKey, resource);
        }
        return innerWebData;
    }

    private String replaceLayoutAssets(String layout) {
        final String[] newLayout = {layout};
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                List<SessionAsset> assets = service.getSession().getSessionAssets();
                for (final SessionAsset asset : assets) {
                    if (newLayout[0].contains(asset.replaceKey)) {
                        new SessionAssetLocalUseCase(
                                service.getFilesDownloadManager(),
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
            }
        });
        return newLayout[0];
    }

    private String replaceImagePlaceholders(String innerWebData,
                                            final Story story,
                                            final int index,
                                            final LruDiskCache cache
    ) throws IOException {
        final String[] newData = {innerWebData};
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                Map<String, Pair<ImagePlaceholderValue, ImagePlaceholderValue>> imgPlaceholders =
                        service.getImagePlaceholdersValuesWithDefaults();
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
                                String uniqueKey = StringsUtils.md5(placeholderValue.first.getUrl());
                                File file = cache.getFullFile(uniqueKey);
                                if (file != null && file.exists() && file.length() > 0) {
                                    path = "file://" + file.getAbsolutePath();
                                } else {
                                    if (placeholderValue.second.getType() == ImagePlaceholderType.URL) {
                                        uniqueKey = StringsUtils.md5(placeholderValue.second.getUrl());
                                        file = cache.getFullFile(uniqueKey);
                                        if (file != null && file.exists() && file.length() > 0) {
                                            path = "file://" + file.getAbsolutePath();
                                        }
                                    }
                                }
                            }
                            newData[0] = newData[0].replace(placeholderKey, path);
                        }
                    }
                }
            }
        });

        return newData[0];
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
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null) {
            LruDiskCache cache = service.getCommonCache();
            localData = replaceStaticResources(localData, story, index, cache);
            service.addVODResources(story, index);
            localData = replaceImagePlaceholders(localData, story, index, cache);
            newLayout = replaceLayoutAssets(layout);
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
