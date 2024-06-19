package com.inappstory.sdk.stories.api.models.callbacks;

import com.inappstory.sdk.OldInAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryListType;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.utils.SessionManager;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

public abstract class LoadListCallback extends NetworkCallback<List<Story>> {
    @Override
    public abstract void onSuccess(List<Story> response);

    @Override
    public Type getType() {
        return new StoryListType();
    }

    @Override
    public void onError(int code, String message) {
        if (CallbackManager.getInstance().getErrorCallback() != null) {
            CallbackManager.getInstance().getErrorCallback().loadListError("");
        }
    }

    @Override
    public void error424(String message) {
        if (CallbackManager.getInstance().getErrorCallback() != null) {
            CallbackManager.getInstance().getErrorCallback().loadListError("");
        }

        String oldUserId = "";
        Locale locale = Locale.getDefault();
        OldInAppStoryManager inAppStoryManager = OldInAppStoryManager.getInstance();
        if (inAppStoryManager != null) {
            oldUserId = inAppStoryManager.getUserId();
            locale = inAppStoryManager.getCurrentLocale();
        }
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null) {
            SessionManager.getInstance().closeSession(
                    true,
                    false,
                    locale,
                    oldUserId,
                    service.getSession().getSessionId()
            );
        }
    }

}
