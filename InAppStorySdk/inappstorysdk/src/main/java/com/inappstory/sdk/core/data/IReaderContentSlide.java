package com.inappstory.sdk.core.data;

import java.util.List;
import java.util.Map;

public interface IReaderContentSlide extends ISlideTimeline {
    ISlideTimeline slideTimeline();
    int index();
    String slidePayload();
    int duration();
    String html();
    List<IResource> staticResources();
    List<IResource> vodResources();
    List<String> placeholdersNames();
    Map<String, String> placeholdersMap();
    int shareType();
}
