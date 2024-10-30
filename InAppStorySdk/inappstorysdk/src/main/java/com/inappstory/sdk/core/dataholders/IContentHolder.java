package com.inappstory.sdk.core.dataholders;

import com.inappstory.sdk.core.dataholders.models.IContentWithStatus;
import com.inappstory.sdk.stories.api.models.ContentType;

public interface IContentHolder {
    IListsContentHolder listsContent();
    IReaderContentHolder readerContent();
    IFavoriteItemsHolder favoriteItems();
    IContentWithStatus getByIdAndType(int id, ContentType type);
    void like(int id, ContentType type, int like);
    void favorite(int id, ContentType type, boolean favorite);
    void clearAllFavorites(ContentType type);
    void clearAll();
}
