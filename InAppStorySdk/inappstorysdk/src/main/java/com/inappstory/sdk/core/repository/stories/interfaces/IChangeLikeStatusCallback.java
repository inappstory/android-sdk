package com.inappstory.sdk.core.repository.stories.interfaces;

import com.inappstory.sdk.core.repository.session.interfaces.NetworkErrorCallback;

public interface IChangeLikeStatusCallback extends NetworkErrorCallback {
    void like(int storyId);
    void dislike(int storyId);
    void clear(int storyId);
}
