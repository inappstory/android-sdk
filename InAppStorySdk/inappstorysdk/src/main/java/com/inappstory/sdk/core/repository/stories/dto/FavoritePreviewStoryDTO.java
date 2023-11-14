package com.inappstory.sdk.core.repository.stories.dto;

import androidx.annotation.Nullable;

import com.inappstory.sdk.core.models.api.Story;

import java.util.Objects;

public class FavoritePreviewStoryDTO implements IFavoritePreviewStoryDTO {
    private String backgroundColor;
    private IListOfImages images;
    private int id;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public String getImageUrl(int coverQuality) {
        IImageDTO proper = images.getProperImage(coverQuality);
        if (proper != null) return proper.getUrl();
        return null;
    }

    public FavoritePreviewStoryDTO(int id) {
        this.id = id;
    }

    public FavoritePreviewStoryDTO(IPreviewStoryDTO previewStoryDTO) {
        this.id = previewStoryDTO.getId();
        this.backgroundColor = previewStoryDTO.getBackgroundColor();
        this.images = previewStoryDTO.getImages();
    }

    public FavoritePreviewStoryDTO(Story story) {
        this.id = story.id;
        this.backgroundColor = story.getBackgroundColor();
        this.images = new ListOfImages(story.getImage());
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == this)
            return true;
        if (!(o instanceof FavoritePreviewStoryDTO))
            return false;
        FavoritePreviewStoryDTO other = (FavoritePreviewStoryDTO)o;
        return (this.getId() == other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
