package com.inappstory.sdk.game.reader;

import android.content.Context;
import android.media.AudioManager;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.api.impl.IASSingleStoryImpl;
import com.inappstory.sdk.core.ui.screens.ShareProcessHandler;
import com.inappstory.sdk.game.reader.logger.AbstractGameLogger;
import com.inappstory.sdk.game.reader.logger.GameLoggerLvl0;
import com.inappstory.sdk.game.reader.logger.GameLoggerLvl1;
import com.inappstory.sdk.game.reader.logger.GameLoggerLvl2;
import com.inappstory.sdk.game.reader.logger.GameLoggerLvl3;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.JsonParser;

import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.jsapiclient.JsApiClient;
import com.inappstory.sdk.network.jsapiclient.JsApiResponseCallback;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.share.IShareCompleteListener;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.UrlObject;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.outercallbacks.common.gamereader.GameReaderCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.utils.StringsUtils;

import java.lang.reflect.Type;
import java.util.List;

public class GameManager {
    public IASCore core() {
        return core;
    }

    private final IASCore core;
    String path;
    final String gameCenterId;
    List<WebResource> resources;
    final GameLoadStatusHolder statusHolder = new GameLoadStatusHolder();
    String gameConfig;
    AbstractGameLogger logger;
    private final GameStoryData dataModel;


    public void setLogger(int loggerLevel) {
        switch (loggerLevel) {
            case 0:
                logger = new GameLoggerLvl0(core);
                break;
            case 1:
                logger = new GameLoggerLvl1(core, gameCenterId);
                break;
            case 2:
                logger = new GameLoggerLvl2(core, gameCenterId);
                break;
            case 3:
                logger = new GameLoggerLvl3(core, gameCenterId);
                break;
        }
    }

    public GameManager(
            GameReaderContentFragment host,
            IASCore core,
            String gameCenterId,
            GameStoryData dataModel
    ) {
        this.core = core;
        this.host = host;
        this.gameCenterId = gameCenterId;
        this.dataModel = dataModel;
        logger = new GameLoggerLvl1(core, gameCenterId);
    }

    void gameInstanceSetData(String gameInstanceId, String data, boolean sendToServer) {

        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) return;
        IASDataSettingsHolder settingsHolder =
                (IASDataSettingsHolder) inAppStoryManager.iasCore().settingsAPI();

        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        String id = gameInstanceId;
        if (id == null) id = gameCenterId;
        if (id == null) return;
        core.keyValueStorage().saveString("gameInstance_" + gameInstanceId
                + "__" + settingsHolder.userId(), data);

