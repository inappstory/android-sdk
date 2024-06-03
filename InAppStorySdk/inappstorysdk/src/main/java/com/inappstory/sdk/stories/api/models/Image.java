package com.inappstory.sdk.stories.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.List;

public class Image implements Parcelable {
    public static final String TYPE_SMALL = "s";
    public static final String TYPE_MEDIUM = "m";
    public static final String TYPE_HIGH = "h";

    public static final int QUALITY_MEDIUM = 1;
    public static final int QUALITY_HIGH = 2;

    public static final String TEMP_IMAGE = "";

    public String getUrl() {
        return url;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @SerializedName("url")
    private String url;

    @SerializedName("width")
    private int width;

    @SerializedName("height")
    private int height;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @SerializedName("type")
    private String type;

    public int getExpire() {
        return expire;
    }

    @SerializedName("expire")
    private int expire;

    protected Image(Parcel in) {
        url = in.readString();
        width = in.readInt();
        height = in.readInt();
        expire = in.readInt();
        type = in.readString();
    }

    public Image() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeInt(expire);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public static String getSquareImage(List<Image> imageObjects) {
        for (Image imageObject : imageObjects) {
            if (imageObject.height == imageObject.width) return imageObject.url;
        }
        return TEMP_IMAGE;
    }

    public static String getProperTabletImage(List<Image> imageObjects) {
        if (imageObjects != null && imageObjects.size() > 0) {
            return getTabletImage(imageObjects);
        }
        return TEMP_IMAGE;
    }

    public static String getLargeImage(List<Image> imageObjects) {
        String resUrl = "";
        int maxHeight = 0;
        for (Image image : imageObjects) {
            if (image.type.equals(TYPE_HIGH) && (((float) image.width / image.height) > 1.1) && (((float) image.width / image.height) < 3)) {
                return image.getUrl();
            }
        }
        /*if (resUrl != null && !resUrl.isEmpty()) {
            return resUrl;
        }*/
        return TEMP_IMAGE;
    }


    public static String getImage(List<Image> images, String type) {
        for (Image image : images) {
            if (image.type.equals(type) && (((float) image.width / image.height) < 3) && (((float) image.width / image.height) > 1.1)) {
                return image.url;
            }
        }
        return TEMP_IMAGE;
    }

    private static String getTabletImage(List<Image> images) {
        for (Image image : images) {
            if (image.type.equals(TYPE_HIGH) && (((float) image.width / image.height) > 3)) {
                return image.url;
            }
        }
        return TEMP_IMAGE;
    }


    @Override
    public String toString() {
        return "Image{" +
                "url='" + url + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", type='" + type + '\'' +
                ", expire=" + expire +
                '}';
    }
}
