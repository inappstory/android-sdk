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

    public int limit;
    public int maxLines;

    public int limit() {
        return limit;
    }

    public int maxLines() {
        return maxLines;
    }

    public float size() {
        return size;
    }

    public float lineHeight() {
        return lineHeight;

    }

    public String align() {
        return align;

    }

    public String color() {
        return color;

    }

    public String value() {
        return value;

    }

    public String placeholder() {
        return placeholder;

    }

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