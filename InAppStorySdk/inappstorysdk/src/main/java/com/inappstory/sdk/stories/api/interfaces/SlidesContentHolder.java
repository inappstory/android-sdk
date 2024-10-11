package com.inappstory.sdk.stories.api.interfaces;

import java.util.List;
import java.util.Map;

public interface SlidesContentHolder {
    String layout();
    String slideByIndex(int index);
    List<IResourceObject> vodResources(int index);
    List<IResourceObject> staticResources(int index);
    List<String> placeholdersNames(int index);
    Map<String, String> placeholdersMap(int index);
}
