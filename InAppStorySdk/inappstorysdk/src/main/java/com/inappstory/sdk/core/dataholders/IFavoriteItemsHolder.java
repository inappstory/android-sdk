package com.inappstory.sdk.core.dataholders;

import com.inappstory.sdk.core.data.IFavoriteItem;
import com.inappstory.sdk.stories.api.models.ContentType;

public interface IFavoriteItemsHolder extends IHolderWithContentTypes<IFavoriteItem> {
    boolean isEmpty(ContentType type);
}