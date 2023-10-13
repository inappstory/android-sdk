package com.inappstory.sdk.stories.ui.list.items.favorite;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.ui.list.items.IStoriesListFavoriteItem;
import com.inappstory.sdk.stories.ui.list.items.BaseStoriesListItem;
import com.inappstory.sdk.stories.uidomain.list.utils.FilePathsFromLinks;
import com.inappstory.sdk.stories.uidomain.list.items.favorite.IStoriesListFavoriteItemPresenter;
import com.inappstory.sdk.stories.uidomain.list.items.favorite.StoriesListFavoriteItemPresenter;

import java.util.ArrayList;
import java.util.List;

public class StoriesListFavoriteItem
        extends BaseStoriesListItem
        implements IStoriesListFavoriteItem {

    private IStoriesListFavoriteItemPresenter manager = new StoriesListFavoriteItemPresenter();

    public StoriesListFavoriteItem(@NonNull View itemView, AppearanceManager appearanceManager) {
        super(itemView, appearanceManager);
        ViewGroup vg = itemView.findViewById(R.id.baseLayout);
        vg.removeAllViews();
        vg.addView(getDefaultFavoriteCell());
    }

    protected View getDefaultFavoriteCell() {
        return getFavoriteListItem.getFavoriteItem();
    }



    @Override
    public void bindFavorite(List<FavoriteImage> favoriteImages) {
        int count = Math.min(favoriteImages.size(), getFavoriteListItem.count());
        final List<Integer> backgroundColors = new ArrayList<>();
        for (int j = 0; j < count; j++) {
            backgroundColors.add(favoriteImages.get(j).getBackgroundColor());
        }
        getFavoriteListItem.bindFavoriteItem(itemView, backgroundColors, count);
    }

    @Override
    public void setImages(List<FavoriteImage> favoriteImages) {
        int maxCount = getFavoriteListItem.count();
        if (maxCount <= 0) maxCount = 4;
        maxCount = Math.min(maxCount, favoriteImages.size());
        List<String> limitedLinks = new ArrayList<>();
        final List<Integer> backgroundColors = new ArrayList<>();
        for (int i = 0; i < maxCount; i++) {
            limitedLinks.add(favoriteImages.get(i).getUrl());
            backgroundColors.add(favoriteImages.get(i).getBackgroundColor());
        }
        manager.getFilePathsFromLinks(limitedLinks, new FilePathsFromLinks() {
            @Override
            public void onSuccess(List<String> filesAbsolutePaths) {
                getFavoriteListItem.setImages(
                        itemView,
                        filesAbsolutePaths,
                        backgroundColors,
                        filesAbsolutePaths.size()
                );
            }
        });
    }
}
