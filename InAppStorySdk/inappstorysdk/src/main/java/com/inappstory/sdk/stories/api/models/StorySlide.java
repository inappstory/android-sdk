package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.List;

public class StorySlide {
    @SerializedName("index")
    public int slideIndex;
    @SerializedName("event_payload")
    public PayloadObject payloadObject;
    @SerializedName("duration")
    public int duration;
    @SerializedName("html")
    public String slideContent;
    @SerializedName("timeline")
    public StoryTimelineSettings timelineSettings;
    @SerializedName("resources")
    public List<ResourceMappingObject> resources;
    @SerializedName("img_placeholders_resources")
    public List<ImagePlaceholderMappingObject> placeholderResources;
    @SerializedName("screenshot_share")
    public boolean isScreenshotShare;

    @Override
    public String toString() {
        return "StorySlide{" +
                "slideIndex=" + slideIndex +
                ", resources=" + resources +
                ", placeholderResources=" + placeholderResources +
                '}';
    }
}
