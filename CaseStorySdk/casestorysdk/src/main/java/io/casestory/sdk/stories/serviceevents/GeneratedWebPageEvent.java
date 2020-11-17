package io.casestory.sdk.stories.serviceevents;

public class GeneratedWebPageEvent {
    public GeneratedWebPageEvent(String webData, String layout, int storyId) {
        this.webData = webData;
        this.layout = layout;
        this.storyId = storyId;
    }

    public String getLayout() {
        return layout;
    }

    String layout;

    public int getStoryId() {
        return storyId;
    }

    int storyId;

    String webData;

    public String getWebData() {
        return webData;
    }
}
