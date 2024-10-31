package com.inappstory.sdk.inappmessage;

public class InAppMessageOpenSettings {
    private Integer id;

    private boolean showOnlyIfLoaded = false;

    private String event;

    public InAppMessageOpenSettings(
            Integer id,
            boolean showOnlyIfLoaded,
            String event
    ) {
        this.id = id;
        this.showOnlyIfLoaded = showOnlyIfLoaded;
        this.event = event;
    }

    public Integer id() {
        return id;
    }

    public String event() {
        return event;
    }

    public boolean showOnlyIfLoaded() {
        return showOnlyIfLoaded;
    }

    public InAppMessageOpenSettings id(Integer id) {
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
}
