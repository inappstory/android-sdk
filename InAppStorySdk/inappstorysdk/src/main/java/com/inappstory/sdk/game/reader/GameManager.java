package com.inappstory.sdk.game.reader;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.network.jsapiclient.JsApiClient;
import com.inappstory.sdk.network.jsapiclient.JsApiResponseCallback;
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.utils.StringsUtils;
import com.inappstory.sdk.utils.ZipLoadCallback;
import com.inappstory.sdk.utils.ZipLoader;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class GameManager {
    String path;
    String gameCenterId;
    String resources;
    String splashImagePath;

    boolean gameLoaded;
    String gameConfig;

    GameStoryData dataModel;
    ZipLoadCallback callback;

    public GameManager(GameActivity host) {
        this.host = host;
    }

    void loadGame() {
        ArrayList<WebResource> resourceList = new ArrayList<>();

        if (resources != null) {
            resourceList = JsonParser.listFromJson(resources, WebResource.class);
        }

        String[] urlParts = ZipLoader.urlParts(path);
        ZipLoader.getInstance().downloadAndUnzip(resourceList, path, urlParts[0], callback, "game");
    }
    void gameInstanceSetData(String gameInstanceId, String data, boolean sendToServer) {
        if (InAppStoryService.isNull()) return;
        String id = gameInstanceId;
        if (id == null) id = gameCenterId;
        if (id == null) return;
        KeyValueStorage.saveString("gameInstance_" + gameInstanceId
                + "__" + InAppStoryService.getInstance().getUserId(), data);

        if (sendToServer) {
            NetworkClient.getApi().sendGameData(gameInstanceId, data)
                    .enqueue(new NetworkCallback<Response>() {
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

    void storySetData(String data, boolean sendToServer) {
        if (InAppStoryService.isNull()) return;
        if (dataModel == null) return;
        KeyValueStorage.saveString("story" + dataModel.storyId
                + "__" + InAppStoryService.getInstance().getUserId(), data);

        if (sendToServer) {
            NetworkClient.getApi().sendStoryData(Integer.toString(dataModel.storyId), data, Session.getInstance().id)
                    .enqueue(new NetworkCallback<Response>() {
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

    GameActivity host;

    void showGoods(String skusString, String widgetId) {
        host.showGoods(skusString, widgetId);
    }


    void setAudioManagerMode(String mode) {
        host.setAudioManagerMode(mode);
    }

    void sendGameStat(String name, String data) {
        StatisticManager.getInstance().sendGameEvent(name, data, dataModel.feedId);
    }

    private void gameCompletedWithObject(String gameState, GameFinishOptions options, String eventData) {
        if (CallbackManager.getInstance().getGameCallback() != null && dataModel != null) {
            CallbackManager.getInstance().getGameCallback().finishGame(
                    dataModel.storyId,
                    dataModel.title,
                    dataModel.tags,
                    dataModel.slidesCount,
                    dataModel.slideIndex,
                    eventData
            );
        }
        if (CallbackManager.getInstance().getGameReaderCallback() != null) {
            CallbackManager.getInstance().getGameReaderCallback().finishGame(
                    dataModel,
                    eventData,
                    gameCenterId
            );
        }
        if (options.openStory != null
                && options.openStory.id != null
                && !options.openStory.id.isEmpty()) {
            InAppStoryManager.getInstance().showStoryCustom(
                    options.openStory.id,
                    host,
                    AppearanceManager.getCommonInstance()
            );
        }
        host.gameCompleted(gameState, null);

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
        if (CallbackManager.getInstance().getGameCallback() != null && dataModel != null) {
            CallbackManager.getInstance().getGameCallback().finishGame(
                    dataModel.storyId,
                    StringsUtils.getNonNull(dataModel.title),
                    StringsUtils.getNonNull(dataModel.tags),
                    dataModel.slidesCount,
                    dataModel.slideIndex,
                    StringsUtils.getNonNull(eventData)
            );
        }
        if (CallbackManager.getInstance().getGameReaderCallback() != null) {
            CallbackManager.getInstance().getGameReaderCallback().finishGame(
                    dataModel,
                    eventData,
                    gameCenterId
            );
        }
        host.gameCompleted(gameState, link);
    }

    void sendApiRequest(String data) {
        new JsApiClient(host).sendApiRequest(data, new JsApiResponseCallback() {
            @Override
            public void onJsApiResponse(String result, String cb) {
                host.loadJsApiResponse(modifyJsResult(result), cb);
            }
        });
    }

    private String modifyJsResult(String data) {
        if (data == null) return "";
        data.replaceAll("'", "\\'");
        return data;
    }

    void tapOnLink(String link) {
        if (InAppStoryService.isNull()) return;
        if (CallbackManager.getInstance().getCallToActionCallback() != null) {
            CallbackManager.getInstance().getCallToActionCallback().callToAction(
                    dataModel.storyId,
                    StringsUtils.getNonNull(dataModel.title),
                    StringsUtils.getNonNull(dataModel.tags),
                    dataModel.slidesCount,
                    dataModel.slideIndex,
                    StringsUtils.getNonNull(link),
                    ClickAction.GAME
            );
        }
        if (CallbackManager.getInstance().getUrlClickCallback() != null) {
            CallbackManager.getInstance().getUrlClickCallback().onUrlClick(
                    StringsUtils.getNonNull(link)
            );
        } else {
            host.tapOnLinkDefault(StringsUtils.getNonNull(link));
        }
    }

    int pausePlaybackOtherApp() {
        AudioManager am = (AudioManager) host.getSystemService(Context.AUDIO_SERVICE);
        return am.requestAudioFocus(host.audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
    }

    void gameLoaded(String data) {
        GameLoadedConfig config = JsonParser.fromJson(data, GameLoadedConfig.class);
        host.gameReaderGestureBack = config.backGesture;
        host.showClose = config.showClose;
        gameLoaded = true;
        host.updateUI();
    }


    void onResume() {
        String shareId = null;
        if (ScreensManager.getInstance().getTempShareId() != null) {
            shareId = ScreensManager.getInstance().getTempShareId();
        } else if (ScreensManager.getInstance().getOldTempShareId() != null) {
            shareId = ScreensManager.getInstance().getOldTempShareId();
        }
        if (shareId != null) {
            host.shareComplete(shareId, false);
        }
        ScreensManager.getInstance().clearShareIds();
    }

    void shareData(String id, String data) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || service.isShareProcess())
            return;
        service.isShareProcess(true);
        InnerShareData shareObj = JsonParser.fromJson(data, InnerShareData.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            ScreensManager.getInstance().setTempShareId(id);
            ScreensManager.getInstance().setTempShareStoryId(-1);
        } else {
            ScreensManager.getInstance().setOldTempShareId(id);
            ScreensManager.getInstance().setOldTempShareStoryId(-1);
        }
        host.share(shareObj);

    }
}
