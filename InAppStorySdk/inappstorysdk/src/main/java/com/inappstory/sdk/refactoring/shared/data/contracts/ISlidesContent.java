package com.inappstory.sdk.refactoring.shared.data.contracts;

import com.inappstory.sdk.core.data.IResource;

import java.util.List;
import java.util.Map;

public interface ISlidesContent {
    int id();
    String layout();
    String slideByIndex(int index);
    List<IResource> vodResources(int index);
    List<IResource> staticResources(int index);
    List<String> placeholdersNames(int index);
    Map<String, String> placeholdersMap(int index);
    int slidesCount();
    String slideEventPayload(int slideIndex);
    int shareType(int slideIndex);
}
