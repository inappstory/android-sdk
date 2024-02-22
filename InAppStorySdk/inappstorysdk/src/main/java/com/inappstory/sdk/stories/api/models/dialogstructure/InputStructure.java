package com.inappstory.sdk.stories.api.models.dialogstructure;

import java.util.Objects;

public class InputStructure {
    public BackgroundStructure background;
    public BorderStructure border;
    public TextStructure text;
    public PaddingStructure padding;
    public String type;

    public int limit() {
        return text.limit;
    }

    public int maxLines() {
        return Math.min(text.maxLines, 1);
    }
}