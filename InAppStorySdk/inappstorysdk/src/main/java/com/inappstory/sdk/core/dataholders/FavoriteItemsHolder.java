package com.inappstory.sdk.core.dataholders;

import com.inappstory.sdk.core.dataholders.models.IFavoriteItem;
import com.inappstory.sdk.stories.api.models.ContentType;

import java.util.ArrayList;
import java.util.List;

public class FavoriteItemsHolder implements IFavoriteItemsHolder {

    private final Object contentLock = new Object();
    private final List<IFavoriteItem> stories = new ArrayList<>();

    private List<IFavoriteItem> getContentList(ContentType type) {
        switch (type) {
            case STORY:
                return stories;
            default:
                return null;
        }
    }

    @Override
    public List<IFavoriteItem> getByType(ContentType type) {
        synchronized (contentLock) {
            return getContentList(type);
        }
    }

    @Override
    public IFavoriteItem getByIdAndType(int id, ContentType type) {
        synchronized (contentLock) {
            List<IFavoriteItem> content = getContentList(type);
            if (content == null) return null;
            for (IFavoriteItem contentItem : content) {
                if (contentItem.id() == id)
                    return contentItem;
            }
        }
        return null;
    }

    @Override
    public void setByIdAndType(IFavoriteItem newContentItem, int id, ContentType type) {
        synchronized (contentLock) {
            List<IFavoriteItem> content = getContentList(type);
            if (content == null) return;
            int index = -1;
            for (int i = 0; i < content.size(); i++) {
                if (content.get(i).id() == id) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                content.remove(index);
            }
            if (newContentItem != null) {
                content.add(0, newContentItem);
            }
        }
    }

    @Override
    public void setByType(List<IFavoriteItem> newContent, ContentType type) {
        synchronized (contentLock) {
            List<IFavoriteItem> content = getContentList(type);
            if (content == null) return;
            content.clear();
            content.addAll(newContent);
        }
    }

    @Override
    public void clearByType(ContentType type) {
        synchronized (contentLock) {
            List<IFavoriteItem> content = getContentList(type);
            if (content == null) return;
            content.clear();
        }
    }

    @Override
    public void clear() {
        synchronized (contentLock) {
            stories.clear();
        }
    }

    @Override
    public boolean isEmpty(ContentType type) {
        synchronized (contentLock) {
            List<IFavoriteItem> content = getContentList(type);
            if (content != null) return content.isEmpty();
        }
        return true;
    }
}
