package com.inappstory.sdk.stories.ui.list;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.imageloader.RoundedCornerLayout;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FileLoadProgressCallback;
import com.inappstory.sdk.stories.utils.Sizes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StoryFavoriteListItem extends BaseStoryListItem {

    public StoryFavoriteListItem(@NonNull View itemView, AppearanceManager manager) {
        super(itemView, manager, true, false);
        ViewGroup vg = itemView.findViewById(R.id.baseLayout);
        vg.removeAllViews();
        vg.addView(getDefaultFavoriteCell());
    }

    protected View getDefaultFavoriteCell() {
        if (getFavoriteListItem != null && getFavoriteListItem.getFavoriteItem() != null) {
            return getFavoriteListItem.getFavoriteItem();
        }
        View v = LayoutInflater.from(itemView.getContext()).inflate(R.layout.cs_story_list_inner_favorite, null, false);
        RoundedCornerLayout cv = v.findViewById(R.id.inner_cv);
        cv.setRadius(manager.csListItemRadius());
        cv.setBackgroundColor(Color.WHITE);
        title = v.findViewById(R.id.title);
        return v;
    }


    private void setImage(AppCompatImageView imageView, FavoriteImage image) {
        if (image.getImage() != null && InAppStoryService.isNotNull()) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageLoader.getInstance().displayImage(image.getUrl(), -1, imageView,
                    InAppStoryService.getInstance().getFastCache());
        } else {
            imageView.setBackgroundColor(image.getBackgroundColor());
        }
    }

    private void loadFavoriteImages(final LoadFavoriteImagesCallback callback, final int count) {
        final List<String> downloadImages = new ArrayList<>();
        final int[] i = {0};
        RunnableCallback runnableCallback = new RunnableCallback() {
            @Override
            public void run(String path) {
                if (InAppStoryService.isNull()) return;
                downloadImages.add(path);
                i[0]++;
                if (i[0] >= count)
                    callback.onLoad(downloadImages);
                else {
                    downloadFileAndSendToInterface(InAppStoryService.getInstance()
                            .getFavoriteImages().get(i[0]).getUrl(), this);
                }
            }

            @Override
            public void error() {
                if (InAppStoryService.isNull()) return;
                downloadImages.add(null);
                i[0]++;
                if (i[0] >= count)
                    callback.onLoad(downloadImages);
                else {
                    downloadFileAndSendToInterface(InAppStoryService.getInstance()
                            .getFavoriteImages().get(i[0]).getUrl(), this);
                }
            }
        };
        downloadFileAndSendToInterface(InAppStoryService.getInstance()
                .getFavoriteImages().get(0).getUrl(), runnableCallback);
    }

    interface LoadFavoriteImagesCallback {
        void onLoad(List<String> downloadImages);
    }

    @Override
    public void bind(Integer id, String titleText, Integer titleColor, String sourceText,
                     String imageUrl, Integer backgroundColor,
                     boolean isOpened, boolean hasAudio, String videoUrl, ClickCallback callback) {

    }

    public void bindFavorite() {

        if (getFavoriteListItem != null
                && InAppStoryService.isNotNull()
                && getFavoriteListItem.getFavoriteItem() != null) {
            int count = InAppStoryService.getInstance().getFavoriteImages().size();
            final List<Integer> backgroundColors = new ArrayList<>();
            for (int j = 0; j < count; j++) {
                backgroundColors.add(InAppStoryService.getInstance().getFavoriteImages().get(j).getBackgroundColor());
            }
            getFavoriteListItem.bindFavoriteItem(itemView, backgroundColors, count);
            loadFavoriteImages(new LoadFavoriteImagesCallback() {
                @Override
                public void onLoad(List<String> downloadImages) {
                    getFavoriteListItem.setImages(itemView, downloadImages, backgroundColors,
                            downloadImages.size());
                }
            }, count);
            return;
        }
        RelativeLayout imageViewLayout = itemView.findViewById(R.id.container);
        boolean lpC = false;
        View outerLayout = itemView.findViewById(R.id.outerLayout);
        if (manager.csListItemHeight() != null) {
            outerLayout.getLayoutParams().height = manager.csListItemHeight();
            lpC = true;
        }
        if (manager.csListItemWidth() != null) {
            outerLayout.getLayoutParams().width = manager.csListItemWidth();
            lpC = true;
        }
        if (lpC) itemView.findViewById(R.id.outerLayout).requestLayout();
        if (title != null) {
            title.setText("Favorites");
            if (manager.csCustomFont() != null) {
                title.setTypeface(manager.csCustomFont());
            }
            title.setTextColor(manager.csListItemTitleColor());
        }
        List<FavoriteImage> favImages = InAppStoryService.getInstance().getFavoriteImages();
        int halfHeight = Sizes.dpToPxExt(55);
        int halfWidth = Sizes.dpToPxExt(55);
        int height = Sizes.dpToPxExt(110);
        int width = Sizes.dpToPxExt(110);
        if (manager.csListItemInterface() == null || (manager.csListItemInterface().getView() == null
                && manager.csListItemInterface().getVideoView() == null)) {
            if (manager.csListItemHeight() != null) {
                height = manager.csListItemHeight() - Sizes.dpToPxExt(10);
                halfHeight = manager.csListItemHeight() / 2 - Sizes.dpToPxExt(5);
            }
            if (manager.csListItemWidth() != null) {
                width = manager.csListItemWidth() - Sizes.dpToPxExt(10);
                halfWidth = manager.csListItemWidth() / 2 - Sizes.dpToPxExt(5);
            }
        }
        if (favImages.size() > 0 && imageViewLayout != null) {
            AppCompatImageView image1 = new AppCompatImageView(itemView.getContext());
            AppCompatImageView image2 = new AppCompatImageView(itemView.getContext());
            AppCompatImageView image3 = new AppCompatImageView(itemView.getContext());
            AppCompatImageView image4 = new AppCompatImageView(itemView.getContext());

            RelativeLayout.LayoutParams piece2;
            RelativeLayout.LayoutParams piece3;
            RelativeLayout.LayoutParams piece4;
            switch (favImages.size()) {
                case 1:
                    image1.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT));
                    setImage(image1, favImages.get(0));
                    imageViewLayout.addView(image1);
                    break;
                case 2:
                    piece2 = new RelativeLayout.LayoutParams(halfWidth,
                            RelativeLayout.LayoutParams.MATCH_PARENT);
                    image1.setLayoutParams(new RelativeLayout.LayoutParams(width - halfWidth,
                            RelativeLayout.LayoutParams.MATCH_PARENT));
                    piece2.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                    image2.setLayoutParams(piece2);

                    setImage(image1, favImages.get(0));
                    setImage(image2, favImages.get(1));
                    imageViewLayout.addView(image1);
                    imageViewLayout.addView(image2);
                    break;
                case 3:
                    piece2 = new RelativeLayout.LayoutParams(halfWidth,
                            height - halfHeight);
                    piece3 = new RelativeLayout.LayoutParams(halfWidth,
                            halfHeight);
                    piece2.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                    piece3.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                    piece3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    image1.setLayoutParams(new RelativeLayout.LayoutParams(width - halfWidth,
                            RelativeLayout.LayoutParams.MATCH_PARENT));
                    image2.setLayoutParams(piece2);
                    image3.setLayoutParams(piece3);
                    setImage(image1, favImages.get(0));
                    setImage(image2, favImages.get(1));
                    setImage(image3, favImages.get(2));
                    imageViewLayout.addView(image1);
                    imageViewLayout.addView(image2);
                    imageViewLayout.addView(image3);
                    break;
                default:

                    piece2 = new RelativeLayout.LayoutParams(halfWidth,
                            height - halfHeight);
                    piece3 = new RelativeLayout.LayoutParams(width - halfWidth,
                            halfHeight);
                    piece4 = new RelativeLayout.LayoutParams(halfWidth,
                            halfHeight);

                    piece2.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                    piece3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    piece4.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                    piece4.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    image1.setLayoutParams(new RelativeLayout.LayoutParams(width - halfWidth,
                            height - halfHeight));
                    image2.setLayoutParams(piece2);
                    image3.setLayoutParams(piece3);
                    image4.setLayoutParams(piece4);
                    setImage(image1, favImages.get(0));
                    setImage(image2, favImages.get(1));
                    setImage(image3, favImages.get(2));
                    setImage(image4, favImages.get(3));
                    imageViewLayout.addView(image1);
                    imageViewLayout.addView(image2);
                    imageViewLayout.addView(image3);
                    imageViewLayout.addView(image4);
                    break;

            }
        }
    }

    @Override
    public void bindUGC() {

    }

    interface RunnableCallback {
        void run(String path);

        void error();
    }

    private void downloadFileAndSendToInterface(String url, final RunnableCallback callback) {
        if (InAppStoryService.isNull()) return;
        Downloader.downloadFileBackground(url, InAppStoryService.getInstance().getFastCache(), new FileLoadProgressCallback() {
            @Override
            public void onProgress(int loadedSize, int totalSize) {

            }

            @Override
            public void onSuccess(File file) {
                final String path = file.getAbsolutePath();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (getListItem != null) {
                            callback.run(path);
                        }
                    }
                });
            }

            @Override
            public void onError() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (getListItem != null) {
                            callback.error();
                        }
                    }
                });
            }
        });
    }

    public Integer backgroundColor;
    public ClickCallback callback;
}
