package com.inappstory.sdk.inappmessage;

import java.util.ArrayList;
import java.util.List;

public class InAppMessagePreloadSettings {
    private List<String> inAppMessageIds;

    private List<String> tags;

    public InAppMessagePreloadSettings(
    ) {
    }

    public List<String> inAppMessageIds() {
        return inAppMessageIds;
    }

    public List<String> tags() {
        return tags;
    }


    public InAppMessagePreloadSettings inAppMessageIds(List<String> inAppMessageIds) {
        if (inAppMessageIds != null)
            this.inAppMessageIds = new ArrayList<>(inAppMessageIds);
        return this;
    }

    public InAppMessagePreloadSettings tags(List<String> tags) {
        if (tags != null)
            this.tags = new ArrayList<>(tags);
        return this;
    }
}
