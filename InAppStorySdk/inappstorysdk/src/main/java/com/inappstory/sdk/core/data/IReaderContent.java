package com.inappstory.sdk.core.data;

import java.util.List;
import java.util.Map;

public interface IReaderContent extends IStatData, IContentWithStatus {
    String layout();
    String slideByIndex(int index);
    List<IResource> vodResources(int index);
    List<IResource> staticResources(int index);
    List<String> placeholdersNames(int index);
    Map<String, String> placeholdersMap(int index);
    int actualSlidesCount();
    String slideEventPayload(int slideIndex);
    boolean checkIfEmpty();
    int shareType(int slideIndex);
}
