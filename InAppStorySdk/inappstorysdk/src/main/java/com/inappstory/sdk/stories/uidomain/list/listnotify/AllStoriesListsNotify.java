package com.inappstory.sdk.stories.uidomain.list.listnotify;


import com.inappstory.sdk.core.IASCore;

public class AllStoriesListsNotify implements IAllStoriesListsNotify {
    private ChangeUserIdListNotify changeUserIdListNotify;


    public AllStoriesListsNotify(
            ChangeUserIdListNotify changeUserIdListNotify
    ) {
        this.changeUserIdListNotify = changeUserIdListNotify;
    }

    @Override
    public void unsubscribe() {
        IASCore.getInstance().removeChangeUserIdListNotify(changeUserIdListNotify);
    }

    @Override
    public void subscribe() {
        IASCore.getInstance().addChangeUserIdListNotify(changeUserIdListNotify);
    }
}
