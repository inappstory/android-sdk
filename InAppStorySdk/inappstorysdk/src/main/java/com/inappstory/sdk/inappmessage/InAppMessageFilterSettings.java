package com.inappstory.sdk.inappmessage;

import java.util.ArrayList;
import java.util.List;

public class InAppMessageFilterSettings {
    private Integer id;


    private String event;

    private List<String> tags;

    public InAppMessageFilterSettings(
    ) {}

    public InAppMessageFilterSettings(
            Integer id,
            String event,
            List<String> tags
    ) {
        this.id = id;
        this.event = event;
        if (tags != null) {
            this.tags = new ArrayList<>(tags);
        }
    }


    public Integer id() {
        return id;
    }

    public String event() {
        return event;
    }

    public List<String> tags() {
        return tags;
    }

    public InAppMessageFilterSettings id(Integer id) {
        this.id = id;
        return this;
    }

    public InAppMessageFilterSettings event(String event) {
        this.event = event;
        return this;
    }

    public InAppMessageFilterSettings tags(List<String> tags) {
        if (tags != null)
            this.tags = new ArrayList<>(tags);
        return this;
    }
}
