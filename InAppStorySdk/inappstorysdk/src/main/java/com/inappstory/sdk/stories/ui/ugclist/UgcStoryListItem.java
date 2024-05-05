package com.inappstory.sdk.stories.ui.ugclist;

import android.graphics.Color;
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
import com.inappstory.sdk.imageloader.RoundedCornerLayout;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FileLoadProgressCallback;
import com.inappstory.sdk.stories.cache.usecases.IGetStoryCoverCallback;
import com.inappstory.sdk.stories.cache.usecases.StoryCoverUseCase;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryItemCoordinates;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.list.BaseStoryListItem;
import com.inappstory.sdk.stories.ui.list.ClickCallback;
import com.inappstory.sdk.stories.ui.list.StoryListItem;
import com.inappstory.sdk.stories.ui.video.VideoPlayer;
import com.inappstory.sdk.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.ui.views.IStoriesListItemWithStoryData;
import com.inappstory.sdk.stories.utils.Sizes;

import java.io.File;

public class UgcStoryListItem extends BaseStoryListItem {

    protected AppCompatImageView image;
    protected VideoPlayer video;
    protected AppCompatImageView hasAudioIcon;
    protected View border;
    public boolean isOpened;
    public boolean hasVideo;

    public UgcStoryListItem(@NonNull View itemView, AppearanceManager manager, boolean isOpened, boolean hasVideo) {
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

        View v = null;
        if (getListItem != null) {
            v = getListItem.getView();
        }
        return v;
    }

    protected View getDefaultVideoCell() {
        View v = null;
        if (getListItem != null) {
            v = (getListItem.getVideoView() != null ? getListItem.getVideoView() : getListItem.getView());
        }
        return v;
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
                        if (getListItem != null) {
                            callback.run(path);
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public Integer backgroundColor;
    public ClickCallback callback;

    public void bind(Integer id,
                     String titleText,
                     Integer titleColor,
                     final String imageUrl,
                     Integer backgroundColor,
                     boolean isOpened,
                     boolean hasAudio,
                     String videoUrl,
                     StoryData storyData,
                     ClickCallback callback) {
        this.callback = callback;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                ScreensManager.getInstance().coordinates =
                        new StoryItemCoordinates(x + v.getWidth() / 2 - Sizes.dpToPxExt(8, itemView.getContext()),
                                y + v.getHeight() / 2);
                if (UgcStoryListItem.this.callback != null)
                    UgcStoryListItem.this.callback.onItemClick(
                            getAbsoluteAdapterPosition(),
                            ScreensManager.getInstance().coordinates
                    );
            }
        });
        final IStoriesListItem getListItem = this.getListItem;
        if (getListItem != null) {
            this.backgroundColor = backgroundColor;
            getListItem.setId(itemView, id);
            getListItem.setTitle(itemView, titleText, titleColor);
            getListItem.setHasAudio(itemView, hasAudio);
            getListItem.setOpened(itemView, isOpened);
            if (getListItem instanceof IStoriesListItemWithStoryData) {
                ((IStoriesListItemWithStoryData) getListItem).setCustomData(itemView, storyData);
            }
            InAppStoryService service = InAppStoryService.getInstance();
            if (service == null) return;

            if (imageUrl != null) {
                new StoryCoverUseCase(
                        service.getFilesDownloadManager(),
                        imageUrl,
                        new IGetStoryCoverCallback() {
                            @Override
                            public void success(String file) {
                                getListItem.setImage(itemView, file,
                                        UgcStoryListItem.this.backgroundColor);
                            }

                            @Override
                            public void error() {
                                getListItem.setImage(itemView, null,
                                        UgcStoryListItem.this.backgroundColor);
                            }
                        }
                ).getFile();
            } else {
                getListItem.setImage(itemView, null,
                        UgcStoryListItem.this.backgroundColor);
            }

            if (videoUrl != null) {
                new StoryCoverUseCase(
                        service.getFilesDownloadManager(),
                        videoUrl,
                        new IGetStoryCoverCallback() {
                            @Override
                            public void success(String file) {
                                getListItem.setVideo(itemView, file);
                            }

                            @Override
                            public void error() {

                            }
                        }
                ).getFile();
            }
        }
    }

    @Override
    public void bindFavorite() {

    }

    @Override
    public void bindUGC() {

    }
}
