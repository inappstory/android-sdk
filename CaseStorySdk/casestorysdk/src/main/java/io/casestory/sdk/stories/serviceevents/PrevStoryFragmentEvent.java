package io.casestory.sdk.stories.serviceevents;

public class PrevStoryFragmentEvent {
    public int getId() {
        return id;
    }

    int id;

    public PrevStoryFragmentEvent(int id) {
        this.id = id;
    }
}
