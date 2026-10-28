package com.inappstory.sdk.domain;

import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderViewModel;

public interface IScreenViewModelsHolder {
    IIAMReaderViewModel iamReaderViewModel();
    IIAMReaderViewModel iamReaderViewModel(int id);
}
