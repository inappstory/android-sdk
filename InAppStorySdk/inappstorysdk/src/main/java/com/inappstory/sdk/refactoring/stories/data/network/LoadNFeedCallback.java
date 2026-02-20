package com.inappstory.sdk.refactoring.stories.data.network;

import com.inappstory.sdk.network.callbacks.NetworkCallback;

import java.lang.reflect.Type;

public abstract class LoadNFeedCallback extends NetworkCallback<NFeed> {

    @Override
    public abstract void onSuccess(NFeed response);

    @Override
    public Type getType() {
        return NFeed.class;
    }


}
