package com.inappstory.sdk.stories.ui.list;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.cache.usecases.IGetStoryCoverCallback;
import com.inappstory.sdk.stories.cache.usecases.StoryCoverUseCase;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.utils.RunnableCallback;

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
        View v = null;
        if (getFavoriteListItem != null && getFavoriteListItem.getFavoriteItem() != null) {
            v = getFavoriteListItem.getFavoriteItem();
        }
        return v;
    }

    private void loadFavoriteImages(final LoadFavoriteImagesCallback callback, final int count) {
        final List<String> downloadImages = new ArrayList<>();
        final int[] i = {0};

        final InAppStoryService service = InAppStoryService.getInstance();
        if (count == 0 ||
                service == null ||
                service.getFavoriteImages() == null ||
                service.getFavoriteImages().isEmpty()
        )
            return;
        RunnableCallback runnableCallback = new RunnableCallback() {
            @Override
            public void run(String path) {
                downloadImages.add(path);
                i[0]++;
                if (i[0] >= count)
                    callback.onLoad(downloadImages);
                else {
                    List<FavoriteImage> images = service.getFavoriteImages();
                    if (images == null || images.size() <= i[0]) {
                        downloadFileAndSendToInterface("", this);
                    } else {
                        downloadFileAndSendToInterface(images.get(i[0]).getUrl(), this);
                    }
                }
            }

            @Override
            public void error() {
                downloadImages.add(null);
                i[0]++;
                if (i[0] >= count)
                    callback.onLoad(downloadImages);
                else {
                    List<FavoriteImage> images = service.getFavoriteImages();
                    if (images == null || images.size() <= i[0]) {
                        downloadFileAndSendToInterface("", this);
                    } else {
                        downloadFileAndSendToInterface(images.get(i[0]).getUrl(), this);
                    }
                }
            }
        };
        downloadFileAndSendToInterface(service.getFavoriteImages().get(0).getUrl(), runnableCallback);
    }

    interface LoadFavoriteImagesCallback {
        void onLoad(List<String> downloadImages);
    }

    @Override
    public void bind(
            Integer id,
            String titleText,
            Integer titleColor,
            String imageUrl,
            Integer backgroundColor,
            boolean isOpened,
            boolean hasAudio,
            String videoUrl,
            StoryData storyData,
            ClickCallback callback
    ) {

    }

    public void bindFavorite() {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        if (getFavoriteListItem != null
                && getFavoriteListItem.getFavoriteItem() != null) {
            int count = service.getFavoriteImages().size();
            final List<Integer> backgroundColors = new ArrayList<>();
            for (int j = 0; j < count; j++) {
                backgroundColors.add(service.getFavoriteImages().get(j).getBackgroundColor());
            }
            if (viewCanBeUsed(itemView)) {
                getFavoriteListItem.bindFavoriteItem(itemView, backgroundColors, count);
            }
            loadFavoriteImages(new LoadFavoriteImagesCallback() {
                @Override
                public void onLoad(List<String> downloadImages) {
                    if (viewCanBeUsed(itemView)) {
                        if (getFavoriteListItem != null
                                && getFavoriteListItem.getFavoriteItem() != null) {
                            getFavoriteListItem.setImages(itemView, downloadImages, backgroundColors,
                                    downloadImages.size());
                        }
                    }
                }
            }, count);
            return;
        }
    }

    @Override
    public void bindUGC() {

    }

    private void downloadFileAndSendToInterface(String url, final RunnableCallback callback) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        if (url == null || url.isEmpty()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    callback.error();
                }
            });
            return;
        }
        new StoryCoverUseCase(
                service.getFilesDownloadManager(),
                url,
                new IGetStoryCoverCallback() {
                    @Override
                    public void success(final String file) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callback.run(file);
                            }
                        });
                    }

                    @Override
                    public void error() {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callback.error();
                            }
                        });
                    }
                }
        ).getFile();

    }

    public Integer backgroundColor;
    public ClickCallback callback;
}
