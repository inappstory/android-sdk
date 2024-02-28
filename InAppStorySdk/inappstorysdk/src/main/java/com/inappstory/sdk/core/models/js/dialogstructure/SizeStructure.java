package com.inappstory.sdk.core.models.js.dialogstructure;

public class SizeStructure {
    public float width;
    public float height;
    public float left;
    public CenterStructure center;

    public SizeStructure() {
    }

    public SizeStructure(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    public float left() {
        return left;
    }

    public CenterStructure center() {
        return center;
    }
}