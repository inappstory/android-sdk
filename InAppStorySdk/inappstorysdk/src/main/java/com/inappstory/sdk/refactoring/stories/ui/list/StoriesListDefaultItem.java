package com.inappstory.sdk.refactoring.stories.ui.list;

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
import com.inappstory.sdk.refactoring.stories.ui.contracts.IStoriesListDefaultItemPresenter;
import com.inappstory.sdk.refactoring.stories.ui.contracts.IStoriesListItem;
import com.inappstory.sdk.stories.ui.video.VideoPlayer;
import com.inappstory.sdk.stories.ui.widgets.StoryListItemBorder;
import com.inappstory.sdk.stories.utils.Sizes;

public class StoriesListDefaultItem implements IStoriesListItem {

    private final AppearanceManager appearanceManager;

    private final IStoriesListDefaultItemPresenter itemPresenter;
    private final Context context;
    private final int layoutDirection;
    private final int parentWidth;

    public StoriesListDefaultItem(
            AppearanceManager appearanceManager,
            IStoriesListDefaultItemPresenter itemPresenter,
            Context context,
            int layoutDirection,
            int parentWidth
    ) {
        this.context = context;
        this.appearanceManager = appearanceManager;
        this.layoutDirection = layoutDirection;
        this.parentWidth = parentWidth;
        this.itemPresenter = itemPresenter;
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(context).inflate(
                R.layout.cs_story_list_inner_item,
                null,
                false
        );
        setContainerSize(v);
        setDefaultViews(v);
        return v;
    }

    @Override
    public View getVideoView() {
        View v = LayoutInflater.from(context).inflate(
                R.layout.cs_story_list_video_inner_item,
                null,
                false
        );
        setContainerSize(v);
        setDefaultViews(v);
        return v;
    }

    private void setContainerSize(View v) {

        View container = v.findViewById(R.id.container);

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

    private void setDefaultViews(View v) {
        AppCompatTextView titleView = v.findViewById(R.id.title);
        StoryListItemBorder borderView = v.findViewById(R.id.border);
        View gradient = v.findViewById(R.id.cell_gradient);
        RoundedCornerLayout cornerLayout = v.findViewById(R.id.item_cv);
        AppCompatImageView hasAudioIcon = v.findViewById(R.id.hasAudio);

        if (hasAudioIcon != null) {
            hasAudioIcon.setScaleX(layoutDirection == LayoutDirection.RTL ? -1 : 1);
        }
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


    @Override
    public void setId(View itemView, int id) {

    }

    @Override
    public void setTitle(View itemView, String title, Integer titleColor) {
        AppCompatTextView titleView = itemView.findViewById(R.id.title);
        if (titleView == null) return;
        Log.e("listUpdateCurrentState", "setTitle " + title + "");
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
        AppCompatImageView image = itemView.findViewById(R.id.image);
        if (image == null) return;
        if (path == null) {
            image.setImageResource(0);
            Log.e("listUpdateCurrentState", "setBackgroundColor " + backgroundColor + "");
            image.setBackgroundColor(backgroundColor);
            return;
        }
        itemPresenter.getBitmap(path, new IGetBitmap() {
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
        View hasAudioIcon = itemView.findViewById(R.id.hasAudio);
        if (hasAudioIcon == null) return;
        hasAudioIcon.setVisibility(hasAudio ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setVideo(View itemView, String videoPath) {
        VideoPlayer video = itemView.findViewById(R.id.video);
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
        StoryListItemBorder borderView = itemView.findViewById(R.id.border);
        if (borderView != null) {
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
