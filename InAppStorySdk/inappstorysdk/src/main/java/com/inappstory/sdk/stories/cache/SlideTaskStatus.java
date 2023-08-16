package com.inappstory.sdk.stories.cache;

import java.util.ArrayList;
import java.util.List;

public class SlideTaskStatus {
    int priority = 0;
    List<String> urls = new ArrayList<>();
    List<String> videoUrls = new ArrayList<>();
    int loadType = 0; //0 - not loaded, 1 - loading, 2 - loaded

    @Override
    public String toString() {
        return "SlideTask{" +
                ", loadType=" + loadType +
                '}';
    }
}