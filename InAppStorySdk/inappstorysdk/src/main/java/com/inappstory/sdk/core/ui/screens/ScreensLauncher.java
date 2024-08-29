package com.inappstory.sdk.core.ui.screens;

import android.content.Context;

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
    public void setOpenGameReader(IOpenGameReader openGameReader) {
        this.openGameReader = openGameReader;
    }

    private IOpenStoriesReader openStoriesReader = new DefaultOpenStoriesReader();
    public void setOpenStoriesReader(IOpenStoriesReader openStoriesReader) {
        this.openStoriesReader = openStoriesReader;
    }

    private IOpenInAppMessageReader openInAppMessageReader = new DefaultOpenInAppMessageReader();
    public void setOpenInAppMessageReader(IOpenInAppMessageReader openInAppMessageReader) {
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
}
