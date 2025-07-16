package com.inappstory.sdk.stories.ui.list;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.ui.screens.storyreader.StoryScreenHolder;
import com.inappstory.sdk.stories.cache.usecases.IGetStoryCoverCallback;
import com.inappstory.sdk.stories.cache.usecases.StoryCoverUseCase;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryItemCoordinates;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.ui.reader.ActiveStoryItem;
import com.inappstory.sdk.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.ui.views.IStoriesListItemWithStoryData;
import com.inappstory.sdk.stories.utils.Sizes;


public class StoryListItem extends BaseStoryListItem {

    public boolean isOpened;
    public boolean hasVideo;

    public StoryListItem(@NonNull View itemView, ViewGroup parent, AppearanceManager manager, boolean isOpened, boolean hasVideo) {
        super(itemView, parent, manager, false, false);
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

    public Integer backgroundColor;
    public ClickCallback callback;

    private void loadCovers(final @NonNull IStoriesListItem getListItem,
                            final String imageUrl,
                            final Integer backgroundColor,
                            final String videoUrl) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull final IASCore core) {
                if (imageUrl != null) {
                    new StoryCoverUseCase(
                            core,
                            imageUrl,
                            new IGetStoryCoverCallback() {
                                @Override
                                public void success(final String file) {
                                    core.contentHolder().listsContent().setPathByUrl(imageUrl, file);
                                    itemView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (viewCanBeUsed(itemView, getParent())) {
                                                getListItem.setImage(itemView, file, backgroundColor);
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void error() {
                                    itemView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (viewCanBeUsed(itemView, getParent())) {
                                                getListItem.setImage(itemView, null, backgroundColor);
                                            }
                                        }
                                    });
                                }
                            }
                    ).getFile();
                } else {
                    if (viewCanBeUsed(itemView, getParent())) {
                        getListItem.setImage(itemView, null, backgroundColor);
                    }
                }

                if (videoUrl != null) {
                    new StoryCoverUseCase(
                            core,
                            videoUrl,
                            new IGetStoryCoverCallback() {
                                @Override
                                public void success(final String file) {
                                    itemView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (viewCanBeUsed(itemView, getParent())) {
                                                getListItem.setVideo(itemView, file);
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void error() {

                                }
                            }
                    ).getFile();
                }
            }

            @Override
            public void error() {
                itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (viewCanBeUsed(itemView, getParent())) {
                            getListItem.setImage(itemView, null, backgroundColor);
                        }
                    }
                });
            }
        });
    }

    public void bind(Integer id,
                     String titleText,
                     Integer titleColor,
                     final String imageUrl,
                     Integer backgroundColor,
                     boolean isOpened,
                     boolean hasAudio,
                     final String videoUrl,
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
                final StoryItemCoordinates coordinates = new StoryItemCoordinates(
                        x + v.getWidth() / 2 - Sizes.dpToPxExt(8, itemView.getContext()),
                        y + v.getHeight() / 2
                );
                InAppStoryManager.useCore(new UseIASCoreCallback() {
                    @Override
                    public void use(@NonNull IASCore core) {
                        core.screensManager().getStoryScreenHolder().coordinates(coordinates);
                    }
                });
                if (StoryListItem.this.callback != null)
                    StoryListItem.this.callback.onItemClick(
                            getAbsoluteAdapterPosition(),
                            coordinates
                    );
            }
        });
        final IStoriesListItem getListItem = this.getListItem;
        if (getListItem != null) {
            this.backgroundColor = backgroundColor;
            if (viewCanBeUsed(itemView, getParent())) {
                getListItem.setId(itemView, id);

                getListItem.setTitle(itemView, titleText, titleColor);
                getListItem.setHasAudio(itemView, hasAudio);
                getListItem.setOpened(itemView, isOpened);
                if (getListItem instanceof IStoriesListItemWithStoryData) {
                    ((IStoriesListItemWithStoryData) getListItem).setCustomData(itemView, storyData);
                }
                InAppStoryManager.useCore(new UseIASCoreCallback() {
                    @Override
                    public void use(@NonNull IASCore core) {
                        String path = core.contentHolder().listsContent().getPathByUrl(imageUrl);
                        getListItem.setImage(itemView, path, StoryListItem.this.backgroundColor);
                    }
                });
            }
            loadCovers(getListItem, imageUrl, backgroundColor, videoUrl);
        }
    }


    @Override
    public void bindFavorite() {

    }

    @Override
    public void bindUGC() {

    }
}
