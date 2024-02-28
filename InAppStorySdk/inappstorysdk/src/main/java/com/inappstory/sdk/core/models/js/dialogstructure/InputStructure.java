package com.inappstory.sdk.core.models.js.dialogstructure;

public class InputStructure {
    public BackgroundStructure background;
    public BorderStructure border;
    public TextStructure text;
    public PaddingStructure padding;
    public String type;


    public BackgroundStructure background() {
        return background;
    }


    public BorderStructure border() {
        return border;
    }

    public TextStructure text() {
        return text;
    }

    public PaddingStructure padding() {
        return padding;
    }

    public String type() {
        return type;
    }


    public int limit() {
        return text().limit();
    }

    public int maxLines() {
        return Math.max(text().maxLines(), 1);
    }
}