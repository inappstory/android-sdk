package com.inappstory.sdk.stories.uidomain.list.defaultitems.favorite;

import com.inappstory.sdk.stories.uidomain.list.defaultitems.IGetBitmap;

public interface IDefaultFavoriteStoryListItemManager {
    boolean isSameImageLink(int index, String link);

    void storeImageLinkLocal(int index, String link);

    void getBitmap(int index, String link, IGetBitmap getBitmapCallback);
}
