package com.inappstory.sdk.core.models.js.dialogstructure;

public class TextStructure {
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