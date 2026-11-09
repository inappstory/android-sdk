package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class SlideLinkObject {
    public SlideLinkObject(String type, StoryLink link) {
        this.type = type;
        this.link = link;
    }


    public SlideLinkObject(String type, StoryLink link,
                           String elementId) {
        this.type = type;
        this.link = link;
        this.elementId = elementId;
    }

    public SlideLinkObject() {

    }

    public String getType() {
        return type;
    }

    public StoryLink getLink() {
        return link;
    }

    public String getElementId() {
        return elementId;
    }

    @SerializedName(ModelFields.SlideLinkObject.elementId)
    public String elementId;
    @SerializedName(ModelFields.SlideLinkObject.type)
    public String type;
    @SerializedName(ModelFields.SlideLinkObject.link)
    public StoryLink link;

}
