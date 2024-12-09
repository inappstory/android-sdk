package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.stories.api.models.ResourceMappingObject;

import java.util.ArrayList;
import java.util.List;

public class SlideTask {
    int priority = 0;
    List<ResourceMappingObject> staticResources = new ArrayList<>();
    List<UrlWithAlter> urlsWithAlter = new ArrayList<>();
    List<ResourceMappingObject> vodResources = new ArrayList<>();
    int loadType = 0; //0 - not loaded, 1 - loading, 2 - loaded

    @Override
    public String toString() {
        return "SlideTask{" +
                "priority=" + priority +
                ", staticResources=" + staticResources +
                ", urlsWithAlter=" + urlsWithAlter +
                ", vodResources=" + vodResources +
                ", loadType=" + loadType +
                '}';
    }
}