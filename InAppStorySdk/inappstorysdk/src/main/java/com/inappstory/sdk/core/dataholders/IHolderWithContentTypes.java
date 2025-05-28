package com.inappstory.sdk.core.dataholders;

import com.inappstory.sdk.stories.api.models.ContentType;

import java.util.List;

public interface IHolderWithContentTypes<T> {
    List<T> getByType(ContentType type);
    T getByIdAndType(int id, ContentType type);
    void setByIdAndType(T newContentItem, int id, ContentType type);
    void setByType(List<T> content, ContentType type);
    void clearByType(ContentType type);
    void clear();
}
