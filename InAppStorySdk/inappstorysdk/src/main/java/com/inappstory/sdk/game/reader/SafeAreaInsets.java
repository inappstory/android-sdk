package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class SafeAreaInsets {
    @SerializedName(ModelFields.SafeAreaInsets.top)
    public int top;
    @SerializedName(ModelFields.SafeAreaInsets.bottom)
    public int bottom;
    @SerializedName(ModelFields.SafeAreaInsets.left)
    public int left;
    @SerializedName(ModelFields.SafeAreaInsets.right)
    public int right;

    @Override
    public String toString() {
        return "SafeAreaInsets{" +
                "top=" + top +
                ", bottom=" + bottom +
                ", left=" + left +
                ", right=" + right +
                '}';
    }
}