        if (core.statistic().v1().disabled()) return;
        if (sendToServer) {
            core.network().enqueue(core.network().getApi().sendGameData(gameInstanceId, data),
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

    void openUrl(String data) {
        UrlObject urlObject = JsonParser.fromJson(data, UrlObject.class);
        if (urlObject != null && urlObject.url != null && !urlObject.url.isEmpty())
            tapOnLink(urlObject.url, host.getContext());
    }

    void vibrate(int[] vibratePattern) {
        if (host != null && host.getContext() != null) {
            core.vibrateUtils().vibrate(host.getContext(), vibratePattern);
        }
    }

    void storySetData(String data, boolean sendToServer) {
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) return;
        IASDataSettingsHolder settingsHolder =
                (IASDataSettingsHolder) inAppStoryManager.iasCore().settingsAPI();
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        if (dataModel == null) return;


        core.keyValueStorage().saveString("story" + dataModel.slideData.story.id
                + "__" + settingsHolder.userId(), data);

        String sessionId = core.sessionManager().getSession().getSessionId();
        if (core.statistic().v1().disabled() || sessionId.isEmpty()) return;
        if (sendToServer) {
            core.network().enqueue(
                    core.network().getApi().sendStoryData(
                            Integer.toString(dataModel.slideData.story.id),
                            data,
                            sessionId
                    ),
                    new NetworkCallback<Response>() {
                        @Override
                        public void onSuccess(Response response) {

                        }

                        @Override
                        public Type getType() {
                            return null;
                        }
                    });
        }
    }

    GameReaderContentFragment host;

    void showGoods(String skusString, String widgetId) {
        host.showGoods(skusString, widgetId);
    }


    void setAudioManagerMode(String mode) {
        host.setAudioManagerMode(mode);
    }

    void sendGameStat(String name, String data) {
        if (dataModel != null)
            core.statistic().v2().sendGameEvent(name, data, dataModel.slideData.story.feed);
    }

    void closeGameReader() {
        core.callbacksAPI().useCallback(
                IASCallbackType.GAME_READER,
                new UseIASCallback<GameReaderCallback>() {
                    @Override
                    public void use(@NonNull GameReaderCallback callback) {
                        callback.closeGame(
                                dataModel,
                                gameCenterId
                        );
                    }
                }
        );
    }

    private void closeOrFinishGameCallback(
            final GameStoryData dataModel,
            final String gameCenterId,
            final String eventData
    ) {
        core.callbacksAPI().useCallback(
                IASCallbackType.GAME_READER,
                new UseIASCallback<GameReaderCallback>() {
                    @Override
                    public void use(@NonNull GameReaderCallback callback) {
                        if (eventData == null ||
                                eventData.isEmpty() ||
                                eventData.equals("{}") ||
                                eventData.equals("null")
                        ) {
                            callback.closeGame(
                                    dataModel,
                                    gameCenterId
                            );
                        } else {
                            callback.finishGame(
                                    dataModel,
                                    eventData,
                                    gameCenterId
                            );
                        }
                    }
                }
        );
    }

    private void gameCompletedWithObject(String gameState, final GameFinishOptions options, String eventData) {
        closeOrFinishGameCallback(dataModel, gameCenterId, eventData);
        host.gameCompleted(gameState, null);
        if (options.openStory != null
                && options.openStory.id != null
                && !options.openStory.id.isEmpty()) {
            ((IASSingleStoryImpl)core.singleStoryAPI()).show(
                    host.getContext(),
                    options.openStory.id,
                    AppearanceManager.getCommonInstance(),
                    null,
                    Story.StoryType.COMMON,
                    0,
                    true,
                    SourceType.SINGLE,
                    ShowStory.ACTION_CUSTOM
            );

        }

    }

    void gameCompleted(String gameState, String urlOrOptions, String eventData) {
        GameFinishOptions options = JsonParser.fromJson(urlOrOptions, GameFinishOptions.class);
        if (options == null) {
            gameCompletedWithUrl(gameState, urlOrOptions, eventData);
        } else if (options.openUrl != null) {
            gameCompletedWithUrl(gameState, options.openUrl, eventData);
        } else {
            gameCompletedWithObject(gameState, options, eventData);
        }
    }

    private void gameCompletedWithUrl(String gameState, String link, String eventData) {
        closeOrFinishGameCallback(dataModel, gameCenterId, eventData);
        host.gameCompleted(gameState, link);
    }

    void sendApiRequest(String data) {
        new JsApiClient(
                core,
                host.getContext(),
                ApiSettings.getInstance().getHost()
        ).sendApiRequest(data, new JsApiResponseCallback() {
            @Override
            public void onJsApiResponse(String result, String cb) {
                host.loadJsApiResponse(modifyJsResult(result), cb);
            }
        });
    }

    private String modifyJsResult(String data) {
        if (data == null) return "";
        return data.replaceAll("'", "\\\\'");
    }

    void tapOnLink(final String link, final Context context) {
        final SlideData data;
        if (dataModel != null) {
            data = dataModel.slideData;
        } else {
            data = null;
        }
        core.callbacksAPI().useCallback(
                IASCallbackType.CALL_TO_ACTION,
                new UseIASCallback<CallToActionCallback>() {
                    @Override
                    public void use(@NonNull CallToActionCallback callback) {
                        callback.callToAction(
                                context,
                                data,
                                StringsUtils.getNonNull(link),
                                ClickAction.GAME
                        );
                    }

                    @Override
                    public void onDefault() {
                        host.tapOnLinkDefault(StringsUtils.getNonNull(link));
                    }
                }
        );
    }

    int pausePlaybackOtherApp() {
        AudioManager am = (AudioManager) host.getContext().getSystemService(Context.AUDIO_SERVICE);
        return am.requestAudioFocus(host.audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
    }

    void gameLoaded() {
        if (statusHolder.hasGameLoadStatus()) return;
        logger.gameLoaded(true);
        statusHolder.setGameLoaded();
        host.gameShouldForeground();
    }

    void gameShouldForegroundCallback(String data) {
        GameLoadedConfig config = JsonParser.fromJson(data, GameLoadedConfig.class);
        host.gameReaderGestureBack = config.backGesture;
        host.showClose = config.showClose;
        host.updateUI();
    }

    void gameLoadFailed(String reason, boolean canTryReload) {
        if (statusHolder.hasGameLoadStatus()) return;
        statusHolder.setGameFailed();
        if (logger != null) {
            logger.gameLoaded(false);
            logger.sendGameError(reason);
        }
        if (canTryReload && statusHolder.updateCurrentReloadTry()) {
            logger.launchTryNumber(statusHolder.launchTryNumber() + 1);
            reloadGame();
        } else {
            clearTries();
            host.gameLoadedErrorCallback.onError(null, reason);
        }
    }

    void gameLoadError() {
        core.callbacksAPI().useCallback(
                IASCallbackType.GAME_READER,
                new UseIASCallback<GameReaderCallback>() {
                    @Override
                    public void use(@NonNull GameReaderCallback callback) {
                        callback.gameLoadError(
                                dataModel,
                                gameCenterId
                        );
                    }
                }
        );
    }

    void jsEvent(final String name, final String data) {
        core.callbacksAPI().useCallback(
                IASCallbackType.GAME_READER,
                new UseIASCallback<GameReaderCallback>() {
                    @Override
                    public void use(@NonNull GameReaderCallback callback) {
                        callback.eventGame(
                                dataModel,
                                gameCenterId,
                                name,
                                data
                        );
                    }
                }
        );
    }

    void clearTries() {
        statusHolder.clearGameLoadTries();
        logger.launchTryNumber(1);
    }

    void reloadGame() {
        statusHolder.clearGameStatus();
        logger.gameLoaded(false);
        host.restartGame();
    }


    void onResume() {
     /*   String shareId = null;
        if (ScreensManager.getInstance().getTempShareId() != null) {
            shareId = ScreensManager.getInstance().getTempShareId();
        } else if (ScreensManager.getInstance().getOldTempShareId() != null) {
            shareId = ScreensManager.getInstance().getOldTempShareId();
        }
        if (shareId != null) {
            host.shareComplete(shareId, false);
        }
        ScreensManager.getInstance().clearShareIds();*/
    }

    void openFilePicker(String data) {
        host.openFilePicker(data);
    }

    boolean hasFilePicker() {
        return core.externalUtilsAPI().getUtilsAPI().hasFilePickerModule();
    }

    void shareData(String id, String data) {
        ShareProcessHandler shareProcessHandler = core.screensManager().getShareProcessHandler();
        if (shareProcessHandler == null || shareProcessHandler.isShareProcess())
            return;
        shareProcessHandler.isShareProcess(true);
        InnerShareData shareObj = JsonParser.fromJson(data, InnerShareData.class);
        shareProcessHandler.shareCompleteListener(
                new IShareCompleteListener(id, -1) {
                    @Override
                    public void complete(String shareId, boolean shared) {
                        if (host != null && host.isAdded())
                            host.shareComplete(shareId, shared);
                    }
                }
        );
        host.share(shareObj);

    }
}
