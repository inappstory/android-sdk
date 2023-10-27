package com.inappstory.sdk.core.repository.stories.usecase;

import android.util.Pair;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCoreManager;
import com.inappstory.sdk.core.network.ApiSettings;
import com.inappstory.sdk.core.network.NetworkClient;
import com.inappstory.sdk.core.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.core.repository.session.interfaces.IGetSessionCallback;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.PreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.StoryDTO;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetFeedCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetStoriesPreviewsCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetStoryCallback;
import com.inappstory.sdk.stories.api.models.Feed;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.LoadFeedCallback;
import com.inappstory.sdk.stories.statistic.ProfilingManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetStoryListByFeed {

    public GetStoryListByFeed(String feed) {
        this.feed = feed;
    }

    final String feed;

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
                        final String loadStoriesUID = ProfilingManager.getInstance().addTask("api_story_list");
                        networkClient.enqueue(
                                networkClient.getApi().getFeed(
                                        feed,
                                        ApiSettings.getInstance().getTestKey(),
                                        0,
                                        InAppStoryService.getInstance().getTagsString(),
                                        null
                                ),
                                new LoadFeedCallback() {
                                    @Override
                                    public void onSuccess(Feed response) {
                                        if (response == null) {
                                            callback.onError();
                                        } else {
                                            ProfilingManager.getInstance().setReady(loadStoriesUID);
                                            List<IPreviewStoryDTO> previews = new ArrayList<>();
                                            for (Story story : response.getStories()) {
                                                previews.add(new PreviewStoryDTO(story));
                                            }
                                            callback.onSuccess(
                                                    new Pair<>(
                                                            previews,
                                                            response.hasFavorite()
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
