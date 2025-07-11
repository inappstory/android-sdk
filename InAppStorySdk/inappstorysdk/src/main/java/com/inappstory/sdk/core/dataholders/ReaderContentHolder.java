package com.inappstory.sdk.core.dataholders;

import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.stories.api.models.ContentType;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ReaderContentHolder implements IReaderContentHolder {

    public ReaderContentHolder() {
    }

    @Override
    public List<IReaderContent> getByType(ContentType type) {
        synchronized (contentLock) {
            return getContentList(type);
        }
    }

    @Override
    public IReaderContent getByIdAndType(int id, ContentType type) {
        synchronized (contentLock) {
            List<IReaderContent> content = getContentList(type);
            if (content == null) return null;
            for (IReaderContent contentItem : content) {
                if (contentItem.id() == id)
                    return contentItem;
            }
        }
        return null;
    }

    private final Object contentLock = new Object();

    private final List<IReaderContent> stories = new ArrayList<>();
    private final List<IReaderContent> ugcStories = new ArrayList<>();
    private final List<IReaderContent> inAppMessages = new ArrayList<>();

    private List<IReaderContent> getContentList(ContentType type) {
        switch (type) {
            case STORY:
                return stories;
            case UGC:
                return ugcStories;
            case IN_APP_MESSAGE:
                return inAppMessages;
            default:
                return null;
        }
    }

    @Override
    public void setByIdAndType(IReaderContent newContentItem, int id, ContentType type) {
        synchronized (contentLock) {
            List<IReaderContent> content = getContentList(type);
            if (content == null) return;
            ListIterator<IReaderContent> contentIterator = content.listIterator();
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
    public void setByType(List<IReaderContent> newContent, ContentType type) {
        synchronized (contentLock) {
            List<IReaderContent> content = getContentList(type);
            if (content == null) return;
            content.clear();
            content.addAll(newContent);
        }
    }

    @Override
    public void clearByType(ContentType type) {
        synchronized (contentLock) {
            List<IReaderContent> content = getContentList(type);
            if (content == null) return;
            content.clear();
        }
    }

    @Override
    public void clear() {
        synchronized (contentLock) {
            stories.clear();
            ugcStories.clear();
            inAppMessages.clear();
        }
    }
}
