package com.inappstory.sdk.stories.ui.list.items.story;

import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.ui.list.ClickCallback;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.usecases.StoryPreviewDownload;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.list.items.IStoryListItemWithCover;
import com.inappstory.sdk.stories.ui.list.items.base.BaseStoryListItem;
import com.inappstory.sdk.stories.ui.video.VideoPlayer;
import com.inappstory.sdk.stories.uidomain.list.items.story.IStoryListItemManager;
import com.inappstory.sdk.stories.uidomain.list.items.story.StoryListItemManager;
import com.inappstory.sdk.stories.utils.Sizes;

public class StoryListItem extends BaseStoryListItem implements IStoryListItemWithCover {

    protected AppCompatTextView title;
    protected AppCompatImageView image;
    protected VideoPlayer video;
    protected AppCompatImageView hasAudioIcon;
    protected View border;
    protected View gradient;
    public boolean isOpened;
    public boolean hasVideo;

    private IStoryListItemManager manager = new StoryListItemManager();

    public StoryListItem(
            @NonNull View itemView,
            AppearanceManager appearanceManager,
            boolean isOpened,
            boolean hasVideo
    ) {
        super(itemView, appearanceManager, false, false);
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

    public Integer backgroundColor;
    public ClickCallback callback;

    @Override
    public void setImage(String url) {
        if (url != null) {
            manager.getFilePathFromLink(url, new IFileDownloadCallback() {
                @Override
                public void onSuccess(String fileAbsolutePath) {
                    getListItem.setImage(
                            itemView,
                            fileAbsolutePath,
                            backgroundColor
                    );
                }

                @Override
                public void onError(int errorCode, String error) {
                    getListItem.setImage(
                            itemView,
                            null,
                            backgroundColor
                    );
                }
            });
        } else {
            getListItem.setImage(
                    itemView,
                    null,
                    backgroundColor
            );
        }

    }

    @Override
    public void setVideo(String url) {
        if (url != null) {
            manager.getFilePathFromLink(url, new IFileDownloadCallback() {
                @Override
                public void onSuccess(String fileAbsolutePath) {
                    getListItem.setVideo(itemView, fileAbsolutePath);
                }

                @Override
                public void onError(int errorCode, String error) {
                    getListItem.setVideo(itemView, null);
                }
            });
        } else {
            getListItem.setVideo(itemView, null);
        }
    }

    public void bind(
            Integer id,
            String titleText,
            Integer titleColor,
            String sourceText,
            Integer backgroundColor,
            boolean isOpened,
            boolean hasAudio,
            ClickCallback callback
    ) {
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
        getListItem.setOpened(itemView, isOpened);
    }

    @Override
    public void bindFavorite() {

    }

    @Override
    public void bindUGC() {

    }
}
