package com.inappstory.sdk.refactoring.stories.data.network;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.core.network.content.models.StorySlide;
import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.stories.api.models.GameInstance;

import java.util.HashMap;
import java.util.List;

public class NStory {
    @Required
    public int id;

    @SerializedName("title")
    public String title;

    @SerializedName("title_color")
    public String titleColor;

    @SerializedName("stat_title")
    public String statTitle;

    @SerializedName("video_cover")
    public List<Image> videoUrl;

    @SerializedName("payload")
    public HashMap<String, Object> ugcPayload;

    @SerializedName("background_color")
    public String backgroundColor;

    @SerializedName("image")
    public List<Image> image;

    @SerializedName("has_swipe_up")
    public Boolean hasSwipeUp;

    @SerializedName("slides")
    public List<StorySlide> slides;

    @SerializedName("like")
    public Integer like;

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

    @SerializedName("hide_timeline")
    public boolean timelineIsHidden;

    @SerializedName("layout")
    public String layout;

}
