package com.inappstory.sdk.core.repository.stories.dto;

import com.inappstory.sdk.core.models.api.PayloadObject;

public class PayloadObjectDTO {
    public PayloadObjectDTO(String eventType, int slideIndex, String payload) {
        this.eventType = eventType;
        this.slideIndex = slideIndex;
        this.payload = payload;
    }
    public PayloadObjectDTO(PayloadObject object) {
        this.eventType = object.getEventType();
        this.slideIndex = object.getSlideIndex();
        this.payload = object.getPayload();
    }

    private String eventType;

    public int getSlideIndex() {
        return slideIndex;
    }

    private int slideIndex;
    private String payload;

    String getEventType() {
        return eventType;
    }


    public String getPayload() {
        return payload;
    }
}
