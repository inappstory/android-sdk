package com.inappstory.sdk.core.repository.stories.usecase;

import android.util.Pair;

import com.inappstory.sdk.core.IASCoreManager;
import com.inappstory.sdk.core.network.ApiSettings;
import com.inappstory.sdk.core.network.NetworkClient;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.core.repository.session.interfaces.IGetSessionCallback;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.PreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetFeedCallback;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.LoadListCallback;
import com.inappstory.sdk.stories.statistic.ProfilingManager;

import java.util.ArrayList;
import java.util.List;

public class GetFavoriteStoryList {

    public GetFavoriteStoryList() {
    }


    public void get(final IGetFeedCallback callback) {
        final NetworkClient networkClient = IASCoreManager.getInstance().getNetworkClient();
        if (networkClient == null) {
            callback.onError();
            return;
        }
        IASCoreManager.getInstance().getSession(
                new IGetSessionCallback<SessionDTO>() {
                    @Override
                    public void onSuccess(SessionDTO session) {
                        final String loadStoriesUID =
                                ProfilingManager.getInstance().addTask("api_favorite_list");
                        networkClient.enqueue(
                                networkClient.getApi().getStories(
                                        ApiSettings.getInstance().getTestKey(),
                                        1,
                                        null,
                                        null
                                ),
                                new LoadListCallback() {
                                    @Override
                                    public void onSuccess(List<Story> response) {
                                        if (response == null) {
                                            callback.onError();
                                        } else {
                                            ProfilingManager.getInstance().setReady(loadStoriesUID);
                                            List<IPreviewStoryDTO> previews = new ArrayList<>();
                                            for (Story story : response) {
                                                previews.add(new PreviewStoryDTO(story));
                                            }
                                            callback.onSuccess(
                                                    new Pair<>(
                                                            previews,
                                                            false
                                                    )
                                            );
                                        }
                                    }

                                    @Override
                                    public void errorDefault(String message) {
                                        ProfilingManager.getInstance().setReady(loadStoriesUID);
                                        callback.onError();
                                    }


                                    @Override
                                    public void error424(String message) {
                                        ProfilingManager.getInstance().setReady(loadStoriesUID);
                                        callback.onError();
                                        IASCoreManager.getInstance().closeSession();
                                        get(callback);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError() {
                        callback.onError();
                    }
                }
        );
    }
}
