package com.inappstory.sdk.core.dataholders;

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
}
