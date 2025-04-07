package com.inappstory.sdk.games.domain.reader;

import android.media.AudioManager;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.game.reader.GameLoadedConfig;
import com.inappstory.sdk.inappmessage.domain.stedata.CallToActionData;
import com.inappstory.sdk.inappmessage.domain.stedata.STEDataType;
import com.inappstory.sdk.inappmessage.domain.stedata.STETypeAndData;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.stories.api.models.UrlObject;
import com.inappstory.sdk.stories.outercallbacks.common.gamereader.GameReaderCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.utils.Observable;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.SingleTimeEvent;
import com.inappstory.sdk.utils.format.StringsUtils;

import java.lang.reflect.Type;

public class GameReaderViewModel implements IGameReaderViewModel {
    private final Observable<GameReaderState> readerStateObservable =
            new Observable<>(
                    new GameReaderState()
            );

    private final IASCore core;

    public GameReaderViewModel(IASCore core) {
        this.core = core;
    }

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
        String gameId = getGameId();
        if (gameId == null) return;
        InAppStoryManager.showDLog("JS_method_call",
                gameId + " " + payload);
    }

    private final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            singleTimeEvents.updateValue(
                    new STETypeAndData(
                            STEDataType.AUDIO_FOCUS_CHANGE,
                            null
                    )
            );
        }
    };

    @Override
    public int pausePlaybackOtherApp() {
        return core.audioManagerUtils().pausePlayback(audioFocusChangeListener);
    }

    @Override
    public void openUrl(String data) {
        final GameReaderState readerState = this.readerStateObservable.getValue();
        if (readerState == null || readerState.gameReaderLaunchData == null) return;
        UrlObject urlObject = JsonParser.fromJson(data, UrlObject.class);
        if (urlObject != null && urlObject.url != null && !urlObject.url.isEmpty()) {
            singleTimeEvents.updateValue(
                    new STETypeAndData(
                            STEDataType.CALL_TO_ACTION,
                            new CallToActionData()
                                    .contentData(
                                            readerState.gameReaderLaunchData.getContentData()
                                    )
                                    .link(
                                            StringsUtils.getNonNull(urlObject.url)
                                    )
                                    .clickAction(
                                            ClickAction.GAME
                                    )
                    )
            );
        }
    }

    @Override
    public void vibrate(int[] vibratePattern) {
        core.vibrateUtils().vibrate(vibratePattern);
    }

    @Override
    public void gameInstanceSetLocalData(String data, boolean sendToServer) {
        String gameId = getGameId();
        if (gameId == null) return;
        IASDataSettingsHolder settingsHolder =
                (IASDataSettingsHolder) core.settingsAPI();
        core.keyValueStorage().saveString("gameInstance_" + gameId
                + "__" + settingsHolder.userId(), data);

        if (core.statistic().storiesV1().disabled()) return;
        if (sendToServer) {
            core.network().enqueue(core.network().getApi().sendGameData(gameId, data),
                    new NetworkCallback<Response>() {
                        @Override
                        public void onSuccess(Response response) {

                        }

                        @Override
                        public Type getType() {
                            return null;
                        }
                    }
            );
        }
    }

    private String getGameId() {
        final GameReaderState readerState = this.readerStateObservable.getValue();
        if (readerState == null || readerState.gameReaderLaunchData == null) return null;
        return readerState.gameReaderLaunchData.getGameId();
    }

    @Override
    public String gameInstanceGetLocalData() {

        String gameId = getGameId();
        if (gameId == null) return "";
        IASDataSettingsHolder settingsHolder =
                (IASDataSettingsHolder) core.settingsAPI();
        String res = core.keyValueStorage().getString("gameInstance_" + gameId
                + "__" + settingsHolder.userId());
        return res == null ? "" : res;
    }

    @Override
    public void jsEvent(final String name, final String data) {
        final GameReaderState readerState = this.readerStateObservable.getValue();
        if (readerState == null || readerState.gameReaderLaunchData == null) return;
        final String gameId = readerState.gameReaderLaunchData.getGameId();
        if (gameId == null) return;
        core.callbacksAPI().useCallback(
                IASCallbackType.GAME_READER,
                new UseIASCallback<GameReaderCallback>() {
                    @Override
                    public void use(@NonNull GameReaderCallback callback) {
                        callback.eventGame(
                                readerState.gameReaderLaunchData.getContentData(),
                                gameId,
                                name,
                                data
                        );
                    }
                }
        );
    }

    @Override
    public void gameShouldForegroundCallback(String data) {
        GameLoadedConfig config = JsonParser.fromJson(data, GameLoadedConfig.class);
        singleTimeEvents.updateValue(
                new STETypeAndData(
                        STEDataType.GAME_SHOULD_FOREGROUND,
                        config
                )
        );
    }

    @Override
    public void gameLoaded() {
        //LaunchGameLogger
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
