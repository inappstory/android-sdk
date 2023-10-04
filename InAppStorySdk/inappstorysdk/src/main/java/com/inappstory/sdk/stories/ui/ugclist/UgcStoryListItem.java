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
import com.inappstory.sdk.stories.ui.views.RoundedCornerLayout;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.usecases.StoryPreviewDownload;
import com.inappstory.sdk.stories.ui.list.items.base.BaseStoryListItem;
import com.inappstory.sdk.stories.ui.list.ClickCallback;
import com.inappstory.sdk.stories.ui.video.VideoPlayer;

public class UgcStoryListItem extends BaseStoryListItem {

    protected AppCompatTextView title;
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

        View v;
        if (getListItem != null) {
            v = getListItem.getView();
        } else {
            v = LayoutInflater.from(itemView.getContext()).inflate(R.layout.cs_story_list_inner_item, null, false);
            View container = v.findViewById(R.id.container);
            if (appearanceManager.csListItemInterface() == null || (appearanceManager.csListItemInterface().getView() == null
                    && appearanceManager.csListItemInterface().getVideoView() == null)) {
                if (appearanceManager.getRealHeight(itemView.getContext()) != null) {
                    container.getLayoutParams().height = appearanceManager.getRealHeight(itemView.getContext());
                }
                if (appearanceManager.getRealWidth(itemView.getContext()) != null) {
                    container.getLayoutParams().width = appearanceManager.getRealWidth(itemView.getContext());
                }
            }
            RoundedCornerLayout cv = v.findViewById(R.id.item_cv);
            cv.setBackgroundColor(Color.TRANSPARENT);
            cv.setRadius(appearanceManager.csListItemRadius(itemView.getContext()));
            title = v.findViewById(R.id.title);
            hasAudioIcon = v.findViewById(R.id.hasAudio);
            image = v.findViewById(R.id.image);
            border = v.findViewById(R.id.border);
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, appearanceManager.csListItemTitleSize(itemView.getContext()));
            title.setTextColor(appearanceManager.csListItemTitleColor());
            border.getBackground().setColorFilter(appearanceManager.csListItemBorderColor(),
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
            if (appearanceManager.csListItemInterface() == null || (appearanceManager.csListItemInterface().getView() == null
                    && appearanceManager.csListItemInterface().getVideoView() == null)) {

                View container = v.findViewById(R.id.container);
                if (appearanceManager.getRealHeight(itemView.getContext()) != null) {
                    container.getLayoutParams().height = appearanceManager.getRealHeight(itemView.getContext());
                }
                if (appearanceManager.getRealWidth(itemView.getContext()) != null) {
                    container.getLayoutParams().width = appearanceManager.getRealWidth(itemView.getContext());
                }
            }
            RoundedCornerLayout cv = v.findViewById(R.id.item_cv);
            cv.setBackgroundColor(Color.TRANSPARENT);
            cv.setRadius(appearanceManager.csListItemRadius(itemView.getContext()));
            title = v.findViewById(R.id.title);
            hasAudioIcon = v.findViewById(R.id.hasAudio);
            video = v.findViewById(R.id.video);
            image = v.findViewById(R.id.image);
            border = v.findViewById(R.id.border);
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, appearanceManager.csListItemTitleSize(itemView.getContext()));
            title.setTextColor(appearanceManager.csListItemTitleColor());
            ((GradientDrawable) border.getBackground()).setCornerRadius((int) (1.25 * appearanceManager.csListItemRadius(itemView.getContext())));
            border.getBackground().setColorFilter(appearanceManager.csListItemBorderColor(),
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
                     Integer backgroundColor,
                     boolean isOpened,
                     boolean hasAudio,
                     ClickCallback callback) {
        this.callback = callback;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UgcStoryListItem.this.callback != null)
                    UgcStoryListItem.this.callback.onItemClick(getAbsoluteAdapterPosition());
            }
        });
        if (getListItem != null) {
            this.backgroundColor = backgroundColor;
            getListItem.setId(itemView, id);
            getListItem.setTitle(itemView, titleText, titleColor);
            getListItem.setHasAudio(itemView, hasAudio);
            getListItem.setOpened(itemView, isOpened);
        }
    }

    @Override
    public void bindFavorite() {

    }

    @Override
    public void bindUGC() {

    }
}
