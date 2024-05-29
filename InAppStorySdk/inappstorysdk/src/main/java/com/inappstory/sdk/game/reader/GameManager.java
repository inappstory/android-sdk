package com.inappstory.sdk.game.reader;

import android.content.Context;
import android.media.AudioManager;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.game.cache.SetGameLoggerCallback;
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
import com.inappstory.sdk.stories.api.models.UrlObject;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.utils.StringsUtils;

import java.lang.reflect.Type;
import java.util.List;

public class GameManager {
    String path;
    String gameCenterId;
    List<WebResource> resources;
    final GameLoadStatusHolder statusHolder = new GameLoadStatusHolder();
    String gameConfig;
    AbstractGameLogger logger;

    GameStoryData dataModel;

    public void setLogger(int loggerLevel) {
        switch (loggerLevel) {
            case 0:
                logger = new GameLoggerLvl0();
                break;
            case 1:
                logger = new GameLoggerLvl1(gameCenterId);
                break;
            case 2:
                logger = new GameLoggerLvl2(gameCenterId);
                break;
            case 3:
                logger = new GameLoggerLvl3(gameCenterId);
                break;
        }
    }

    public GameManager(GameReaderContentFragment host) {
        this.host = host;
    }

    void gameInstanceSetData(String gameInstanceId, String data, boolean sendToServer) {

        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        String id = gameInstanceId;
        if (id == null) id = gameCenterId;
        if (id == null) return;
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            return;
        }
        KeyValueStorage.saveString("gameInstance_" + gameInstanceId
                + "__" + service.getUserId(), data);

        if (service.statV1Disallowed()) return;
        if (sendToServer) {
            networkClient.enqueue(networkClient.getApi().sendGameData(gameInstanceId, data),
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
            InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
            if (inAppStoryManager != null) {
                inAppStoryManager.getVibrateUtils().vibrate(host.getContext(), vibratePattern);
            }
        }
    }

    void storySetData(String data, boolean sendToServer) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        if (dataModel == null) return;
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            return;
        }
        KeyValueStorage.saveString("story" + dataModel.slideData.story.id
                + "__" + service.getUserId(), data);

        String sessionId = service.getSession().getSessionId();
        if (service.statV1Disallowed() || sessionId.isEmpty()) return;
        if (sendToServer) {
            networkClient.enqueue(
                    networkClient.getApi().sendStoryData(
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
            StatisticManager.getInstance().sendGameEvent(name, data, dataModel.slideData.story.feed);
    }

    private void closeOrFinishGameCallback(GameStoryData dataModel, String gameCenterId, String eventData) {
        if (CallbackManager.getInstance().getGameReaderCallback() != null) {
            if (eventData == null ||
                    eventData.isEmpty() ||
                    eventData.equals("{}") ||
                    eventData.equals("null")
            ) {
                CallbackManager
                        .getInstance()
                        .getGameReaderCallback()
                        .closeGame(
                                dataModel,
                                gameCenterId
                        );
            } else {
                CallbackManager
                        .getInstance()
                        .getGameReaderCallback()
                        .finishGame(
                                dataModel,
                                eventData,
                                gameCenterId
                        );
            }
        }
    }

    private void gameCompletedWithObject(String gameState, final GameFinishOptions options, String eventData) {
        closeOrFinishGameCallback(dataModel, gameCenterId, eventData);
        host.gameCompleted(gameState, null);
        if (options.openStory != null
                && options.openStory.id != null
                && !options.openStory.id.isEmpty()) {
            InAppStoryManager.getInstance().showStoryCustom(
                    options.openStory.id,
                    host.getContext(),
                    AppearanceManager.getCommonInstance()
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

    void tapOnLink(String link, Context context) {
        SlideData data = null;
        if (dataModel != null) {
            data = dataModel.slideData;
        }

        if (CallbackManager.getInstance().getCallToActionCallback() != null) {
            CallbackManager.getInstance().getCallToActionCallback().callToAction(
                    context,
                    data,
                    StringsUtils.getNonNull(link),
                    ClickAction.GAME
            );
        } else if (CallbackManager.getInstance().getUrlClickCallback() != null) {
            CallbackManager.getInstance().getUrlClickCallback().onUrlClick(
                    StringsUtils.getNonNull(link)
            );
        } else {
            host.tapOnLinkDefault(StringsUtils.getNonNull(link));
        }
    }

    int pausePlaybackOtherApp() {
        AudioManager am = (AudioManager) host.getContext().getSystemService(Context.AUDIO_SERVICE);
        return am.requestAudioFocus(host.audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
    }


    void gameLoaded(String data) {
        if (statusHolder.hasGameLoadStatus()) return;
        logger.gameLoaded(true);
        statusHolder.setGameLoaded();
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

    void jsEvent(String name, String data) {
        host.jsEvent(name, data);
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
        InAppStoryManager manager = InAppStoryManager.getInstance();
        return (manager != null && manager.getUtilModulesHolder().hasFilePickerModule());
    }

    void shareData(String id, String data) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || service.isShareProcess())
            return;
        service.isShareProcess(true);
        InnerShareData shareObj = JsonParser.fromJson(data, InnerShareData.class);
        ScreensManager.getInstance().shareCompleteListener(
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
