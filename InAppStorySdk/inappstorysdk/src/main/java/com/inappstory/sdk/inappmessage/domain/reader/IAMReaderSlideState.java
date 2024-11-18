package com.inappstory.sdk.inappmessage.domain.reader;

public class IAMReaderSlideState {

    int contentStatus = 0; //0 - loading, 1 - loaded, -1 - failed

    public int contentStatus() {
        return contentStatus;
    }

    public IAMReaderSlideState contentStatus(int contentStatus) {
        this.contentStatus = contentStatus;
        return this;
    }


    String content;

    public String content() {
        return content;
    }

    public IAMReaderSlideState content(String content) {
        this.content = content;
        return this;
    }


    public IAMReaderSlideState copy() {
        return new IAMReaderSlideState()
                .content(this.content)
                .contentStatus(this.contentStatus);
    }
}
