package com.inappstory.sdk.refactoring.stories.ui.list.states;

import java.util.ArrayList;
import java.util.List;

public class StoriesListFavoriteCellState {
    public StoriesListFavoriteCellState(List<StoriesListFavoriteCellItemState> covers) {
        if (covers != null)
            this.covers = new ArrayList<>(covers);
    }

    public List<StoriesListFavoriteCellItemState> covers() {
        return covers;
    }

    List<StoriesListFavoriteCellItemState> covers = new ArrayList<>();

    public StoriesListFavoriteCellState copy() {
        return new StoriesListFavoriteCellState(covers);
    }
}
