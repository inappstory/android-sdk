package com.inappstory.sdk.stories.uidomain.list.defaultitems.story;

import com.inappstory.sdk.stories.uidomain.list.defaultitems.IGetBitmap;

public interface IDefaultStoryListItemManager {

    void getBitmap(String link, IGetBitmap getBitmapCallback);
}
