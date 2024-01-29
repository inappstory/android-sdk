package com.inappstory.sdk.stories.stackfeed;

public class StackStoryCover implements IStackStoryCover {
    public int backgroundColor() {
        return backgroundColor;
    }

    public String imageCoverPath() {
        return imageCoverPath;
    }

    public String videoCoverPath() {
        return videoCoverPath;
    }

    public StackStoryCover(
            int backgroundColor,
            String imageCoverPath,
            String videoCoverPath
    ) {
        this.backgroundColor = backgroundColor;
        this.imageCoverPath = imageCoverPath;
        this.videoCoverPath = videoCoverPath;
    }

    int backgroundColor;

    public void imageCoverPath(String imageCoverPath) {
        this.imageCoverPath = imageCoverPath;
    }

    public void videoCoverPath(String videoCoverPath) {
        this.videoCoverPath = videoCoverPath;
    }

    String imageCoverPath;
    String videoCoverPath;
}
