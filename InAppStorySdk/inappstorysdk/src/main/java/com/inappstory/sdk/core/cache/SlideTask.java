package com.inappstory.sdk.core.cache;

import com.inappstory.sdk.core.repository.stories.dto.ResourceMappingObjectDTO;

import java.util.ArrayList;
import java.util.List;

public class SlideTask {
    int priority = 0;
    List<ResourceMappingObjectDTO> resources = new ArrayList<>();
    List<UrlWithAlter> placeholders = new ArrayList<>();
    int loadType = 0; //0 - not loaded, 1 - loading, 2 - loaded

    @Override
    public String toString() {
        return "SlideTask{" +
                ", loadType=" + loadType +
                '}';
    }
}