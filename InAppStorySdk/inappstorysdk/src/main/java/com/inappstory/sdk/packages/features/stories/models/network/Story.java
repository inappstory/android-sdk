package com.inappstory.sdk.packages.features.stories.models.network;

import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.HashMap;
import java.util.List;

public class Story {
    @Required
    public int id;
    @SerializedName("title_color")
    public String titleColor;

    @SerializedName("stat_title")
    public String statTitle;

    @SerializedName("video_cover")
    public List<Image> videoUrl;

    @SerializedName("slides_payload")
    public List<StoryPayload> slidesPayload;

    @SerializedName("payload")
    public HashMap<String, Object> ugcPayload;

    @SerializedName("background_color")
    public String backgroundColor;

    @SerializedName("image")
    public List<Image> image;

    @SerializedName("has_swipe_up")
    public Boolean hasSwipeUp;

    @SerializedName("src_list")
    public List<StoryResource> srcList;

    @SerializedName("img_placeholder_src_list")
    public List<StoryResource> imagePlaceholdersList;

    @SerializedName("like")
    public Integer like;

    @SerializedName("slides_screenshot_share")
    public List<Integer> slidesShare;

    @SerializedName("slides_count")
    public int slidesCount;

    @SerializedName("favorite")
    public boolean favorite;

    @SerializedName("hide_in_reader")
    public Boolean hideInReader;

    @SerializedName("deeplink")
    public String deeplink;

    @SerializedName("game_instance")
    public GameInstance gameInstance;

    @SerializedName("is_opened")
    public boolean isOpened;

    @SerializedName("disable_close")
    public boolean disableClose;

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

    @SerializedName("layout")
    public String layout;

}
