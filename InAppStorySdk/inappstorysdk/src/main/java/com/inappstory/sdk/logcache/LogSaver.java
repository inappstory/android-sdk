package com.inappstory.sdk.logcache;

import java.util.List;

public interface LogSaver {
    void saveLog(String tag, String message);
    void prepareFiles();
    List<String> getFiles();
}
