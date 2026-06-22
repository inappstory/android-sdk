package com.inappstory.sdk.core.logcache;

public interface LogManager {
    void saveLog(String tag, String message);

    void sendLogs();

    void clearOldLogs();

    void archiveLogs();
}
