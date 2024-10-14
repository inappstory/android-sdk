package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.stories.api.interfaces.IResourceObject;
import com.inappstory.sdk.stories.api.models.ResourceMappingObject;

import java.util.ArrayList;
import java.util.List;

public class SlideTask {
    int priority = 0;

    public List<IResourceObject> staticResources() {
        return staticResources;
    }

    public List<UrlWithAlter> urlsWithAlter() {
        return urlsWithAlter;
    }

    public List<IResourceObject> vodResources() {
        return vodResources;
    }

    List<IResourceObject> staticResources = new ArrayList<>();
    List<UrlWithAlter> urlsWithAlter = new ArrayList<>();
    List<IResourceObject> vodResources = new ArrayList<>();
    int loadType = 0; //-1 - error, 0 - not loaded, 1 - loading, 2 - loaded

    public SlideTask(
            List<IResourceObject> staticResources,
            List<IResourceObject> vodResources,
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
                '}';
    }
}