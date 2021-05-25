package com.inappstory.sdk.stories.cache;

import java.util.ArrayList;
import java.util.List;

public class StoryPageTask {
    int priority = 0;
    List<String> urls = new ArrayList<>();
    public List<String> urlKeys = new ArrayList<>();
    List<String> videoUrls = new ArrayList<>();
    int loadType = 0; //0 - not loaded, 1 - loading, 2 - loaded
}