package com.inappstory.sdk.core.dataholders;

import java.util.List;
import java.util.Map;

public interface IReaderContent extends IStatData {
    String layout();
    String slideByIndex(int index);
    List<IResource> vodResources(int index);
    List<IResource> staticResources(int index);
    List<String> placeholdersNames(int index);
    Map<String, String> placeholdersMap(int index);
    int actualSlidesCount();
    List<Integer> slidesShare();
    boolean hasSwipeUp();
    String slideEventPayload(int slideIndex);
    boolean checkIfEmpty();
    boolean disableClose();
    int shareType(int slideIndex);
}
