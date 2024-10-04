package com.inappstory.sdk.stories.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.network.annotations.models.Ignore;
import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.stories.api.models.slidestructure.SlideStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Paperrose on 08.07.2018.
 */


public class Story implements Parcelable {
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
        String tmp = title != null ? title : "";
        return getReplacedField(tmp);
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

    public boolean checkIfEmpty() {
        return (getLayout() == null || pages == null || pages.isEmpty());
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

    @SerializedName("feed_info")
    public StoryFeedInfo feedInfo;

    @SerializedName("payload")
    public HashMap<String, Object> ugcPayload;


    public String getVideoUrl() {
        return (videoUrl != null && !videoUrl.isEmpty()) ? videoUrl.get(0).getUrl() : null;
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
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager == null) return tmp;
        Map<String, String> localPlaceholders =
                ((IASDataSettingsHolder)manager.iasCore().settingsAPI()).placeholders();
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
        return Math.max(slidesCount, 0);
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

    public static final String VOD = "vod";

    public List<ResourceMappingObject> vodResources(int index) {
        ArrayList<ResourceMappingObject> res = new ArrayList<>();
        for (ResourceMappingObject object : getSrcList()) {
            if (Objects.equals(VOD, object.purpose) && object.getIndex() == index) {
                res.add(object);
            }
        }
        return res;
    }

    public List<ResourceMappingObject> staticResources(int index) {
        ArrayList<ResourceMappingObject> res = new ArrayList<>();
        for (ResourceMappingObject object : getSrcList()) {
            if (!Objects.equals(VOD, object.purpose) && object.getIndex() == index) {
                res.add(object);
            }
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
        return (hideInReader != null && hideInReader) || slidesCount <= 0;
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

    public void saveStoryOpened(final StoryType type) {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                service.saveStoryOpened(id, type);
            }
        });
    }


    @SerializedName("like_functional")
    public Boolean hasLike;

    @SerializedName("has_audio")
    public Boolean hasAudio;

    @SerializedName("favorite_functional")
    public Boolean hasFavorite;

    @SerializedName("share_functional")
    public Boolean hasShare;

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
        in.readList(pages, String.class.getClassLoader());
        favorite = (in.readInt() == 1);
        layout = in.readString();
        in.readList(slidesShare, Boolean.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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
        dest.writeInt(slidesCount);
        dest.writeString(titleColor);
        dest.writeInt(isOpened ? 1 : 0);
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