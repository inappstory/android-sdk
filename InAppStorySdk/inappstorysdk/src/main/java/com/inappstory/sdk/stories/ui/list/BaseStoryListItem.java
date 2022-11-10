package com.inappstory.sdk.stories.ui.list;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.stories.ui.views.IGetFavoriteListItem;
import com.inappstory.sdk.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.utils.Sizes;
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
        if (manager.csListItemMargin() >= 0) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            lp.setMargins(Sizes.dpToPxExt(manager.csListItemMargin() / 2), 0,
                    Sizes.dpToPxExt(manager.csListItemMargin() / 2), 0);
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
