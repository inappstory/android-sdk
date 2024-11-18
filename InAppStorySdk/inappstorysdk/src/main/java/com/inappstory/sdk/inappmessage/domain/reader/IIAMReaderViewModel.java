package com.inappstory.sdk.inappmessage.domain.reader;

import com.inappstory.sdk.stories.utils.Observer;

public interface IIAMReaderViewModel {
    void addSubscriber(Observer<IAMReaderState> observable);
    void removeSubscriber(Observer<IAMReaderState> observable);
    void initState(IAMReaderState state);
    IIAMReaderSlideViewModel slideViewModel();
    IAMReaderState getCurrentState();
    void updateCurrentUiState(IAMReaderUIStates newState);
    void updateCurrentLoadState(IAMReaderLoadStates newState);
    void clear();
}
