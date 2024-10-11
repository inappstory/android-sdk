package com.inappstory.sdk.stories.api.models;

import static com.inappstory.sdk.stories.api.models.ResourceMappingObject.VOD;

import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.stories.api.interfaces.IResourceObject;

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
    public List<ResourceMappingObject> srcList;

    @SerializedName("img_placeholder_src_list")
    public List<ResourceMappingObject> imagePlaceholdersList;

    @SerializedName("campaign_name")
    public String campaignName;

    

    @Override
    public InAppMessage copy() {
        return null;
    }

    @Override
    public InAppMessage mergedCopy(InAppMessage comparedObject) {
        return null;
    }

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
    public List<IResourceObject> vodResources(int index) {
        List<IResourceObject> res = new ArrayList<>();
        if (srcList == null) return res;
        for (IResourceObject object : srcList) {
            if (Objects.equals(VOD, object.getPurpose()) && object.getIndex() == index) {
                res.add(object);
            }
        }
        return res;
    }

    @Override
    public List<IResourceObject> staticResources(int index) {
        List<IResourceObject> res = new ArrayList<>();
        if (srcList == null) return res;
        for (IResourceObject object : srcList) {
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
        for (IResourceObject object : imagePlaceholdersList) {
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
        for (IResourceObject object : imagePlaceholdersList) {
            if (object.getIndex() == index && (object.getType().equals("image-placeholder"))) {
                res.put(object.getKey(), object.getUrl());
            }
        }
        return res;
    }

    @Override
    public int id() {
        return id;
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
