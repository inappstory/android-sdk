package com.inappstory.sdk.flutter;

import android.content.Context;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.flutter.adapters.ErrorCallback;
import com.inappstory.sdk.flutter.adapters.SuccessCallback;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.utils.SessionManager;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FlutterManager {
    private void checkAndApplyRequest(OpenSessionCallback openSessionCallback,
                                      ErrorCallback errorCallback) {
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

    public void getStoriesList(final SuccessCallback successCallback,
                               final ErrorCallback errorCallback) {
        checkAndApplyRequest(new OpenSessionCallback() {
            @Override
            public void onSuccess() {
                NetworkClient.getApi().getStories(
                        ApiSettings.getInstance().getTestKey(),
                        0,
                        InAppStoryService.getInstance().getTagsString(),
                        null)
                        .enqueue(new NetworkCallback<String>() {
                            @Override
                            public void onSuccess(String response) {
                                successCallback.onSuccess(response);
                            }

                            @Override
                            public Type getType() {
                                return String.class;
                            }

                            @Override
                            public void onError(int code, String message) {
                                errorCallback.onError();
                            }

                            @Override
                            public void onTimeout() {
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

    public void openStoriesReader(final Context context,
                                  final String[] ids,
                                  final AppearanceManager manager,
                                  final int openFromIndex, final ErrorCallback errorCallback) {
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
}
