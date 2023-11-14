package com.inappstory.sdk.core.models.callbacks;

import com.inappstory.sdk.core.utils.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.models.api.Feed;

import java.lang.reflect.Type;

public abstract class LoadFeedCallback extends NetworkCallback<Feed> {

    @Override
    public abstract void onSuccess(Feed response);

    @Override
    public Type getType() {
        return Feed.class;
    }


}
