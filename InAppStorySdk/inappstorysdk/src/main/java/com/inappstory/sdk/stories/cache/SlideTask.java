package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.core.dataholders.models.IResource;

import java.util.ArrayList;
import java.util.List;

public class SlideTask {
    int priority = 0;

    public List<IResource> staticResources() {
        return staticResources;
    }

    public List<UrlWithAlter> urlsWithAlter() {
        return urlsWithAlter;
    }

    public List<IResource> vodResources() {
        return vodResources;
    }

    List<IResource> staticResources = new ArrayList<>();
    List<UrlWithAlter> urlsWithAlter = new ArrayList<>();
    List<IResource> vodResources = new ArrayList<>();
    int loadType = 0; //-1 - error, 0 - not loaded, 1 - loading, 2 - loaded

    public SlideTask(
            List<IResource> staticResources,
            List<IResource> vodResources,
            List<UrlWithAlter> urlsWithAlter
    ) {
        this.staticResources = staticResources;
        this.urlsWithAlter = urlsWithAlter;
        this.vodResources = vodResources;
    }

    @Override
    public String toString() {
        return "SlideTask{" +
                ", loadType=" + loadType +
                ", staticResources=" + staticResources.size() +
                ", urlsWithAlter=" + urlsWithAlter.size() +
                ", vodResources=" + vodResources.size() +
                '}';
    }
}