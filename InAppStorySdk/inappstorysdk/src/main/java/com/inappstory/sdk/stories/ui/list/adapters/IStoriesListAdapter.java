package com.inappstory.sdk.stories.ui.list.adapters;

import android.view.View;

import com.inappstory.sdk.stories.ui.list.items.BaseStoriesListItem;
import com.inappstory.sdk.core.repository.stories.dto.PreviewStoryDTO;

import java.util.List;

public interface IStoriesListAdapter {

    void refreshList();

    void clearAllFavorites();

    void updateStoriesData(List<PreviewStoryDTO> data);

    void notify(PreviewStoryDTO data);

    BaseStoriesListItem getViewHolderItem(View view, int viewType);

    List<PreviewStoryDTO> getCurrentStories();
}
