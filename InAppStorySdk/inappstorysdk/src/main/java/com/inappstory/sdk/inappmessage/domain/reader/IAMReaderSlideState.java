package com.inappstory.sdk.inappmessage.domain.reader;

public class IAMReaderSlideState {

    int contentStatus = 0; //0 - loading, 1 - loaded, -1 - failed

    int slideJSStatus = 0; //0 - none, 1 - loaded, 2 - started, 3 - paused?

    public int contentStatus() {
        return contentStatus;
    }

    public IAMReaderSlideState contentStatus(int contentStatus) {
        this.contentStatus = contentStatus;
        return this;
    }

    public int slideJSStatus() {
        return slideJSStatus;
    }

    public IAMReaderSlideState slideJSStatus(int slideJSStatus) {
        this.slideJSStatus = slideJSStatus;
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
                .contentStatus(this.contentStatus)
                .slideJSStatus(this.slideJSStatus);
    }
}
