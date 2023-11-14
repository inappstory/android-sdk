package com.inappstory.sdk.core.models.api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Paperrose on 08.07.2018.
 */


public class GameInstance implements Parcelable {


    public String id;


    public GameInstance() {
    }

    public GameInstance getSimpleCopy() {
        GameInstance story = new GameInstance();
        story.id = id;

        return story;
    }

    public GameInstance(Parcel in) {
        super();
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
    }

    public static final Creator<GameInstance> CREATOR = new Creator<GameInstance>() {
        public GameInstance createFromParcel(Parcel in) {
            return new GameInstance(in);
        }

        public GameInstance[] newArray(int size) {

            return new GameInstance[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof GameInstance) {
            return id == ((GameInstance) o).id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * id.hashCode();
    }


}