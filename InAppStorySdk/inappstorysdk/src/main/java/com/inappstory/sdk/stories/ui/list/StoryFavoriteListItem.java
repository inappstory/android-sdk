package com.inappstory.sdk.stories.ui.list;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.imageloader.RoundedCornerLayout;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FileLoadProgressCallback;

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
        return v;
    }

    private void clearImage(AppCompatImageView imageView) {
        imageView.setImageBitmap(null);
        imageView.setBackgroundColor(Color.TRANSPARENT);
        imageView.setVisibility(View.INVISIBLE);
    }

    private void setImage(AppCompatImageView imageView, FavoriteImage image) {
        if (image.getImage() != null && InAppStoryService.isNotNull()) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageLoader.getInstance().displayImage(image.getUrl(), -1, imageView,
                    InAppStoryService.getInstance().getFastCache());
        } else {
            imageView.setBackgroundColor(image.getBackgroundColor());
        }
        imageView.setVisibility(View.VISIBLE);
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
                    List<FavoriteImage> images = InAppStoryService.getInstance()
                            .getFavoriteImages();
                    if (images == null || images.size() <= i[0]) {
                        downloadFileAndSendToInterface("", this);
                    } else {
                        downloadFileAndSendToInterface(images.get(i[0]).getUrl(), this);
                    }
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
                    List<FavoriteImage> images = InAppStoryService.getInstance()
                            .getFavoriteImages();
                    if (images == null || images.size() <= i[0]) {
                        downloadFileAndSendToInterface("", this);
                    } else {
                        downloadFileAndSendToInterface(images.get(i[0]).getUrl(), this);
                    }
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
                    if (getFavoriteListItem != null
                            && getFavoriteListItem.getFavoriteItem() != null) {
                        getFavoriteListItem.setImages(itemView, downloadImages, backgroundColors,
                                downloadImages.size());
                    }
                }
            }, count);
            return;
        }
        boolean lpC = false;
        View outerLayout = itemView.findViewById(R.id.outerLayout);
        if (manager.getRealHeight(itemView.getContext()) != null) {
            outerLayout.getLayoutParams().height = manager.getRealHeight(itemView.getContext());
            lpC = true;
        }
        if (manager.getRealWidth(itemView.getContext()) != null) {
            outerLayout.getLayoutParams().width = manager.getRealWidth(itemView.getContext());
            lpC = true;
        }
        RoundedCornerLayout container1 = itemView.findViewById(R.id.container1);
        RoundedCornerLayout container2 = itemView.findViewById(R.id.container2);
        RoundedCornerLayout container3 = itemView.findViewById(R.id.container3);
        RoundedCornerLayout container4 = itemView.findViewById(R.id.container4);
        Context context = itemView.getContext();
        container1.setRadius(manager.csListItemRadius(context) / 2);
        container2.setRadius(manager.csListItemRadius(context) / 2);
        container3.setRadius(manager.csListItemRadius(context) / 2);
        container4.setRadius(manager.csListItemRadius(context) / 2);
        if (lpC) itemView.findViewById(R.id.outerLayout).requestLayout();
        List<FavoriteImage> favImages = InAppStoryService.getInstance().getFavoriteImages();

        if (favImages.size() > 0) {
            AppCompatImageView image1 = itemView.findViewById(R.id.image1);
            AppCompatImageView image2 = itemView.findViewById(R.id.image2);
            AppCompatImageView image3 = itemView.findViewById(R.id.image3);
            AppCompatImageView image4 = itemView.findViewById(R.id.image4);
            clearImage(image1);
            clearImage(image2);
            clearImage(image3);
            clearImage(image4);
            switch (favImages.size()) {
                case 1:
                    setImage(image1, favImages.get(0));
                    break;
                case 2:
                    setImage(image1, favImages.get(0));
                    setImage(image2, favImages.get(1));
                    break;
                case 3:
                    setImage(image1, favImages.get(0));
                    setImage(image2, favImages.get(1));
                    setImage(image3, favImages.get(2));
                    break;
                default:
                    setImage(image1, favImages.get(0));
                    setImage(image2, favImages.get(1));
                    setImage(image3, favImages.get(2));
                    setImage(image4, favImages.get(3));
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
        Downloader.downloadFileBackground(url, false, InAppStoryService.getInstance().getFastCache(), new FileLoadProgressCallback() {
            @Override
            public void onProgress(long loadedSize, long totalSize) {

            }

            @Override
            public void onSuccess(File file) {
                final String path = file.getAbsolutePath();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callback.run(path);
                    }
                });
            }

            @Override
            public void onError(String error) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callback.error();
                    }
                });
            }
        });
    }

    public Integer backgroundColor;
    public ClickCallback callback;
}
