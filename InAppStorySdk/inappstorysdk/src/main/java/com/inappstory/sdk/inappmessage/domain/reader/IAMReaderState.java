package com.inappstory.sdk.inappmessage.domain.reader;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.impl.InAppMessageBottomSheetSettings;

public class IAMReaderState {
    public IAMReaderUIStates uiState = IAMReaderUIStates.CLOSED;
    public IAMReaderLoadStates loadState = IAMReaderLoadStates.EMPTY;
    public InAppMessageAppearance appearance = new InAppMessageBottomSheetSettings();
    public boolean showOnlyIfLoaded = false;
    public Integer iamId = null;

    public IAMReaderState iamId(Integer iamId) {
        this.iamId = iamId;
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

    public IAMReaderState showOnlyIfLoaded(boolean showOnlyIfLoaded) {
        this.showOnlyIfLoaded = showOnlyIfLoaded;
        return this;
    }

    public IAMReaderState copy() {
        return new IAMReaderState()
                .uiState(this.uiState)
                .loadState(this.loadState)
                .iamId(this.iamId)
                .appearance(this.appearance)
                .showOnlyIfLoaded(this.showOnlyIfLoaded);
    }
}
