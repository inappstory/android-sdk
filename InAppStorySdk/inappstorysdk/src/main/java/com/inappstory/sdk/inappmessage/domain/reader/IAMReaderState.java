package com.inappstory.sdk.inappmessage.domain.reader;

import android.util.Pair;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.impl.InAppMessageBottomSheetSettings;
import com.inappstory.sdk.inappmessage.InAppMessageData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;

public class IAMReaderState {
    public IAMReaderUIStates uiState = IAMReaderUIStates.CLOSED;
    public IAMReaderLoadStates loadState = IAMReaderLoadStates.EMPTY;
    public IAMReaderLoaderStates loaderState = IAMReaderLoaderStates.EMPTY;
    public boolean canBeClosed = false;
    public InAppMessageAppearance appearance = new InAppMessageBottomSheetSettings();
    public boolean showOnlyIfLoaded = false;
    public boolean contentIsPreloaded = false;
    public Integer iamId = null;
    public String event = null;
    public String cancellationTokenUID = null;
    public Pair<Integer, Integer> safeArea = new Pair<>(0, 0);
    public SourceType sourceType = SourceType.EVENT_IN_APP_MESSAGE;
    public InAppMessageData inAppMessageData = null;


    public IAMReaderState inAppMessageData(InAppMessageData inAppMessageData) {
        this.inAppMessageData = inAppMessageData;
        return this;
    }

    public IAMReaderState safeArea(Pair<Integer, Integer> safeArea) {
        this.safeArea = safeArea;
        return this;
    }

    public IAMReaderState cancellationTokenUID(String cancellationTokenUID) {
        this.cancellationTokenUID = cancellationTokenUID;
        return this;
    }

    public IAMReaderState iamId(Integer iamId) {
        this.iamId = iamId;
        return this;
    }

    public IAMReaderState event(String event) {
        this.event = event;
        return this;
    }

    public IAMReaderState canBeClosed(boolean canBeClosed) {
        this.canBeClosed = canBeClosed;
        return this;
    }

    public IAMReaderState sourceType(SourceType sourceType) {
        this.sourceType = sourceType;
        return this;
    }


    public IAMReaderState uiState(IAMReaderUIStates uiState) {
        this.uiState = uiState;
        return this;
    }

    public IAMReaderState loaderState(IAMReaderLoaderStates loaderState) {
        this.loaderState = loaderState;
        return this;
    }

    public IAMReaderState loadState(IAMReaderLoadStates loadState) {
        this.loadState = loadState;
        return this;
    }

    public IAMReaderState appearance(InAppMessageAppearance appearance) {
        this.appearance = appearance;
        return this;
    }

    public IAMReaderState contentIsPreloaded(boolean contentIsPreloaded) {
        this.contentIsPreloaded = contentIsPreloaded;
        return this;
    }

    public IAMReaderState showOnlyIfLoaded(boolean showOnlyIfLoaded) {
        this.showOnlyIfLoaded = showOnlyIfLoaded;
        return this;
    }

    public IAMReaderState copy() {
        return new IAMReaderState()
                .uiState(this.uiState)
                .sourceType(this.sourceType)
                .cancellationTokenUID(this.cancellationTokenUID)
                .loadState(this.loadState)
                .loaderState(this.loaderState)
                .iamId(this.iamId)
                .canBeClosed(this.canBeClosed)
                .event(this.event)
                .safeArea(this.safeArea)
                .inAppMessageData(this.inAppMessageData)
                .appearance(this.appearance)
                .showOnlyIfLoaded(this.showOnlyIfLoaded)
                .contentIsPreloaded(this.contentIsPreloaded);
    }
}
