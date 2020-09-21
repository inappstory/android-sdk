package io.casestory.sdk.stories.serviceevents;

public class PrevStoryPageFragmentEvent {
    public int getId() {
        return id;
    }

    int id;

    public PrevStoryPageFragmentEvent(int id, int index) {
        this.id = id;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    int index;

}
