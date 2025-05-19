package com.inappstory.sdk.core.ui.screens.inappmessagereader;

import static com.inappstory.sdk.core.api.impl.IASSettingsImpl.TAG_LIMIT;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.core.data.IInAppMessageLimit;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.inappmessages.InAppMessageByIdCallback;
import com.inappstory.sdk.core.inappmessages.InAppMessageDownloadManager;
import com.inappstory.sdk.core.inappmessages.InAppMessageFeedCallback;
import com.inappstory.sdk.core.inappmessages.InAppMessagesLimitCallback;
import com.inappstory.sdk.core.network.content.usecase.InAppMessageByIdUseCase;
import com.inappstory.sdk.core.network.content.usecase.InAppMessageLimitsUseCase;
import com.inappstory.sdk.core.network.content.usecase.InAppMessagesUseCase;
import com.inappstory.sdk.core.ui.screens.holder.IScreensHolder;
import com.inappstory.sdk.core.ui.screens.launcher.LaunchScreenStrategy;
import com.inappstory.sdk.core.ui.screens.ScreenType;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderState;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageAppearance;
import com.inappstory.sdk.inappmessage.InAppMessageOpenSettings;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenInAppMessageReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.utils.TagsUtils;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
                        SourceType sourceType = SourceType.EVENT_IN_APP_MESSAGE;
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
                }
        );
    }

    private void getLocalReaderContent(GetLocalInAppMessage getLocalInAppMessage, List<String> tagsToCheck) {
        if (inAppMessageOpenSettings.id() != null) {
            IInAppMessage inAppMessage = (IInAppMessage) core.contentHolder().readerContent().getByIdAndType(
                    inAppMessageOpenSettings.id(),
                    ContentType.IN_APP_MESSAGE
            );
            if (inAppMessage != null) {
                getLocalInAppMessage.get(inAppMessage);
            } else {
                getLocalInAppMessage.error();
            }
        } else if (inAppMessageOpenSettings.event() != null) {
            if (tagsToCheck != null && !core.contentLoader().getIamWereLoadedStatus(TagsUtils.tagsHash(tagsToCheck))) {
                getLocalInAppMessage.error();
            } else {
                getContentByEvent(
                        getLocalInAppMessage,
                        inAppMessageOpenSettings.event()
                );
            }
        } else {
            getLocalInAppMessage.error();
        }
    }

    private boolean checkContentForShownFrequency(IInAppMessage inAppMessage) {
        if (inAppMessage.displayFrom() > 0 && System.currentTimeMillis() < inAppMessage.displayFrom())
            return false;
        if (inAppMessage.displayTo() > 0 && System.currentTimeMillis() > inAppMessage.displayTo())
            return false;
        IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        String localOpensKey = "iam_opened";
        if (settingsHolder.userId() != null) {
            localOpensKey += settingsHolder.userId();
        }
        Set<String> opens = core.sharedPreferencesAPI().getStringSet(localOpensKey);
        Integer openedId = null;
        Long lastTime = null;
        if (opens != null) {
            for (String open : opens) {
                IAMShownTime shownTime = new IAMShownTime(open);
                if (shownTime.iamId == inAppMessage.id()) {
                    openedId = shownTime.iamId;
                    lastTime = shownTime.latestShownTime;
                }
            }
        }
        if (openedId == null) return true;
        long frequencyLimit = inAppMessage.frequencyLimit();
        if (frequencyLimit == -1) return false;
        if (frequencyLimit > 0)
            return (System.currentTimeMillis() - lastTime) >= frequencyLimit;
        return true;
    }

    private void checkIfMessageCanBeOpened(final CheckLocalIAMCallback loadScreen) {
        final InAppMessageDownloadManager downloadManager = core.contentLoader().inAppMessageDownloadManager();
        final InAppMessageOpenSettings localSettings = inAppMessageOpenSettings;
        final List<String> localTags = new ArrayList<>();

        if (localSettings == null) {
            launchScreenError("Need to pass opening settings (id or event)");
            return;
        }
        if (inAppMessageOpenSettings.tags() != null) {
            List<String> filteredList = new ArrayList<>();
            List<String> copyTags = new ArrayList<>(inAppMessageOpenSettings.tags());
            for (String tag : copyTags) {
                if (!TagsUtils.checkTagPattern(tag)) {
                    InAppStoryManager.showELog(
                            InAppStoryManager.IAS_WARN_TAG,
                            StringsUtils.getFormattedErrorStringFromContext(
                                    core.appContext(),
                                    R.string.ias_tag_pattern_error,
                                    tag
                            )
                    );
                    continue;
                }
                filteredList.add(tag);
            }
            if (StringsUtils.getBytesLength(TextUtils.join(",", filteredList)) > TAG_LIMIT) {
                InAppStoryManager.showELog(
                        InAppStoryManager.IAS_ERROR_TAG,
                        StringsUtils.getErrorStringFromContext(
                                core.appContext(),
                                R.string.ias_setter_tags_length_error
                        )
                );
                launchScreenError(StringsUtils.getErrorStringFromContext(
                        core.appContext(),
                        R.string.ias_setter_tags_length_error
                ));
                return;
            }
            localTags.addAll(filteredList);
        } else {
            localTags.addAll(((IASDataSettingsHolder) core.settingsAPI()).tags());
        }
        if (localSettings.id() == null &&
                (localSettings.event() == null ||
                        localSettings.event().isEmpty())
        ) {
            launchScreenError("Need to pass opening settings (id or event)");
            return;
        }
        getLocalReaderContent(
                new GetLocalInAppMessage() {
                    @Override
                    public void get(@NonNull IInAppMessage readerContent) {
                        boolean contentIsPreloaded =
                                downloadManager.allSlidesLoaded(readerContent) &&
                                        downloadManager.allBundlesLoaded();
                        if (localSettings.showOnlyIfLoaded()) {
                            if (contentIsPreloaded) {
                                loadScreen.success(readerContent, true);
                            } else {
                                launchScreenError("Need to preload InAppMessages and session bundles first");
                            }
                        } else {
                            loadScreen.success(readerContent, contentIsPreloaded);
                        }
                    }

                    @Override
                    public void error() {
                        if (localSettings.showOnlyIfLoaded()) {
                            launchScreenError("Need to preload InAppMessages and session bundles first");
                        } else {
                            if (localSettings.id() != null) {
                                new InAppMessageByIdUseCase(
                                        core,
                                        localSettings.id()
                                ).get(
                                        new InAppMessageByIdCallback() {
                                            @Override
                                            public void success(IReaderContent content) {
                                                if (content != null) {
                                                    boolean contentIsPreloaded =
                                                            downloadManager.allSlidesLoaded(content) &&
                                                                    downloadManager.allBundlesLoaded();
                                                    loadScreen.success(
                                                            (IInAppMessage) content,
                                                            contentIsPreloaded
                                                    );
                                                } else {
                                                    launchScreenError(
                                                            "Can't load InAppMessage with settings: [id: "
                                                                    + localSettings.id() +
                                                                    ", event: " + localSettings.event() + "]"
                                                    );
                                                }
                                            }

                                            @Override
                                            public void error() {
                                                launchScreenError("Can't load InAppMessage " + localSettings.id());
                                            }
                                        });
                            } else {
                                if (core.contentLoader().getIamWereLoadedStatus(TagsUtils.tagsHash(localTags))) {
                                    launchScreenError(
                                            "Can't load InAppMessage with settings: [id: "
                                                    + localSettings.id() +
                                                    ", event: " + localSettings.event() + "]"
                                    );
                                    return;
                                }
                                new InAppMessagesUseCase(core, null, localTags).get(
                                        new InAppMessageFeedCallback() {
                                            @Override
                                            public void success(List<IReaderContent> content) {
                                                getLocalReaderContent(
                                                        new GetLocalInAppMessage() {
                                                            @Override
                                                            public void get(@NonNull IInAppMessage readerContent) {
                                                                boolean contentIsPreloaded =
                                                                        downloadManager.allSlidesLoaded(readerContent) &&
                                                                                downloadManager.allBundlesLoaded();
                                                                loadScreen.success(readerContent,
                                                                        contentIsPreloaded
                                                                );
                                                            }

                                                            @Override
                                                            public void error() {
                                                                launchScreenError(
                                                                        "Can't load InAppMessage with settings: [id: "
                                                                                + localSettings.id() +
                                                                                ", event: " + localSettings.event() + "]"
                                                                );
                                                            }
                                                        },
                                                        null
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
                },
                localTags
        );
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
        inAppMessageScreenActions.readerIsOpened();
        core.screensManager().iamReaderViewModel(inAppMessage.id()).initState(
                new IAMReaderState()
                        .sourceType(sourceType)
                        .iamId(inAppMessage.id())
                        .event(inAppMessageOpenSettings.event())
                        .contentIsPreloaded(contentIsPreloaded)
                        .showOnlyIfLoaded(inAppMessageOpenSettings.showOnlyIfLoaded())
                        .appearance(appearance)
        );
        saveIAMOpened(inAppMessage.id());
        ((IOpenInAppMessageReader) openReader).onOpen(
                inAppMessage,
                inAppMessageOpenSettings.showOnlyIfLoaded(),
                parentContainerFM,
                containerId,
                inAppMessageScreenActions
        );
    }

    private void saveIAMOpened(int iamId) {
        IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        String localOpensKey = "iam_opened";
        if (settingsHolder.userId() != null) {
            localOpensKey += settingsHolder.userId();
        }
        Set<String> opens = core.sharedPreferencesAPI().getStringSet(localOpensKey);
        IAMShownTime savedShownTime = null;
        if (opens != null) {
            for (Iterator<String> iterator = opens.iterator(); iterator.hasNext(); ) {
                IAMShownTime shownTime = new IAMShownTime(iterator.next());
                if (shownTime.iamId == iamId) {
                    shownTime.updateLatestShownTime();
                    savedShownTime = shownTime;
                    iterator.remove();
                    break;
                }
            }
        } else {
            opens = new HashSet<>();
        }
        if (savedShownTime == null) {
            savedShownTime = new IAMShownTime(iamId);
        }
        opens.add(savedShownTime.getSaveKey());
        core.sharedPreferencesAPI().saveStringSet(localOpensKey, opens);
    }

    private void launchScreenError(String message) {
        inAppMessageScreenActions.readerOpenError(message);
    }

    private void getContentByEvent(
            final GetLocalInAppMessage getLocalInAppMessage,
            final String event
    ) {
        final List<IReaderContent> readerContents =
                core.contentHolder().readerContent().getByType(
                        ContentType.IN_APP_MESSAGE
                );
        IInAppMessage resContent = null;
        int currentPriority = 0;
        if (readerContents != null && !readerContents.isEmpty()) {
            List<Integer> contentIds = new ArrayList<>();
            for (IReaderContent content : readerContents) {
                IInAppMessage inAppMessage = (IInAppMessage) content;
                int messagePriority = inAppMessage.getEventPriority(event);
                if (messagePriority >= 0 && checkContentForShownFrequency(inAppMessage)) {
                    contentIds.add(content.id());
                    if (messagePriority >= currentPriority) {
                        currentPriority = messagePriority;
                        resContent = inAppMessage;
                    }
                }
            }
            if (resContent != null && resContent.hasLimit()) {
                InAppMessageLimitsUseCase inAppMessageLimitsUseCase =
                        new InAppMessageLimitsUseCase(core, contentIds);
                inAppMessageLimitsUseCase.loadLimits(
                        new InAppMessagesLimitCallback() {
                            @Override
                            public void success(List<IInAppMessageLimit> limits) {
                                int currentPriority = 0;
                                IInAppMessage resContent = null;
                                for (IReaderContent content : readerContents) {
                                    IInAppMessage inAppMessage = (IInAppMessage) content;
                                    boolean canOpen = false;
                                    for (IInAppMessageLimit limit : limits) {
                                        if (limit.messageId() == inAppMessage.id()) {
                                            if (limit.canOpen()) canOpen = true;
                                        }
                                    }
                                    if (!canOpen)
                                        continue;
                                    int messagePriority = inAppMessage.getEventPriority(event);
                                    if (messagePriority >= 0 && checkContentForShownFrequency(inAppMessage)) {
                                        if (messagePriority >= currentPriority) {
                                            currentPriority = messagePriority;
                                            resContent = inAppMessage;
                                        }
                                    }
                                }
                                if (resContent != null)
                                    getLocalInAppMessage.get(resContent);
                                else
                                    getLocalInAppMessage.error();
                            }

                            @Override
                            public void error() {
                                getLocalInAppMessage.error();
                            }
                        }
                );
            } else {
                if (resContent != null)
                    getLocalInAppMessage.get(resContent);
                else
                    getLocalInAppMessage.error();
            }
        } else {
            getLocalInAppMessage.error();
        }
    }

    @Override
    public ScreenType getType() {
        return ScreenType.IN_APP_MESSAGE;
    }
}
