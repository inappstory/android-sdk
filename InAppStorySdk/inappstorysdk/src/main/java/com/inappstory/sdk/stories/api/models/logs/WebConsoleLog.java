package com.inappstory.sdk.stories.api.models.logs;

public class WebConsoleLog {
    public long timestamp;
    public String logType;
    public String storyId;
    public int slideIndex;
    public int contentType;
    public String id;
    public String message;
    public String sourceId;
    public int lineNumber;

    public WebConsoleLog(long timestamp,
                         String logType,
                         String storyId,
                         int slideIndex,
                         String id,
                         String message,
                         String sourceId,
                         int lineNumber) {
        this.timestamp = timestamp;
        this.logType = logType;
        this.storyId = storyId;
        this.slideIndex = slideIndex;
        this.id = id;
        this.message = message;
        this.sourceId = sourceId;
        this.lineNumber = lineNumber;
    }

    public WebConsoleLog() {
    }
}
