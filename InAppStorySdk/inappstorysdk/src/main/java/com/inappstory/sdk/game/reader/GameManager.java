package com.inappstory.sdk.game.reader;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.game.loader.GameLoadCallback;
import com.inappstory.sdk.game.loader.GameLoader;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.network.jsapiclient.JsApiClient;
import com.inappstory.sdk.network.jsapiclient.JsApiRequestConfig;
import com.inappstory.sdk.network.jsapiclient.JsApiResponseCallback;
import com.inappstory.sdk.stories.api.models.ShareObject;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outerevents.CallToAction;
import com.inappstory.sdk.stories.outerevents.ClickOnButton;
import com.inappstory.sdk.stories.outerevents.FinishGame;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.utils.KeyValueStorage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import static com.inappstory.sdk.network.JsonParser.toMap;

public class GameManager {
    String storyId;
    String path;
    String resources;
    String loaderPath;
    String title;
    String tags;
    int index;
    int slidesCount;

    boolean gameLoaded;
    String gameConfig;

    GameLoadCallback callback;

    public GameManager(GameActivity host) {
        this.host = host;
    }

    void loadGame() {
        ArrayList<WebResource> resourceList = new ArrayList<>();

        if (resources != null) {
            resourceList = JsonParser.listFromJson(resources, WebResource.class);
        }

        String[] urlParts = urlParts(path);
        GameLoader.getInstance().downloadAndUnzip(host, resourceList, path, urlParts[0], callback);
    }

    private String[] urlParts(String url) {
        String[] parts = url.split("/");
        String fName = parts[parts.length - 1].split("\\.")[0];
        return fName.split("_");
    }


    void storySetData(String data, boolean sendToServer) {
        KeyValueStorage.saveString("story" + storyId
                + "__" + InAppStoryService.getInstance().getUserId(), data);

        if (!InAppStoryService.getInstance().getSendStatistic()) return;
        if (sendToServer) {
            NetworkClient.getApi().sendStoryData(storyId, data, StatisticSession.getInstance().id)
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

    void gameCompleted(String gameState, String link, String eventData) {
        CsEventBus.getDefault().post(new FinishGame(Integer.parseInt(storyId), title, tags,
                slidesCount, index, eventData));
        if (CallbackManager.getInstance().getGameCallback() != null) {
            CallbackManager.getInstance().getGameCallback().finishGame(
                    Integer.parseInt(storyId), title, tags,
                    slidesCount, index, eventData);
        }
        host.gameCompleted(gameState, link);
    }

    void sendApiRequest(String data) {
        new JsApiClient(host).sendApiRequest(data, new JsApiResponseCallback() {
            @Override
            public void onJsApiResponse(String result, String cb) {
                host.loadJsApiResponse(result, cb);
            }
        });
    }

    void tapOnLink(String link) {
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(
                Integer.parseInt(storyId));
        CsEventBus.getDefault().post(new ClickOnButton(story.id, story.title,
                story.tags, story.getSlidesCount(), story.lastIndex,
                link));
        int cta = CallToAction.GAME;
        CsEventBus.getDefault().post(new CallToAction(story.id, story.title,
                story.tags, story.getSlidesCount(), story.lastIndex,
                link, cta));
        if (CallbackManager.getInstance().getCallToActionCallback() != null) {
            CallbackManager.getInstance().getCallToActionCallback().callToAction(story.id, story.title,
                    story.tags, story.getSlidesCount(), story.lastIndex,
                    link, ClickAction.GAME);
        }
        // OldStatisticManager.getInstance().addLinkOpenStatistic();
        if (CallbackManager.getInstance().getUrlClickCallback() != null) {
            CallbackManager.getInstance().getUrlClickCallback().onUrlClick(
                    link
            );
        } else {
            if (!InAppStoryService.isConnected()) {
                return;
            }
            host.tapOnLinkDefault(link);
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
        ScreensManager.getInstance().setTempShareStoryId(0);
        ScreensManager.getInstance().setTempShareId(null);
        if (ScreensManager.getInstance().getOldTempShareId() != null) {
            host.shareComplete(ScreensManager.getInstance().getOldTempShareId(), true);
        }
        ScreensManager.getInstance().setOldTempShareStoryId(0);
        ScreensManager.getInstance().setOldTempShareId(null);
    }

    void shareData(String id, String data) {
        ShareObject shareObj = JsonParser.fromJson(data, ShareObject.class);
        if (CallbackManager.getInstance().getShareCallback() != null) {
            CallbackManager.getInstance().getShareCallback()
                    .onShare(shareObj.getUrl(), shareObj.getTitle(), shareObj.getDescription(), id);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                ScreensManager.getInstance().setTempShareId(id);
                ScreensManager.getInstance().setTempShareStoryId(-1);
            } else {
                ScreensManager.getInstance().setOldTempShareId(id);
                ScreensManager.getInstance().setOldTempShareStoryId(-1);
            }
            host.shareDefault(shareObj);
        }
    }
}
