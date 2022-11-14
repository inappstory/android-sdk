package com.inappstory.sdk.stories.ui.list;

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
import com.inappstory.sdk.imageloader.RoundedCornerLayout;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FileLoadProgressCallback;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.video.VideoPlayer;
import com.inappstory.sdk.stories.utils.Sizes;

import java.io.File;

public class StoryListItem extends BaseStoryListItem {

    protected AppCompatTextView source;
    protected AppCompatImageView image;
    protected VideoPlayer video;
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
                if (manager.csListItemHeight() != null) {
                    container.getLayoutParams().height = manager.csListItemHeight();
                }
                if (manager.csListItemWidth() != null) {
                    container.getLayoutParams().width = manager.csListItemWidth();
                }
            }
            RoundedCornerLayout cv = v.findViewById(R.id.item_cv);
            cv.setBackgroundColor(Color.TRANSPARENT);
            cv.setRadius(manager.csListItemRadius());
            title = v.findViewById(R.id.title);
            source = v.findViewById(R.id.source);
            hasAudioIcon = v.findViewById(R.id.hasAudio);
            image = v.findViewById(R.id.image);
            border = v.findViewById(R.id.border);
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, manager.csListItemTitleSize());
            title.setTextColor(manager.csListItemTitleColor());
            source.setTextSize(TypedValue.COMPLEX_UNIT_PX, manager.csListItemSourceSize());
            source.setTextColor(manager.csListItemSourceColor());
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
                if (manager.csListItemHeight() != null) {
                    container.getLayoutParams().height = manager.csListItemHeight();
                }
                if (manager.csListItemWidth() != null) {
                    container.getLayoutParams().width = manager.csListItemWidth();
                }
            }
            RoundedCornerLayout cv = v.findViewById(R.id.item_cv);
            cv.setBackgroundColor(Color.TRANSPARENT);
            cv.setRadius(manager.csListItemRadius());
            title = v.findViewById(R.id.title);
            source = v.findViewById(R.id.source);
            hasAudioIcon = v.findViewById(R.id.hasAudio);
            video = v.findViewById(R.id.video);
            image = v.findViewById(R.id.image);
            border = v.findViewById(R.id.border);
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, manager.csListItemTitleSize());
            title.setTextColor(manager.csListItemTitleColor());
            source.setTextSize(TypedValue.COMPLEX_UNIT_PX, manager.csListItemSourceSize());
            source.setTextColor(manager.csListItemSourceColor());
            ((GradientDrawable) border.getBackground()).setCornerRadius((int) (1.25 * manager.csListItemRadius()));
            border.getBackground().setColorFilter(manager.csListItemBorderColor(),
                    PorterDuff.Mode.SRC_ATOP);
        }
        return v;
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

            }
        });
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
                ScreensManager.getInstance().coordinates = new Point(x + v.getWidth() / 2 - Sizes.dpToPxExt(8),
                        y + v.getHeight() / 2);
                if (StoryListItem.this.callback != null)
                    StoryListItem.this.callback.onItemClick(getAbsoluteAdapterPosition());
            }
        });
        if (getListItem != null) {
            this.backgroundColor = backgroundColor;
            getListItem.setId(itemView, id);
            getListItem.setTitle(itemView, titleText, titleColor);
            getListItem.setHasAudio(itemView, hasAudio);
            String fileLink = ImageLoader.getInstance().getFileLink(imageUrl);
            if (fileLink != null) {
                getListItem.setImage(itemView, fileLink,
                        StoryListItem.this.backgroundColor);
            } else {
                if (imageUrl != null) {
                    downloadFileAndSendToInterface(imageUrl, new RunnableCallback() {
                        @Override
                        public void run(String path) {
                            ImageLoader.getInstance().addLink(imageUrl, path);
                            getListItem.setImage(itemView, path,
                                    StoryListItem.this.backgroundColor);
                        }

                        @Override
                        public void error() {
                            getListItem.setImage(itemView, null,
                                    StoryListItem.this.backgroundColor);
                        }
                    });
                } else {
                    getListItem.setImage(itemView, null,
                            StoryListItem.this.backgroundColor);
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
            return;
        }

        RoundedCornerLayout cv = itemView.findViewById(R.id.item_cv);
        cv.setBackgroundColor(Color.TRANSPARENT);
        cv.setRadius(manager.csListItemRadius());
        if (border != null)
            ((GradientDrawable) border.getBackground()).setCornerRadius((int) (1.25 * manager.csListItemRadius()));
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
        if (source != null) {
            source.setText(sourceText);
            if (manager.csCustomFont() != null) {
                source.setTypeface(manager.csCustomFont());
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
        if (InAppStoryService.isNull()) return;
        if (videoUrl != null) {
            if (image != null) {
                if (imageUrl != null) {
                    //  image.setImageResource(0);
                    ImageLoader.getInstance().displayImage(imageUrl, 0, image,
                            InAppStoryService.getInstance().getFastCache());
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
                    ImageLoader.getInstance().displayImage(imageUrl, 0, image,
                            InAppStoryService.getInstance().getFastCache());
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
