package io.casestory.sdk.stories.events;

import android.util.Log;

public class ContentLoadedEvent {
    public boolean isEmpty() {
        return empty;
    }

    boolean empty;

    public ContentLoadedEvent(boolean isEmpty) {
        Log.d("ContentLoadedEvent", "" + isEmpty);
        this.empty = isEmpty;
    }
}
