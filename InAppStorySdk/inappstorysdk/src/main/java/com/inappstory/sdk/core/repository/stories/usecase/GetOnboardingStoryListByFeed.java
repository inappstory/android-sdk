package com.inappstory.sdk.core.repository.stories.usecase;


import android.util.Pair;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.utils.network.NetworkClient;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.core.repository.session.interfaces.IGetSessionCallback;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.PreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetFeedCallback;
import com.inappstory.sdk.core.models.api.Feed;
import com.inappstory.sdk.core.models.api.Story;
import com.inappstory.sdk.core.models.callbacks.LoadFeedCallback;
import com.inappstory.sdk.core.repository.statistic.ProfilingManager;

import java.util.ArrayList;
import java.util.List;

public class GetOnboardingStoryListByFeed {

    public GetOnboardingStoryListByFeed(String feed, Integer limit, String tags) {
        this.feed = feed;
        this.tags = tags;
        this.limit = limit;
    }

    final String feed;
    final Integer limit;
    final String tags;

    public void get(final IGetFeedCallback callback) {
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
                                ProfilingManager.getInstance().addTask("api_onboarding");
                        networkClient.enqueue(
                                networkClient.getApi().getOnboardingFeed(
                                        feed,
                                        limit,
                                        tags
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
