package com.inappstory.sdk.core.network.content.models;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.core.data.IReaderContentSlide;
import com.inappstory.sdk.core.data.IResource;
import com.inappstory.sdk.core.exceptions.NotImplementedMethodException;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.impl.InAppMessageBottomSheetSettings;
import com.inappstory.sdk.inappmessage.ui.appearance.impl.InAppMessageFullscreenSettings;
import com.inappstory.sdk.inappmessage.ui.appearance.impl.InAppMessagePopupSettings;
import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.inappmessage.IAMUiContainerType;

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

    @SerializedName("has_limit")
    public boolean hasLimit;

    @SerializedName("layout")
    public String layout;

    @SerializedName("frequency_limit")
    public Long frequencyLimit;

    @SerializedName("display_to")
    public Long displayTo;

    @SerializedName("display_from")
    public Long displayFrom;

    @SerializedName("slides")
    public List<InAppMessageSlide> slides;

    @SerializedName("campaign_name")
    public String campaignName;

    @SerializedName("has_swipe_up")
    public Boolean hasSwipeUp;

    @SerializedName("disable_close")
    public boolean disableClose;

    @SerializedName("type")
    public int screenType;

    @SerializedName("appearance")
    public Map<String, Object> appearance;


    @SerializedName("events")
    public List<InAppMessageEvent> events;

    @Override
    public String layout() {
        return layout;
    }

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
        return slide(index).html();
    }

    @Override
    public List<IResource> vodResources(int index) {
        return slide(index).vodResources();
    }

    @Override
    public List<IResource> staticResources(int index) {
        return slide(index).staticResources();
    }

    @Override
    public List<String> placeholdersNames(int index) {
        return slide(index).placeholdersNames();
    }

    @Override
    public Map<String, String> placeholdersMap(int index) {
        return slide(index).placeholdersMap();
    }

    @Override
    public int actualSlidesCount() {
        return slides != null ? slides.size() : 0;
    }

    @Override
    public String slideEventPayload(int index) {
        return slide(index).slidePayload();
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
        return actualSlidesCount();
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
    public IAMUiContainerType screenType() {
        switch (screenType) {
            case 2:
                return IAMUiContainerType.POPUP;
            case 3:
                return IAMUiContainerType.FULLSCREEN;
            default:
                return IAMUiContainerType.BOTTOM_SHEET;
        }
    }

    @Override
    public InAppMessageAppearance inAppMessageAppearance() {
        switch (screenType) {
            case 2:
                return new InAppMessagePopupSettings(appearance, disableClose);
            case 3:
                return new InAppMessageFullscreenSettings(appearance, disableClose);
            default:
                return new InAppMessageBottomSheetSettings(appearance, disableClose);
        }
    }

    @Override
    public boolean belongsToEvent(@NonNull String eventToCompare) {
        if (events != null) {
            for (InAppMessageEvent event : events) {
                if (Objects.equals(event.name, eventToCompare)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getEventPriority(@NonNull String eventToCompare) {
        if (events != null) {
            for (InAppMessageEvent event : events) {
                if (Objects.equals(event.name, eventToCompare)) {
                    return event.priority;
                }
            }
        }
        return -1;
    }
}
