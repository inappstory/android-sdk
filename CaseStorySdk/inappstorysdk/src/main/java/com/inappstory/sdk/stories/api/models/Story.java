package com.inappstory.sdk.stories.api.models;

import android.os.Parcel;
import android.os.Parcelable;



import java.util.ArrayList;
import java.util.List;

import com.inappstory.sdk.network.SerializedName;

/**
 * Created by Paperrose on 08.07.2018.
 */

public class Story implements Parcelable {
    public int id;

    public String getTitle() {
        return title;
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


    public String getTitleColor() {
        return titleColor;
    }

    @SerializedName("title_color")
    public String titleColor;

    /**
     * Последний открытый слайд
     */
    public int lastIndex;

    public String title;

    public String tags;

    public String getSource() {
        return source;
    }

    public String source;

    public String getBackgroundColor() {
        if (backgroundColor == null) return "#000000";
        return backgroundColor;
    }

    @SerializedName("background_color")
    public String backgroundColor;

    @SerializedName("image")
    public List<Image> image;

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

    @SerializedName("src_list")
    public List<ResourceMappingObject> srcList;

    @SerializedName("like")
    public Integer like;

    @SerializedName("slides_count")
    public int slidesCount;


    @SerializedName("favorite")
    public boolean favorite;

    public int getLike() {
        return like != null ? like : 0;
    }

    public boolean liked() {
        return getLike() == 1;
    }

    public boolean disliked() {
        return getLike() == -1;
    }

    @SerializedName("hide_in_reader")
    public Boolean hideInReader;

    public boolean isHideInReader() {
        return hideInReader != null && hideInReader;
    }

    public String getDeeplink() {
        return deeplink;
    }

    @SerializedName("deeplink")
    public String deeplink;

    @SerializedName("is_opened")
    public boolean isOpened;

    @SerializedName("disable_close")
    public boolean disableClose;

    public Boolean hasLike() {
        return hasLike != null ? hasLike : true;
    }

    public Boolean hasFavorite() {
        return hasFavorite != null ? hasFavorite : true;
    }

    public Boolean hasShare() {
        return hasShare != null ? hasShare : true;
    }

    @SerializedName("like_functional")
    private Boolean hasLike = true;

    @SerializedName("favorite_functional")
    private Boolean hasFavorite = true;

    @SerializedName("share_functional")
    private Boolean hasShare = true;

    @SerializedName("slides_duration")
    public List<Integer> durations;

    @SerializedName("slides_html")
    public List<String> pages;

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
        story.slidesCount = slidesCount;
        story.titleColor = titleColor;
        story.isOpened = isOpened;
        story.durations = new ArrayList<>();
        if (durations != null)
            story.durations.addAll(durations);
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
        dest.writeInt(slidesCount);
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