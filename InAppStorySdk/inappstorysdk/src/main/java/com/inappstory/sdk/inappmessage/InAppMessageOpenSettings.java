package com.inappstory.sdk.inappmessage;

import java.util.ArrayList;
import java.util.List;

public class InAppMessageOpenSettings {
    private String id;

    private boolean showOnlyIfLoaded = false;

    private String event;

    private List<String> tags;

    private boolean useTargeting;

    @Override
    public String toString() {
        return "IAM_DATA {" + "id=" + id + ", event='" + event + '\'' + '}';
    }

    public InAppMessageOpenSettings(
    ) {
    }

    public InAppMessageOpenSettings(
            String id,
            boolean showOnlyIfLoaded,
            String event,
            List<String> tags
    ) {
        this.id = id;
        this.showOnlyIfLoaded = showOnlyIfLoaded;
        this.event = event;
        if (tags != null) {
            this.tags = new ArrayList<>(tags);
        }
    }


    public String id() {
        return id;
    }

    public String event() {
        return event;
    }

    public boolean hasTargeting() {
        return useTargeting;
    }

    public InAppMessageOpenSettings useTargeting() {
        this.useTargeting = true;
        return this;
    }

    public List<String> tags() {
        return tags;
    }

    public boolean showOnlyIfLoaded() {
        return showOnlyIfLoaded;
    }

    public InAppMessageOpenSettings id(String id) {
        this.id = id;
        return this;
    }

    public InAppMessageOpenSettings showOnlyIfLoaded(boolean showOnlyIfLoaded) {
        this.showOnlyIfLoaded = showOnlyIfLoaded;
        return this;
    }

    public InAppMessageOpenSettings event(String event) {
        this.event = event;
        return this;
    }

    public InAppMessageOpenSettings tags(List<String> tags) {
        if (tags != null) {
            this.tags = new ArrayList<>(tags);
            if (!tags.isEmpty())
                useTargeting = true;
        }
        return this;
    }
}
