package com.inappstory.sdk.core.dataholders;

import com.inappstory.sdk.core.data.IContentWithStatus;
import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.stories.api.models.ContentType;

import java.util.List;

public class ContentHolder implements IContentHolder {
    private final IListsContentHolder listsContentHolder = new ListContentHolder();
    private final IReaderContentHolder readerContentHolder = new ReaderContentHolder();
    private final IFavoriteItemsHolder favoriteItemsHolder = new FavoriteItemsHolder();


    @Override
    public IListsContentHolder listsContent() {
        return listsContentHolder;
    }

    @Override
    public IReaderContentHolder readerContent() {
        return readerContentHolder;
    }

    @Override
    public IFavoriteItemsHolder favoriteItems() {
        return favoriteItemsHolder;
    }

    @Override
    public IContentWithStatus getByIdAndType(int id, ContentType type) {
        IContentWithStatus content = readerContentHolder.getByIdAndType(id, type);
        if (content == null) content = listsContentHolder.getByIdAndType(id, type);
        return content;
    }

    @Override
    public void like(int id, ContentType type, int like) {
        IReaderContent readerContent = readerContentHolder.getByIdAndType(id, type);
        if (readerContent != null) readerContent.like(like);
        IListItemContent listItemContent = listsContentHolder.getByIdAndType(id, type);
        if (listItemContent != null) listItemContent.like(like);
    }

    @Override
    public void favorite(int id, ContentType type, boolean favorite) {
        IReaderContent readerContent = readerContentHolder.getByIdAndType(id, type);
        if (readerContent != null) readerContent.favorite(favorite);
        IListItemContent listItemContent = listsContentHolder.getByIdAndType(id, type);
        if (listItemContent != null) {
            listItemContent.favorite(favorite);
            if (favorite)
                listItemContent.setOpened(true);
        }
        if (!favorite) {
            favoriteItemsHolder.setByIdAndType(null, id, type);
        }
    }

    @Override
    public void clearAllFavorites(ContentType type) {
        List<IReaderContent> readerContent = readerContentHolder.getByType(type);
        List<IListItemContent> listsContent = listsContentHolder.getByType(type);
        favoriteItemsHolder.clearByType(type);
        for (IReaderContent readerContentItem : readerContent) {
            readerContentItem.favorite(false);
        }
        for (IListItemContent listContentItem : listsContent) {
            listContentItem.favorite(false);
        }
    }

    @Override
    public void clearAll() {
        readerContentHolder.clear();
     //   listsContentHolder.clear();
     //   favoriteItemsHolder.clear();
    }
}
