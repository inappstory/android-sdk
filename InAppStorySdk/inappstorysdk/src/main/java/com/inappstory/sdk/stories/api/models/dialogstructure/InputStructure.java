package com.inappstory.sdk.stories.api.models.dialogstructure;

import java.util.Objects;

public class InputStructure {
    public BackgroundStructure background;
    public BorderStructure border;
    public TextStructure text;
    public PaddingStructure padding;
    public String type;

    private int getDefaultMaxLines() {
        if (Objects.equals(type, "email")) {
            return 1;
        } else if (Objects.equals(type, "tel")) {
            return 1;
        } else {
            return 3;
        }
    }

    public Integer limit() {
        return text.limit;
    }

    public int maxLines() {
        return text.maxLines != null ? text.maxLines : getDefaultMaxLines();
    }
}