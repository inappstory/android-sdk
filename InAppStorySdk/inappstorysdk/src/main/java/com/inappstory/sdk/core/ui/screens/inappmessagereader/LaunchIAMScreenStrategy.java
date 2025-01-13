package com.inappstory.sdk.core.ui.screens.inappmessagereader;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.inappmessages.InAppMessageByIdCallback;
import com.inappstory.sdk.core.inappmessages.InAppMessageDownloadManager;
import com.inappstory.sdk.core.inappmessages.InAppMessageFeedCallback;
import com.inappstory.sdk.core.network.content.usecase.InAppMessageByIdUseCase;
import com.inappstory.sdk.core.network.content.usecase.InAppMessagesUseCase;
import com.inappstory.sdk.core.ui.screens.holder.IScreensHolder;
import com.inappstory.sdk.core.ui.screens.launcher.LaunchScreenStrategy;
import com.inappstory.sdk.core.ui.screens.ScreenType;
import com.inappstory.sdk.inappmessage.IAMUiContainerType;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderState;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageAppearance;
import com.inappstory.sdk.inappmessage.InAppMessageOpenSettings;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenInAppMessageReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.utils.Sizes;

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
        checkIfMessageCanBeOpened(
                new CheckLocalIAMCallback() {
                    @Override
                    public void success(IInAppMessage inAppMessage, boolean contentIsPreloaded) {
                        SourceType sourceType = SourceType.IN_APP_MESSAGES;
                        if (inAppMessageOpenSettings.id() != null)
                            sourceType = SourceType.SINGLE_IN_APP_MESSAGE;
                        launchScreenSuccess(
                                sourceType,
                                inAppMessage,
                                contentIsPreloaded,
                                openReader,
                                screensHolders
                        );
                    }
                },
                Sizes.isTablet(context)
        );
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
                for (IReaderContent content : readerContents) {
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

    private void checkIfMessageCanBeOpened(final CheckLocalIAMCallback loadScreen, boolean isTablet) {
        if (inAppMessageOpenSettings == null) {
            launchScreenError("Need to pass opening settings");
            return;
        }

        final IReaderContent readerContent = getLocalReaderContent();
        final InAppMessageDownloadManager downloadManager = core.contentLoader().inAppMessageDownloadManager();
        boolean contentIsPreloaded = readerContent != null &&
                downloadManager.allSlidesLoaded(readerContent) &&
                downloadManager.allBundlesLoaded();
        if (inAppMessageOpenSettings.showOnlyIfLoaded()) {
            if (contentIsPreloaded) {
                loadScreen.success((IInAppMessage) readerContent, true);
            } else {
                launchScreenError("Need to preload InAppMessages and session bundles first");
            }
        } else {
            if (readerContent != null) {
                loadScreen.success((IInAppMessage) readerContent, contentIsPreloaded);
            } else {
                if (inAppMessageOpenSettings.id() != null) {
                    new InAppMessageByIdUseCase(core, inAppMessageOpenSettings.id()).get(new InAppMessageByIdCallback() {
                        @Override
                        public void success(IReaderContent content) {
                            if (content != null) {
                                boolean contentIsPreloaded =
                                        downloadManager.allSlidesLoaded(content) &&
                                                downloadManager.allBundlesLoaded();
                                loadScreen.success((IInAppMessage) content, contentIsPreloaded);
                            } else {
                                launchScreenError(
                                        "Can't load InAppMessage with settings: [id: "
                                                + inAppMessageOpenSettings.id() +
                                                ", event: " + inAppMessageOpenSettings.event() + "]"
                                );
                            }
                        }

                        @Override
                        public void error() {

                            launchScreenError("Can't load InAppMessage " + inAppMessageOpenSettings.id());
                        }
                    });
                } else {
                    if (core.contentLoader().iamWereLoaded()) {
                        launchScreenError(
                                "Can't load InAppMessage with settings: [id: "
                                        + inAppMessageOpenSettings.id() +
                                        ", event: " + inAppMessageOpenSettings.event() + "]"
                        );
                        return;
                    }
                    new InAppMessagesUseCase(core).get(
                            new InAppMessageFeedCallback() {
                                @Override
                                public void success(List<IReaderContent> content) {
                                    IReaderContent readerContent = getLocalReaderContent();
                                    boolean contentIsPreloaded =
                                            downloadManager.allSlidesLoaded(readerContent) &&
                                                    downloadManager.allBundlesLoaded();
                                    if (readerContent != null)
                                        loadScreen.success((IInAppMessage) readerContent, contentIsPreloaded);
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

    }

    private void launchScreenSuccess(
            SourceType sourceType,
            IInAppMessage inAppMessage,
            boolean contentIsPreloaded,
            IOpenReader openReader,
            IScreensHolder screensHolders
    ) {
        boolean cantBeOpened = screensHolders.hasActiveScreen();
        if (cantBeOpened) {
            String message = "InAppMessage reader can't be opened. Please, close another opened reader first.";
            launchScreenError(message);
            return;
        }
        cantBeOpened = core.sessionManager().getSession().getSessionId().isEmpty();
        if (cantBeOpened) {
            String message = "Session is not opened.";
            launchScreenError(message);
            return;
        }
        if (!(openReader instanceof IOpenInAppMessageReader)) return;
        InAppMessageAppearance appearance = inAppMessage.inAppMessageAppearance();
        /*switch (inAppMessage.screenType()) {
            case POPUP:
                appearance = new InAppMessagePopupSettings();
                break;
            case FULLSCREEN:
                appearance = new InAppMessageFullscreenSettings();
                break;
            default:
                appearance = new InAppMessageBottomSheetSettings();
                break;
        }*/
        inAppMessageScreenActions.readerIsOpened();
        core.screensManager().iamReaderViewModel().initState(
                new IAMReaderState()
                        .sourceType(sourceType)
                        .iamId(inAppMessage.id())
                        .contentIsPreloaded(contentIsPreloaded)
                        .showOnlyIfLoaded(inAppMessageOpenSettings.showOnlyIfLoaded())
                        .appearance(appearance)
        );
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

    private boolean deviceIsSupported(IInAppMessage message, boolean isTablet) {
        if (message.screenType().equals(IAMUiContainerType.POPUP)) return true;
        return !isTablet;
    }

    private IReaderContent getContentByEvent() {
        List<IReaderContent> readerContents =
                core.contentHolder().readerContent().getByType(
                        ContentType.IN_APP_MESSAGE
                );
        if (readerContents != null) {
            for (IReaderContent content : readerContents) {
                IInAppMessage inAppMessage = (IInAppMessage) content;
                if (inAppMessage.belongsToEvent(inAppMessageOpenSettings.event()) &&
                        checkContentForShownFrequency(inAppMessage)) {
                    return content;
                }
            }
        }
        return null;
    }

    @Override
    public ScreenType getType() {
        return ScreenType.IN_APP_MESSAGE;
    }
}
