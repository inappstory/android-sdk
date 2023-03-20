package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.network.SerializedName;

public class GameDataPlaceholder {
    public GameDataPlaceholder(String type, String name, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    @SerializedName("name")
    public String name;
    @SerializedName("type")
    public String type;
    @SerializedName("value")
    public String value;

    public GameDataPlaceholder() {
    }


}