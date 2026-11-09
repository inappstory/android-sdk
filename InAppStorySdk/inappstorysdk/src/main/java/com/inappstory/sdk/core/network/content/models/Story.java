package com.inappstory.sdk.core.network.content.models;


import static com.inappstory.sdk.core.network.content.models.StorySlideTimeline.DEFAULT_TIMELINE_BACKGROUND_COLOR;
import static com.inappstory.sdk.core.network.content.models.StorySlideTimeline.DEFAULT_TIMELINE_FOREGROUND_COLOR;

import android.os.Parcel;
import android.os.Parcelable;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.data.IContentWithTimeline;
import com.inappstory.sdk.core.data.IReaderContentSlide;
import com.inappstory.sdk.core.data.IStory;
import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.core.data.IResource;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.GameInstance;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Paperrose on 08.07.2018.
 */


public class Story implements Parcelable, IStory, IContentWithTimeline {
    @Required
    @SerializedName(ModelFields.Story.id)
    public int id;
    @SerializedName(ModelFields.Story.title)
    public String title;
    @SerializedName(ModelFields.Story.titleColor)
    public String titleColor;
    @SerializedName(ModelFields.Story.statTitle)
    public String statTitle;
    @SerializedName(ModelFields.Story.isFullscreen)
    public boolean isFullscreen;
    @SerializedName(ModelFields.Story.backgroundColor)
    public String backgroundColor;
    @SerializedName(ModelFields.Story.hasSwipeUp)
    public Boolean hasSwipeUp;
    @SerializedName(ModelFields.Story.like)
    public Integer like;
    @SerializedName(ModelFields.Story.slidesCount)
    public int slidesCount;
    @SerializedName(ModelFields.Story.favorite)
    public boolean favorite;
    @SerializedName(ModelFields.Story.hideInReader)
    public Boolean hideInReader;
    @SerializedName(ModelFields.Story.deeplink)
    public String deeplink;
    @SerializedName(ModelFields.Story.isOpened)
    public boolean isOpened;
    @SerializedName(ModelFields.Story.disableClose)
    public boolean disableClose;
    @SerializedName(ModelFields.Story.hasLike)
    public Boolean hasLike;
    @SerializedName(ModelFields.Story.hasAudio)
    public Boolean hasAudio;
    @SerializedName(ModelFields.Story.hasFavorite)
    public Boolean hasFavorite;
    @SerializedName(ModelFields.Story.hasShare)
    public Boolean hasShare;
    @SerializedName(ModelFields.Story.timelineIsHidden)
    public boolean timelineIsHidden;
    @SerializedName(ModelFields.Story.ugcPayload)
    public HashMap<String, Object> ugcPayload;
    @SerializedName(ModelFields.Story.customAttributes)
    public Map<String, String> customAttributes;
    @SerializedName(ModelFields.Story.layoutTemplateVariables)
    public Map<String, String> layoutTemplateVariables;
    @SerializedName(ModelFields.Story.videoUrl)
    public List<Image> videoUrl;
    @SerializedName(ModelFields.Story.image)
    public List<Image> image;
    @SerializedName(ModelFields.Story.slides)
    public List<StorySlide> slides;
    @SerializedName(ModelFields.Story.gameInstance)
    public GameInstance gameInstance;

    public boolean disableClose() {
        return disableClose;
    }

    @Override
    public boolean fullscreen() {
        return isFullscreen;
    }


    @Override
    public String title() {
        return title != null ? title : "";
    }

    @Override
    public String slideEventPayload(int slideIndex) {
        IReaderContentSlide slide = slide(slideIndex);
        if (slide == null) return null;
        return slide.slidePayload();
    }

    @Override
    public boolean checkIfEmpty() {
        return (
                slides == null ||
                slides.isEmpty() ||
                layoutTemplateVariables == null ||
                layoutTemplateVariables.isEmpty()
        );
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
    public int shareType(int slideIndex) {
        IReaderContentSlide slide = slide(slideIndex);
        if (slide == null) return 0;
        return slide.shareType();
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
        if (slides == null || index < 0 || slides.size() <= index) {
            InAppStoryManager.showELog(
                    LoggerTags.IAS_ERROR_TAG,
                    "Slide index out of bounds: " + index + " from " + slidesCount
            );
            return null;
        }
        for (IReaderContentSlide slide : slides) {
            if (slide.index() == index) return slide;
        }
        InAppStoryManager.showELog(
                LoggerTags.IAS_ERROR_TAG,
                "Slide " + index + " not in slides array of story " + id()
        );
        return null;
    }

    @Override
    public List<IResource> vodResources(int slideIndex) {
        IReaderContentSlide slide = slide(slideIndex);
        if (slide == null) return null;
        return slide.vodResources();
    }

    @Override
    public List<IResource> staticResources(int slideIndex) {
        IReaderContentSlide slide = slide(slideIndex);
        if (slide == null) return null;
        return slide.staticResources();
    }

    @Override
    public List<String> placeholdersNames(int slideIndex) {
        IReaderContentSlide slide = slide(slideIndex);
        if (slide == null) return null;
        return slide.placeholdersNames();
    }

    @Override
    public Map<String, String> placeholdersMap(int slideIndex) {
        IReaderContentSlide slide = slide(slideIndex);
        if (slide == null) return null;
        return slide.placeholdersMap();
    }

    @Override
    public int actualSlidesCount() {
        if (slides != null) return slides.size();
        return 0;
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
    public Map<String, String> customAttributes() {
        return customAttributes;
    }


    @Override
    public Map<String, String> layoutTemplateVariables() {
        if (layoutTemplateVariables == null) layoutTemplateVariables = new HashMap<>();
        return layoutTemplateVariables;
    }

    @Override
    public String slideByIndex(int slideIndex) {
        IReaderContentSlide slide = slide(slideIndex);
        if (slide == null) return null;
        return slide.html();
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
        IReaderContentSlide slide = slide(slideIndex);
        if (slide == null) return DEFAULT_TIMELINE_BACKGROUND_COLOR;
        return slide.timelineBackgroundColor();
    }

    @Override
    public String timelineForegroundColor(int slideIndex) {
        IReaderContentSlide slide = slide(slideIndex);
        if (slide == null) return DEFAULT_TIMELINE_FOREGROUND_COLOR;
        return slide.timelineForegroundColor();
    }
}