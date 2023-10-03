package com.inappstory.sdk.stories.ui.list;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.imagememcache.GetBitmapFromCacheWithFilePath;
import com.inappstory.sdk.imagememcache.IGetBitmapFromMemoryCache;
import com.inappstory.sdk.imagememcache.IGetBitmapFromMemoryCacheError;
import com.inappstory.sdk.stories.ui.views.RoundedCornerLayout;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.usecases.StoryPreviewDownload;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.video.VideoPlayer;
import com.inappstory.sdk.stories.utils.Sizes;

public class StoryListItem extends BaseStoryListItem {

    protected AppCompatTextView title;
    protected AppCompatImageView image;
    protected VideoPlayer video;
    protected AppCompatImageView hasAudioIcon;
    protected View border;
    protected View gradient;
    public boolean isOpened;
    public boolean hasVideo;

    public StoryListItem(@NonNull View itemView, AppearanceManager manager, boolean isOpened, boolean hasVideo) {
        super(itemView, manager, false, false);
        this.isOpened = isOpened;
        this.hasVideo = hasVideo;
        ViewGroup vg = itemView.findViewById(R.id.baseLayout);
        vg.removeAllViews();
        if (hasVideo) {
            vg.addView(getDefaultVideoCell());
        } else {
            vg.addView(getDefaultCell());
        }
    }

    protected View getDefaultCell() {
        return getListItem.getView();
    }

    protected View getDefaultVideoCell() {
        return getListItem.getVideoView() != null ? getListItem.getVideoView() : getListItem.getView();
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
                        if (getListItem != null) {
                            callback.run(fileAbsolutePath);
                        }
                    }
                });
            }

            @Override
            public void onError(int errorCode, String error) {

            }
        }).downloadOrGetFromCache();

    }

    public Integer backgroundColor;
    public ClickCallback callback;

    public void bind(Integer id,
                     String titleText,
                     Integer titleColor,
                     String sourceText,
                     final String imageUrl,
                     Integer backgroundColor,
                     boolean isOpened,
                     boolean hasAudio,
                     String videoUrl,
                     ClickCallback callback) {
        this.callback = callback;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                ScreensManager.getInstance().coordinates = new Point(x + v.getWidth() / 2 - Sizes.dpToPxExt(8, itemView.getContext()),
                        y + v.getHeight() / 2);
                if (StoryListItem.this.callback != null)
                    StoryListItem.this.callback.onItemClick(getAbsoluteAdapterPosition());
            }
        });
        this.backgroundColor = backgroundColor;
        getListItem.setId(itemView, id);
        getListItem.setTitle(itemView, titleText, titleColor);
        getListItem.setHasAudio(itemView, hasAudio);
        String fileLink = ImageLoader.getInstance().getFileLink(imageUrl);
        if (fileLink != null) {
            getListItem.setImage(itemView, fileLink, backgroundColor);
        } else {
            if (imageUrl != null) {
                downloadFileAndSendToInterface(imageUrl, new RunnableCallback() {
                    @Override
                    public void run(String path) {
                        ImageLoader.getInstance().addLink(imageUrl, path);
                        getListItem.setImage(itemView, path, StoryListItem.this.backgroundColor);
                    }

                    @Override
                    public void error() {
                        getListItem.setImage(itemView, null, StoryListItem.this.backgroundColor);
                    }
                });
            } else {
                getListItem.setImage(itemView, null, StoryListItem.this.backgroundColor);
            }
        }

        getListItem.setOpened(itemView, isOpened);
        if (videoUrl != null) {
            downloadFileAndSendToInterface(videoUrl, new RunnableCallback() {
                @Override
                public void run(String path) {
                    getListItem.setVideo(itemView, path);
                }

                @Override
                public void error() {
                    getListItem.setVideo(itemView, null);
                }
            });
        }
    }

    @Override
    public void bindFavorite() {

    }

    @Override
    public void bindUGC() {

    }
}
