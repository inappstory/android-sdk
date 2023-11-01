package com.inappstory.sdk.core.repository.stories.usecase;

import android.util.Pair;

import com.inappstory.sdk.core.IASCoreManager;
import com.inappstory.sdk.core.network.NetworkClient;
import com.inappstory.sdk.core.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.core.repository.session.interfaces.IGetSessionCallback;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.PreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.StoryDTO;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetStoryCallback;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.statistic.ProfilingManager;

import java.lang.reflect.Type;

public class GetStoryById {

    public GetStoryById(String storyId) {
        this.storyId = storyId;
    }

    final String storyId;
    final String EXPAND_STRING = "slides_html,slides_structure,layout,slides_duration,src_list,img_placeholder_src_list,slides_screenshot_share,slides_payload";

    public void get(final IGetStoryCallback<Pair<com.inappstory.sdk.core.repository.stories.dto.IStoryDTO, IPreviewStoryDTO>> callback) {
        final NetworkClient networkClient = IASCoreManager.getInstance().getNetworkClient();
        if (networkClient == null) {
            callback.onError();
            return;
        }
        IASCoreManager.getInstance().getSession(
                new IGetSessionCallback<SessionDTO>() {
                    @Override
                    public void onSuccess(SessionDTO session) {
                        final String storyUID = ProfilingManager.getInstance().addTask("api_story");
                        networkClient.enqueue(
                                networkClient.getApi().getStoryById(
                                        storyId,
                                        1,
                                        EXPAND_STRING
                                ),
                                new NetworkCallback<Story>() {
                                    @Override
                                    public void onSuccess(final Story response) {
                                        ProfilingManager.getInstance().setReady(storyUID);
                                        callback.onSuccess(new Pair<com.inappstory.sdk.core.repository.stories.dto.IStoryDTO, IPreviewStoryDTO>(
                                                new StoryDTO(response),
                                                new PreviewStoryDTO(response)
                                        ));
                                    }

                                    @Override
                                    public Type getType() {
                                        return Story.class;
                                    }

                                    @Override
                                    public void errorDefault(String message) {
                                        ProfilingManager.getInstance().setReady(storyUID);
                                        callback.onError();
                                    }
                                });
                    }

                    @Override
                    public void onError() {
                        callback.onError();
                    }
                }
        );
    }
}
