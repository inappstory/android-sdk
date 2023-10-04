package com.inappstory.sdk.stories.ui.list.items.base;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.stories.ui.list.ClickCallback;
import com.inappstory.sdk.stories.ui.list.defaultitems.DefaultFavoriteStoryListItem;
import com.inappstory.sdk.stories.ui.list.defaultitems.DefaultStoryListItem;
import com.inappstory.sdk.stories.ui.list.defaultitems.DefaultUgcListItem;
import com.inappstory.sdk.stories.ui.views.IGetFavoriteListItem;
import com.inappstory.sdk.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.utils.Sizes;

import com.inappstory.sdk.ugc.list.IStoriesListUGCItem;


public abstract class BaseStoryListItem extends RecyclerView.ViewHolder {

    public boolean isFavorite;
    public boolean isUGC;

    protected AppearanceManager appearanceManager;
    protected final IStoriesListItem getListItem;
    protected final IGetFavoriteListItem getFavoriteListItem;
    protected final IStoriesListUGCItem getUGCListItem;

    public Integer backgroundColor;



    public BaseStoryListItem(
            @NonNull View itemView,
                             AppearanceManager appearanceManager,
                             boolean isFavorite,
                             boolean isUGC
    ) {
        super(itemView);
        Context context = itemView.getContext();
        this.appearanceManager = appearanceManager;
        this.isFavorite = isFavorite;
        this.isUGC = isUGC;
        if (appearanceManager.csListItemInterface() != null) {
            getFavoriteListItem = appearanceManager.csFavoriteListItemInterface();
        } else {
            getFavoriteListItem = new DefaultFavoriteStoryListItem(appearanceManager, context);
        }
        if (appearanceManager.csListItemInterface() != null) {
            getListItem = appearanceManager.csListItemInterface();
        } else {
            getListItem = new DefaultStoryListItem(appearanceManager, context);
        }
        if (appearanceManager.csListUGCItemInterface() != null) {
            getUGCListItem = appearanceManager.csListUGCItemInterface();
        } else {
            getUGCListItem = new DefaultUgcListItem(appearanceManager, context);
        }
        if (appearanceManager.csListItemMargin(context) >= 0) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            lp.setMargins(Sizes.dpToPxExt(appearanceManager.csListItemMargin(context) / 2, context), 0,
                    Sizes.dpToPxExt(appearanceManager.csListItemMargin(context) / 2, context), 0);
            itemView.setLayoutParams(lp);
        }
    }

    public abstract void bind(
            Integer id,
            String titleText,
            Integer titleColor,
            String sourceText,
            Integer backgroundColor,
            boolean isOpened,
            boolean hasAudio,
            ClickCallback callback
    );

    public abstract void bindFavorite();

    public abstract void bindUGC();
}
