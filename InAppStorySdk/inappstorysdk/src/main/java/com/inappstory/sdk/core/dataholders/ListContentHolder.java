package com.inappstory.sdk.core.dataholders;

import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.stories.api.models.ContentType;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ListContentHolder implements IListsContentHolder {

    public ListContentHolder() {
    }


    @Override
    public List<IListItemContent> getByType(ContentType type) {
        synchronized (contentLock) {
            return getContentList(type);
        }
    }

    @Override
    public IListItemContent getByIdAndType(int id, ContentType type) {
        synchronized (contentLock) {
            List<IListItemContent> content = getContentList(type);
            if (content == null) return null;
            for (IListItemContent contentItem : content) {
                if (contentItem.id() == id)
                    return contentItem;
            }
        }
        return null;
    }

    private final Object contentLock = new Object();

    private final List<IListItemContent> stories = new ArrayList<>();
    private final List<IListItemContent> ugcStories = new ArrayList<>();

    private List<IListItemContent> getContentList(ContentType type) {
        switch (type) {
            case STORY:
                return stories;
            case UGC:
                return ugcStories;
            default:
                return null;
        }
    }

    @Override
    public void setByIdAndType(IListItemContent newContentItem, int id, ContentType type) {
        synchronized (contentLock) {
            List<IListItemContent> content = getContentList(type);
            if (content == null) return;
            ListIterator<IListItemContent> contentIterator = content.listIterator();
            boolean contentIsNotReplaced = true;
            while (contentIterator.hasNext()) {
                if (contentIterator.next().id() == id) {
                    if (newContentItem == null) {
                        contentIterator.remove();
                    } else {
                        contentIterator.set(newContentItem);
                    }
                    contentIsNotReplaced = false;
                    break;
                }
            }
            if (contentIsNotReplaced && newContentItem != null) {
                content.add(newContentItem);
            }
        }
    }

    @Override
    public void setByType(List<IListItemContent> newContent, ContentType type) {
        synchronized (contentLock) {
            List<IListItemContent> content = getContentList(type);
            if (content == null) return;
            content.clear();
            content.addAll(newContent);
        }
    }

    @Override
    public void clearByType(ContentType type) {
        synchronized (contentLock) {
            List<IListItemContent> content = getContentList(type);
            if (content == null) return;
            content.clear();
        }
    }

    @Override
    public void clear() {
        synchronized (contentLock) {
            stories.clear();
            ugcStories.clear();
        }
    }
}
