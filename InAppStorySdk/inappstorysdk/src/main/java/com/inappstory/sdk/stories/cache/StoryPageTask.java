package com.inappstory.sdk.stories.cache;

import java.util.ArrayList;
import java.util.List;

public class StoryPageTask {
    public int priority = 0;
    public List<String> urls = new ArrayList<>();
    public List<String> urlKeys = new ArrayList<>();
    public List<String> videoUrls = new ArrayList<>();
    public int loadType = 0; //0 - not loaded, 1 - loading, 2 - loaded
}