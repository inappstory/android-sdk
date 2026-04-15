package com.inappstory.sdk.inappmessage.domain.reader;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.inappmessage.CloseInAppMessageCallback;
import com.inappstory.sdk.inappmessage.InAppMessageViewController;
import com.inappstory.sdk.inappmessage.ui.reader.InAppMessageCloseAction;
import com.inappstory.sdk.inappmessage.ui.reader.InAppMessageOpenAction;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.inappmessage.ShowInAppMessageCallback;
import com.inappstory.sdk.inappmessage.InAppMessageData;
import com.inappstory.sdk.stories.utils.Observable;
import com.inappstory.sdk.stories.utils.Observer;

public class IAMReaderViewModel implements IIAMReaderViewModel {
    private final Observable<IAMReaderState> readerStateObservable =
            new Observable<>(
                    new IAMReaderState()
            );

    private final IASCore core;
    private final IAMReaderSlideViewModel slideViewModel;

    public IAMReaderViewModel(IASCore core) {
        this.core = core;
        this.slideViewModel = new IAMReaderSlideViewModel(this, core);
    }

    @Override
    public InAppMessageData getCurrentInAppMessageData() {
        final IAMReaderState readerState = this.readerStateObservable.getValue();
        if (readerState != null) {
            InAppMessageData inAppMessageData = readerState.inAppMessageData;
            if (inAppMessageData == null) {
                if (readerState.iamId != null) {
                    IInAppMessage inAppMessage = (IInAppMessage) core
                            .contentHolder()
                            .readerContent()
                            .getByIdAndType(
                                    readerState.iamId,
                                    ContentType.IN_APP_MESSAGE
                            );
                    if (inAppMessage != null) {
                        return new InAppMessageData(
                                inAppMessage.id(),
                                inAppMessage.statTitle(),
                                readerState.event,
                                readerState.sourceType,
                                inAppMessage.messageType()
                        );
                    }
                }
            } else {
                return inAppMessageData;
            }
        }
        return null;
    }


    @Override
    public void addSubscriber(Observer<IAMReaderState> observable) {
        this.readerStateObservable.subscribe(observable);
    }

    @Override
    public void removeSubscriber(Observer<IAMReaderState> observable) {
        this.readerStateObservable.unsubscribe(observable);
    }

    @Override
    public void initState(IAMReaderState state) {
        this.readerStateObservable.updateValue(state);
    }

    @Override
    public IIAMReaderSlideViewModel slideViewModel() {
        return slideViewModel;
    }

    @Override
    public IAMReaderState getCurrentState() {
        return readerStateObservable.getValue();
    }

    @Override
    public void updateCurrentUiState(IAMReaderUIStates newState) {
        final IAMReaderState readerState = this.readerStateObservable.getValue();
        Log.e("sendIAMScrollEvent", "updateCurrentUiState");
        IAMReaderUIStates currentUiState = readerState.uiState;

        if (currentUiState != newState) {
            InAppMessageData messageData = getCurrentInAppMessageData();
            if (newState == IAMReaderUIStates.OPENED) {
                if (messageData != null)
                    core.callbacksAPI().useCallback(
                            IASCallbackType.SHOW_IN_APP_MESSAGE,
                            new UseIASCallback<ShowInAppMessageCallback>() {
                                @Override
                                public void use(@NonNull ShowInAppMessageCallback callback) {
                                    callback.showInAppMessage(
                                            messageData
                                    );
                                }
                            }
                    );
            } else if (newState == IAMReaderUIStates.CLOSED) {
                if (messageData != null)
                    core.callbacksAPI().useCallback(
                            IASCallbackType.CLOSE_IN_APP_MESSAGE,
                            new UseIASCallback<CloseInAppMessageCallback>() {
                                @Override
                                public void use(@NonNull CloseInAppMessageCallback callback) {
                                    callback.closeInAppMessage(
                                            messageData
                                    );
                                }
                            }
                    );
                Log.e("sendIAMScrollEvent", "CloseIAM");
                if (slideViewModel != null) {
                    slideViewModel.sendSlideScrollEvents();
                }
            }
        }
        this.readerStateObservable.updateValue(
                this.readerStateObservable.getValue()
                        .copy()
                        .uiState(newState)
        );
    }

    @Override
    public void updateCurrentSafeArea(Pair<Integer, Integer> safeArea) {
        this.readerStateObservable.updateValue(
                this.readerStateObservable.getValue()
                        .copy()
                        .safeArea(safeArea)
        );
    }

    @Override
    public void updateCurrentLoaderState(IAMReaderLoaderStates newState) {
        this.readerStateObservable.updateValue(
                this.readerStateObservable.getValue()
                        .copy()
                        .loaderState(newState)
        );
    }

    @Override
    public void updateCurrentLoadState(IAMReaderLoadStates newState) {
        this.readerStateObservable.updateValue(
                this.readerStateObservable.getValue()
                        .copy()
                        .loadState(newState)
        );
    }


    private InAppMessageCloseAction onCloseAction;
    private InAppMessageOpenAction onOpenAction;
    InAppMessageViewController controller;

    @Override
    public void openAction(InAppMessageOpenAction openAction) {
        this.onOpenAction = openAction;
    }

    @Override
    public void closeAction(InAppMessageCloseAction closeAction) {
        this.onCloseAction = closeAction;
    }

    @Override
    public void onOpenAction() {
        if (onOpenAction != null) onOpenAction.onOpen();
    }

    @Override
    public void onCloseAction() {
        if (onCloseAction != null) onCloseAction.onClose();
    }

    @Override
    public void controller(InAppMessageViewController controller) {
        this.controller = controller;
    }

    @Override
    public InAppMessageViewController controller() {
        return controller;
    }

    @Override
    public void clear() {
        InAppMessageData inAppMessageData = this.readerStateObservable.getValue().inAppMessageData;
        this.readerStateObservable.updateValue(
                new IAMReaderState().inAppMessageData(inAppMessageData)
        );
        this.slideViewModel.clear();
        this.onCloseAction = null;
        this.onOpenAction = null;
        this.controller = null;
    }
}
