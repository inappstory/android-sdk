package io.casestory.sdk.stories.ui.list;

import java.util.List;

import io.casestory.sdk.stories.api.models.Image;

public class FavoriteImage {
    public int getId() {
        return id;
    }

    public List<Image> getImage() {
        return image;
    }

    private int id;

    private List<Image> image;
    public String backgroundColor;

    public FavoriteImage(int id, List<Image> image, String backgroundColor) {
        this.id = id;
        this.image = image;
        this.backgroundColor = backgroundColor;
    }
}
