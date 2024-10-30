package com.inappstory.sdk.core.network.content.models;

import static com.inappstory.sdk.stories.api.models.ResourceMapping.VOD;

import com.inappstory.sdk.core.dataholders.models.IInAppMessage;
import com.inappstory.sdk.core.dataholders.models.IResource;
import com.inappstory.sdk.core.exceptions.NotImplementedMethodException;
import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.stories.api.models.PayloadObject;
import com.inappstory.sdk.stories.api.models.ResourceMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InAppMessage implements IInAppMessage {
    @Required
    @SerializedName("id")
    public int id;

    @SerializedName("has_placeholder")
    public boolean hasPlaceholders;

    @SerializedName("slides_html")
    public List<String> slides;

    @SerializedName("layout")
    public String layout;

    @SerializedName("frequency_limit_day")
    public int dayLimit;

    @SerializedName("src_list")
    public List<ResourceMapping> srcList;

    @SerializedName("img_placeholder_src_list")
    public List<ResourceMapping> imagePlaceholdersList;

    @SerializedName("campaign_name")
    public String campaignName;

    @SerializedName("tags")
    public String tags;

    @SerializedName("slides_payload")
    public List<PayloadObject> slidesPayload;

    @SerializedName("has_swipe_up")
    public Boolean hasSwipeUp;

    @SerializedName("disable_close")
    public boolean disableClose;

    @SerializedName("slides_count")
    public int slidesCount;


    @Override
    public String layout() {
        return layout;
    }

    @Override
    public String slideByIndex(int index) {
        if (index < 0 || slides.size() < index)
            throw new RuntimeException("Slide index out of bounds: " + index + " from " + slides.size());
        return slides.get(index);
    }

    @Override
    public List<IResource> vodResources(int index) {
        List<IResource> res = new ArrayList<>();
        if (srcList == null) return res;
        for (IResource object : srcList) {
            if (Objects.equals(VOD, object.getPurpose()) && object.getIndex() == index) {
                res.add(object);
            }
        }
        return res;
    }

    @Override
    public List<IResource> staticResources(int index) {
        List<IResource> res = new ArrayList<>();
        if (srcList == null) return res;
        for (IResource object : srcList) {
            if (!Objects.equals(VOD, object.getPurpose()) && object.getIndex() == index) {
                res.add(object);
            }
        }
        return res;
    }

    @Override
    public List<String> placeholdersNames(int index) {
        List<String> res = new ArrayList<>();
        if (imagePlaceholdersList == null) return res;
        for (IResource object : imagePlaceholdersList) {
            if (object.getIndex() == index && (object.getType().equals("image-placeholder"))) {
                String name = object.getUrl();
                if (name != null) res.add(name);
            }

        }
        return res;
    }

    @Override
    public Map<String, String> placeholdersMap(int index) {
        Map<String, String> res = new HashMap<>();
        if (imagePlaceholdersList == null) return res;
        for (IResource object : imagePlaceholdersList) {
            if (object.getIndex() == index && (object.getType().equals("image-placeholder"))) {
                res.put(object.getKey(), object.getUrl());
            }
        }
        return res;
    }

    @Override
    public int actualSlidesCount() {
        if (slides != null)
            return slides.size();
        return 0;
    }

    @Override
    public String slideEventPayload(int slideIndex) {
        if (slidesPayload == null) return null;
        for (PayloadObject payloadObject : slidesPayload) {
            if (slideIndex == payloadObject.slideIndex) {
                return payloadObject.getPayload();
            }
        }
        return null;
    }

    @Override
    public boolean checkIfEmpty() {
        return (layout == null || slides == null || slides.isEmpty());
    }

    @Override
    public int shareType(int slideIndex) {
        throw new NotImplementedMethodException();
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String statTitle() {
        return campaignName;
    }

    @Override
    public int slidesCount() {
        return slidesCount;
    }

    @Override
    public String tags() {
        return tags;
    }

    @Override
    public Map<String, Object> ugcPayload() {
        throw new NotImplementedMethodException();
    }

    @Override
    public boolean hasFavorite() {
        throw new NotImplementedMethodException();
    }

    @Override
    public boolean hasLike() {
        throw new NotImplementedMethodException();
    }

    @Override
    public boolean hasShare() {
        throw new NotImplementedMethodException();
    }

    @Override
    public boolean hasAudio() {
        throw new NotImplementedMethodException();
    }

    @Override
    public boolean favorite() {
        throw new NotImplementedMethodException();
    }

    @Override
    public void like(int like) {
        throw new NotImplementedMethodException();
    }

    @Override
    public void favorite(boolean favorite) {
        throw new NotImplementedMethodException();
    }

    @Override
    public int like() {
        throw new NotImplementedMethodException();
    }

    @Override
    public boolean hasSwipeUp() {
        return hasSwipeUp;
    }

    @Override
    public boolean disableClose() {
        return disableClose;
    }

    @Override
    public boolean isOpened() {
        throw new NotImplementedMethodException();
    }

    @Override
    public void setOpened(boolean isOpened) {
        throw new NotImplementedMethodException();
    }

    @Override
    public boolean hasPlaceholders() {
        return hasPlaceholders;
    }

    @Override
    public int dayLimit() {
        return dayLimit;
    }
}
