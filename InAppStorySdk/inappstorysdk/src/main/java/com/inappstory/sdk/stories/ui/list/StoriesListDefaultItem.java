package com.inappstory.sdk.stories.ui.list;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.LayoutDirection;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.ui.widgets.roundedlayout.RoundedCornerLayout;
import com.inappstory.sdk.memcache.IGetBitmap;
import com.inappstory.sdk.stories.ui.video.VideoPlayer;
import com.inappstory.sdk.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.ui.widgets.StoryListItemBorder;
import com.inappstory.sdk.stories.utils.Sizes;

public final class StoriesListDefaultItem implements IStoriesListItem {
    private AppCompatImageView image;
    private VideoPlayer video;
    private AppCompatTextView titleView;
    private AppCompatImageView hasAudioIcon;
    private StoryListItemBorder borderView;
    private View gradient;

    private View container;
    private RoundedCornerLayout cornerLayout;

    AppearanceManager appearanceManager;

    IStoriesListDefaultItemPresenter manager = new StoriesListDefaultItemPresenter();
    Context context;
    int layoutDirection;

    int parentWidth;

    public StoriesListDefaultItem(
            AppearanceManager appearanceManager,
            Context context,
            int layoutDirection,
            int parentWidth
    ) {
        this.context = context;
        this.appearanceManager = appearanceManager;
        this.layoutDirection = layoutDirection;
        this.parentWidth = parentWidth;
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(context).inflate(
                R.layout.cs_story_list_inner_item,
                null,
                false
        );
        bindViews(v);
        setContainerSize();
        setDefaultViews();
        return v;
    }

    @Override
    public View getVideoView() {
        View v = LayoutInflater.from(context).inflate(
                R.layout.cs_story_list_video_inner_item,
                null,
                false
        );
        bindViews(v);
        bindVideoViews(v);
        setContainerSize();
        setDefaultViews();
        return v;
    }

    private void setContainerSize() {
        if (container == null) return;
        Integer rh = appearanceManager.getRealHeight(context, parentWidth);
        Integer rw = appearanceManager.getRealWidth(context, parentWidth);
        if (rh != null) {
            container.getLayoutParams().height = rh;
        }
        if (rw != null) {
            container.getLayoutParams().width = rw;
        }
        container.requestLayout();
    }

    private void setDefaultViews() {
        if (cornerLayout != null) {
            cornerLayout.setBackgroundColor(Color.TRANSPARENT);
            cornerLayout.setRadius(Math.max(appearanceManager.csListItemRadius(context) - Sizes.dpToPxExt(4, context), 0));
        }
        if (gradient != null)
            gradient.setVisibility(appearanceManager.csListItemGradientEnable() ? View.VISIBLE : View.INVISIBLE);
        if (titleView != null) {
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, appearanceManager.csListItemTitleSize(context));
            titleView.setTextColor(appearanceManager.csListItemTitleColor());
        }
        if (borderView != null) {
            borderView.radius(appearanceManager.csListItemRadius(context));
            borderView.color(appearanceManager.csListItemBorderColor());
        }
    }

    private void bindViews(View parent) {
        container = parent.findViewById(R.id.container);
        cornerLayout = parent.findViewById(R.id.item_cv);
        titleView = parent.findViewById(R.id.title);
        hasAudioIcon = parent.findViewById(R.id.hasAudio);
        hasAudioIcon.setScaleX(layoutDirection == LayoutDirection.RTL ? -1 : 1);
        image = parent.findViewById(R.id.image);
        borderView = parent.findViewById(R.id.border);
        gradient = parent.findViewById(R.id.cell_gradient);
    }

    private void bindVideoViews(View parent) {
        video = parent.findViewById(R.id.video);
    }

    @Override
    public void setId(View itemView, int id) {

    }

    @Override
    public void setTitle(View itemView, String title, Integer titleColor) {
        if (titleView == null) return;
        titleView.setText(title);
        if (titleColor != null) {
            titleView.setTextColor(titleColor);
        } else {
            titleView.setTextColor(appearanceManager.csListItemTitleColor());
        }
        if (appearanceManager.csCustomFont() != null) {
            titleView.setTypeface(appearanceManager.csCustomFont());
        }
    }

    @Override
    public void setImage(View itemView, String path, final int backgroundColor) {
        if (image == null) return;
        if (path == null) {
            image.setImageResource(0);
            image.setBackgroundColor(backgroundColor);

            return;
        }
        manager.getBitmap(path, new IGetBitmap() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                image.setImageBitmap(bitmap);
            }

            @Override
            public void onError() {
                image.setImageResource(0);
                image.setBackgroundColor(backgroundColor);
            }
        });
    }

    @Override
    public void setHasAudio(View itemView, boolean hasAudio) {
        if (hasAudioIcon == null) return;
        hasAudioIcon.setVisibility(hasAudio ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setVideo(View itemView, String videoPath) {
        if (video != null) {
            if (videoPath != null) {
                video.release();
                video.loadVideo(videoPath);
            } else {
                video.release();
            }
        }
    }

    @Override
    public void setOpened(View itemView, boolean isOpened) {
        if (borderView != null) {
            /*  */
            borderView.setVisibility(
                    isOpened ?
                            (appearanceManager.csListOpenedItemBorderVisibility() ? View.VISIBLE : View.GONE)
                            : (appearanceManager.csListItemBorderVisibility() ? View.VISIBLE : View.GONE)
            );

            borderView.radius(appearanceManager.csListItemRadius(context));
            borderView.color(isOpened ?
                    appearanceManager.csListOpenedItemBorderColor()
                    : appearanceManager.csListItemBorderColor());
        }
    }
}
