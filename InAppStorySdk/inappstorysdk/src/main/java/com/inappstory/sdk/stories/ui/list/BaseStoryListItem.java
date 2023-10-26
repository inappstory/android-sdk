package com.inappstory.sdk.stories.ui.list;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.ui.views.IGetFavoriteListItem;
import com.inappstory.sdk.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.utils.Sizes;

import android.view.View.MeasureSpec;

import com.inappstory.sdk.ugc.list.IStoriesListUGCItem;


public abstract class BaseStoryListItem extends RecyclerView.ViewHolder {

    protected AppCompatTextView title;
    protected AppearanceManager manager;

    protected IStoriesListItem getListItem;

    public boolean isFavorite;
    protected IGetFavoriteListItem getFavoriteListItem;

    public boolean isUGC;
    protected IStoriesListUGCItem getUGCListItem;


    public BaseStoryListItem(@NonNull View itemView, AppearanceManager manager,
                             boolean isFavorite,
                             boolean isUGC) {
        super(itemView);
        this.manager = manager;
        this.isFavorite = isFavorite;
        this.isUGC = isUGC;
        getFavoriteListItem = manager.csFavoriteListItemInterface();
        getListItem = manager.csListItemInterface();
        getUGCListItem = manager.csListUGCItemInterface();
        Context context = itemView.getContext();
        if (manager.csListItemMargin(context) >= 0) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            lp.setMargins(Sizes.dpToPxExt(manager.csListItemMargin(context) / 2, context), 0,
                    Sizes.dpToPxExt(manager.csListItemMargin(context) / 2, context), 0);
            itemView.setLayoutParams(lp);
        }

    }

    public Integer backgroundColor;
    public ClickCallback callback;

    public abstract void bind(Integer id,
                              String titleText,
                              Integer titleColor,
                              String sourceText,
                              String imageUrl,
                              Integer backgroundColor,
                              boolean isOpened,
                              boolean hasAudio,
                              String videoUrl,
                              StoryData storyData,
                              ClickCallback callback);

   /* public void measure(int widthMeasureSpec, int heightMeasureSpec, float aspectRatio) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = widthMode == MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE : MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = heightMode == MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE : MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY && widthMode == MeasureSpec.EXACTLY) {
            measuredWidth = widthSize;
            measuredHeight = heightSize;
        } else if (heightMode == MeasureSpec.EXACTLY) {
            measuredWidth = (int) Math.min(widthSize, heightSize * aspectRatio);
            measuredHeight = (int) (measuredWidth / aspectRatio);
        } else if (widthMode == MeasureSpec.EXACTLY) {
            measuredHeight = (int) Math.min(heightSize, widthSize / aspectRatio);
            measuredWidth = (int) (measuredHeight * aspectRatio);
        } else {
            if (widthSize > heightSize * aspectRatio) {
                measuredHeight = heightSize;
                measuredWidth = (int) (measuredHeight * aspectRatio);
            } else {
                measuredWidth = widthSize;
                measuredHeight = (int) (measuredWidth / aspectRatio);
            }

        }
    }*/

    public abstract void bindFavorite();

    public abstract void bindUGC();
}
