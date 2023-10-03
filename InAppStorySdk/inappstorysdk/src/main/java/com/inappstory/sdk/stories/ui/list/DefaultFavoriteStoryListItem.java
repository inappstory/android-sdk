package com.inappstory.sdk.stories.ui.list;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.imagememcache.GetBitmapFromCacheWithFilePath;
import com.inappstory.sdk.imagememcache.IGetBitmapFromMemoryCache;
import com.inappstory.sdk.imagememcache.IGetBitmapFromMemoryCacheError;
import com.inappstory.sdk.stories.ui.views.IGetFavoriteListItem;
import com.inappstory.sdk.stories.ui.views.RoundedCornerLayout;

import java.util.HashMap;
import java.util.List;

public final class DefaultFavoriteStoryListItem implements IGetFavoriteListItem {

    AppearanceManager manager;

    RoundedCornerLayout group0;
    RoundedCornerLayout group1;
    RoundedCornerLayout group2;
    RoundedCornerLayout group3;

    AppCompatImageView image0;
    AppCompatImageView image1;
    AppCompatImageView image2;
    AppCompatImageView image3;
    View container;


    Context context;

    DefaultFavoriteStoryListItem(AppearanceManager manager, Context context) {
        this.context = context;
        this.manager = manager;
    }

    private void bindViews(View parent) {
        group0 = parent.findViewById(R.id.container1);
        group1 = parent.findViewById(R.id.container2);
        group2 = parent.findViewById(R.id.container3);
        group3 = parent.findViewById(R.id.container4);
        container = parent.findViewById(R.id.outerLayout);
        image0 = parent.findViewById(R.id.image1);
        image1 = parent.findViewById(R.id.image2);
        image2 = parent.findViewById(R.id.image3);
        image3 = parent.findViewById(R.id.image4);
    }

    private void setDefaultViews() {
        group0.setRadius(manager.csListItemRadius(context) / 2);
        group1.setRadius(manager.csListItemRadius(context) / 2);
        group2.setRadius(manager.csListItemRadius(context) / 2);
        group3.setRadius(manager.csListItemRadius(context) / 2);

        image0.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image3.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    private void setContainerSize() {
        if (container == null) return;
        if (manager.getRealHeight(context) != null) {
            container.getLayoutParams().height = manager.getRealHeight(context);
        }
        if (manager.getRealWidth(context) != null) {
            container.getLayoutParams().width = manager.getRealWidth(context);
        }
        container.requestLayout();
    }

    @Override
    public View getFavoriteItem() {
        return LayoutInflater.from(context)
                .inflate(R.layout.cs_story_list_inner_favorite, null, false);
    }

    @Override
    public void bindFavoriteItem(View favCell,
                                 List<Integer> backgroundColors,
                                 int count
    ) {
        bindViews(favCell);
        setContainerSize();
        setDefaultViews();
        setBackgroundColors(backgroundColors);
    }

    private void setBackground(ImageView imageView, int backgroundColor) {
        imageView.setBackgroundColor(backgroundColor);
    }

    private void setBackgroundColors(List<Integer> backgroundColors) {
        switch (backgroundColors.size()) {
            case 1:
                setBackground(image0, backgroundColors.get(0));
                break;
            case 2:
                setBackground(image0, backgroundColors.get(0));
                setBackground(image1, backgroundColors.get(1));
                break;
            case 3:
                setBackground(image0, backgroundColors.get(0));
                setBackground(image1, backgroundColors.get(1));
                setBackground(image2, backgroundColors.get(2));
                break;
            default:
                setBackground(image0, backgroundColors.get(0));
                setBackground(image1, backgroundColors.get(1));
                setBackground(image2, backgroundColors.get(2));
                setBackground(image3, backgroundColors.get(3));
                break;

        }
    }

    @Override
    public void setImages(
            View favCell,
            List<String> favoriteImages,
            List<Integer> backgroundColors,
            int count
    ) {
        switch (favoriteImages.size()) {
            case 1:
                setImage(image0, favoriteImages.get(0), getColorOrTransparent(backgroundColors, 0));
                break;
            case 2:
                setImage(image0, favoriteImages.get(0), getColorOrTransparent(backgroundColors, 0));
                setImage(image1, favoriteImages.get(1), getColorOrTransparent(backgroundColors, 1));
                break;
            case 3:
                setImage(image0, favoriteImages.get(0), getColorOrTransparent(backgroundColors, 0));
                setImage(image1, favoriteImages.get(1), getColorOrTransparent(backgroundColors, 1));
                setImage(image2, favoriteImages.get(2), getColorOrTransparent(backgroundColors, 2));
                break;
            default:
                setImage(image0, favoriteImages.get(0), getColorOrTransparent(backgroundColors, 0));
                setImage(image1, favoriteImages.get(1), getColorOrTransparent(backgroundColors, 1));
                setImage(image2, favoriteImages.get(2), getColorOrTransparent(backgroundColors, 2));
                setImage(image3, favoriteImages.get(3), getColorOrTransparent(backgroundColors, 3));
                break;
        }
    }

    private int getColorOrTransparent(List<Integer> array, int index) {
        if (index < 0 || index >= array.size()) return Color.TRANSPARENT;
        return array.get(index);
    }

    private void clearImage(AppCompatImageView imageView) {
        imageView.setImageBitmap(null);
        imageView.setVisibility(View.INVISIBLE);
    }

    private HashMap<ImageView, String> localLink = new HashMap<>();
    private void setImage(
            final AppCompatImageView imageView,
            final String path,
            final int backgroundColor
    ) {

        if (path == null) {
            clearImage(imageView);
            imageView.setImageResource(0);
            imageView.setBackgroundColor(backgroundColor);
            imageView.setVisibility(View.VISIBLE);
            localLink.put(imageView, null);
            return;
        }
        String currentPath = localLink.get(imageView);
        if (currentPath == null || !currentPath.equals(path)) {
            clearImage(imageView);
        }
        new GetBitmapFromCacheWithFilePath(
                path,
                new IGetBitmapFromMemoryCache() {
                    @Override
                    public void get(final Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                        imageView.setVisibility(View.VISIBLE);
                        localLink.put(imageView, path);
                    }
                },
                new IGetBitmapFromMemoryCacheError() {
                    @Override
                    public void onError() {
                        imageView.setImageResource(0);
                        imageView.setBackgroundColor(backgroundColor);
                        imageView.setVisibility(View.VISIBLE);
                        localLink.put(imageView, null);
                    }
                }
        ).get();
    }
}
