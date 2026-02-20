package com.inappstory.sdk.refactoring.stories.data.local;

import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.stories.api.models.GameInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StoryListItemDTO implements IListItemContent {
    public StoryListItemDTO(
            int id,
            String title,
            String titleColor,
            String statTitle,
            List<Image> videoUrl,
            HashMap<String, Object> ugcPayload,
            String backgroundColor,
            List<Image> image,
            Boolean hasSwipeUp,
            Integer like,
            int slidesCount,
            boolean favorite,
            Boolean hideInReader,
            String deeplink,
            GameInstance gameInstance,
            boolean isOpened,
            boolean disableClose,
            Boolean hasLike,
            Boolean hasAudio,
            Boolean hasFavorite,
            Boolean hasShare
    ) {
        this.id = id;
        this.title = title;
        this.titleColor = titleColor;
        this.statTitle = statTitle;
        this.videoUrl = videoUrl;
        this.ugcPayload = ugcPayload;
        this.backgroundColor = backgroundColor;
        this.image = image;
        this.hasSwipeUp = hasSwipeUp;
        this.like = like;
        this.slidesCount = slidesCount;
        this.favorite = favorite;
        this.hideInReader = hideInReader;
        this.deeplink = deeplink;
        this.gameInstance = gameInstance;
        this.isOpened = isOpened;
        this.disableClose = disableClose;
        this.hasLike = hasLike;
        this.hasAudio = hasAudio;
        this.hasFavorite = hasFavorite;
        this.hasShare = hasShare;
    }

    private int id;
    private String title;
    private String titleColor;
    private String statTitle;
    private List<Image> videoUrl;
    private HashMap<String, Object> ugcPayload;
    private String backgroundColor;
    private List<Image> image;
    private Boolean hasSwipeUp;
    private Integer like;
    private int slidesCount;
    private boolean favorite;
    private Boolean hideInReader;
    private String deeplink;
    private GameInstance gameInstance;
    private boolean isOpened;
    private boolean disableClose;
    private Boolean hasLike;
    private Boolean hasAudio;
    private Boolean hasFavorite;
    private Boolean hasShare;

    @Override
    public boolean hasFavorite() {
        return hasFavorite != null ? hasFavorite : false;
    }

    @Override
    public boolean hasLike() {
        return hasLike != null ? hasLike : false;
    }

    @Override
    public boolean hasShare() {
        return hasShare != null ? hasShare : false;
    }

    @Override
    public boolean favorite() {
        return this.favorite;
    }

    @Override
    public void like(int like) {
        this.like = like;
    }

    @Override
    public void favorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public int like() {
        return like != null ? like : 0;
    }

    @Override
    public boolean isOpened() {
        return isOpened;
    }

    @Override
    public void setOpened(boolean isOpened) {
        this.isOpened = isOpened;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String titleColor() {
        return titleColor;
    }

    @Override
    public String backgroundColor() {
        return backgroundColor;
    }

    @Override
    public String imageCoverByQuality(int quality) {
        if (image == null || image.isEmpty())
            return null;
        String q = Image.TYPE_MEDIUM;
        if (quality == Image.QUALITY_HIGH) {
            q = Image.TYPE_HIGH;
        }
        for (Image img : image) {
            if (img.getType().equals(q)) return img.getUrl();
        }
        return image.get(0).getUrl();
    }

    @Override
    public String videoCover() {
        return (videoUrl != null && !videoUrl.isEmpty()) ? videoUrl.get(0).getUrl() : null;
    }

    @Override
    public boolean hasAudio() {
        return hasAudio != null ? hasAudio : false;
    }

    @Override
    public boolean hasSwipeUp() {
        return hasSwipeUp != null ? hasSwipeUp : false;
    }

    @Override
    public boolean disableClose() {
        return disableClose;
    }

    @Override
    public String deeplink() {
        return deeplink;
    }

    @Override
    public String gameInstanceId() {
        if (gameInstance != null)
            return gameInstance.id;
        return null;
    }

    @Override
    public boolean hideInReader() {
        return (hideInReader != null && hideInReader) || slidesCount <= 0;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String statTitle() {
        return statTitle;
    }

    @Override
    public int slidesCount() {
        return slidesCount;
    }

    @Override
    public Map<String, Object> ugcPayload() {
        return ugcPayload;
    }
}
