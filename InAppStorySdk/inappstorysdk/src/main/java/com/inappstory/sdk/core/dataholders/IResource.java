package com.inappstory.sdk.core.dataholders;

public interface IResource {
    String getType();

    String getUrl();

    int getIndex();

    String getKey();

    String getFileName();

    String getPurpose();

    long getRangeStart();

    long getRangeEnd();
}
