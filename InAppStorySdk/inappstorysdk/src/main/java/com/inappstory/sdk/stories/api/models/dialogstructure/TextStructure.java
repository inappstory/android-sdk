package com.inappstory.sdk.stories.api.models.dialogstructure;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.io.Serializable;

public class TextStructure implements Serializable {
    @SerializedName(ModelFields.TextStructure.size)
    public float size;
    @SerializedName(ModelFields.TextStructure.lineHeight)
    public float lineHeight;
    @SerializedName(ModelFields.TextStructure.align)
    public String align;
    @SerializedName(ModelFields.TextStructure.color)
    public String color;
    @SerializedName(ModelFields.TextStructure.value)
    public String value;
    @SerializedName(ModelFields.TextStructure.placeholder)
    public String placeholder;
    @SerializedName(ModelFields.TextStructure.family)
    public String family;
    @SerializedName(ModelFields.TextStructure.weight)
    public String weight;
    @SerializedName(ModelFields.TextStructure.style)
    public String style;
    @SerializedName(ModelFields.TextStructure.limit)
    public int limit;
    @SerializedName(ModelFields.TextStructure.maxLines)
    public int maxLines;

    public boolean isBold() {
        return weight != null && weight.equals("bold");
    }

    public boolean isItalic() {
        return style != null && style.equals("italic");
    }

    public boolean isSecondary() {
        return family != null && family.equals("InternalSecondaryFont");
    }
}