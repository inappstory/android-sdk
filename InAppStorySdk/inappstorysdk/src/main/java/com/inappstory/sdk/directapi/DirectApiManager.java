package com.inappstory.sdk.directapi;

import android.content.Context;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.directapi.adapters.ApiErrorCallback;
import com.inappstory.sdk.directapi.adapters.ApiSuccessCallback;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.utils.SessionManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DirectApiManager {
    private void checkAndApplyRequest(OpenSessionCallback openSessionCallback,
                                      ApiErrorCallback errorCallback) {
        if (InAppStoryService.isNull()) {
            errorCallback.onError();
            return;
        }
        if (!InAppStoryService.isConnected()) {
            errorCallback.onError();
            return;
        }
        SessionManager.getInstance().useOrOpenSession(openSessionCallback);
    }

    private void localCaching(String json) {
        List<Story> stories = JsonParser.listFromJson(json, Story.class);
        InAppStoryService.getInstance().getDownloadManager().uploadingAdditional(stories);
        List<Story> newStories = new ArrayList<>();
        if (InAppStoryService.getInstance().getDownloadManager().getStories() != null) {
            for (Story story : stories) {
                if (!InAppStoryService.getInstance().getDownloadManager().getStories().contains(story)) {
                    newStories.add(story);
                }
            }
        }
        if (newStories.size() > 0) {
            InAppStoryService.getInstance().getDownloadManager().uploadingAdditional(newStories);
        }
    }

    private void getStories(final ApiSuccessCallback successCallback,
                            final ApiErrorCallback errorCallback, final boolean isFavorite,
                            final boolean simple) {

        checkAndApplyRequest(new OpenSessionCallback() {
            @Override
            public void onSuccess() {
                final String loadStoriesUID = ProfilingManager.getInstance().addTask(isFavorite
                        ? "api_favorite_list" : "api_story_list");
                NetworkClient.getApi().getStories(
                        ApiSettings.getInstance().getTestKey(),
                        isFavorite ? 1 : 0,
                        InAppStoryService.getInstance().getTagsString(),
                        simple ? "id, background_color, image" : null)
                        .enqueue(new NetworkCallback<String>() {
                            @Override
                            public void onSuccess(String response) {
                                ProfilingManager.getInstance().setReady(loadStoriesUID);
                                try {
                                    localCaching(response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                successCallback.onSuccess(response);
                            }

                            @Override
                            public Type getType() {
                                return String.class;
                            }

                            @Override
                            public void onError(int code, String message) {
                                ProfilingManager.getInstance().setReady(loadStoriesUID);
                                errorCallback.onError();
                            }

                            @Override
                            public void onTimeout() {

                                ProfilingManager.getInstance().setReady(loadStoriesUID);
                                errorCallback.onError();
                            }
                        });
            }

            @Override
            public void onError() {
                errorCallback.onError();
            }
        }, errorCallback);
    }

    public void getStoriesList(ApiSuccessCallback successCallback,
                               ApiErrorCallback errorCallback) {
        getStories(successCallback, errorCallback, false, false);
    }

    private void getStoriesListFavoriteItem(ApiSuccessCallback successCallback,
                                            ApiErrorCallback errorCallback) {
        getStories(successCallback, errorCallback, true, true);
    }

    private void getStoriesFavoriteList(ApiSuccessCallback successCallback,
                                        ApiErrorCallback errorCallback) {
        getStories(successCallback, errorCallback, true, false);
    }

    public void openStoriesReader(final Context context,
                                  final String[] ids,
                                  final AppearanceManager manager,
                                  final int openFromIndex, final ApiErrorCallback errorCallback) {
        checkAndApplyRequest(new OpenSessionCallback() {
            @Override
            public void onSuccess() {
                ArrayList<Integer> idList = new ArrayList<>();
                if (ids != null) {
                    for (String id : ids) {
                        try {
                            idList.add(Integer.parseInt(id));
                        } catch (NumberFormatException e) {
                            InAppStoryManager.showDLog("IAS_Flutter", e.getMessage());
                        }
                    }
                }
                ScreensManager.getInstance().openStoriesReader(context, manager, idList,
                        openFromIndex, ShowStory.CUSTOM);
            }

            @Override
            public void onError() {
                errorCallback.onError();
            }
        }, errorCallback);

    }


    public void sendListPreviewStat(String[] ids) {
        sendPreviewStat(ids, false);
    }
    private void sendPreviewStat(String[] ids, boolean isFavoriteList) {
        ArrayList<Integer> indexes = new ArrayList<>();
        for (String id : ids) {
            try {
                indexes.add(Integer.parseInt(id));
            } catch (NumberFormatException e) {

            }
        }
        OldStatisticManager.getInstance().previewStatisticEvent(indexes);
        if (StatisticManager.getInstance() != null) {
            StatisticManager.getInstance().sendViewStory(indexes,
                    isFavoriteList ? StatisticManager.FAVORITE : StatisticManager.LIST);
        }
    }
}
