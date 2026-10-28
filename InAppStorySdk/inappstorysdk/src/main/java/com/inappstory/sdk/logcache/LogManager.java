package com.inappstory.sdk.logcache;

public interface LogManager {
    void saveLog(String tag, String message);

    void sendLogs();

    void clearOldLogs();

    void archiveLogs();
}