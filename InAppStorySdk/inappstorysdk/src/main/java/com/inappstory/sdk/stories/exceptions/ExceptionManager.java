package com.inappstory.sdk.stories.exceptions;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.utils.SessionManager;

import java.lang.reflect.Type;
import java.util.UUID;

public class ExceptionManager {
    public static final String SAVED_EX = "saved_exception";

    public ExceptionManager(IASCore core) {
        this.core = core;
    }

    private final IASCore core;

    public void sendException(ExceptionLog log) {
        logException(log);

        final ExceptionLog copiedLog = new ExceptionLog();
        copiedLog.stacktrace = log.stacktrace;
        copiedLog.timestamp = log.timestamp;
        copiedLog.message = log.message;
        copiedLog.file = log.file;
        copiedLog.session = log.session;
        copiedLog.line = log.line;
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            return;
        }
        final InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess(String sessionId) {
                if (service.getSession().allowCrash())
                    networkClient.enqueue(
                            networkClient.getApi().sendException(
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
                                    SharedPreferencesAPI.removeString(SAVED_EX);
                                }

                                @Override
                                public Type getType() {
                                    return null;
                                }
                            });
                else
                    SharedPreferencesAPI.removeString(SAVED_EX);
            }

            @Override
            public void onError() {
                SharedPreferencesAPI.removeString(SAVED_EX);
            }
        });

    }

    public void sendSavedException() {
        ExceptionLog log = getSavedException();
        if (log == null) return;
   //     sendException(log);
    }


    public ExceptionLog generateExceptionLog(Throwable throwable) {
        ExceptionLog log = new ExceptionLog();
        log.id = UUID.randomUUID().toString();
        log.timestamp = System.currentTimeMillis();
        log.message = throwable.getClass().getCanonicalName() + ": " + throwable.getMessage();

        InAppStoryService service = InAppStoryService.getInstance();
        log.session = (service != null ? service.getSession().getSessionId() : null);
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

    public void saveException(ExceptionLog log) {
        try {
            SharedPreferencesAPI.saveString(SAVED_EX, JsonParser.getJson(log));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ExceptionLog getSavedException() {
        String ex = SharedPreferencesAPI.getString(SAVED_EX, null);
        if (ex == null) return null;
        return JsonParser.fromJson(ex, ExceptionLog.class);
    }

    void logException(ExceptionLog log) {
        InAppStoryManager.sendExceptionLog(log);
    }
}
