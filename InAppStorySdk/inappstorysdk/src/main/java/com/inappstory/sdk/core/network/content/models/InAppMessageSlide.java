package com.inappstory.sdk.core.network.content.models;

import static com.inappstory.sdk.core.network.content.models.ContentResource.VOD;
import static com.inappstory.sdk.core.network.content.models.StorySlideTimeline.DEFAULT_TIMELINE_BACKGROUND_COLOR;
import static com.inappstory.sdk.core.network.content.models.StorySlideTimeline.DEFAULT_TIMELINE_FOREGROUND_COLOR;

import com.inappstory.sdk.core.data.IReaderContentSlide;
import com.inappstory.sdk.core.data.IResource;
import com.inappstory.sdk.core.data.ISlideTimeline;
import com.inappstory.sdk.core.exceptions.NotImplementedMethodException;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.stories.api.models.SlidePayload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InAppMessageSlide implements IReaderContentSlide {
    @SerializedName("slide_index")
    public int slideIndex;
    @SerializedName("event_payload")
    public SlidePayload slidePayload;
    @SerializedName("duration")
    public int duration;
    @SerializedName("html")
    public String html;
    @SerializedName("resources")
    public List<ContentResource> resources;
    @SerializedName("img_placeholders_resources")
    public List<ContentResource> placeholders;

    @Override
    public ISlideTimeline slideTimeline() {
        throw new NotImplementedMethodException();
    }

    @Override
    public int index() {
        return slideIndex;
    }

    @Override
    public String slidePayload() {
        return slidePayload != null ? slidePayload.payload() : null;
    }

    @Override
    public int duration() {
        return duration;
    }

    @Override
    public String html() {
        return html != null ? html : "";
    }

    @Override
    public List<IResource> staticResources() {
        List<IResource> res = new ArrayList<>();
        List<IResource> input = new ArrayList<>();
        if (resources != null) input.addAll(resources);
        for (IResource object : input) {
            if (!Objects.equals(VOD, object.getPurpose())) {
                res.add(object);
            }
        }
        return res;
    }

    @Override
    public List<IResource> vodResources() {
        List<IResource> res = new ArrayList<>();
        List<IResource> input = new ArrayList<>();
        if (resources != null) input.addAll(resources);
        for (IResource object : input) {
            if (Objects.equals(VOD, object.getPurpose())) {
                res.add(object);
            }
        }
        return res;
    }

    @Override
    public List<String> placeholdersNames() {
        List<String> res = new ArrayList<>();
        List<IResource> input = new ArrayList<>();
        if (placeholders != null) input.addAll(placeholders);
        for (IResource object : input) {
            if (object.getType().equals("image-placeholder")) {
                String name = object.getUrl();
                if (name != null) res.add(name);
            }

        }
        return res;
    }

    @Override
    public Map<String, String> placeholdersMap() {
        Map<String, String> res = new HashMap<>();
        List<IResource> input = new ArrayList<>();
        if (placeholders != null) input.addAll(placeholders);
        for (IResource object : input) {
            if (object.getType().equals("image-placeholder")) {
                res.put(object.getKey(), object.getUrl());
            }
        }
        return res;
    }

    @Override
    public int shareType() {
        throw new NotImplementedMethodException();
    }

    @Override
    public String timelineForegroundColor() {
        throw new NotImplementedMethodException();
    }

    @Override
    public String timelineBackgroundColor() {
        throw new NotImplementedMethodException();
    }

    @Override
    public boolean isTimelineHidden() {
        throw new NotImplementedMethodException();
    }
}
