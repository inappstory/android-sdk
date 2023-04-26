package com.inappstory.sdk.stories.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.Required;
import com.inappstory.sdk.network.SerializedName;
import com.inappstory.sdk.stories.api.models.slidestructure.SlideStructure;
import com.inappstory.sdk.stories.utils.PlaceholderKeyConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Paperrose on 08.07.2018.
 */


public class Story implements Parcelable {
    @Required
    public int id;

    public enum StoryType {
        COMMON, UGC
    }

    public String getTitle() {
        String tmp = title != null ? title : "";
        return getReplacedField(tmp);
    }

    public String getSlideEventPayload(String eventType, int slideIndex) {
        if (slidesPayload == null) return null;
        for (PayloadObject payloadObject : slidesPayload) {
            if (slideIndex == payloadObject.slideIndex && eventType.equals(payloadObject.getEventType())) {
                return payloadObject.getPayload();
            }
        }
        return null;
    }

    public boolean checkIfEmpty() {
        boolean res = (getLayout() == null || pages == null || pages.isEmpty());
        res = res && (slidesStructure == null || slidesStructure.isEmpty());
        return res;
    }

    public List<Image> getImage() {
        return image;
    }

    public Image getProperImage(int quality) {
        if (image == null || image.isEmpty())
            return null;
        String q = Image.TYPE_MEDIUM;
        switch (quality) {
            case Image.QUALITY_HIGH:
                q = Image.TYPE_HIGH;
        }
        for (Image img : image) {
            if (img.getType().equals(q)) return img;
        }
        return image.get(0);
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

    @SerializedName("video_cover")
    public List<Image> videoUrl;

    @SerializedName("slides_payload")
    public List<PayloadObject> slidesPayload;


    public String getVideoUrl() {
        return (videoUrl != null && !videoUrl.isEmpty()) ? videoUrl.get(0).getUrl() : null;
    }

    public void setLastIndex(int lastIndex, StoryType type) {
        this.lastIndex = lastIndex;
        try {
            InAppStoryService.getInstance().getDownloadManager()
                    .getStoryById(id, type).lastIndex = lastIndex;
        } catch (Exception e) {

        }
    }


    /**
     * Последний открытый слайд
     */
    public int lastIndex;

    public String title;

    public String tags;

    public String getSource() {
        String tmp = source != null ? source : "";
        return getReplacedField(tmp);
    }

    private String getReplacedField(String tmp) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return tmp;
        Map<String, String> localPlaceholders = InAppStoryService.getInstance().getPlaceholders();
        for (String key : localPlaceholders.keySet()) {
            String modifiedKey = "%" + key + "%";
            String value = localPlaceholders.get(key);
            if (value != null) {
                tmp = tmp.replace(modifiedKey, value);
            }
        }
        return tmp;
    }

    public String source;


    @SerializedName("background_color")
    public String backgroundColor;

    @SerializedName("image")
    public List<Image> image;

    public boolean hasSwipeUp() {
        return hasSwipeUp != null ? hasSwipeUp : false;
    }

    @SerializedName("has_swipe_up")
    private Boolean hasSwipeUp;

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

    public boolean isScreenshotShare(int index) {
        return shareType(index) == 1;
    }


