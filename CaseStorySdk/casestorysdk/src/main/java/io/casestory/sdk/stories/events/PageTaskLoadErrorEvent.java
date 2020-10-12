package io.casestory.sdk.stories.events;

public class PageTaskLoadErrorEvent {
    private int id;
    private int index;

    public int getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public PageTaskLoadErrorEvent(int id, int index) {
        this.id = id;
        this.index = index;
    }
}
