package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryListType;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.utils.SessionManager;

import java.lang.reflect.Type;
import java.util.List;

public abstract class LoadListCallback extends NetworkCallback<List<Story>> {
    @Override
    public abstract void onSuccess(List<Story> response);

    @Override
    public Type getType() {
        return new StoryListType();
    }

    @Override
    public void onError(int code, String message) {

        CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_LIST));
        super.onError(code, message);
    }

    @Override
    protected void error424(String message) {
        CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_LIST));
        SessionManager.getInstance().closeSession(true, false);
    }

}
