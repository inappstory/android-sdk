package com.inappstory.sdk.stories.api.models;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.network.SerializedName;
import com.inappstory.sdk.stories.api.models.slidestructure.SlideStructure;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;

/**
 * Created by Paperrose on 08.07.2018.
 */

public class Story implements Parcelable {
    public int id;

    public String getTitle() {
        String tmp = title != null ? title : "";
        if (InAppStoryManager.getInstance() == null) return title;
        for (String key : InAppStoryManager.getInstance().getPlaceholders().keySet()) {
            if (tmp.contains(key)) {
                tmp = tmp.replace(key, InAppStoryManager.getInstance().getPlaceholders().get(key));
            }
        }
        return tmp;
    }

    public boolean checkIfEmpty() {
        boolean res = (getLayout() == null || pages == null || pages.isEmpty());
        res = res && (slidesStructure == null || slidesStructure.isEmpty());
        return res;
    }

    public List<Image> getImage() {
        return image;
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

    public boolean isFavorite() {
        return favorite;
    }

    public String getTitleColor() {
        return titleColor;
    }

    @SerializedName("title_color")
    public String titleColor;


    @SerializedName("video_cover")
    public List<Image> videoUrl;


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
        if (InAppStoryManager.getInstance() == null) return source;
        for (String key : InAppStoryManager.getInstance().getPlaceholders().keySet()) {
            if (tmp.contains(key)) {
                tmp = tmp.replace(key, InAppStoryManager.getInstance().getPlaceholders().get(key));
            }
        }
        return tmp;
        //  return source;
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

    @SerializedName("like")
    public Integer like;

    @SerializedName("slides_count")
    public int slidesCount;


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
        if (backgroundColor == null) return "#000000";
        return backgroundColor;
    }

    public List<ResourceMappingObject> getSrcList() {
        if (srcList == null) srcList = new ArrayList<>();
        return srcList;
    }

    public List<String> getSrcListKeys(int index, String type) {
        ArrayList<String> res = new ArrayList<>();
        for (ResourceMappingObject object : getSrcList()) {
            if (object.getIndex() == index && (type == null || object.getType().equals(type)))
                res.add(object.getKey());
        }
        return res;
    }

    public List<String> getSrcListUrls(int index, String type) {
        ArrayList<String> res = new ArrayList<>();
        for (ResourceMappingObject object : getSrcList()) {

            if (object.getIndex() == index && (type == null || object.getType().equals(type)))
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

    public void saveStoryOpened() {
        Set<String> opens = SharedPreferencesAPI.getStringSet(InAppStoryManager.getInstance().getLocalOpensKey());
        if (opens == null) opens = new HashSet<>();
        opens.add(Integer.toString(id));
        SharedPreferencesAPI.saveStringSet(InAppStoryManager.getInstance().getLocalOpensKey(), opens);
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
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (durations == null) durations = new ArrayList<>();
        if (pages == null) pages = new ArrayList<>();
        dest.writeInt(id);
        dest.writeInt(lastIndex);
        dest.writeString(title);
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