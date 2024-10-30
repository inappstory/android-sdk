package com.inappstory.sdk.stories.api.models;


import com.inappstory.sdk.core.network.content.models.StoryPlaceholder;

import java.util.List;

public class CachedSessionData {
    public String userId;
    public float previewAspectRatio;
    public String tags;
    public List<StoryPlaceholder> placeholders;
    public String sessionId;
    public String testKey;
    public String token;
    public boolean isAllowUGC;
}
