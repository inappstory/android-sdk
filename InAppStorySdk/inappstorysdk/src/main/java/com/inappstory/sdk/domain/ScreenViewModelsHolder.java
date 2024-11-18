package com.inappstory.sdk.domain;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderViewModel;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderViewModel;

public class ScreenViewModelsHolder implements IScreenViewModelsHolder {
    private final IASCore core;
    private final IIAMReaderViewModel iamReaderViewModel;

    public ScreenViewModelsHolder(IASCore core) {
        this.core = core;
        this.iamReaderViewModel = new IAMReaderViewModel(core);

    }

    @Override
    public IIAMReaderViewModel iamReaderViewModel() {
        return iamReaderViewModel;
    }
}
