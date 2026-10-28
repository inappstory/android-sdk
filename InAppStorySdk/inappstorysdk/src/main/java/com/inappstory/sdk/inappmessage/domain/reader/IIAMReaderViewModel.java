package com.inappstory.sdk.inappmessage.domain.reader;

import android.util.Pair;

import com.inappstory.sdk.inappmessage.InAppMessageData;
import com.inappstory.sdk.inappmessage.InAppMessageViewController;
import com.inappstory.sdk.inappmessage.ui.reader.InAppMessageCloseAction;
import com.inappstory.sdk.inappmessage.ui.reader.InAppMessageOpenAction;
import com.inappstory.sdk.stories.utils.Observer;

public interface IIAMReaderViewModel {
    InAppMessageData getCurrentInAppMessageData();
    void addSubscriber(Observer<IAMReaderState> observable);
    void removeSubscriber(Observer<IAMReaderState> observable);
    void initState(IAMReaderState state);
    IIAMReaderSlideViewModel slideViewModel();
    IAMReaderState getCurrentState();
    void updateCurrentUiState(IAMReaderUIStates newState);
    void updateCurrentSafeArea(Pair<Integer, Integer> safeArea);
    void updateCurrentLoaderState(IAMReaderLoaderStates newState);
    void updateCurrentLoadState(IAMReaderLoadStates newState);
    void onOpenAction();
    void onCloseAction();
    void openAction(InAppMessageOpenAction openAction);
    void closeAction(InAppMessageCloseAction closeAction);
    void controller(InAppMessageViewController controller);
    InAppMessageViewController controller();
    void clear();
}
