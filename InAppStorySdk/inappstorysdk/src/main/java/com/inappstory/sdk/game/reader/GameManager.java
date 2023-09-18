package com.inappstory.sdk.game.reader;

import static com.inappstory.sdk.network.NetworkClient.NC_IS_UNAVAILABLE;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.JsonParser;

import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.jsapiclient.JsApiClient;
import com.inappstory.sdk.network.jsapiclient.JsApiResponseCallback;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.stories.api.models.GameCenterData;
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.UrlObject;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
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

    void loadGame(GameCenterData gameCenterData) {
        ArrayList<WebResource> resourceList = new ArrayList<>();

        if (resources != null) {
            resourceList = JsonParser.listFromJson(resources, WebResource.class);
        }

        String[] urlParts = ZipLoader.urlParts(path);
        ZipLoader.getInstance().downloadAndUnzip(
                resourceList,
                path,
                urlParts[0],
                gameCenterId,
                gameCenterData,
                callback,
                host.interruption,
                "game"
        );
    }

    void gameInstanceSetData(String gameInstanceId, String data, boolean sendToServer) {
        if (InAppStoryService.isNull()) return;
        String id = gameInstanceId;
        if (id == null) id = gameCenterId;
        if (id == null) return;
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            return;
        }
        KeyValueStorage.saveString("gameInstance_" + gameInstanceId
                + "__" + InAppStoryService.getInstance().getUserId(), data);

        if (!InAppStoryService.getInstance().getSendStatistic()) return;
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
            tapOnLink(urlObject.url);
    }

    void storySetData(String data, boolean sendToServer) {
        if (InAppStoryService.isNull()) return;
        if (dataModel == null) return;
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            callback.onError(NC_IS_UNAVAILABLE);
            return;
        }
        KeyValueStorage.saveString("story" + dataModel.slideData.story.id
                + "__" + InAppStoryService.getInstance().getUserId(), data);

        if (!InAppStoryService.getInstance().getSendStatistic()) return;
        if (sendToServer) {
            networkClient.enqueue(
                    networkClient.getApi().sendStoryData(
                            Integer.toString(dataModel.slideData.story.id),
                            data,
                            Session.getInstance().id
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

    GameActivity host;

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

    private void gameCompletedWithObject(String gameState, GameFinishOptions options, String eventData) {
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
        new JsApiClient(
                host,
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

    void tapOnLink(String link) {
        if (InAppStoryService.isNull()) return;
        SlideData data = null;
        if (dataModel != null) {
            data = dataModel.slideData;
        }

        if (CallbackManager.getInstance().getCallToActionCallback() != null) {
            CallbackManager.getInstance().getCallToActionCallback().callToAction(
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
