package com.inappstory.sdk.compatibleapi;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.stories.ui.ScreensManager;

public class InAppStoryAPI {
    public void initSDK(@NonNull Context context) {
        InAppStoryManager.initSDK(context);
    }

    public void setIASLogger(InAppStoryManager.IASLogger logger) {
        InAppStoryManager.logger = logger;
    }

    public void clearCache() {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.clearCache();
            }
        });
    }

    public void closeStoryReader() {
        //TODO Not implemented yet
    }

    public void openGame(final String gameId, @NonNull final Context context) {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.openGame(gameId, context);
            }
        });
    }


    public void closeGame() {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.closeGame();
            }
        });
    }
}
