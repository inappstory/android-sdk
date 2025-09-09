package com.inappstory.sdk.core.data.models;

import com.inappstory.sdk.core.data.IUserOptions;

public class UserOptions implements IUserOptions {
    private String pos;

    public UserOptions pos(String posName) {
        this.pos = posName;
        return this;
    }

    @Override
    public String pos() {
        return pos;
    }
}
