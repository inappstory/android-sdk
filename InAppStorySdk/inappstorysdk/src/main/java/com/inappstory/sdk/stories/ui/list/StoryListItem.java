package com.inappstory.sdk.stories.ui.list;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.inappstory.sdk.stories.ui.reader.ActiveStoryItem;
import com.inappstory.sdk.stories.ui.video.VideoPlayer;
import com.inappstory.sdk.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.ui.views.IStoriesListItemWithStoryData;
import com.inappstory.sdk.stories.utils.RunnableCallback;
import com.inappstory.sdk.stories.utils.Sizes;

import java.io.File;

public class StoryListItem extends BaseStoryListItem {

    protected AppCompatImageView hasAudioIcon;
    protected View border;
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

        View v;
        if (getListItem != null) {
            v = getListItem.getView();
        } else {
            v = LayoutInflater.from(itemView.getContext()).inflate(R.layout.cs_story_list_inner_item, null, false);
            View container = v.findViewById(R.id.container);
            if (manager.csListItemInterface() == null || (manager.csListItemInterface().getView() == null
                    && manager.csListItemInterface().getVideoView() == null)) {
                if (manager.getRealHeight(itemView.getContext()) != null) {
                    container.getLayoutParams().height = manager.getRealHeight(itemView.getContext());
                }
                if (manager.getRealWidth(itemView.getContext()) != null) {
                    container.getLayoutParams().width = manager.getRealWidth(itemView.getContext());
                }
            }
            RoundedCornerLayout cv = v.findViewById(R.id.item_cv);
            cv.setBackgroundColor(Color.TRANSPARENT);
            cv.setRadius(Math.max(manager.csListItemRadius(itemView.getContext()) - Sizes.dpToPxExt(4, itemView.getContext()), 0));
            title = v.findViewById(R.id.title);
            hasAudioIcon = v.findViewById(R.id.hasAudio);
            //image = v.findViewById(R.id.image);
            border = v.findViewById(R.id.border);
            View gradient = v.findViewById(R.id.cell_gradient);
            gradient.setVisibility(manager.csListItemGradientEnable() ? View.VISIBLE : View.INVISIBLE);
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, manager.csListItemTitleSize(itemView.getContext()));
            title.setTextColor(manager.csListItemTitleColor());
            border.getBackground().setColorFilter(manager.csListItemBorderColor(),
                    PorterDuff.Mode.SRC_ATOP);
        }
        return v;
    }

    protected View getDefaultVideoCell() {
        View v;
        if (getListItem != null) {
            v = (getListItem.getVideoView() != null ? getListItem.getVideoView() : getListItem.getView());
        } else {
            v = LayoutInflater.from(itemView.getContext()).inflate(R.layout.cs_story_list_video_inner_item, null, false);
            if (manager.csListItemInterface() == null || (manager.csListItemInterface().getView() == null
                    && manager.csListItemInterface().getVideoView() == null)) {

                View container = v.findViewById(R.id.container);
                if (manager.getRealHeight(itemView.getContext()) != null) {
                    container.getLayoutParams().height = manager.getRealHeight(itemView.getContext());
                }
                if (manager.getRealWidth(itemView.getContext()) != null) {
                    container.getLayoutParams().width = manager.getRealWidth(itemView.getContext());
                }
            }
            RoundedCornerLayout cv = v.findViewById(R.id.item_cv);
            cv.setBackgroundColor(Color.TRANSPARENT);
            cv.setRadius(Math.max(manager.csListItemRadius(itemView.getContext()) - Sizes.dpToPxExt(4, itemView.getContext()), 0));
            View gradient = v.findViewById(R.id.cell_gradient);
            gradient.setVisibility(manager.csListItemGradientEnable() ? View.VISIBLE : View.INVISIBLE);
            title = v.findViewById(R.id.title);
            hasAudioIcon = v.findViewById(R.id.hasAudio);
            //video = v.findViewById(R.id.video);
            // image = v.findViewById(R.id.image);
            border = v.findViewById(R.id.border);
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, manager.csListItemTitleSize(itemView.getContext()));
            title.setTextColor(manager.csListItemTitleColor());
            ((GradientDrawable) border.getBackground()).setCornerRadius(manager.csListItemRadius(itemView.getContext()));
            border.getBackground().setColorFilter(manager.csListItemBorderColor(),
                    PorterDuff.Mode.SRC_ATOP);
        }
        return v;
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
                if (StoryListItem.this.callback != null)
                    StoryListItem.this.callback.onItemClick(
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
                                        StoryListItem.this.backgroundColor);
                            }

                            @Override
                            public void error() {
                                getListItem.setImage(itemView, null,
                                        StoryListItem.this.backgroundColor);
                            }
                        }
                ).getFile();
            } else {
                getListItem.setImage(itemView, null,
                        StoryListItem.this.backgroundColor);
            }

            if (videoUrl != null) {
                new StoryCoverUseCase(
                        service.getFilesDownloadManager(),
                        videoUrl,
                        new IGetStoryCoverCallback() {
                            @Override
                            public void success(String file) {
                                getListItem.setImage(itemView, file,
                                        StoryListItem.this.backgroundColor);
                            }

                            @Override
                            public void error() {
                                getListItem.setImage(itemView, null,
                                        StoryListItem.this.backgroundColor);
                            }
                        }
                ).getFile();
            }
            return;
        }
        RoundedCornerLayout cv = itemView.findViewById(R.id.item_cv);
        cv.setBackgroundColor(Color.TRANSPARENT);
        cv.setRadius(Math.max(manager.csListItemRadius(itemView.getContext()) - Sizes.dpToPxExt(4, itemView.getContext()), 0));
        if (border != null)
            ((GradientDrawable) border.getBackground()).setCornerRadius(manager.csListItemRadius(itemView.getContext()));
        if (title != null) {
            title.setText(titleText);
            if (titleColor != null) {
                title.setTextColor(titleColor);
            } else {
                title.setTextColor(manager.csListItemTitleColor());
            }
            if (manager.csCustomFont() != null) {
                title.setTypeface(manager.csCustomFont());
            }
        }
        if (hasAudioIcon != null)
            hasAudioIcon.setVisibility(hasAudio ? View.VISIBLE : View.GONE);
        if (border != null) {
            border.setVisibility(isOpened ?
                    (manager.csListOpenedItemBorderVisibility() ? View.VISIBLE : View.GONE)
                    : (manager.csListItemBorderVisibility() ? View.VISIBLE : View.GONE));
            border.getBackground().setColorFilter(isOpened ? manager.csListOpenedItemBorderColor() :
                            manager.csListItemBorderColor(),
                    PorterDuff.Mode.SRC_ATOP);
        }
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;

        AppCompatImageView image = itemView.findViewById(R.id.image);
        VideoPlayer video = itemView.findViewById(R.id.video);
        if (videoUrl != null) {
            if (image != null) {
                if (imageUrl != null) {
                    //  image.setImageResource(0);
                    ImageLoader.getInstance().displayImage(
                            imageUrl,
                            0,
                            image,
                            service.getFastCache()
                    );
                } else if (backgroundColor != null) {
                    image.setImageResource(0);
                    image.setBackgroundColor(backgroundColor);
                }
            }
            if (video != null) {
                video.release();
                video.loadVideoByUrl(videoUrl);
            }
        } else {
            if (video != null) {
                video.release();
            }
            if (image != null) {
                if (imageUrl != null) {
                    //  image.setImageResource(0);
                    ImageLoader.getInstance().displayImage(
                            imageUrl,
                            0,
                            image,
                            service.getFastCache());
                } else if (backgroundColor != null) {
                    image.setImageResource(0);
                    image.setBackgroundColor(backgroundColor);
                }
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
