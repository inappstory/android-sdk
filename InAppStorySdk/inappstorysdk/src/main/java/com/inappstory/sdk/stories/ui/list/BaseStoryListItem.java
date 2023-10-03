package com.inappstory.sdk.stories.ui.list;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.stories.ui.views.IGetFavoriteListItem;
import com.inappstory.sdk.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.utils.Sizes;

import android.view.View.MeasureSpec;

import com.inappstory.sdk.ugc.list.IStoriesListUGCItem;


public abstract class BaseStoryListItem extends RecyclerView.ViewHolder {

    protected AppearanceManager manager;

    protected final IStoriesListItem getListItem;

    public boolean isFavorite;
    protected final IGetFavoriteListItem getFavoriteListItem;

    public boolean isUGC;
    protected IStoriesListUGCItem getUGCListItem;


    public BaseStoryListItem(@NonNull View itemView, AppearanceManager manager,
                             boolean isFavorite,
                             boolean isUGC) {
        super(itemView);
        this.manager = manager;
        this.isFavorite = isFavorite;
        this.isUGC = isUGC;
        if (manager.csListItemInterface() != null) {
            getFavoriteListItem = manager.csFavoriteListItemInterface();
        } else {
            getFavoriteListItem = new DefaultFavoriteStoryListItem(manager, itemView.getContext());
        }
        if (manager.csListItemInterface() != null) {
            getListItem = manager.csListItemInterface();
        } else {
            getListItem = new DefaultStoryListItem(manager, itemView.getContext());
        }
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
                              ClickCallback callback);


    public abstract void bindFavorite();

    public abstract void bindUGC();
}
