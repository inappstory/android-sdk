package com.inappstory.sdk.core.repository.stories.usecase;


import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.utils.network.ApiSettings;
import com.inappstory.sdk.core.utils.network.NetworkClient;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.core.repository.session.interfaces.IGetSessionCallback;
import com.inappstory.sdk.core.repository.stories.dto.FavoritePreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.IFavoritePreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetFavoritePreviewsCallback;
import com.inappstory.sdk.core.models.api.Story;
import com.inappstory.sdk.core.models.callbacks.LoadListCallback;
import com.inappstory.sdk.core.repository.statistic.ProfilingManager;

import java.util.ArrayList;
import java.util.List;

public class GetFavoriteStoryPreviews {

    public GetFavoriteStoryPreviews() {
    }


    public void get(final IGetFavoritePreviewsCallback callback) {
        final NetworkClient networkClient = IASCore.getInstance().getNetworkClient();
        if (networkClient == null) {
            callback.onError();
            return;
        }
        IASCore.getInstance().getSession(
                new IGetSessionCallback<SessionDTO>() {
                    @Override
                    public void onSuccess(SessionDTO session) {
                        final String loadStoriesUID =
                                ProfilingManager.getInstance().addTask("api_favorite_cell");
                        networkClient.enqueue(
                                networkClient.getApi().getStories(
                                        ApiSettings.getInstance().getTestKey(),
                                        1,
                                        null,
                                        "id, background_color, image"
                                ),
                                new LoadListCallback() {
                                    @Override
                                    public void onSuccess(List<Story> response) {
                                        if (response == null) {
                                            callback.onError();
                                        } else {
                                            ProfilingManager.getInstance().setReady(loadStoriesUID);
                                            List<IFavoritePreviewStoryDTO> previews = new ArrayList<>();
                                            for (Story story : response) {
                                                previews.add(new FavoritePreviewStoryDTO(story));
                                            }
                                            callback.onSuccess(previews);
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
                                        IASCore.getInstance().closeSession();
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
