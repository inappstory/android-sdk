package com.inappstory.sdk.stories.ui.list.items.story;

import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryData;
import com.inappstory.sdk.stories.ui.list.ClickCallback;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.list.items.IStoriesListCommonItem;
import com.inappstory.sdk.stories.ui.list.items.IStoriesListItemWithCover;
import com.inappstory.sdk.stories.ui.list.items.BaseStoriesListItem;
import com.inappstory.sdk.stories.ui.views.IStoriesListItemWithStoryData;
import com.inappstory.sdk.stories.uidomain.list.utils.FilePathFromLink;
import com.inappstory.sdk.stories.uidomain.list.items.story.IStoriesListItemPresenter;
import com.inappstory.sdk.stories.uidomain.list.items.story.StoriesListItemPresenter;
import com.inappstory.sdk.stories.utils.Sizes;

public class StoriesListItem
        extends BaseStoriesListItem
        implements IStoriesListItemWithCover, IStoriesListCommonItem {

    private IStoriesListItemPresenter manager = new StoriesListItemPresenter();

    public StoriesListItem(
            @NonNull View itemView,
            AppearanceManager appearanceManager,
            boolean hasVideo
    ) {
        super(itemView, appearanceManager);
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
            manager.getFilePathFromLink(
                    url,
                    new FilePathFromLink() {
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
                    }
            );
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
            manager.getFilePathFromLink(
                    url,
                    new FilePathFromLink() {
                        @Override
                        public void onSuccess(String fileAbsolutePath) {
                            getListItem.setVideo(itemView, fileAbsolutePath);
                        }

                        @Override
                        public void onError(int errorCode, String error) {
                            getListItem.setVideo(itemView, null);
                        }
                    }
            );
        } else {
            getListItem.setVideo(itemView, null);
        }
    }

    @Override
    public void bindCommon(
            Integer id,
            StoryData storyData,
            String titleText,
            Integer titleColor,
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
                ScreensManager.getInstance().coordinates = new Point(
                        x + v.getWidth() / 2 - Sizes.dpToPxExt(8, itemView.getContext()),
                        y + v.getHeight() / 2
                );
                if (StoriesListItem.this.callback != null)
                    StoriesListItem.this.callback.onItemClick(getAbsoluteAdapterPosition());
            }
        });
        this.backgroundColor = backgroundColor;
        getListItem.setId(itemView, id);
        getListItem.setTitle(itemView, titleText, titleColor);
        getListItem.setHasAudio(itemView, hasAudio);
        getListItem.setOpened(itemView, isOpened);
        if (getListItem instanceof IStoriesListItemWithStoryData) {
            ((IStoriesListItemWithStoryData) getListItem).setCustomData(itemView, storyData);
        }
    }
}
