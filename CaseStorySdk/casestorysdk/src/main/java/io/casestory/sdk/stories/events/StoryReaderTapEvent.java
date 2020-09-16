package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 08.07.2018.
 */

public class StoryReaderTapEvent {

    public String getLink() {
        return link;
    }

    public String link;

    public boolean isForbidden() {
        return forbidden;
    }
    boolean forbidden = false;


    public StoryReaderTapEvent(String link) {
        this.link = link;
    }

    public StoryReaderTapEvent(int coordinate, boolean forbidden) {
        this.coordinate = coordinate;
        this.forbidden = forbidden;
    }
    public int getCoordinate() {
        return coordinate;
    }

    public StoryReaderTapEvent(int coordinate) {
        this.coordinate = coordinate;
    }

    int coordinate;
}
