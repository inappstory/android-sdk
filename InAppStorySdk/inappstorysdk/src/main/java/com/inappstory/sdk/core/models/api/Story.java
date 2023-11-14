package com.inappstory.sdk.core.models.api;


import com.inappstory.sdk.core.models.api.slidestructure.SlideStructure;
import com.inappstory.sdk.core.utils.network.annotations.models.Required;
import com.inappstory.sdk.core.utils.network.annotations.models.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Paperrose on 08.07.2018.
 */


public class Story {
    @Required
    public int id;

    public enum StoryType {
        COMMON, UGC
    }

    public static StoryType storyTypeFromName(String storyType) {
        if (storyType.equals(StoryType.UGC.name())) {
            return StoryType.UGC;
        }
        return StoryType.COMMON;
    }

    public static String nameFromStoryType(StoryType storyType) {
        return storyType.name();
    }

    public String getTitle() {
        return title;
    }

    public String getSlideEventPayload(int slideIndex) {
        if (slidesPayload == null) return null;
        for (PayloadObject payloadObject : slidesPayload) {
            if (slideIndex == payloadObject.slideIndex) {
                return payloadObject.getPayload();
            }
        }
        return null;
    }


    public List<Image> getImage() {
        return image;
    }

    public Image getProperImage(int quality) {
        return Image.getProperImage(image, quality);
    }

    public boolean isOpened() {
        return isOpened;
    }

    public List<Integer> getDurations() {
        return durations;
    }

    public List<String> getPages() {
        return pages;
    }

    public String getLayout() {
        return layout;
    }


    public String getTitleColor() {
        return titleColor;
    }

    @SerializedName("title_color")
    public String titleColor;

    @SerializedName("stat_title")
    public String statTitle;

    @SerializedName("updated_at")
    public Long updatedAt;

    @SerializedName("video_cover")
    public List<Image> videoUrl;

    public Long getUpdatedAt() {
        return updatedAt != null ? updatedAt : 0L;
    }

    public List<PayloadObject> getSlidesPayload() {
        if (slidesPayload == null) return new ArrayList<>();
        return slidesPayload;
    }

    @SerializedName("slides_payload")
    public List<PayloadObject> slidesPayload;


    @SerializedName("payload")
    public HashMap<String, Object> payload;


    public String getVideoUrl() {
        return (videoUrl != null && !videoUrl.isEmpty()) ? videoUrl.get(0).getUrl() : null;
    }

    /**
     * Последний открытый слайд
     */
    public int lastIndex;

    public String title;

    public String tags;


    public String source;


    @SerializedName("background_color")
    public String backgroundColor;

    @SerializedName("image")
    public List<Image> image;


    public boolean hasSwipeUp() {
        return hasSwipeUp != null ? hasSwipeUp : false;
    }

    @SerializedName("has_swipe_up")
    public Boolean hasSwipeUp;

    @SerializedName("src_list")
    public List<ResourceMappingObject> srcList;

    @SerializedName("img_placeholder_src_list")
    public List<ImagePlaceholderMappingObject> imagePlaceholdersList;


    @SerializedName("like")
    public Integer like;

    @SerializedName("slides_screenshot_share")
    public List<Integer> slidesShare;

    public List<Integer> getSlidesShare() {
        if (slidesShare == null) {
            slidesShare = new ArrayList<>();
        }
        return slidesShare;
    }

    public int getSlidesCount() {
        if (slidesCount == 0 && durations != null) return durations.size();
        return slidesCount;
    }

    public void setSlidesCount(int slidesCount) {
        this.slidesCount = slidesCount;
    }

    @SerializedName("slides_count")
    public int slidesCount;

    public boolean isFavorite() {
        return favorite;
    }

    @SerializedName("favorite")
    public boolean favorite;


    @SerializedName("hide_in_reader")
    public Boolean hideInReader;

    public String getDeeplink() {
        return deeplink;
    }

    public String getGameInstanceId() {
        if (gameInstance != null)
            return gameInstance.id;
        return null;
    }

    @SerializedName("deeplink")
    public String deeplink;

    @SerializedName("game_instance")
    public GameInstance gameInstance;

    @SerializedName("is_opened")
    public boolean isOpened;

    @SerializedName("disable_close")
    public boolean disableClose;

    public String getBackgroundColor() {
        if (backgroundColor == null) return "#FFFFFF";
        return backgroundColor;
    }


    public List<ResourceMappingObject> getSrcList() {
        if (srcList == null) srcList = new ArrayList<>();
        return srcList;
    }

    public List<ImagePlaceholderMappingObject> getImagePlaceholdersList() {
        if (imagePlaceholdersList == null) imagePlaceholdersList = new ArrayList<>();
        return imagePlaceholdersList;
    }

    public int getLike() {
        return like != null ? like : 0;
    }

    public boolean isHideInReader() {
        return hideInReader != null && hideInReader;
    }


    public Boolean hasLike() {
        return hasLike != null ? hasLike : false;
    }

    public Boolean hasFavorite() {
        return hasFavorite != null ? hasFavorite : false;
    }

    public Boolean hasShare() {
        return hasShare != null ? hasShare : false;
    }

    public Boolean hasAudio() {
        return hasAudio != null ? hasAudio : false;
    }

    @SerializedName("like_functional")
    public Boolean hasLike;

    @SerializedName("has_audio")
    public Boolean hasAudio;

    @SerializedName("favorite_functional")
    public Boolean hasFavorite;

    @SerializedName("share_functional")
    public Boolean hasShare;

    @SerializedName("slides_duration")
    public List<Integer> durations;

    @SerializedName("slides_html")
    public List<String> pages;

    @SerializedName("slides_structure")
    public List<SlideStructure> slidesStructure;

    @SerializedName("layout")
    public String layout;

    public Story() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Story) {
            return id == ((Story) o).id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }


}