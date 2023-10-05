package com.inappstory.sdk.stories.ui.list.items;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.stories.ui.list.defaultitems.StoriesListDefaultFavoriteItem;
import com.inappstory.sdk.stories.ui.list.defaultitems.StoriesListDefaultItem;
import com.inappstory.sdk.stories.ui.list.defaultitems.StoriesListDefaultUgcEditorItem;
import com.inappstory.sdk.stories.ui.views.IGetFavoriteListItem;
import com.inappstory.sdk.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.utils.Sizes;

import com.inappstory.sdk.ugc.list.IStoriesListUGCItem;


public abstract class BaseStoriesListItem extends RecyclerView.ViewHolder {
    protected AppearanceManager appearanceManager;
    protected final IStoriesListItem getListItem;
    protected final IGetFavoriteListItem getFavoriteListItem;
    protected final IStoriesListUGCItem getUGCListItem;


    public BaseStoriesListItem(
            @NonNull View itemView,
            AppearanceManager appearanceManager
    ) {
        super(itemView);
        Context context = itemView.getContext();
        this.appearanceManager = appearanceManager;
        if (appearanceManager.csListItemInterface() != null) {
            getFavoriteListItem = appearanceManager.csFavoriteListItemInterface();
        } else {
            getFavoriteListItem = new StoriesListDefaultFavoriteItem(appearanceManager, context);
        }
        if (appearanceManager.csListItemInterface() != null) {
            getListItem = appearanceManager.csListItemInterface();
        } else {
            getListItem = new StoriesListDefaultItem(appearanceManager, context);
        }
        if (appearanceManager.csListUGCItemInterface() != null) {
            getUGCListItem = appearanceManager.csListUGCItemInterface();
        } else {
            getUGCListItem = new StoriesListDefaultUgcEditorItem(appearanceManager, context);
        }
        setItemMargin();
    }

    private void setItemMargin() {
        Context context = itemView.getContext();
        if (appearanceManager.csListItemMargin(context) >= 0) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            lp.setMargins(Sizes.dpToPxExt(appearanceManager.csListItemMargin(context) / 2, context), 0,
                    Sizes.dpToPxExt(appearanceManager.csListItemMargin(context) / 2, context), 0);
            itemView.setLayoutParams(lp);
        }
    }
}
