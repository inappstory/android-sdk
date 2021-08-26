package com.inappstory.sdk.game.reader;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.BuildConfig;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.eventbus.CsThreadMode;
import com.inappstory.sdk.game.loader.GameLoader;
import com.inappstory.sdk.game.loader.GameLoadCallback;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.network.Callback;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkHandler;
import com.inappstory.sdk.network.Request;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.stories.api.models.ShareObject;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.events.ShareCompleteEvent;
import com.inappstory.sdk.stories.outerevents.CloseGame;
import com.inappstory.sdk.stories.outerevents.FinishGame;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.views.IGameLoaderView;
import com.inappstory.sdk.stories.utils.SessionManager;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.stories.utils.StoryShareBroadcastReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.inappstory.sdk.network.JsonParser.toMap;

public class GameActivity extends AppCompatActivity {
    private String storyId;
    private String title;
    private String tags;
    private int index;
    private int slidesCount;

    private WebView webView;
    private ImageView loader;
    private View closeButton;
    private RelativeLayout loaderContainer;
    private IGameLoaderView loaderView;
    private View blackTop;
    private View blackBottom;
    private View baseContainer;
    private String path;
    private String resources;
    private String loaderPath;

    public static final int GAME_READER_REQUEST = 878;

    private boolean gameLoaded;
    private String gameConfig;
    private boolean closing = false;
    boolean showClose = true;

    @Override
    public void onBackPressed() {
        if (gameReaderGestureBack) {
            gameReaderGestureBack();
        } else {
            closeGame();
        }

    }

    void gameReaderGestureBack() {
        if (webView != null) {
            webView.evaluateJavascript("gameReaderGestureBack();", null);
        }
    }

