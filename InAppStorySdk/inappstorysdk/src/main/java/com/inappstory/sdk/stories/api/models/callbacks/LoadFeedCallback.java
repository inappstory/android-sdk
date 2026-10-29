package com.inappstory.sdk.stories.api.models.callbacks;

import com.inappstory.sdk.core.network.content.models.Feed;
import com.inappstory.sdk.network.callbacks.NetworkCallback;

public abstract class LoadFeedCallback extends NetworkCallback<Feed> {

    @Override
    public abstract void onSuccess(Feed response);


}
