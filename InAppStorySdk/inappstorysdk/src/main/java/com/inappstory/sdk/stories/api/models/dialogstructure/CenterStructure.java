package com.inappstory.sdk.stories.api.models.dialogstructure;

import java.io.Serializable;

public class CenterStructure implements Serializable {
    public float x;
    public float y;

    public CenterStructure() {}

    public CenterStructure(float x, float y) {
        this.x = x;
        this.y = y;
    }

}