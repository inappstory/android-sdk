package io.casestory.sdk.stories.serviceevents;

public class ShareClickEvent {
    public ShareClickEvent(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    int id;
}
