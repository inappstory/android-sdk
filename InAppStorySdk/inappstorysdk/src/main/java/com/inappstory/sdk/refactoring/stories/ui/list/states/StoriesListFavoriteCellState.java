package com.inappstory.sdk.refactoring.stories.ui.list.states;

import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;

import java.util.ArrayList;
import java.util.List;

public class StoriesListFavoriteCellState {
    public StoriesListFavoriteCellState(List<StoryCoverDTO> covers) {
        if (covers != null)
            this.covers = new ArrayList<>(covers);
    }

    public List<StoryCoverDTO> covers() {
        return covers;
    }

    List<StoryCoverDTO> covers = new ArrayList<>();
}
