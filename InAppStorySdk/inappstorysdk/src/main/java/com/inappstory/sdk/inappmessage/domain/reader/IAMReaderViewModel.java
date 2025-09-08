package com.inappstory.sdk.inappmessage.domain.reader;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.inappmessage.CloseInAppMessageCallback;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.inappmessage.ShowInAppMessageCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.InAppMessageData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
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
                            readerState.sourceType
                    );
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
        IAMReaderUIStates currentUiState = readerState.uiState;

        if (currentUiState != newState) {
            if (newState == IAMReaderUIStates.OPENED) {
                core.callbacksAPI().useCallback(
                        IASCallbackType.SHOW_IN_APP_MESSAGE,
                        new UseIASCallback<ShowInAppMessageCallback>() {
                            @Override
                            public void use(@NonNull ShowInAppMessageCallback callback) {
                                callback.showInAppMessage(
                                        getCurrentInAppMessageData()
                                );
                            }
                        }
                );
            } else if (newState == IAMReaderUIStates.CLOSED) {
                core.callbacksAPI().useCallback(
                        IASCallbackType.CLOSE_IN_APP_MESSAGE,
                        new UseIASCallback<CloseInAppMessageCallback>() {
                            @Override
                            public void use(@NonNull CloseInAppMessageCallback callback) {
                                callback.closeInAppMessage(
                                        getCurrentInAppMessageData()
                                );
                            }
                        }
                );
            }
        }
        this.readerStateObservable.updateValue(
                this.readerStateObservable.getValue()
                        .copy()
                        .uiState(newState)
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

    @Override
    public void clear() {
        this.readerStateObservable.updateValue(
                new IAMReaderState()
        );
        this.slideViewModel.clear();
    }
}
