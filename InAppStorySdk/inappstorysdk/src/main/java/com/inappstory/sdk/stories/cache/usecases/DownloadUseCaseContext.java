package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.utils.ProgressCallback;

public class DownloadUseCaseContext<T> {

    private final ProgressCallback progressCallback;
    private final DownloadInterruption interruption;
    private final UseCaseCallback<T> useCaseCallback;

    public DownloadUseCaseContext(
            ProgressCallback progressCallback,
                                  DownloadInterruption interruption,
            UseCaseCallback<T> useCaseCallback) {
        this.progressCallback = progressCallback;
        this.interruption = interruption;
        this.useCaseCallback = useCaseCallback;
    }

    public ProgressCallback progressCallback() {
        return progressCallback;
    }

    public DownloadInterruption interruption() {
        return interruption;
    }

    public UseCaseCallback<T> useCaseCallback() {
        return useCaseCallback;
    }

}
