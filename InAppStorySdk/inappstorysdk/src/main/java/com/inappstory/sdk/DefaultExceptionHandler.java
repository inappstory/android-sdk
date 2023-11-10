package com.inappstory.sdk;

import android.util.Log;

import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;
import com.inappstory.sdk.stories.exceptions.ExceptionManager;

class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {


    private void createExceptionLog(Throwable throwable) {
        ExceptionManager em = new ExceptionManager();
        ExceptionLog el = em.generateExceptionLog(throwable);
        em.saveException(el);
        em.sendException(el);
    }

    Thread.UncaughtExceptionHandler oldHandler;

    public DefaultExceptionHandler() {
        oldHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable throwable) {
        createExceptionLog(throwable);
        Log.d("InAppStory_SDK_error", throwable.getCause() + "\n"
                + throwable.getMessage());

        if (oldHandler != null)
            oldHandler.uncaughtException(thread, throwable);
        try {
            if (InAppStoryManager.getInstance() != null) {
                if (InAppStoryManager.getInstance().getExceptionCallback() != null) {
                    InAppStoryManager.getInstance().getExceptionCallback().onException(throwable);
                }
            }
        } catch (Exception ignored) {

        }
    }
}