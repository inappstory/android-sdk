package com.inappstory.sdk.stories.ui.list;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.imageloader.RoundedCornerLayout;
import com.inappstory.sdk.memcache.IGetBitmap;
import com.inappstory.sdk.stories.ui.views.IGetFavoriteListItem;

import java.util.List;

public final class StoriesListDefaultFavoriteItem implements IGetFavoriteListItem {

    AppearanceManager appearanceManager;
    Context context;

    RoundedCornerLayout group0;
    RoundedCornerLayout group1;
    RoundedCornerLayout group2;
    RoundedCornerLayout group3;

    AppCompatImageView image0;
    AppCompatImageView image1;
    AppCompatImageView image2;
    AppCompatImageView image3;
    View container;


    IStoriesListDefaultFavoriteItemPresenter manager = new StoriesListDefaultFavoriteItemPresenter();


    public StoriesListDefaultFavoriteItem(AppearanceManager appearanceManager, Context context) {
        this.context = context;
        this.appearanceManager = appearanceManager;
    }


    @Override
    public View getFavoriteItem() {
        return LayoutInflater.from(context)
                .inflate(R.layout.cs_story_list_inner_favorite, null, false);
    }

    @Override
    public void bindFavoriteItem(
            View favCell,
            List<Integer> backgroundColors,
            int count
    ) {
        bindViews(favCell);
        setContainerSize();
        setDefaultViews();
        setBackgroundColors(backgroundColors);
    }


    @Override
    public void setImages(
            View favCell,
            List<String> favoriteImages,
            List<Integer> backgroundColors,
            int count
    ) {
        switch (count) {
            case 1:
                setImage(0, favoriteImages.get(0), getColorOrTransparent(backgroundColors, 0));
                break;
            case 2:
                setImage(0, favoriteImages.get(0), getColorOrTransparent(backgroundColors, 0));
                setImage(1, favoriteImages.get(1), getColorOrTransparent(backgroundColors, 1));
                break;
            case 3:
                setImage(0, favoriteImages.get(0), getColorOrTransparent(backgroundColors, 0));
                setImage(1, favoriteImages.get(1), getColorOrTransparent(backgroundColors, 1));
                setImage(2, favoriteImages.get(2), getColorOrTransparent(backgroundColors, 2));
                break;
            default:
                setImage(0, favoriteImages.get(0), getColorOrTransparent(backgroundColors, 0));
                setImage(1, favoriteImages.get(1), getColorOrTransparent(backgroundColors, 1));
                setImage(2, favoriteImages.get(2), getColorOrTransparent(backgroundColors, 2));
                setImage(3, favoriteImages.get(3), getColorOrTransparent(backgroundColors, 3));
                break;
        }
        switch (count) {
            case 0:
                clearImage(getImageByIndex(0));
            case 1:
                clearImage(getImageByIndex(1));
            case 2:
                clearImage(getImageByIndex(2));
            case 3:
                clearImage(getImageByIndex(3));
                break;
        }
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
        group0.setRadius(appearanceManager.csListItemRadius(context) / 2);
        group1.setRadius(appearanceManager.csListItemRadius(context) / 2);
        group2.setRadius(appearanceManager.csListItemRadius(context) / 2);
        group3.setRadius(appearanceManager.csListItemRadius(context) / 2);

        image0.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image3.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    private void setContainerSize() {
        if (container == null) return;
        if (appearanceManager.getRealHeight(context) != null) {
            container.getLayoutParams().height = appearanceManager.getRealHeight(context);
        }
        if (appearanceManager.getRealWidth(context) != null) {
            container.getLayoutParams().width = appearanceManager.getRealWidth(context);
        }
        container.requestLayout();
    }

    private void setBackground(
            ImageView imageView,
            int backgroundColor
    ) {
        imageView.setBackgroundColor(backgroundColor);
    }

    private void setBackgroundColors(List<Integer> backgroundColors) {
        switch (backgroundColors.size()) {
            case 0:
                break;
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

    private AppCompatImageView getImageByIndex(int index) {
        switch (index) {
            case 1:
                return image1;
            case 2:
                return image2;
            case 3:
                return image3;
            default:
                return image0;
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

    private void setImage(
            final int index,
            final String path,
            final int backgroundColor
    ) {
        final AppCompatImageView imageView = getImageByIndex(index);
        if (path == null) {
            clearImage(imageView);
            imageView.setImageResource(0);
            imageView.setBackgroundColor(backgroundColor);
            imageView.setVisibility(View.VISIBLE);
            manager.storeImageLinkLocal(index, null);
            return;
        }
        if (manager.isSameImageLink(index, path)) clearImage(imageView);
        manager.getBitmap(index, path, new IGetBitmap() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
                imageView.setImageResource(0);
                imageView.setBackgroundColor(backgroundColor);
                imageView.setVisibility(View.VISIBLE);
            }
        });
    }
}
