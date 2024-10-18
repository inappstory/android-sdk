package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.inappmessage.core.models.IInAppMessage;
import com.inappstory.sdk.inappmessage.core.models.InAppMessageFeed;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class InAppMessageDownloadManager {
    private final IASCore core;
    private final List<IInAppMessage> currentMessages = new ArrayList<>();
    private final Object messagesLock = new Object();

    public void clearMessages() {
        synchronized (messagesLock) {
            currentMessages.clear();
        }
    }

    public List<IInAppMessage> messages() {
        synchronized (messagesLock) {
            return currentMessages;
        }
    }

    public InAppMessageDownloadManager(IASCore core) {
        this.core = core;
    }

    public void getInAppMessageById(String messageId) {

    }

    public void getInAppMessages() {
        core.sessionManager().useOrOpenSession(
                new OpenSessionCallback() {
                    @Override
                    public void onSuccess(String sessionId) {
                        core.network().enqueue(
                                core.network().getApi().getInAppMessages(1, null, null),
                                new NetworkCallback<InAppMessageFeed>() {
                                    @Override
                                    public void onSuccess(InAppMessageFeed inAppMessageFeed) {
                                        if (inAppMessageFeed == null) {
                                            return;
                                        }
                                        synchronized (messagesLock) {
                                            currentMessages.addAll(inAppMessageFeed.messages());
                                        }
                                    }

                                    @Override
                                    public Type getType() {
                                        return InAppMessageFeed.class;
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError() {

                    }
                }
        );
    }
}
