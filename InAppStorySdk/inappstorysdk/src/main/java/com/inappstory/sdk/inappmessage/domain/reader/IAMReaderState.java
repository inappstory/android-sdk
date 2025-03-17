package com.inappstory.sdk.inappmessage.domain.reader;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.impl.InAppMessageBottomSheetSettings;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;

public class IAMReaderState {
    public IAMReaderUIState uiState = IAMReaderUIState.CLOSED;
    public IAMReaderLoadState loadState = IAMReaderLoadState.IDLE;
    public InAppMessageAppearance appearance = new InAppMessageBottomSheetSettings();
    public boolean showOnlyIfLoaded = false;
    public boolean contentIsPreloaded = false;
    public Integer iamId = null;
    public String event = null;
    public SourceType sourceType = SourceType.EVENT_IN_APP_MESSAGE;

    public IAMReaderState iamId(Integer iamId) {
        this.iamId = iamId;
        return this;
    }

    public IAMReaderState event(String event) {
        this.event = event;
        return this;
    }

    public IAMReaderState sourceType(SourceType sourceType) {
        this.sourceType = sourceType;
        return this;
    }


    public IAMReaderState uiState(IAMReaderUIState uiState) {
        this.uiState = uiState;
        return this;
    }

    public IAMReaderState loadState(IAMReaderLoadState loadState) {
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
                .loadState(this.loadState)
                .iamId(this.iamId)
                .event(this.event)
                .appearance(this.appearance)
                .showOnlyIfLoaded(this.showOnlyIfLoaded)
                .contentIsPreloaded(this.contentIsPreloaded);
    }
}
