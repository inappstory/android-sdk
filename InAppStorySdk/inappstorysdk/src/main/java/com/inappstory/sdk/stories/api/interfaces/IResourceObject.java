package com.inappstory.sdk.stories.api.interfaces;

public interface IResourceObject {
    String getType();

    String getUrl();

    int getIndex();

    String getKey();

    String getFileName();

    String getPurpose();

    long getRangeStart();

    long getRangeEnd();
}
