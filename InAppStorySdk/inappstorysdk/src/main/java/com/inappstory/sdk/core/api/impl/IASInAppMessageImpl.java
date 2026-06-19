package com.inappstory.sdk.core.api.impl;


import static com.inappstory.sdk.core.api.impl.IASSettingsImpl.TAG_LIMIT;

import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.CancellationTokenWithStatus;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASInAppMessage;
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
import com.inappstory.sdk.core.ui.screens.inappmessagereader.GetLocalInAppMessage;
import com.inappstory.sdk.core.ui.screens.inappmessagereader.LaunchIAMScreenStrategy;
import com.inappstory.sdk.inappmessage.InAppMessageContainerProvider;
import com.inappstory.sdk.inappmessage.InAppMessageContainerSettings;
import com.inappstory.sdk.inappmessage.InAppMessageData;
import com.inappstory.sdk.inappmessage.InAppMessageFilterSettings;
import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.inappmessage.InAppMessageOpenSettings;
import com.inappstory.sdk.inappmessage.InAppMessagePreloadSettings;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;
import com.inappstory.sdk.inappmessage.InAppMessageViewController;
import com.inappstory.sdk.inappmessage.domain.reader.IAMViewController;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageUndefinedAppearance;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.utils.TagsUtils;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class IASInAppMessageImpl implements IASInAppMessage {

    private final IASCore core;

    public IASInAppMessageImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void preload(InAppMessagePreloadSettings preloadSettings, InAppMessageLoadCallback callback) {
        IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        if (settingsHolder.anonymous()) {
            InAppStoryManager.showELog(
                    LoggerTags.IAS_ERROR_TAG,
                    "In-app messages are unavailable for anonymous mode"
            );
            callback.loadError();
            return;
        }
        core.contentLoader().inAppMessageDownloadManager().clearLocalData();
        core.contentPreload().downloadInAppMessages(preloadSettings, callback);
    }

    @Override
    public boolean hasLoadedInAppMessage(InAppMessageFilterSettings filterSettings) {
        final InAppMessageDownloadManager downloadManager =
                core.contentLoader().inAppMessageDownloadManager();
        final InAppMessageFilterSettings localSettings = filterSettings;
        final List<String> localTags = new ArrayList<>();
        String error = null;
        if (localSettings == null) {
            error = "Need to pass opening settings (id or event)";
        } else
        if (localSettings.tags() != null) {
            List<String> filteredList = new ArrayList<>();
            List<String> copyTags = new ArrayList<>(localSettings.tags());
            for (String tag : copyTags) {
                if (!TagsUtils.checkTagPattern(tag)) {
                    InAppStoryManager.showELog(
                            LoggerTags.IAS_WARN_TAG,
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
                error = StringsUtils.getErrorStringFromContext(
                        core.appContext(),
                        R.string.ias_setter_tags_length_error
                );
            }
            localTags.addAll(filteredList);
        } else {
            localTags.addAll(((IASDataSettingsHolder) core.settingsAPI()).tags());
        }
        if (localSettings.id() == null &&
                (localSettings.event() == null ||
                        localSettings.event().isEmpty())
        ) {
            InAppStoryManager.showELog(
                    LoggerTags.IAS_ERROR_TAG,
                    "Need to pass opening settings (id or event)"
            );
            return false;
        }
        final Set<Integer> preloadedIndexes = new HashSet<>();
        preloadedIndexes.add(0);
        IInAppMessage inAppMessage = null;
        if (localSettings.id() != null) {
            inAppMessage = (IInAppMessage) core.contentHolder().readerContent().getByIdAndType(
                    localSettings.id(),
                    ContentType.IN_APP_MESSAGE
            );
        } else if (localSettings.event() != null) {
            String event = localSettings.event();
            if (!core.contentLoader().getIamWereLoadedStatus(TagsUtils.tagsHash(localTags))) {
                return false;
            } else {
                final List<IReaderContent> readerContents =
                        core.contentHolder().readerContent().getByType(
                                ContentType.IN_APP_MESSAGE
                        );
                IInAppMessage resContent = null;
                int currentPriority = 0;
                if (readerContents != null && !readerContents.isEmpty()) {
                    List<Integer> contentIds = new ArrayList<>();
                    for (IReaderContent content : readerContents) {
                        inAppMessage = (IInAppMessage) content;
                        if (inAppMessage.inAppMessageAppearance() instanceof InAppMessageUndefinedAppearance)
                            continue;
                        int messagePriority = inAppMessage.getEventPriority(event);
                        if (messagePriority >= 0 && checkContentForShownFrequency(inAppMessage)) {
                            contentIds.add(content.id());
                            if (messagePriority > currentPriority) {
                                currentPriority = messagePriority;
                                resContent = inAppMessage;
                            } else if (messagePriority == currentPriority && resContent == null) {
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
                                                if (messagePriority > currentPriority) {
                                                    currentPriority = messagePriority;
                                                    resContent = inAppMessage;
                                                } else if (messagePriority == currentPriority && resContent == null) {
                                                    resContent = inAppMessage;
                                                }
                                            }
                                        }
                                        if (resContent != null)
                                            getLocalInAppMessage.get(resContent);
                                        else
                                            getLocalInAppMessage.error("No InAppMessage matching the display limits was found.");
                                    }

                                    @Override
                                    public void error() {
                                        getLocalInAppMessage.error("Can't load limits for InAppMessages");
                                    }
                                }
                        );
                    } else {
                        if (resContent != null)
                            getLocalInAppMessage.get(resContent);
                        else
                            getLocalInAppMessage.error("No InAppMessage was found that satisfies the conditions.");
                    }
                } else {
                    getLocalInAppMessage.error("No InAppMessage was found that satisfies the conditions.");
                }
            }
        }
        if (inAppMessage == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void show(
            CancellationTokenWithStatus cancellationToken,
            InAppMessageOpenSettings openData,
            FragmentManager fragmentManager,
            int containerId,
            final InAppMessageScreenActions screenActions
    ) {
        IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        if (settingsHolder.anonymous()) {
            InAppStoryManager.showELog(
                    LoggerTags.IAS_ERROR_TAG,
                    "In-app messages are unavailable for anonymous mode"
            );
            if (screenActions != null)
                screenActions.readerOpenError("In-app messages are unavailable for anonymous mode");
            return;
        }
        core.screensManager().openScreen(
                null,
                new LaunchIAMScreenStrategy(core)
                        .cancellationToken(cancellationToken)
                        .containerProvider(new InAppMessageContainerProvider() {
                            @Override
                            public InAppMessageContainerSettings provideContainer(InAppMessageData messageData) {
                                return new InAppMessageContainerSettings().fragment(fragmentManager, containerId);
                            }

                            @Override
                            public IAMViewController layoutController() {
                                return null;
                            }
                        })
                        .inAppMessageOpenSettings(openData)
                        .inAppMessageScreenActions(screenActions)
        );
    }

    @Override
    public void show(
            CancellationTokenWithStatus cancellationToken,
            InAppMessageOpenSettings openData,
            InAppMessageContainerProvider containerProvider,
            InAppMessageScreenActions screenActions
    ) {
        IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        if (settingsHolder.anonymous()) {
            InAppStoryManager.showELog(
                    LoggerTags.IAS_ERROR_TAG,
                    "In-app messages are unavailable for anonymous mode"
            );
            if (screenActions != null)
                screenActions.readerOpenError("In-app messages are unavailable for anonymous mode");
            return;
        }
        core.screensManager().openScreen(
                null,
                new LaunchIAMScreenStrategy(core)
                        .cancellationToken(cancellationToken)
                        .containerProvider(containerProvider)
                        .inAppMessageOpenSettings(openData)
                        .inAppMessageScreenActions(screenActions)
        );
    }

    @Override
    public void show(
            CancellationTokenWithStatus cancellationToken,
            InAppMessageOpenSettings openData,
            FrameLayout frameLayout,
            InAppMessageScreenActions screenActions,
            InAppMessageViewController controller
    ) {
        IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        if (settingsHolder.anonymous()) {
            InAppStoryManager.showELog(
                    LoggerTags.IAS_ERROR_TAG,
                    "In-app messages are unavailable for anonymous mode"
            );
            if (screenActions != null)
                screenActions.readerOpenError("In-app messages are unavailable for anonymous mode");
            return;
        }
        core.screensManager().openScreen(
                null,
                new LaunchIAMScreenStrategy(core)
                        .cancellationToken(cancellationToken)
                        .layout(frameLayout)
                        .containerProvider(new InAppMessageContainerProvider() {
                            @Override
                            public InAppMessageContainerSettings provideContainer(InAppMessageData messageData) {
                                return new InAppMessageContainerSettings().layout(frameLayout);
                            }

                            @Override
                            public IAMViewController layoutController() {
                                return controller;
                            }
                        })
                        .inAppMessageOpenSettings(openData)
                        .inAppMessageScreenActions(screenActions)
        );
    }


    @Override
    public void callback(InAppMessageLoadCallback callback) {
        core.callbacksAPI().setCallback(
                IASCallbackType.IN_APP_MESSAGE_LOAD,
                callback
        );
    }
}
