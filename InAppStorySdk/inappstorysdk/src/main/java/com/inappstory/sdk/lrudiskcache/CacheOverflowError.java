package com.inappstory.sdk.lrudiskcache;

import androidx.annotation.Nullable;

import com.inappstory.sdk.stories.api.models.logs.CanBeConvertedToExceptionLog;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;


public class CacheOverflowError extends Throwable implements CanBeConvertedToExceptionLog {
    private final long currentSize;
    private final long newFileSize;
    private final long limitSize;

    public CacheOverflowError(
            long currentSize,
            long newFileSize,
            long limitSize
    ) {
        this.currentSize = currentSize;
        this.newFileSize = newFileSize;
        this.limitSize = limitSize;
    }

    @Nullable
    @Override
    public String getMessage() {
        return "Current size: "
                + currentSize
                + "\nNew file size: "
                + newFileSize
                + "\nLimit size: "
                + limitSize;
    }

    @Override
    public ExceptionLog toExceptionLog() {
        ExceptionLog log = new ExceptionLog();
        log.message = "CacheOverflow";
        log.stacktrace = "Current size: "
                + currentSize
                + "\nNew file size: "
                + newFileSize
                + "\nLimit size: "
                + limitSize;
        log.file = "DEBUG LOG";
        log.line = 0;
        return log;
    }
}
