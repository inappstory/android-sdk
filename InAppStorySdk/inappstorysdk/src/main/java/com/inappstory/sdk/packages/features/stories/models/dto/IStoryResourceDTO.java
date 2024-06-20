package com.inappstory.sdk.packages.features.stories.models.dto;


public interface IStoryResourceDTO {
    String url();

    String key();

    boolean isVod();

    boolean isStatic();

    String filename();

    long rangeStart();

    long rangeEnd();
}
