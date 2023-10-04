package com.inappstory.sdk.stories.ui.list.defaultitems;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.uidomain.list.defaultitems.IGetBitmap;
import com.inappstory.sdk.stories.ui.video.VideoPlayer;
import com.inappstory.sdk.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.ui.views.RoundedCornerLayout;
import com.inappstory.sdk.stories.uidomain.list.defaultitems.story.DefaultStoryListItemManager;
import com.inappstory.sdk.stories.uidomain.list.defaultitems.story.IDefaultStoryListItemManager;
import com.inappstory.sdk.stories.utils.Sizes;

public final class DefaultStoryListItem implements IStoriesListItem {
    private AppCompatImageView image;
    private VideoPlayer video;
    private AppCompatTextView titleView;
    private AppCompatImageView hasAudioIcon;
    private View borderView;
    private View gradient;

    private View container;
    private RoundedCornerLayout cornerLayout;

    AppearanceManager appearanceManager;

    IDefaultStoryListItemManager manager = new DefaultStoryListItemManager();
    Context context;

    public DefaultStoryListItem(AppearanceManager appearanceManager, Context context) {
        this.context = context;
        this.appearanceManager = appearanceManager;
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
        if (appearanceManager.getRealHeight(context) != null) {
            container.getLayoutParams().height = appearanceManager.getRealHeight(context);
        }
        if (appearanceManager.getRealWidth(context) != null) {
            container.getLayoutParams().width = appearanceManager.getRealWidth(context);
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
            borderView.getBackground().setColorFilter(appearanceManager.csListItemBorderColor(),
                    PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void bindViews(View parent) {
        container = parent.findViewById(R.id.container);
        cornerLayout = parent.findViewById(R.id.item_cv);
        titleView = parent.findViewById(R.id.title);
        hasAudioIcon = parent.findViewById(R.id.hasAudio);
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
                video.loadVideoByUrl(videoPath);
            } else {
                video.release();
            }
        }
    }

    @Override
    public void setOpened(View itemView, boolean isOpened) {
        if (borderView != null) {
            ((GradientDrawable) borderView.getBackground())
                    .setCornerRadius(appearanceManager.csListItemRadius(context));
            borderView.setVisibility(
                    isOpened ?
                            (appearanceManager.csListOpenedItemBorderVisibility() ? View.VISIBLE : View.GONE)
                            : (appearanceManager.csListItemBorderVisibility() ? View.VISIBLE : View.GONE)
            );
            borderView.getBackground().setColorFilter(
                    isOpened ?
                            appearanceManager.csListOpenedItemBorderColor()
                            : appearanceManager.csListItemBorderColor(),
                    PorterDuff.Mode.SRC_ATOP
            );
        }
    }
}
