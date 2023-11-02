package com.inappstory.sdk.core.repository.stories.usecase;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCoreManager;
import com.inappstory.sdk.core.network.NetworkClient;
import com.inappstory.sdk.core.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.network.models.Response;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.core.repository.session.interfaces.IGetSessionCallback;
import com.inappstory.sdk.core.repository.session.interfaces.IGetSessionDTOCallbackAdapter;
import com.inappstory.sdk.core.repository.stories.interfaces.IChangeFavoriteStatusCallback;
import com.inappstory.sdk.stories.statistic.ProfilingManager;

import java.lang.reflect.Type;

public class ChangeStoryFavoriteStatus {
    private int storyId;
    private boolean initialStatus;

    public ChangeStoryFavoriteStatus(int storyId, boolean initialStatus) {
        this.storyId = storyId;
        this.initialStatus = initialStatus;
    }

    public void changeStatus(final IChangeFavoriteStatusCallback callback) {
        final NetworkClient networkClient = IASCoreManager.getInstance().getNetworkClient();
        if (networkClient == null) {
            callback.onError();
            return;
        }
        IASCoreManager.getInstance().getSession(new IGetSessionDTOCallbackAdapter(callback) {
            @Override
            public void onSuccess(SessionDTO response) {
                final String favUID = ProfilingManager.getInstance().addTask("api_favorite");
                networkClient.enqueue(
                        networkClient.getApi().storyFavorite(
                                Integer.toString(storyId),
                                initialStatus ? 0 : 1
                        ),
                        new NetworkCallback<Response>() {
                            @Override
                            public void onSuccess(Response response) {
                                ProfilingManager.getInstance().setReady(favUID);
                                if (!initialStatus) {
                                    callback.addedToFavorite(storyId);
                                } else {
                                    callback.removedFromFavorite(storyId);
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
