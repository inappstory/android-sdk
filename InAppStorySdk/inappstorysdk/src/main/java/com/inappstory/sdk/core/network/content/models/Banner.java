package com.inappstory.sdk.core.network.content.models;


import com.inappstory.sdk.core.banners.BannerAppearance;
import com.inappstory.sdk.core.banners.IBannerAppearance;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.core.data.IReaderContentSlide;
import com.inappstory.sdk.core.data.IResource;
import com.inappstory.sdk.core.exceptions.NotImplementedMethodException;
import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Banner implements IBanner {
    @Required
    @SerializedName(ModelFields.Banner.id)
    public int id;

    @SerializedName(ModelFields.Banner.hasLimit)
    public boolean hasLimit;

    @SerializedName(ModelFields.Banner.frequencyLimit)
    public Long frequencyLimit;

    @SerializedName(ModelFields.Banner.displayTo)
    public Long displayTo;

    @SerializedName(ModelFields.Banner.displayFrom)
    public Long displayFrom;

    @SerializedName(ModelFields.Banner.slides)
    public List<BannerSlide> slides;

    @SerializedName(ModelFields.Banner.hasSwipeUp)
    public Boolean hasSwipeUp;

    @SerializedName(ModelFields.Banner.disableClose)
    public boolean disableClose;

    @SerializedName(ModelFields.Banner.appearance)
    public Map<String, Object> appearance;

    @SerializedName(ModelFields.Banner.layoutTemplateVariables)
    public Map<String, String> layoutTemplateVariables;


    @SerializedName(ModelFields.Banner.customAttributes)
    public Map<String, String> customAttributes;

    private IReaderContentSlide slide(int index) {
        if (slides == null || index < 0 || slides.size() < index)
            throw new RuntimeException("Slide index out of bounds: " + index + " from " + slides.size());
        for (IReaderContentSlide slide : slides) {
            if (slide.index() == index) return slide;
        }
        throw new RuntimeException("Slide " + index + " not in slides array of inAppMessage " + id());
    }

    @Override
    public String slideByIndex(int index) {
        return slide(0).html();
    }

    @Override
    public List<IResource> vodResources(int index) {
        return slide(0).vodResources();
    }

    @Override
    public List<IResource> staticResources(int index) {
        return slide(0).staticResources();
    }

    @Override
    public List<String> placeholdersNames(int index) {
        return slide(0).placeholdersNames();
    }

    @Override
    public Map<String, String> layoutTemplateVariables() {
        if (layoutTemplateVariables == null) layoutTemplateVariables = new HashMap<>();
        return layoutTemplateVariables;
    }

    @Override
    public Map<String, String> placeholdersMap(int index) {
        return slide(0).placeholdersMap();
    }

    @Override
    public int actualSlidesCount() {
        return slides != null ? slides.size() : 0;
    }

    @Override
    public String slideEventPayload(int index) {
        return slide(0).slidePayload();
    }

    @Override
    public boolean checkIfEmpty() {
        return (slides == null ||
                slides.isEmpty() ||
                layoutTemplateVariables == null ||
                layoutTemplateVariables.isEmpty());
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
        throw new NotImplementedMethodException();
    }


    @Override
    public Map<String, String> customAttributes() {
        return customAttributes;
    }

    @Override
    public int slidesCount() {
        return 1;
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
        throw new NotImplementedMethodException();
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
    public boolean hasLimit() {
        return hasLimit;
    }

    @Override
    public long frequencyLimit() {
        return frequencyLimit != null ? frequencyLimit * 1000 : -1L;
    }

    @Override
    public long displayFrom() {
        return displayFrom != null ? (displayFrom * 1000) : -1;
    }

    @Override
    public long displayTo() {
        return displayTo != null ? (displayTo * 1000) : -1;
    }

    @Override
    public IBannerAppearance bannerAppearance() {
        return new BannerAppearance(appearance);
    }
}
