package com.inappstory.sdk.refactoring.core.utils.stedata;

import com.inappstory.sdk.refactoring.core.utils.observers.ISTEData;

import java.io.Serializable;

public class ContentIdWithIndex implements Serializable, ISTEData {
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

    public ContentIdWithIndex copy() {
        return new ContentIdWithIndex(this.id, this.index);
    }

}
