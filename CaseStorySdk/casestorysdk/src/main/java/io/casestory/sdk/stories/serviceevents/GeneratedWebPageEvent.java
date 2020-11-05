package io.casestory.sdk.stories.serviceevents;

public class GeneratedWebPageEvent {
    public GeneratedWebPageEvent(String webData, String layout, int storyId) {
        this.webData = webData;
        this.layout = layout;
        this.storyId = storyId;
    }

    public int getStoryId() {
        return storyId;
    }

    int storyId;

    String webData;

    public String getLayout() {
        return layout;
    }

    String layout;

    public String getWebData() {
        return webData;
    }
}
