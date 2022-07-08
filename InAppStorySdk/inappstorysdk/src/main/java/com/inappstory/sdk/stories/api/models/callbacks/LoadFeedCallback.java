package com.inappstory.sdk.stories.api.models.callbacks;

import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.stories.api.models.Feed;

import java.lang.reflect.Type;

public abstract class LoadFeedCallback extends NetworkCallback<Feed> {

    @Override
    public abstract void onSuccess(Feed response);

    @Override
    public Type getType() {
        return Feed.class;
    }


}
