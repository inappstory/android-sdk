package com.inappstory.sdk.imageloader;

import android.content.Context;
import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.game.cache.SuccessUseCaseCallback;
import com.inappstory.sdk.memcache.GetBitmapFromCacheWithFilePath;
import com.inappstory.sdk.memcache.IGetBitmapFromMemoryCache;
import com.inappstory.sdk.memcache.IGetBitmapFromMemoryCacheError;
import com.inappstory.sdk.stories.cache.usecases.CustomFileUseCase;

import java.io.File;

public class ImageLoader {

    public void getBitmapFromUrl(
            @NonNull final String url,
            @NonNull final IGetBitmapFromMemoryCache success
    ) {
        getBitmapFromUrl(url, success, null);
    }

    public void getFileLinkFromUrl(
            @NonNull final String url,
            final SuccessUseCaseCallback<String> useCaseCallback
    ) {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                new CustomFileUseCase(
                        service.getFilesDownloadManager(),
                        url,
                        new SuccessUseCaseCallback<File>() {
                            @Override
                            public void onSuccess(File result) {
                                final String path = result.getAbsolutePath();
                                useCaseCallback.onSuccess(path);
                            }

                            @Override
                            public void onError(String message) {
                                useCaseCallback.onError(message);
                            }
                        }
                ).getFile();
            }

            @Override
            public void error() throws Exception {
                useCaseCallback.onError("");
            }
        });

    }

    public void getBitmapFromUrl(
            @NonNull final String url,
            @NonNull final IGetBitmapFromMemoryCache success,
            final IGetBitmapFromMemoryCacheError error
    ) {
        getFileLinkFromUrl(url, new SuccessUseCaseCallback<String>() {
            @Override
            public void onSuccess(String result) {
                getBitmapFromFilePath(result, success, error);
            }

            @Override
            public void onError(String message) {
                error.onError();
            }
        });
    }

    public void getBitmapFromFilePath(
            @NonNull String path,
            @NonNull IGetBitmapFromMemoryCache success,
            IGetBitmapFromMemoryCacheError error
    ) {
        new GetBitmapFromCacheWithFilePath(path, success, error).get();
    }
}
