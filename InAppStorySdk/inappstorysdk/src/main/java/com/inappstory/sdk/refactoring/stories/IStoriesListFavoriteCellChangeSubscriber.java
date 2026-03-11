package com.inappstory.sdk.refactoring.stories;

import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;

import java.util.List;

public interface IStoriesListFavoriteCellChangeSubscriber {
    void onChange(List<StoryCoverDTO> covers);
}