    void updateUI() {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                closeButton.setVisibility(showClose ? View.VISIBLE : View.GONE);
                if (loaderContainer != null)
                    loaderContainer.setVisibility(View.GONE);
            }
        });
    }

    private void setViews() {
        webView = findViewById(R.id.gameWebview);
        webView.setBackgroundColor(Color.BLACK);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccess(true);
        loader = findViewById(R.id.loader);
        baseContainer = findViewById(R.id.draggable_frame);
        loaderContainer = findViewById(R.id.loaderContainer);
        blackTop = findViewById(R.id.blackTop);
        blackBottom = findViewById(R.id.blackBottom);
        if (AppearanceManager.getCommonInstance().csGameLoaderView() == null) {
            loaderView = new GameLoadProgressBar(GameActivity.this,
                    null,
                    android.R.attr.progressBarStyleHorizontal);
        } else {
            loaderView = AppearanceManager.getCommonInstance().csGameLoaderView();
        }
        if (Sizes.isTablet() && baseContainer != null) {
            baseContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeGame();
                }
            });
        }
        if (!Sizes.isTablet()) {
            if (blackBottom != null) {
                Point screenSize = Sizes.getScreenSize(GameActivity.this);
                final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) blackBottom.getLayoutParams();
                float realProps = screenSize.y / ((float) screenSize.x);
                float sn = 1.85f;
                if (realProps > sn) {
                    lp.height = (int) (screenSize.y - screenSize.x * sn) / 2;

                }

                blackBottom.setLayoutParams(lp);
                blackTop.setLayoutParams(lp);
                if (Build.VERSION.SDK_INT >= 28) {
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (getWindow() != null && getWindow().getDecorView().getRootWindowInsets() != null) {
                                DisplayCutout cutout = getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
                                if (cutout != null) {
                                    if (closeButton != null) {
                                        RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) closeButton.getLayoutParams();
                                        lp1.topMargin += Math.max(cutout.getSafeInsetTop() - lp.height, 0);
                                        closeButton.setLayoutParams(lp1);
                                    }
                                } else {

                                }
                            }
                        }
                    });
                }
            }
        }
        loaderContainer.addView(loaderView.getView());
        closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeGame();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHARE_EVENT && resultCode == RESULT_CANCELED) {
            closeGame();
        }
    }

    public static final int SHARE_EVENT = 909;

    @Override
    public void onResume() {
        ScreensManager.getInstance().setTempShareStoryId(0);
        ScreensManager.getInstance().setTempShareId(null);
        if (ScreensManager.getInstance().getOldTempShareId() != null) {
            shareComplete(ScreensManager.getInstance().getOldTempShareId(), true);
        }
        ScreensManager.getInstance().setOldTempShareStoryId(0);
        ScreensManager.getInstance().setOldTempShareId(null);
        super.onResume();
        resumeGame();
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void shareCompleteEvent(ShareCompleteEvent event) {
        shareComplete(event.getId(), event.isSuccess());
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseGame();
    }

    private void resumeGame() {
        if (webView != null) {
            webView.evaluateJavascript("resumeUI();", null);
        }
    }

    private void pauseGame() {
        if (webView != null) {
            webView.evaluateJavascript("pauseUI();", null);
        }
    }


    boolean gameReaderGestureBack = false;

    private void shareData(String id, String data) {
        ShareObject shareObj = JsonParser.fromJson(data, ShareObject.class);
        if (CallbackManager.getInstance().getShareCallback() != null) {
            CallbackManager.getInstance().getShareCallback()
                    .onShare(shareObj.getUrl(), shareObj.getTitle(), shareObj.getDescription(), id);
        } else {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, shareObj.getTitle());
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareObj.getUrl());
            sendIntent.setType("text/plain");
            PendingIntent pi = PendingIntent.getBroadcast(GameActivity.this, SHARE_EVENT,
                    new Intent(GameActivity.this, StoryShareBroadcastReceiver.class),
                    FLAG_UPDATE_CURRENT);
            Intent finalIntent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                finalIntent = Intent.createChooser(sendIntent, null, pi.getIntentSender());
                //finalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ScreensManager.getInstance().setTempShareId(id);
                ScreensManager.getInstance().setTempShareStoryId(-1);
                startActivityForResult(finalIntent, SHARE_EVENT);
            } else {
                finalIntent = Intent.createChooser(sendIntent, null);
                finalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                InAppStoryService.getInstance().getContext().startActivity(finalIntent);
                ScreensManager.getInstance().setOldTempShareId(id);
                ScreensManager.getInstance().setOldTempShareStoryId(-1);
            }
        }
    }

    public void shareComplete(String id, boolean success) {
        webView.loadUrl("javascript:(function(){share_complete(\"" + id + "\", " + success + ");})()");
    }

    private void getIntentValues() {
        path = getIntent().getStringExtra("gameUrl");
        resources = getIntent().getStringExtra("gameResources");
        storyId = getIntent().getStringExtra("storyId");
        index = getIntent().getIntExtra("slideIndex", 0);
        slidesCount = getIntent().getIntExtra("slidesCount", 0);
        title = getIntent().getStringExtra("title");
        tags = getIntent().getStringExtra("tags");
        gameConfig = getIntent().getStringExtra("gameConfig");
        gameConfig = gameConfig.replace("{{%sdkVersion}}", BuildConfig.VERSION_NAME);
        loaderPath = getIntent().getStringExtra("preloadPath");
    }

    public void checkAndSendRequest(final String method,
                                    final String path,
                                    final Map<String, String> headers,
                                    final Map<String, String> getParams,
                                    final String body,
                                    final String requestId,
                                    final String cb) {
        if (StatisticSession.needToUpdate()) {
            SessionManager.getInstance().useOrOpenSession(new OpenSessionCallback() {
                @Override
                public void onSuccess() {
                    sendRequest(method, path, headers, getParams, body, requestId, cb);
                }

                @Override
                public void onError() {

                }
            });
        } else {
            sendRequest(method, path, headers, getParams, body, requestId, cb);
        }
    }

    public void sendRequest(final String method,
                            final String path,
                            final Map<String, String> headers,
                            final Map<String, String> getParams,
                            final String body,
                            final String requestId,
                            final String cb) {

        new AsyncTask<Void, String, GameResponse>() {
            @Override
            protected GameResponse doInBackground(Void... voids) {
                try {
                    GameResponse s = GameNetwork.sendRequest(method, path, headers, getParams, body, requestId, GameActivity.this);
                    return s;
                } catch (Exception e) {
                    GameResponse response = new GameResponse();
                    response.status = 12002;
                    return response;
                }
            }

            @Override
            protected void onPostExecute(GameResponse result) {
                try {
                    JSONObject resultJson = new JSONObject();
                    resultJson.put("requestId", result.requestId);
                    resultJson.put("status", result.status);
                    resultJson.put("data", oldEscape(result.data));
                    try {
                        resultJson.put("headers", new JSONObject(result.headers));
                    } catch (Exception e) {
                    }
                    loadGameResponse(resultJson.toString(), cb);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void loadGameResponse(String gameResponse, String cb) {
        webView.evaluateJavascript(cb + "('" + gameResponse + "');", null);
    }

    private String oldEscape(String raw) {
        String escaped = JSONObject.quote(raw)
                .replaceFirst("^\"(.*)\"$", "$1")
                .replaceAll("\n", " ")
                .replaceAll("\r", " ");
        return escaped;
    }

    private void initWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setWebChromeClient(new WebChromeClient() {
            boolean init = false;

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 10) {
                    if (!init && gameConfig != null) {
                        init = true;
                        initGame(gameConfig);
                    }
                }
            }


            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.e("MyApplication", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                return super.onConsoleMessage(consoleMessage);
            }
        });
        webView.addJavascriptInterface(new WebAppInterface(GameActivity.this, index, storyId), "Android");
    }

    private void setLoader() {
        if (loaderPath != null && !loaderPath.isEmpty())
            ImageLoader.getInstance().displayImage(loaderPath, -1, loader, InAppStoryService.getInstance().getCommonCache());
        else
            loader.setBackgroundColor(Color.BLACK);
    }

    @Override
    protected void onDestroy() {
        CsEventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState1) {
        super.onCreate(savedInstanceState1);
        CsEventBus.getDefault().register(this);
        setContentView(R.layout.cs_activity_game);
        setViews();
        getIntentValues();
        ArrayList<WebResource> resourceList = new ArrayList<>();

        if (resources != null) {
            resourceList = JsonParser.listFromJson(resources, WebResource.class);
        }
        initWebView();
        setLoader();
        String[] urlParts = urlParts(path);
        GameLoader.getInstance().downloadAndUnzip(GameActivity.this, resourceList, path, urlParts[0], new GameLoadCallback() {
            @Override
            public void onLoad(String baseUrl, String data) {
                webView.loadDataWithBaseURL(baseUrl, data,
                        "text/html; charset=utf-8", "UTF-8", null);
            }

            @Override
            public void onError() {

            }

            @Override
            public void onProgress(int loadedSize, int totalSize) {
                int percent = (int) ((loadedSize * 100) / (totalSize));
                loaderView.setProgress(percent, 100);
            }
        });
    }

    private void closeGame() {
        if (closing) return;
        GameLoader.getInstance().terminate();
        closing = true;
        CsEventBus.getDefault().post(new CloseGame(Integer.parseInt(storyId), title, tags,
                slidesCount, index));
        if (gameLoaded) {
            webView.loadUrl("javascript:closeGameReader()");
        } else {
            gameCompleted(null);
        }
    }


    private String[] urlParts(String url) {
        String[] parts = url.split("/");
        String fName = parts[parts.length - 1].split("\\.")[0];
        return fName.split("_");
    }


    private void gameCompleted(String gameState) {
        try {
            Intent intent = new Intent();
            if (Sizes.isTablet()) {
                CsEventBus.getDefault().post(new GameCompleteEvent(
                        gameState,
                        Integer.parseInt(storyId),
                        index));
            } else {
                intent.putExtra("storyId", storyId);
                intent.putExtra("slideIndex", index);
                if (gameState != null)
                    intent.putExtra("gameState", gameState);
            }
            setResult(RESULT_OK, intent);
            finish();

        } catch (Exception e) {
            closing = false;
        }
    }


    private void initGame(String data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(data, null);
        } else {
            webView.loadUrl("javascript:" + data);
        }
    }

    private class WebAppInterface {
        Context mContext;
        int lindex;
        String storyId;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c, int lindex, String storyId) {
            this.lindex = lindex;
            this.storyId = storyId;
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */


        @JavascriptInterface
        public void gameLoaded(String data) {
            GameLoadedConfig config = JsonParser.fromJson(data, GameLoadedConfig.class);
            gameReaderGestureBack = config.backGesture;
            showClose = config.showClose;
            gameLoaded = true;
            updateUI();
        }

        @JavascriptInterface
        public void sendApiRequest(String data) {
            Log.e("gameJS", "sendApiRequest | " + data);
            GameRequestConfig config = JsonParser.fromJson(data, GameRequestConfig.class);
            Map<String, String> headers = null;
            if (config.headers != null && !config.headers.isEmpty()) {
                headers = toMap(config.headers);
            }
            Map<String, String> getParams = null;
            if (config.params != null && !config.params.isEmpty()) {
                getParams = toMap(config.params);
            }
            checkAndSendRequest(config.method, config.url, headers, getParams,
                    config.data, config.id, config.cb);
        }

        @JavascriptInterface
        public void gameComplete(String data) {
            gameCompleted(data);
        }

        @JavascriptInterface
        public void gameComplete(String data, String eventData) {
            CsEventBus.getDefault().post(new FinishGame(Integer.parseInt(storyId), title, tags,
                    slidesCount, index, eventData));
            gameCompleted(data);
        }

        @JavascriptInterface
        public void gameStatisticEvent(String name, String data) {
            StatisticManager.getInstance().sendGameEvent(name, data);
        }

        @JavascriptInterface
        public void emptyLoaded() {
        }


        @JavascriptInterface
        public void share(String id, String data) {
            shareData(id, data);
        }

    }


}
