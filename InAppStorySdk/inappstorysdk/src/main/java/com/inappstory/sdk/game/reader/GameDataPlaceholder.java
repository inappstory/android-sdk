package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class GameDataPlaceholder {
    public GameDataPlaceholder(String type, String name, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    @SerializedName(ModelFields.GameDataPlaceholder.name)
    public String name;
    @SerializedName(ModelFields.GameDataPlaceholder.type)
    public String type;
    @SerializedName(ModelFields.GameDataPlaceholder.value)
    public String value;

    public GameDataPlaceholder() {
    }


}