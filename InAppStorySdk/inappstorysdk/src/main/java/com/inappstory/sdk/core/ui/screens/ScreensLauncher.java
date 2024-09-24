package com.inappstory.sdk.core.ui.screens;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.stories.outercallbacks.common.objects.DefaultOpenGameReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.DefaultOpenInAppMessageReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.DefaultOpenStoriesReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenGameReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenInAppMessageReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenStoriesReader;

import java.util.ArrayList;
import java.util.List;

public class ScreensLauncher implements IScreensLauncher {
    private final ScreensHolder holder;
    public ScreensLauncher(ScreensHolder holder) {
        this.holder = holder;
    }

    private IOpenGameReader openGameReader = new DefaultOpenGameReader();

    public void setOpenGameReader(@NonNull IOpenGameReader openGameReader) {
        this.openGameReader = openGameReader;
    }

    private IOpenStoriesReader openStoriesReader = new DefaultOpenStoriesReader();

    public void setOpenStoriesReader(@NonNull IOpenStoriesReader openStoriesReader) {
        this.openStoriesReader = openStoriesReader;
    }

    private IOpenInAppMessageReader openInAppMessageReader = new DefaultOpenInAppMessageReader();

    public void setOpenInAppMessageReader(@NonNull IOpenInAppMessageReader openInAppMessageReader) {
        this.openInAppMessageReader = openInAppMessageReader;
    }

    @Override
    public void openScreen(Context context, LaunchScreenStrategy strategy) {
        holder.closeUGCEditor();
        switch (strategy.getType()) {
            case GAME:
                strategy.launch(context, openGameReader, holder);
                break;
            case IN_APP_MESSAGE:
                strategy.launch(context, openInAppMessageReader, holder);
                break;
            default:
                strategy.launch(context, openStoriesReader, holder);
                break;
        }
    }

    @Override
    @NonNull
    public IOpenReader getOpenReader(ScreenType screenType) {
        switch (screenType) {
            case STORY:
                return openStoriesReader;
            case GAME:
                return openGameReader;
            case IN_APP_MESSAGE:
                return openInAppMessageReader;
            default:
                throw new RuntimeException("Wrong screen type");
        }
    }
}
