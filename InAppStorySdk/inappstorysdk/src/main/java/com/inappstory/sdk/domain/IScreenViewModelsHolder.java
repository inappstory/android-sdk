package com.inappstory.sdk.domain;

import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderViewModel;
import com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels.StoryReaderViewModel;

public interface IScreenViewModelsHolder {
    IIAMReaderViewModel iamReaderViewModel();
    StoryReaderViewModel storyReaderViewModel();
    IIAMReaderViewModel iamReaderViewModel(int id);
}
