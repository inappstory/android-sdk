package io.casestory.sdk.stories.events;

import java.util.List;

import io.casestory.sdk.stories.api.models.Image;

/**
 * Created by Paperrose on 08.07.2018.
 */

public class StoryFavEvent {
    public List<Image> getImage() {
        return image;
    }

    private List<Image> image;
    public int getId() {
        return id;
    }

    private int id;

    public boolean isFav() {
        return fav;
    }

    private boolean fav;

    public String getBackgroundColor() {
        return backgroundColor;
    }

    private String backgroundColor;

    public StoryFavEvent(int id, boolean fav, List<Image> image, String backgroundColor) {
        this.id = id;
        this.fav = fav;
        this.image = image;
        this.backgroundColor = backgroundColor;
    }
}
