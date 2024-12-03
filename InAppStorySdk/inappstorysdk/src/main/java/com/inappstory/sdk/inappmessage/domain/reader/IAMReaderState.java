package com.inappstory.sdk.inappmessage.domain.reader;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.impl.InAppMessageBottomSheetSettings;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;

public class IAMReaderState {
    public IAMReaderUIStates uiState = IAMReaderUIStates.CLOSED;
    public IAMReaderLoadStates loadState = IAMReaderLoadStates.EMPTY;
    public InAppMessageAppearance appearance = new InAppMessageBottomSheetSettings();
    public boolean showOnlyIfLoaded = false;
    public boolean contentIsPreloaded = false;
    public Integer iamId = null;
    public SourceType sourceType = SourceType.IN_APP_MESSAGES;

    public IAMReaderState iamId(Integer iamId) {
        this.iamId = iamId;
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
                .loadState(this.loadState)
                .iamId(this.iamId)
                .appearance(this.appearance)
                .showOnlyIfLoaded(this.showOnlyIfLoaded)
                .contentIsPreloaded(this.contentIsPreloaded);
    }
}
