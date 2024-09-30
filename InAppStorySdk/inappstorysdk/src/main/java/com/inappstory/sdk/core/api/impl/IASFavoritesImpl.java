package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASFavorites;

public class IASFavoritesImpl implements IASFavorites {
    private final IASCore core;

    public IASFavoritesImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void removeAll() {

    }

    @Override
    public void removeByStoryId(int storyId) {

    }
}
