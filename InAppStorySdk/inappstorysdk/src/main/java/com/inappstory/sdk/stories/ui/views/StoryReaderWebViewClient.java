package com.inappstory.sdk.stories.ui.views;


import com.inappstory.sdk.core.IASCore;

import java.io.File;


public final class StoryReaderWebViewClient extends BaseWebViewClient {

    @Override
    protected File getCachedFile(String url) {
        String filePath = IASCore.getInstance().filesRepository.getLocalStoryFile(url);
        if (filePath != null) return new File(filePath);
        return null;
    }
}