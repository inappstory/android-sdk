package com.inappstory.sdk.stories.api.models;

import static com.inappstory.sdk.stories.api.models.ResourceMapping.VOD;

import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.core.dataholders.IResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InAppMessageImpl implements InAppMessage {
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
    public List<Integer> slidesShare() {
        return null;
    }

    @Override
    public String slideEventPayload(int slideIndex) {
        return null;
    }

    @Override
    public boolean checkIfEmpty() {
        return false;
    }

    @Override
    public int shareType(int slideIndex) {
        return 0;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String statTitle() {
        return null;
    }

    @Override
    public int slidesCount() {
        return 0;
    }

    @Override
    public String tags() {
        return null;
    }

    @Override
    public Map<String, Object> ugcPayload() {
        return null;
    }

    @Override
    public boolean hasFavorite() {
        return false;
    }

    @Override
    public boolean hasLike() {
        return false;
    }

    @Override
    public boolean hasShare() {
        return false;
    }

    @Override
    public boolean hasAudio() {
        return false;
    }

    @Override
    public boolean favorite() {
        return false;
    }

    @Override
    public void like(int like) {

    }

    @Override
    public void favorite(boolean favorite) {

    }

    @Override
    public int like() {
        return 0;
    }

    @Override
    public boolean hasSwipeUp() {
        return false;
    }

    @Override
    public boolean disableClose() {
        return false;
    }

    @Override
    public boolean isOpened() {
        return false;
    }

    @Override
    public void setOpened(boolean isOpened) {

    }

    @Override
    public String campaignName() {
        return campaignName;
    }

    @Override
    public boolean hasPlaceholder() {
        return false;
    }

    @Override
    public int dayLimit() {
        return dayLimit;
    }
}
