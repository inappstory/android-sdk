package com.inappstory.sdk.core.repository.stories.usecase;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.utils.network.NetworkClient;
import com.inappstory.sdk.core.utils.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.utils.network.models.Response;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.core.repository.session.interfaces.IGetSessionDTOCallbackAdapter;
import com.inappstory.sdk.core.repository.stories.interfaces.IChangeLikeStatusCallback;
import com.inappstory.sdk.core.repository.statistic.ProfilingManager;

import java.lang.reflect.Type;

public class ChangeStoryLikeStatus {
    private int storyId;
    private int newValue;

    public ChangeStoryLikeStatus(int storyId, boolean likeClicked, boolean initialStatus) {
        this.storyId = storyId;
        if (initialStatus) newValue = 0;
        else if (likeClicked) newValue = 1;
        else newValue = -1;
    }

    public void changeStatus(final IChangeLikeStatusCallback callback) {
        final NetworkClient networkClient = IASCore.getInstance().getNetworkClient();
        if (networkClient == null) {
            callback.onError();
            return;
        }
        IASCore.getInstance().getSession(new IGetSessionDTOCallbackAdapter(callback) {
            @Override
            public void onSuccess(SessionDTO response) {
                final String favUID = ProfilingManager.getInstance().addTask("api_like");
                networkClient.enqueue(
                        networkClient.getApi().storyLike(
                                Integer.toString(storyId),
                                newValue
                        ),
                        new NetworkCallback<Response>() {
                            @Override
                            public void onSuccess(Response response) {
                                ProfilingManager.getInstance().setReady(favUID);
                                switch (newValue) {
                                    case -1:
                                        callback.dislike(storyId);
                                        break;
                                    case 1:
                                        callback.like(storyId);
                                        break;
                                    default:
                                        callback.clear(storyId);
                                        break;
                                }
                            }


                            @Override
                            public void errorDefault(String message) {
                                ProfilingManager.getInstance().setReady(favUID);
                                if (callback != null)
                                    callback.onError();
                            }

                            @Override
                            public Type getType() {
                                return null;
                            }
                        }
                );
            }
        });
    }
}
