package com.inappstory.sdk.core;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.stories.callbacks.ExceptionCallback;

public class IASExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Thread.UncaughtExceptionHandler oldHandler;
    private final IASCore core;

    public IASExceptionHandler(IASCore core) {
        this.oldHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.core = core;
    }

    @Override
    public void uncaughtException(final @NonNull Thread t, @NonNull final Throwable e) {
        core.exceptionManager().createExceptionLog(e);

        if (oldHandler != null)
            oldHandler.uncaughtException(t, e);
        core.callbacksAPI().useCallback(
                IASCallbackType.EXCEPTION,
                new UseIASCallback<ExceptionCallback>() {
                    @Override
                    public void use(@NonNull ExceptionCallback callback) {
                        callback.onException(e);
                    }
                }
        );
    }
}
