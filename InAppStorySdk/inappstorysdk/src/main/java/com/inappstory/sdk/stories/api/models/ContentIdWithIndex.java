package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.inappmessage.domain.stedata.STEData;

import java.io.Serializable;

public class ContentIdWithIndex implements Serializable, STEData {
    private final int id;

    public int id() {
        return id;
    }

    public int index() {
        return index;
    }

    public void index(int index) {
        this.index = index;
    }

    private int index;

    public ContentIdWithIndex(int id, int index) {
        this.id = id;
        this.index = index;
    }
}
