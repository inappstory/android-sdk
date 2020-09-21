package io.casestory.sdk.stories.serviceevents;

public class GeneratedWebPageEvent {
    public GeneratedWebPageEvent(String webData, int storyId) {
        this.webData = webData;
        this.storyId = storyId;
    }

    public int getStoryId() {
        return storyId;
    }

    int storyId;

    String webData;

    public String getWebData() {
        return webData;
    }
}
