package com.inappstory.sdk.core.network.content.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.inappstory.sdk.core.data.IContentWithTimeline;
import com.inappstory.sdk.core.data.IReaderContentSlide;
import com.inappstory.sdk.core.data.IStory;
import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.core.data.IResource;
import com.inappstory.sdk.stories.api.models.GameInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Paperrose on 08.07.2018.
 */


public class Story implements Parcelable, IStory, IContentWithTimeline {
    @Required
    public int id;

    @SerializedName("title")
    public String title;

    @SerializedName("tags")
    public String tags;

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

    public boolean disableClose() {
        return disableClose;
    }

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

    @Override
    public String title() {
        return title != null ? title : "";
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
    public boolean isOpened() {
        return isOpened;
    }

    @Override
    public void setOpened(boolean isOpened) {
        this.isOpened = isOpened;
    }

    @Override
    public String titleColor() {
        return titleColor;
    }

    @Override
    public String videoCover() {
        return (videoUrl != null && !videoUrl.isEmpty()) ? videoUrl.get(0).getUrl() : null;
    }

    @Override
    public boolean hasSwipeUp() {
        return hasSwipeUp != null ? hasSwipeUp : false;
    }

    @Override
    public int shareType(int index) {
        return slide(index).shareType();
    }

    @Override
    public int slidesCount() {
        return Math.max(slidesCount, 0);
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
    public String backgroundColor() {
        if (backgroundColor == null) return "#FFFFFF";
        return backgroundColor;
    }

    private IReaderContentSlide slide(int index) {
        if (slides == null || index < 0 || slides.size() <= index)
            throw new RuntimeException("Slide index out of bounds: " + index + " from " + slidesCount);
        for (IReaderContentSlide slide : slides) {
            if (slide.index() == index) return slide;
        }
        throw new RuntimeException("Slide " + index + " not in slides array of story " + id());
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
        if (slides != null) return slides.size();
        return 0;
    }

    @Override
    public String tags() {
        return tags;
    }

    @Override
    public Map<String, Object> ugcPayload() {
        return ugcPayload;
    }

    @Override
    public boolean favorite() {
        return favorite;
    }

    @Override
    public int like() {
        return like != null ? like : 0;
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
    public boolean hideInReader() {
        return (hideInReader != null && hideInReader) || slidesCount <= 0;
    }

    @Override
    public boolean hasLike() {
        return hasLike != null ? hasLike : false;
    }

    @Override
    public boolean hasFavorite() {
        return hasFavorite != null ? hasFavorite : false;
    }

    @Override
    public boolean hasShare() {
        return hasShare != null ? hasShare : false;
    }

    @Override
    public boolean hasAudio() {
        return hasAudio != null ? hasAudio : false;
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
    public String layout() {
        return layout;
    }

    @Override
    public String slideByIndex(int index) {
        return slide(index).html();
    }

    public Story() {
    }

    public Story(Parcel in) {
        super();
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        title = in.readString();
        backgroundColor = in.readString();
        image = in.createTypedArrayList(Image.CREATOR);
        like = in.readInt();
        slidesCount = in.readInt();
        titleColor = in.readString();
        isOpened = (in.readInt() == 1);
        favorite = (in.readInt() == 1);
        layout = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(statTitle);
        dest.writeString(backgroundColor);
        dest.writeTypedList(image);
        dest.writeInt(like);
        dest.writeInt(slidesCount);
        dest.writeString(titleColor);
        dest.writeInt(isOpened ? 1 : 0);
        dest.writeInt(favorite ? 1 : 0);
        dest.writeString(layout);

    }

    public static final Parcelable.Creator<Story> CREATOR = new Parcelable.Creator<Story>() {
        public Story createFromParcel(Parcel in) {
            return new Story(in);
        }

        public Story[] newArray(int size) {

            return new Story[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
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

    @Override
    public boolean timelineIsHidden() {
        return timelineIsHidden;
    }

    @Override
    public String timelineBackgroundColor(int slideIndex) {
        return slide(slideIndex).timelineBackgroundColor();
    }

    @Override
    public String timelineForegroundColor(int slideIndex) {
        return slide(slideIndex).timelineForegroundColor();
    }
}