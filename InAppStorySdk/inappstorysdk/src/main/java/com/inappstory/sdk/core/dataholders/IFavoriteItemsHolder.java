package com.inappstory.sdk.core.dataholders;

import com.inappstory.sdk.stories.api.models.ContentType;

import java.util.List;

public interface IFavoriteItemsHolder extends IHolderWithContentTypes<IFavoriteItem> {
    boolean isEmpty(ContentType type);
}