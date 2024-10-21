package com.inappstory.sdk.stories.api.models;

import static com.inappstory.sdk.stories.api.models.ResourceMapping.VOD;

import android.os.Parcel;
import android.os.Parcelable;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.dataholders.IListItemContent;
import com.inappstory.sdk.core.dataholders.IReaderContentWithStatus;
import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.core.dataholders.IResource;
import com.inappstory.sdk.core.dataholders.IReaderContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Paperrose on 08.07.2018.
 */


public class Story implements Parcelable, IReaderContentWithStatus, IListItemContent {
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

    @SerializedName("slides_payload")
    public List<PayloadObject> slidesPayload;

    @SerializedName("payload")
    public HashMap<String, Object> ugcPayload;

    @SerializedName("background_color")
    public String backgroundColor;

    @SerializedName("image")
    public List<Image> image;

    @SerializedName("has_swipe_up")
    public Boolean hasSwipeUp;

    @SerializedName("src_list")
    public List<ResourceMapping> srcList;


    @SerializedName("img_placeholder_src_list")
    public List<ResourceMapping> imagePlaceholdersList;


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

    @SerializedName("slides_html")
    public List<String> pages;

    public List<Boolean> loadedPages = new ArrayList<>();

    @SerializedName("layout")
    public String layout;

    @Override
    public String title() {
        String tmp = title != null ? title : "";
        return replacedField(tmp);
    }

    @Override
    public String slideEventPayload(int slideIndex) {
        if (slidesPayload == null) return null;
        for (PayloadObject payloadObject : slidesPayload) {
            if (slideIndex == payloadObject.slideIndex) {
                return payloadObject.getPayload();
            }
        }
        return null;
    }

    @Override
    public boolean checkIfEmpty() {
        return (layout == null || pages == null || pages.isEmpty());
    }

    @Override
    public String imageCoverByQuality(int quality) {
        if (image == null || image.isEmpty())
            return null;
        String q = Image.TYPE_MEDIUM;
        switch (quality) {
            case Image.QUALITY_HIGH:
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

    public List<String> getPages() {
        return pages;
    }

    public String titleColor() {
        return titleColor;
    }

    @Override
    public String videoCover() {
        return (videoUrl != null && !videoUrl.isEmpty()) ? videoUrl.get(0).getUrl() : null;
    }


    /**
     * Последний открытый слайд
     */

    private String replacedField(String tmp) {
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager == null) return tmp;
        Map<String, String> localPlaceholders =
                ((IASDataSettingsHolder) manager.iasCore().settingsAPI()).placeholders();
        for (String key : localPlaceholders.keySet()) {
            String modifiedKey = "%" + key + "%";
            String value = localPlaceholders.get(key);
            if (value != null) {
                tmp = tmp.replace(modifiedKey, value);
            }
        }
        return tmp;
    }

    @Override
    public boolean hasSwipeUp() {
        return hasSwipeUp != null ? hasSwipeUp : false;
    }

    public void srcList(List<IResource> srcList) {
        this.srcList = new ArrayList<>();
        for (IResource resourceObject: srcList) {
            this.srcList.add((ResourceMapping) resourceObject);
        }
    }


    public void imagePlaceholdersList(List<IResource> imagePlaceholdersList) {
        this.imagePlaceholdersList = new ArrayList<>();
        for (IResource resourceObject: imagePlaceholdersList) {
            this.imagePlaceholdersList.add((ResourceMapping) resourceObject);
        }
    }


    public int shareType(int index) {
        if (slidesShare == null) return 0;
        if (slidesShare.size() <= index) return 0;
        if (slidesShare.get(index) != null)
            return slidesShare.get(index);
        return 0;
    }

    public int slidesCount() {
        return Math.max(slidesCount, 0);
    }


    public String deeplink() {
        return deeplink;
    }

    public String gameInstanceId() {
        if (gameInstance != null)
            return gameInstance.id;
        return null;
    }

    public String backgroundColor() {
        if (backgroundColor == null) return "#FFFFFF";
        return backgroundColor;
    }

    public List<IResource> srcList() {
        if (srcList == null) srcList = new ArrayList<>();
        List<IResource> res = new ArrayList<>();
        res.addAll(srcList);
        return res;
    }

    public List<IResource> getImagePlaceholdersList() {
        if (imagePlaceholdersList == null) imagePlaceholdersList = new ArrayList<>();
        List<IResource> res = new ArrayList<>();
        res.addAll(imagePlaceholdersList);
        return res;
    }

    @Override
    public List<IResource> vodResources(int index) {
        List<IResource> res = new ArrayList<>();
        for (IResource object : srcList()) {
            if (Objects.equals(VOD, object.getPurpose()) && object.getIndex() == index) {
                res.add(object);
            }
        }
        return res;
    }

    @Override
    public List<IResource> staticResources(int index) {
        List<IResource> res = new ArrayList<>();
        for (IResource object : srcList()) {
            if (!Objects.equals(VOD, object.getPurpose()) && object.getIndex() == index) {
                res.add(object);
            }
        }
        return res;
    }

    @Override
    public List<String> placeholdersNames(int index) {
        List<String> res = new ArrayList<>();
        for (IResource object : getImagePlaceholdersList()) {
            if (object.getIndex() == index && (object.getType().equals("image-placeholder"))) {
                String name = object.getUrl();
                if (name != null) res.add(name);
            }

        }
        return res;
    }

    @Override
    public Map<String, String> placeholdersMap(int index) {
        Map<String, String> res = new HashMap<>();
        for (IResource object : getImagePlaceholdersList()) {
            if (object.getIndex() == index && (object.getType().equals("image-placeholder"))) {
                res.put(object.getKey(), object.getUrl());
            }
        }
        return res;
    }

    @Override
    public int actualSlidesCount() {
        if (pages != null) return pages.size();
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
    public List<Integer> slidesShare() {
        if (slidesShare == null) {
            slidesShare = new ArrayList<>();
        }
        return slidesShare;
    }

    public boolean favorite() {
        return favorite;
    }


    public int like() {
        return like != null ? like : 0;
    }

    public void like(int like) {
        this.like = like;
    }

    public void favorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public boolean hideInReader() {
        return (hideInReader != null && hideInReader) || slidesCount <= 0;
    }

    public boolean hasLike() {
        return hasLike != null ? hasLike : false;
    }

    public boolean hasFavorite() {
        return hasFavorite != null ? hasFavorite : false;
    }

    public boolean hasShare() {
        return hasShare != null ? hasShare : false;
    }

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
        if (pages != null && pages.size() > index && index >= 0) {
            return pages.get(index);
        }
        return null;
    }

    public void layout(String layout) {
        this.layout = layout;
    }

    public void pages(List<String> pages) {
        this.pages = new ArrayList<>(pages);
    }


    public Story() {
    }

    public Story getSimpleCopy() {
        Story story = new Story();
        story.id = id;
        story.title = title;
        story.statTitle = statTitle;
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
        title = in.readString();
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
        dest.writeString(title);
        dest.writeString(statTitle);
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