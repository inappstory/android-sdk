package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.SerializedName;

public class SendExceptionObject {
    @SerializedName("m")
    String message;
    @SerializedName("f")
    String file;
    @SerializedName("l")
    Integer line;
    @SerializedName("t")
    String trace;


    public SendExceptionObject(String message, String file, Integer line, String trace) {
        this.message = message;
        this.file = file;
        this.line = line;
        this.trace = trace;
    }
}
