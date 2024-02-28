package com.inappstory.sdk.core.models.js.dialogstructure;

public class ButtonStructure {
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

    public BackgroundStructure background;
    public BorderStructure border;
    public TextStructure text;
    public PaddingStructure padding;
}