    public int shareType(int index) {
        if (slidesShare == null) return 0;
        if (slidesShare.size() <= index) return 0;
        if (slidesShare.get(index) != null)
            return slidesShare.get(index);
        return 0;
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

    @SerializedName("deeplink")
    public String deeplink;

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

    public List<String> getPlaceholdersListNames(int index) {
        ArrayList<String> res = new ArrayList<>();
        for (ImagePlaceholderMappingObject object : getImagePlaceholdersList()) {
            if (object.getIndex() == index && (object.getType().equals("image-placeholder"))) {
                String name = object.getUrl();
                if (name != null) res.add(name);
            }

        }
        return res;
    }


    public Map<String, String> getPlaceholdersList(int index, String type) {
        Map<String, String> res = new HashMap<>();
        for (ImagePlaceholderMappingObject object : getImagePlaceholdersList()) {
            if (object.getIndex() == index && (object.getType().equals("image-placeholder"))) {
                res.put(object.getKey(), object.getUrl());
            }
        }
        return res;
    }


    public List<String> getSrcListKeys(int index, String type) {
        ArrayList<String> res = new ArrayList<>();
        for (ResourceMappingObject object : getSrcList()) {
            String objType = object.getType();
            if (object.getIndex() == index &&
                    (
                            (type == null && (objType == null || objType.equals("image")))
                                    ||
                                    object.getType().equals(type)
                    )
            )
                res.add(object.getKey());
        }
        return res;
    }


    public List<String> getSrcListUrls(int index, String type) {
        ArrayList<String> res = new ArrayList<>();
        for (ResourceMappingObject object : getSrcList()) {
            String objType = object.getType();
            if (object.getIndex() == index &&
                    (
                            (type == null && (objType == null || objType.equals("image")))
                                    ||
                                    object.getType().equals(type)
                    )
            )
                res.add(object.getUrl());

        }
        return res;
    }


    public int getLike() {
        return like != null ? like : 0;
    }

    public boolean liked() {
        return getLike() == 1;
    }

    public boolean disliked() {
        return getLike() == -1;
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

    public void saveStoryOpened(StoryType type) {
        if (InAppStoryService.isNotNull()) {
            InAppStoryService.getInstance().saveStoryOpened(id, type);
        }
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

    public List<Boolean> loadedPages = new ArrayList<>();

    @SerializedName("layout")
    public String layout;

    public Story() {
    }

    public Story getSimpleCopy() {
        Story story = new Story();
        story.id = id;
        story.lastIndex = lastIndex;
        story.title = title;
        story.statTitle = statTitle;
        story.source = source;
        story.backgroundColor = backgroundColor;
        story.image = image;
        story.like = like;
        story.hasAudio = hasAudio;
        story.slidesCount = slidesCount;
        story.titleColor = titleColor;
        story.isOpened = isOpened;
        story.durations = new ArrayList<>();
        if (durations != null) {
            story.durations.addAll(durations);
            story.slidesCount = durations.size();
        }
        if (slidesShare != null) {
            story.slidesShare.addAll(slidesShare);
        }
        story.favorite = favorite;
        //nar.pages = pages;
        return story;
    }

    public Story(Parcel in) {
        super();
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        if (durations == null) durations = new ArrayList<>();
        if (slidesShare == null) slidesShare = new ArrayList<>();
        if (pages == null) pages = new ArrayList<>();
        id = in.readInt();
        lastIndex = in.readInt();
        title = in.readString();
        source = in.readString();
        backgroundColor = in.readString();
        image = in.createTypedArrayList(Image.CREATOR);
        like = in.readInt();
        slidesCount = in.readInt();
        titleColor = in.readString();
        isOpened = (in.readInt() == 1);
        in.readList(durations, Integer.class.getClassLoader());
        if (durations != null || !durations.isEmpty()) {
            slidesCount = durations.size();
        }
        in.readList(pages, String.class.getClassLoader());
        favorite = (in.readInt() == 1);
        layout = in.readString();
        in.readList(slidesShare, Boolean.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (durations == null) durations = new ArrayList<>();
        if (slidesShare == null) slidesShare = new ArrayList<>();
        if (pages == null) pages = new ArrayList<>();
        dest.writeInt(id);
        dest.writeInt(lastIndex);
        dest.writeString(title);
        dest.writeString(statTitle);
        dest.writeString(source);
        dest.writeString(backgroundColor);
        dest.writeTypedList(image);
        dest.writeInt(like);
        dest.writeInt((durations != null && !durations.isEmpty()) ? durations.size() : slidesCount);
        dest.writeString(titleColor);
        dest.writeInt(isOpened ? 1 : 0);
        dest.writeList(durations);
        dest.writeList(pages);
        dest.writeInt(favorite ? 1 : 0);
        dest.writeString(layout);
        dest.writeList(slidesShare);

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


}