package com.inappstory.sdk.core.repository.stories.usecase;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.network.NetworkClient;
import com.inappstory.sdk.core.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.network.models.Response;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.core.repository.session.interfaces.IGetSessionDTOCallbackAdapter;
import com.inappstory.sdk.core.repository.stories.interfaces.IRemoveAllStoriesFromFavoritesCallback;
import com.inappstory.sdk.stories.statistic.ProfilingManager;

import java.lang.reflect.Type;

public class RemoveAllStoriesFromFavorites {

    public RemoveAllStoriesFromFavorites() {
    }

    public void remove(final IRemoveAllStoriesFromFavoritesCallback callback) {
        final NetworkClient networkClient = IASCore.getInstance().getNetworkClient();
        if (networkClient == null) {
            callback.onError();
            return;
        }
        IASCore.getInstance().getSession(new IGetSessionDTOCallbackAdapter(callback) {
            @Override
            public void onSuccess(SessionDTO response) {
                final String favUID = ProfilingManager.getInstance().addTask("api_favorite_remove_all");
                networkClient.enqueue(
                        networkClient.getApi().removeAllFavorites(),
                        new NetworkCallback<Response>() {
                            @Override
                            public void onSuccess(Response response) {
                                ProfilingManager.getInstance().setReady(favUID);
                                callback.onRemove();
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
