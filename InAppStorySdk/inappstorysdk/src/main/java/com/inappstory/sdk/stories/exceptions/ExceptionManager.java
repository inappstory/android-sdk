package com.inappstory.sdk.stories.exceptions;

import android.util.Log;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;

import java.lang.reflect.Type;
import java.util.UUID;

public class ExceptionManager {
    public static final String SAVED_EX = "saved_exception";

    public ExceptionManager(IASCore core) {
        this.core = core;
    }

    private final IASCore core;

    public void createExceptionLog(Throwable throwable) {
        ExceptionLog el = generateExceptionLog(throwable);
        Log.d("EXCEPTION", throwable.getMessage() + "");
        saveException(el);
        sendException(el);
    }

    private void sendException(ExceptionLog log) {
        logException(log);

        final ExceptionLog copiedLog = new ExceptionLog();
        copiedLog.stacktrace = log.stacktrace;
        copiedLog.timestamp = log.timestamp;
        copiedLog.message = log.message;
        copiedLog.file = log.file;
        copiedLog.session = log.session;
        copiedLog.line = log.line;

        core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess(RequestLocalParameters sessionParameters) {
                if (core.statistic().exceptions().disabled()) {
                    core.sharedPreferencesAPI().removeString(SAVED_EX);
                    return;
                }
                core.network().enqueue(
                        core.network().getApi().sendException(
                                copiedLog.session,
                                copiedLog.timestamp / 1000,
                                copiedLog.message,
                                copiedLog.file,
                                copiedLog.line,
                                copiedLog.stacktrace
                        ),
                        new NetworkCallback() {
                            @Override
                            public void onSuccess(Object response) {
                                core.sharedPreferencesAPI().removeString(SAVED_EX);
                            }

                            @Override
                            public Type getType() {
                                return null;
                            }
                        }
                );
            }

            @Override
            public void onError() {
                core.sharedPreferencesAPI().removeString(SAVED_EX);
            }
        });

    }

    public void sendSavedException() {
        ExceptionLog log = getSavedException();
        if (log == null) return;
        //     sendException(log);
    }


    private ExceptionLog generateExceptionLog(Throwable throwable) {
        ExceptionLog log = new ExceptionLog();
        log.id = UUID.randomUUID().toString();
        log.timestamp = System.currentTimeMillis();
        log.message = throwable.getClass().getCanonicalName() + ": " + throwable.getMessage();
        log.session = core.sessionManager().getSession().getSessionId();
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        if (stackTraceElements.length > 0) {
            String stackTrace = "";
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                stackTrace += stackTraceElement.toString() + "\n";
            }
            log.stacktrace = stackTrace;
            log.file = stackTraceElements[0].getFileName();
            log.line = stackTraceElements[0].getLineNumber();
        }
        return log;
    }

    private void saveException(ExceptionLog log) {
        try {
            core.sharedPreferencesAPI().saveString(SAVED_EX, JsonParser.getJson(log));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ExceptionLog getSavedException() {
        String ex = core.sharedPreferencesAPI().getString(SAVED_EX, null);
        if (ex == null) return null;
        return JsonParser.fromJson(ex, ExceptionLog.class);
    }

    void logException(ExceptionLog log) {
        InAppStoryManager.sendExceptionLog(log);
    }
}
