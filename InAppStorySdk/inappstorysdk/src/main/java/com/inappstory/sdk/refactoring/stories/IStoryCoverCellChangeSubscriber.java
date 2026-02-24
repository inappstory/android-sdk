package com.inappstory.sdk.refactoring.stories;

import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;

import java.util.List;

public interface IStoryCoverCellChangeSubscriber {
    void onChange(List<StoryCoverDTO> covers);
}
