package com.inappstory.sdk.core.ui.screens.inappmessagereader;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.exceptions.NotImplementedMethodException;
import com.inappstory.sdk.core.inappmessages.InAppMessageFeedCallback;
import com.inappstory.sdk.core.network.content.usecase.InAppMessagesUseCase;
import com.inappstory.sdk.core.ui.screens.holder.IScreensHolder;
import com.inappstory.sdk.core.ui.screens.launcher.ILaunchScreenCallback;
import com.inappstory.sdk.core.ui.screens.launcher.LaunchScreenStrategy;
import com.inappstory.sdk.core.ui.screens.ScreenType;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageAppearance;
import com.inappstory.sdk.inappmessage.InAppMessageOpenSettings;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenInAppMessageReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenStoriesReader;

import java.util.ArrayList;
import java.util.List;

public class LaunchIAMScreenStrategy implements LaunchScreenStrategy {
    private final IASCore core;

    private InAppMessageOpenSettings inAppMessageOpenSettings;

    public LaunchIAMScreenStrategy inAppMessageScreenActions(
            InAppMessageScreenActions inAppMessageScreenActions
    ) {
        this.inAppMessageScreenActions = inAppMessageScreenActions;
        return this;
    }

    private InAppMessageScreenActions inAppMessageScreenActions;
    private FragmentManager parentContainerFM;
    private int containerId;

    public LaunchIAMScreenStrategy(IASCore core) {
        this.core = core;
    }

    public LaunchIAMScreenStrategy inAppMessageOpenSettings(InAppMessageOpenSettings inAppMessageOpenSettings) {
        this.inAppMessageOpenSettings = inAppMessageOpenSettings;
        return this;
    }

    public LaunchIAMScreenStrategy parentContainer(
            FragmentManager parentContainerFM,
            int containerId
    ) {
        this.parentContainerFM = parentContainerFM;
        this.containerId = containerId;
        return this;
    }


    @Override
    public void launch(Context context,
                       final IOpenReader openReader,
                       final IScreensHolder screensHolders
    ) {
        checkIfMessageCanBeOpened(new CheckLocalIAMCallback() {
            @Override
            public void success(IInAppMessage inAppMessage) {
                launchScreenSuccess(
                        inAppMessage,
                        openReader,
                        screensHolders
                );
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
        } else {
            List<IReaderContent> readerContents =
                    core.contentHolder().readerContent().getByType(
                            ContentType.IN_APP_MESSAGE
                    );
            if (readerContents != null) {
                for (IReaderContent content: readerContents) {
                    if (checkContentForShownFrequency((IInAppMessage) content)) {
                        return content;
                    }
                }
            }
        }
        return readerContent;
    }
    
    private boolean checkContentForShownFrequency(IInAppMessage inAppMessage) {
        return true;
    }

    private void checkIfMessageCanBeOpened(final CheckLocalIAMCallback loadScreen) {
        if (inAppMessageOpenSettings == null) {
            launchScreenError("Need to pass opening settings");
            return;
        }
        final IReaderContent readerContent = getLocalReaderContent();
        if (inAppMessageOpenSettings.showOnlyIfLoaded()) {
            if (readerContent != null && core.contentLoader().inAppMessageDownloadManager()
                    .allSlidesLoaded(readerContent)) {
                loadScreen.success((IInAppMessage) readerContent);
            } else {
                launchScreenError("Need to preload InAppMessages first");
            }
        } else {
            if (readerContent != null) {
                loadScreen.success((IInAppMessage) readerContent);
            } else {
                new InAppMessagesUseCase(core).get(
                        new InAppMessageFeedCallback() {
                            @Override
                            public void success(List<IReaderContent> content) {
                                IReaderContent readerContent = getLocalReaderContent();
                                if (readerContent != null)
                                    loadScreen.success((IInAppMessage) readerContent);
                                else
                                    launchScreenError(
                                            "Can't load InAppMessage with settings: [id: "
                                            + inAppMessageOpenSettings.id() +
                                            ", event: " + inAppMessageOpenSettings.event() + "]"
                                    );
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

    private void launchScreenSuccess(
            IInAppMessage inAppMessage,
            IOpenReader openReader,
            IScreensHolder screensHolders
    ) {
        //check if another readers is opened

        inAppMessageScreenActions.readerIsOpened();
        ((IOpenInAppMessageReader) openReader).onOpen(
                inAppMessage,
                inAppMessageOpenSettings.showOnlyIfLoaded(),
                parentContainerFM,
                containerId,
                inAppMessageScreenActions
        );
    }

    private void launchScreenError(String message) {
        inAppMessageScreenActions.readerOpenError(message);
    }

    private IReaderContent getContentByEvent() {
        throw new NotImplementedMethodException();
    }

    @Override
    public ScreenType getType() {
        return ScreenType.IN_APP_MESSAGE;
    }
}
