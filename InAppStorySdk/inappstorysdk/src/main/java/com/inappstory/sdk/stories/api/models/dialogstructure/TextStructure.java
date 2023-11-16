package com.inappstory.sdk.stories.api.models.dialogstructure;

import java.io.Serializable;

public class TextStructure implements Serializable {
    public float size;
    public float lineHeight;
    public String align;
    public String color;
    public String value;
    public String placeholder;
    String family;
    String weight;
    String style;

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