package com.inappstory.sdk.stories.ui.list;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
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
import com.inappstory.sdk.stories.ui.views.RoundedCornerLayout;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.usecases.StoryPreviewDownload;

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
        return getFavoriteListItem.getFavoriteItem();
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
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null) {
            int count = service.getFavoriteImages().size();
            final List<Integer> backgroundColors = new ArrayList<>();
            for (int j = 0; j < count; j++) {
                backgroundColors.add(service.getFavoriteImages().get(j).getBackgroundColor());
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
        new StoryPreviewDownload(url, new IFileDownloadCallback() {
            @Override
            public void onSuccess(final String fileAbsolutePath) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callback.run(fileAbsolutePath);
                    }
                });
            }

            @Override
            public void onError(int errorCode, String error) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callback.error();
                    }
                });
            }
        }).downloadOrGetFromCache();
    }

    public Integer backgroundColor;
    public ClickCallback callback;
}
