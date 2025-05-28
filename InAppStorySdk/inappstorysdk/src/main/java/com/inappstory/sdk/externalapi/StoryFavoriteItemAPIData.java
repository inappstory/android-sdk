package com.inappstory.sdk.externalapi;

import com.inappstory.sdk.core.data.IFavoriteItem;

public class StoryFavoriteItemAPIData {
    private StoryFavoriteItemAPIData(
            int id,
            String imageFilePath,
            String backgroundColor
    ) {
        this.id = id;
        this.imageFilePath = imageFilePath;
        this.backgroundColor = backgroundColor;
    }

    public int id;
    public String imageFilePath;
    public String backgroundColor;

    @Override
    public String toString() {
        return "StoryData{" +
                "id=" + id +
                ", backgroundColor='" + backgroundColor + '\'' +
                ", imageFilePath=" + imageFilePath +
                '}';
    }

    public StoryFavoriteItemAPIData(
            IFavoriteItem favoriteImage,
            String imageFilePath
    ) {
        this.id = favoriteImage.id();
        String hexColor = String.format("#%06X", (0xFFFFFF & favoriteImage.backgroundColor()));
        this.backgroundColor = hexColor;
        this.imageFilePath = imageFilePath;
    }
}
