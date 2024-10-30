package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.core.IASCore;

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

    }
}
