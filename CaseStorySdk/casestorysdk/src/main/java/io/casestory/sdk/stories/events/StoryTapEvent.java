package io.casestory.sdk.stories.events;

public class StoryTapEvent {

    public String getLink() {
        return link;
    }

    public String link;

    public boolean isForbidden() {
        return forbidden;
    }
    boolean forbidden = false;


    public StoryTapEvent(String link) {
        this.link = link;
    }

    public StoryTapEvent(int coordinate, boolean forbidden) {
        this.coordinate = coordinate;
        this.forbidden = forbidden;
    }
    public int getCoordinate() {
        return coordinate;
    }

    public StoryTapEvent(int coordinate) {
        this.coordinate = coordinate;
    }

    int coordinate;
}