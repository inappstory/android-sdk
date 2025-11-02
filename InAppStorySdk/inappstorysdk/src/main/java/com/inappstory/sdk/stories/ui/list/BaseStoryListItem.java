package com.inappstory.sdk.stories.ui.list;

import android.app.Activity;
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
import android.view.ViewGroup;

import com.inappstory.sdk.ugc.list.IStoriesListUGCItem;


public abstract class BaseStoryListItem extends RecyclerView.ViewHolder {

    protected AppCompatTextView title;
    protected AppearanceManager manager;

    protected IStoriesListItem getListItem;

    public boolean isFavorite;
    protected IGetFavoriteListItem getFavoriteListItem;

    public boolean isUGC;
    protected IStoriesListUGCItem getUGCListItem;


    public ViewGroup getParent() {
        return parent;
    }

    ViewGroup parent = null;

    public BaseStoryListItem(@NonNull View itemView,
                             ViewGroup parent,
                             AppearanceManager manager,
                             boolean isFavorite,
                             boolean isUGC) {
        super(itemView);
        this.parent = parent;
        this.manager = manager;
        this.isFavorite = isFavorite;
        this.isUGC = isUGC;
        Context context = itemView.getContext();
        getFavoriteListItem = manager.csFavoriteListItemInterface();
        getListItem = manager.csListItemInterface();
        getUGCListItem = manager.csListUGCItemInterface();

        int pWidth = parent.getWidth();
        pWidth = pWidth > 0 ? pWidth : Sizes.getScreenSize(context).x;
        if (getListItem == null)
            getListItem = new StoriesListDefaultItem(
                    manager,
                    context,
                    parent.getLayoutDirection(),
                    pWidth
            );
        if (getFavoriteListItem == null || getFavoriteListItem.getFavoriteItem() == null)
            getFavoriteListItem = new StoriesListDefaultFavoriteItem(
                    manager,
                    context,
                    parent.getLayoutDirection(),
                    pWidth
            );
        if (getUGCListItem == null)
            getUGCListItem = new StoriesListDefaultUgcEditorItem(manager, context, parent.getLayoutDirection());
        if (manager.csListItemMargin(context) >= 0) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            lp.setMargins(manager.csListItemMargin(context) / 2, 0,
                    manager.csListItemMargin(context) / 2, 0);
            itemView.setLayoutParams(lp);
        }

    }

    public Integer backgroundColor;
    public ClickCallback callback;

    protected boolean viewCanBeUsed(View view, ViewGroup parent) {
        if (view == null) return false;
        if (parent == null) return false;
        if (!parent.isAttachedToWindow()) return false;
        Context context = view.getContext();
        if (context == null)
            return false;
        if (context instanceof Activity) {
            return !((Activity) context).isFinishing() && !((Activity) context).isDestroyed();
        }
        return true;
    }

    public abstract void bind(Integer id,
                              String titleText,
                              Integer titleColor,
                              String imageUrl,
                              Integer backgroundColor,
                              boolean isOpened,
                              boolean hasAudio,
                              String videoUrl,
                              StoryData storyData,
                              ClickCallback callback);

    public abstract void bindFavorite();

    public abstract void bindUGC();
}
