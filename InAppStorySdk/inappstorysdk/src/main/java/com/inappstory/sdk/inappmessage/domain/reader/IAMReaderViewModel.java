package com.inappstory.sdk.inappmessage.domain.reader;

import com.inappstory.sdk.core.IASCore;
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
    }
}
