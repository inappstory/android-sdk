package com.inappstory.sdk.core.repository.stories.dto;

import com.inappstory.sdk.core.models.api.Image;

import java.util.ArrayList;
import java.util.List;

public class ListOfImages implements IListOfImages {

    public ListOfImages(List<Image> images) {
        this.images = new ArrayList<>();
        if (images == null) return;
        for (Image image : images) {
            this.images.add(new ImageDTO(image));
        }
    }

    private List<IImageDTO> images;

    private final String TYPE_SMALL = "s";
    private final String TYPE_MEDIUM = "m";
    public static final String TYPE_HIGH = "h";

    public static final int QUALITY_MEDIUM = 1;
    public static final int QUALITY_HIGH = 2;

    private IImageDTO defaultImage() {
        if (images.size() > 0) return images.get(0);
        return null;
    }

    @Override
    public IImageDTO getProperImage(int quality) {
        String q = (quality == QUALITY_HIGH) ? TYPE_HIGH : TYPE_MEDIUM;
        for (IImageDTO img : images) {
            if (img.getType().equals(q)) return img;
        }
        if (quality == QUALITY_HIGH)
            return getProperImage(QUALITY_MEDIUM);
        return defaultImage();
    }


}
