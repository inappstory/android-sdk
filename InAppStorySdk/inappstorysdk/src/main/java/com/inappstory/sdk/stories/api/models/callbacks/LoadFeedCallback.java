package com.inappstory.sdk.stories.api.models.callbacks;

import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.network.content.models.Feed;

import java.lang.reflect.Type;

public abstract class LoadFeedCallback extends NetworkCallback<Feed> {

    @Override
    public abstract void onSuccess(Feed response);

    @Override
    public Type getType() {
        return Feed.class;
    }


}
