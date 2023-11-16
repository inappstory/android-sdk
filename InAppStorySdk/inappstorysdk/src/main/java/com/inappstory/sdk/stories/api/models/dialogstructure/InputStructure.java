package com.inappstory.sdk.stories.api.models.dialogstructure;

import java.io.Serializable;

public class InputStructure implements Serializable {
    public BackgroundStructure background;
    public BorderStructure border;
    public TextStructure text;
    public PaddingStructure padding;
    public String type;
}