package com.inappstory.sdk.core.ui.screens.inappmessagereader;

import android.content.Context;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.dataholders.models.IReaderContent;
import com.inappstory.sdk.core.inappmessages.InAppMessageFeedCallback;
import com.inappstory.sdk.core.network.content.usecase.InAppMessageByIdUseCase;
import com.inappstory.sdk.core.network.content.usecase.InAppMessagesUseCase;
import com.inappstory.sdk.core.ui.screens.holder.IScreensHolder;
import com.inappstory.sdk.core.ui.screens.launcher.ILaunchScreenCallback;
import com.inappstory.sdk.core.ui.screens.launcher.LaunchScreenStrategy;
import com.inappstory.sdk.core.ui.screens.ScreenType;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenData;
import com.inappstory.sdk.inappmessage.InAppMessageAppearance;
import com.inappstory.sdk.inappmessage.InAppMessageOpenSettings;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;

import java.util.ArrayList;
import java.util.List;

public class LaunchIAMScreenStrategy implements LaunchScreenStrategy {
    private final IASCore core;
    private InAppMessageOpenSettings inAppMessageOpenSettings;
    private InAppMessageAppearance readerAppearanceSettings;
    private List<ILaunchScreenCallback> launchScreenCallbacks = new ArrayList<>();
    private FragmentManager parentContainerFM;
    private int containerId;

    public LaunchIAMScreenStrategy(IASCore core) {
        this.core = core;
    }

    public LaunchIAMScreenStrategy parentContainer(
            FragmentManager parentContainerFM,
            int containerId
    ) {
        this.parentContainerFM = parentContainerFM;
        this.containerId = containerId;
        return this;
    }

    public LaunchIAMScreenStrategy readerAppearanceSettings(
            InAppMessageAppearance readerAppearanceSettings
    ) {
        this.readerAppearanceSettings = readerAppearanceSettings;
        return this;
    }


    @Override
    public void launch(Context context,
                       IOpenReader openReader,
                       IScreensHolder screensHolders
    ) {
        checkIfMessageCanBeOpened(new CheckLocalIAMCallback() {
            @Override
            public void success(int id) {
                launchScreenSuccess();

            }
        });
    }

    private IReaderContent getLocalReaderContent() {
        IReaderContent readerContent = null;
        if (inAppMessageOpenSettings.id() != null) {
            readerContent = core.contentHolder().readerContent().getByIdAndType(
                    inAppMessageOpenSettings.id(),
                    ContentType.IN_APP_MESSAGE
            );
        } else if (inAppMessageOpenSettings.event() != null) {
            readerContent = getContentByEvent();
        }
        return readerContent;
    }

    private void checkIfMessageCanBeOpened(final CheckLocalIAMCallback loadScreen) {
        if (inAppMessageOpenSettings == null) {
            launchScreenError("Need pass opening settings");
            return;
        }
        final IReaderContent readerContent = getLocalReaderContent();
        if (inAppMessageOpenSettings.showOnlyIfLoaded()) {
            if (readerContent != null && core.contentLoader().inAppMessageDownloadManager()
                    .allSlidesLoaded(readerContent)) {
                loadScreen.success(readerContent.id());
            } else {
                launchScreenError("Need to preload InAppMessage first");
                return;
            }
        } else {
            if (readerContent != null) {
                loadScreen.success(readerContent.id());
            } else {
                new InAppMessagesUseCase(core).get(
                        new InAppMessageFeedCallback() {
                            @Override
                            public void success(List<IReaderContent> content) {
                                loadScreen.success(readerContent.id());
                            }

                            @Override
                            public void isEmpty() {
                                launchScreenError("InAppMessage feed is empty");
                            }

                            @Override
                            public void error() {
                                launchScreenError("Can't load InAppMessages");
                            }
                        }
                );
            }
        }

    }

    private void launchScreenSuccess() {
        for (ILaunchScreenCallback launchScreenCallback : launchScreenCallbacks) {
            launchScreenCallback.onSuccess(
                    ScreenType.IN_APP_MESSAGE
            );
        }
    }

    private void launchScreenError(String message) {
        for (ILaunchScreenCallback launchScreenCallback : launchScreenCallbacks) {
            launchScreenCallback.onError(
                    ScreenType.IN_APP_MESSAGE,
                    message
            );
        }
    }

    private IReaderContent getContentByEvent() {
        return null;
    }

    @Override
    public ScreenType getType() {
        return ScreenType.IN_APP_MESSAGE;
    }
}
