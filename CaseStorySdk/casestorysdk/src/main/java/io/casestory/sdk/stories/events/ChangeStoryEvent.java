package io.casestory.sdk.stories.events;

import android.util.Log;

/**
 * Created by Paperrose on 08.07.2018.
 */

public class ChangeStoryEvent {
    public int getIndex() {
        return index;
    }

    private int index;
    public ChangeStoryEvent(int index) {
        this.index = index;
    }

    public int getId() {
        return id;
    }

    private int id;
    public ChangeStoryEvent(int id, int index) {
        this.index = index;
        this.id = id;
    }
}
