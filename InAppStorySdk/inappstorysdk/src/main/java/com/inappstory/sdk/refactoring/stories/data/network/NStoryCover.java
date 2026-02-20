package com.inappstory.sdk.refactoring.stories.data.network;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.List;

public class NStoryCover {
    @Required
    public int id;

    @SerializedName("image")
    public List<Image> image;

    @SerializedName("background_color")
    public String backgroundColor;

    public String imageCoverByQuality(int quality) {
        if (image == null || image.isEmpty())
            return null;
        String q = Image.TYPE_MEDIUM;
        if (quality == Image.QUALITY_HIGH) {
            q = Image.TYPE_HIGH;
        }
        for (Image img : image) {
            if (img.getType().equals(q)) return img.getUrl();
        }
        return image.get(0).getUrl();
    }

}
