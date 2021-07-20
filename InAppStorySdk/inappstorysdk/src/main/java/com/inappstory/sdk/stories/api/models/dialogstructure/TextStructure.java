package com.inappstory.sdk.stories.api.models.dialogstructure;

public class TextStructure {
    public int size;
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