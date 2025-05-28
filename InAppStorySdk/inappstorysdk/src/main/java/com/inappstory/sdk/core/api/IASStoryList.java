package com.inappstory.sdk.core.api;

import android.content.Context;

import com.inappstory.sdk.AppearanceManager;

import java.util.List;

public interface IASStoryList {
    void load(
            String feed,
            String uniqueId,
            boolean hasFavorite,
            boolean isFavorite,
            List<String> tags
    );

    void openStoryReader(
            Context context,
            String uniqueKey,
            int storyId,
            AppearanceManager appearanceManager
    );

    void showFavoriteItem(
            String uniqueId
    );

    void updateVisiblePreviews(
            List<Integer> storyIds,
            String uniqueId
    );
}
