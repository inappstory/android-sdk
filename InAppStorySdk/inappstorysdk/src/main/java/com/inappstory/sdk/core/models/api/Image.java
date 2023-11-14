package com.inappstory.sdk.core.models.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.inappstory.sdk.core.utils.network.annotations.models.SerializedName;

import java.util.List;

public class Image implements Parcelable {

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


}
