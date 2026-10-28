package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.core.data.IResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    Set<String> assetKeys = null;
    boolean forced = false;
    int loadType = 0; //-1 - error, 0 - not loaded, 1 - loading, 2 - loaded

    public SlideTask forced(boolean forced) {
        this.forced = forced;
        return this;
    }

    public SlideTask(
            List<IResource> staticResources,
            List<IResource> vodResources,
            List<UrlWithAlter> urlsWithAlter,
            Set<String> assetKeys
    ) {
        this.staticResources = staticResources;
        this.urlsWithAlter = urlsWithAlter;
        this.vodResources = vodResources;
        this.assetKeys = assetKeys;
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