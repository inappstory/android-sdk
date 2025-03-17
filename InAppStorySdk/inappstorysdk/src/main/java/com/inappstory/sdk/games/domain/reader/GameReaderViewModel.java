package com.inappstory.sdk.games.domain.reader;

import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderState;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderUIState;
import com.inappstory.sdk.inappmessage.domain.stedata.STETypeAndData;
import com.inappstory.sdk.stories.utils.Observable;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.SingleTimeEvent;

public class GameReaderViewModel implements IGameReaderViewModel {
    private final Observable<GameReaderState> readerStateObservable =
            new Observable<>(
                    new GameReaderState()
            );


    public SingleTimeEvent<STETypeAndData> singleTimeEvents() {
        return singleTimeEvents;
    }

    private final SingleTimeEvent<STETypeAndData> singleTimeEvents =
            new SingleTimeEvent<>();

    @Override
    public void addSubscriber(Observer<GameReaderState> observer) {
        this.readerStateObservable.subscribe(observer);
    }

    @Override
    public void removeSubscriber(Observer<GameReaderState> observer) {
        this.readerStateObservable.unsubscribe(observer);
    }

    @Override
    public void readerIsOpened(boolean fromScratch) {

    }

    @Override
    public void readerIsClosing() {

    }

    @Override
    public void closeReader() {

    }

    @Override
    public void initState(GameReaderState state) {

    }

    @Override
    public GameReaderState getCurrentState() {
        return readerStateObservable.getValue();
    }

    @Override
    public void updateCurrentUiState(GameReaderUIState newState) {
        final GameReaderState readerState = this.readerStateObservable.getValue();
        GameReaderUIState currentUiState = readerState.uiState;
        if (currentUiState != newState) {

        }
    }

    @Override
    public void updateCurrentLoadState(GameReaderLoadState newState) {

    }


    @Override
    public void logMethod(String payload) {

    }

    @Override
    public int pausePlaybackOtherApp() {
        return 0;
    }

    @Override
    public void openUrl(String data) {

    }

    @Override
    public void vibrate(int[] vibratePattern) {

    }

    @Override
    public void gameInstanceSetLocalData(String data, boolean sendToServer) {

    }

    @Override
    public String gameInstanceGetLocalData() {
        return null;
    }

    @Override
    public void jsEvent(String name, String data) {

    }

    @Override
    public void gameShouldForegroundCallback(String data) {

    }

    @Override
    public void gameLoaded() {

    }

    @Override
    public void gameLoadFailed(String reason, boolean canTryReload) {

    }

    @Override
    public void reloadGameReader() {

    }

    @Override
    public void initUserAccelerationSensor(String options) {

    }

    @Override
    public void startUserAccelerationSensor() {

    }

    @Override
    public void stopUserAccelerationSensor() {

    }

    @Override
    public void sendApiRequest(String data) {

    }

    @Override
    public void setAudioManagerMode(String mode) {

    }

    @Override
    public void gameComplete(String data, String eventData, String urlOrOptions) {

    }

    @Override
    public void gameStatisticEvent(String name, String data) {

    }

    @Override
    public void showGoodsWidget(String id, String skus) {

    }

    @Override
    public void share(String id, String data) {

    }

    @Override
    public void openFilePicker(String data) {

    }

    @Override
    public boolean hasFilePicker() {
        return false;
    }
}
