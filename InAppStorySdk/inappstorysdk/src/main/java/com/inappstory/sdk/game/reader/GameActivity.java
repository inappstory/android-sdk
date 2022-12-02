package com.inappstory.sdk.game.reader;

import static com.inappstory.sdk.share.ShareManager.SHARE_EVENT;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.BuildConfig;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.share.JSShareModel;
import com.inappstory.sdk.share.ShareManager;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderType;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.outerevents.CloseGame;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.views.IASWebView;
import com.inappstory.sdk.stories.ui.views.IGameLoaderView;
import com.inappstory.sdk.stories.utils.AudioModes;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.utils.ZipLoadCallback;
import com.inappstory.sdk.utils.ZipLoader;

import java.util.ArrayList;
import java.util.Map;

public class GameActivity extends AppCompatActivity {
    private IASWebView webView;
    private ImageView loader;
    private View closeButton;
    private View webViewContainer;
    private RelativeLayout loaderContainer;
    private IGameLoaderView loaderView;
    private View blackTop;
    private View blackBottom;
    private View baseContainer;
    GameManager manager;
    private PermissionRequest audioRequest;
    private boolean isFullscreen = false;

    public static final int GAME_READER_REQUEST = 878;

    private boolean closing = false;
    boolean showClose = true;


    @Override
    public void onBackPressed() {
        if (onBackPressedLocked) return;
        if (gameReaderGestureBack) {
            gameReaderGestureBack();
        } else {
            closeGame();
        }

    }

    public void close() {
        closeGame();
    }

    void gameReaderGestureBack() {
        if (webView != null) {
            if (manager.gameLoaded) {
                webView.evaluateJavascript("gameReaderGestureBack();", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        if (!s.equals("true"))
                            closeGame();
                    }
                });
            } else {
                gameCompleted(null, null);
            }
        }
    }

    void showGoods(final String skusString, final String widgetId) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ScreensManager.getInstance().showGoods(skusString, GameActivity.this, new ShowGoodsCallback() {
                            @Override
                            public void onPause() {
                                pauseGame();
                            }

                            @Override
                            public void onResume(String widgetId) {
                                goodsWidgetComplete(widgetId);
                                resumeGame();
                            }

                            @Override
                            public void onEmptyResume(String widgetId) {
                                goodsWidgetComplete(widgetId);
                            }
                        }, true, widgetId,
                        Integer.parseInt(manager.storyId),
                        manager.index, manager.feedId);
            }
        });
    }

    void goodsWidgetComplete(String widgetId) {
        if (manager.gameLoaded) {
            webView.evaluateJavascript("goodsWidgetComplete(\"" + widgetId + "\");", null);
        }
    }

    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(final int focusChange) {
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            gameReaderAudioFocusChange(focusChange);
                        }
                    });
                }
            };

    void gameReaderAudioFocusChange(int focusChange) {
        if (webView != null) {
            webView.evaluateJavascript("('handleAudioFocusChange' in window) && handleAudioFocusChange(" + focusChange + ");", null);
        }
    }


    void updateUI() {
        ProfilingManager.getInstance().setReady("game_" + manager.storyId + "_" + manager.index);
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                closeButton.setVisibility(showClose ? View.VISIBLE : View.GONE);
                hideView(loaderContainer);
            }
        });

    }

    private boolean onBackPressedLocked = false;


    void hideView(final View view) {
        if (view == null) return;
        onBackPressedLocked = true;
        view.animate().alpha(0).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
                view.setAlpha(1f);
                onBackPressedLocked = false;
            }
        });
    }

    private void setViews() {
        webView = findViewById(R.id.gameWebview);
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

        closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeGame();
            }
        });
        webViewContainer = findViewById(R.id.webViewContainer);
        //if (!Sizes.isTablet()) {
        if (!isFullscreen) {
            if (Build.VERSION.SDK_INT >= 28) {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        WindowInsets insets = getWindow().getDecorView().getRootWindowInsets();
                        if (insets != null) {
                            DisplayCutout cutout = insets.getDisplayCutout();
                            if (cutout != null && webViewContainer != null) {
                                LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) webViewContainer.getLayoutParams();
                                lp1.topMargin += Math.max(cutout.getSafeInsetTop(), 0);
                                webViewContainer.setLayoutParams(lp1);
                            }
                            if (Sizes.isTablet()) {
                                View gameContainer = findViewById(R.id.gameContainer);
                                if (gameContainer != null) {
                                    int dialogHeight = getResources().getDimensionPixelSize(R.dimen.cs_tablet_height);
                                    Point size = Sizes.getScreenSize();
                                    size.y -= (insets.getSystemWindowInsetTop() +
                                            insets.getSystemWindowInsetBottom());
                                    dialogHeight = Math.min(dialogHeight, size.y);
                                    gameContainer.getLayoutParams().width = Math.round(dialogHeight / 1.5f);
                                    gameContainer.getLayoutParams().height = dialogHeight;
                                    gameContainer.requestLayout();
                                }
                            }
                        }
                    }
                });
            }
            /*if (Sizes.isTablet()) {
                getWindow().getAttributes().flags = getWindow().getAttributes().flags |
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            }*/
            /* if (blackBottom != null) {
                Point screenSize = Sizes.getScreenSize(GameActivity.this);
                final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) blackBottom.getLayoutParams();
                float realProps = screenSize.y / ((float) screenSize.x);
                float sn = 1.85f;
                if (realProps > sn) {
                    lp.height = (int) (screenSize.y - screenSize.x * sn) / 2;

                }

                //    blackBottom.setLayoutParams(lp);
                //    blackTop.setLayoutParams(lp);

            }*/
        } else {
            /*baseContainer.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
            baseContainer.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
            baseContainer.requestLayout();*/
            int systemUiVisibility = 0;
            int navigationBarColor = Color.TRANSPARENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                systemUiVisibility = systemUiVisibility | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            systemUiVisibility = systemUiVisibility |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;/* |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN;*/
            getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
            getWindow().getAttributes().flags = getWindow().getAttributes().flags |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(navigationBarColor);
            }

        }

        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= 28) {
                    if (getWindow() != null) {
                        WindowInsets windowInsets = getWindow().getDecorView().getRootWindowInsets();
                        if (windowInsets != null) {
                            ((RelativeLayout.LayoutParams) closeButton.getLayoutParams()).topMargin =
                                    windowInsets.getSystemWindowInsetTop();
                            closeButton.requestLayout();
                        }
                    }
                }
                closeButton.setVisibility(View.VISIBLE);
            }
        });
        // }
        loaderContainer.addView(loaderView.getView());
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    public void tapOnLinkDefault(String link) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setData(Uri.parse(link));
        startActivity(i);
        overridePendingTransition(R.anim.popup_show, R.anim.empty_animation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHARE_EVENT) {
            String id = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                id = ScreensManager.getInstance().getTempShareId();
            } else {
                id = ScreensManager.getInstance().getOldTempShareId();
            }
            shareComplete(id, resultCode == RESULT_OK);
        }
    }

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 101;

    @Override
    public void onResume() {
        manager.onResume();
        super.onResume();
        resumeGame();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_RECORD_AUDIO: {
                if (audioRequest != null && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    audioRequest.grant(audioRequest.getResources());
                } else {
                    audioRequest.deny();
                }
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void askForPermission(String origin, String permission, int requestCode) {

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(GameActivity.this,
                    permission)) {
                ActivityCompat.requestPermissions(GameActivity.this,
                        new String[]{permission},
                        requestCode);
            } else {

                ActivityCompat.requestPermissions(GameActivity.this,
                        new String[]{permission},
                        requestCode);
            }
        } else {
            if (audioRequest != null)
                audioRequest.grant(audioRequest.getResources());
        }
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

    public void shareDefault(JSShareModel shareObject) {
        new ShareManager().shareDefault(GameActivity.this, shareObject);
    }

    public void shareComplete(String id, boolean success) {
        webView.loadUrl("javascript:(function(){share_complete(\"" + id + "\", " + success + ");})()");
    }

    private boolean getIntentValues() {
        manager.path = getIntent().getStringExtra("gameUrl");
        if (manager.path == null) {
            finish();
            return false;
        }
        manager.observableId = getIntent().getStringExtra("observableId");
        manager.resources = getIntent().getStringExtra("gameResources");
        manager.storyId = getIntent().getStringExtra("storyId");
        manager.index = getIntent().getIntExtra("slideIndex", 0);
        manager.slidesCount = getIntent().getIntExtra("slidesCount", 0);
        manager.title = getIntent().getStringExtra("title");
        manager.tags = getIntent().getStringExtra("tags");
        manager.gameConfig = getIntent().getStringExtra("gameConfig");
        if (manager.gameConfig != null) {
            if (manager.gameConfig.contains("{{%sdkVersion}}"))
                manager.gameConfig = manager.gameConfig.replace("{{%sdkVersion}}", BuildConfig.VERSION_NAME);

            if (manager.gameConfig.contains("{{%sdkConfig}}") || manager.gameConfig.contains("\"{{%sdkConfig}}\"")) {
                String replacedConfig = generateJsonConfig();
                manager.gameConfig = manager.gameConfig.replace("\"{{%sdkConfig}}\"", replacedConfig);
            } else if (manager.gameConfig.contains("{{%sdkPlaceholders}}") || manager.gameConfig.contains("\"{{%sdkPlaceholders}}\"")) {
                String replacedPlaceholders = generateJsonPlaceholders();
                manager.gameConfig = manager.gameConfig.replace("\"{{%sdkPlaceholders}}\"", replacedPlaceholders);
                manager.gameConfig = manager.gameConfig.replace("{{%sdkPlaceholders}}", replacedPlaceholders);
            }
        }
        manager.loaderPath = getIntent().getStringExtra("preloadPath");
        return true;
    }

    private String generateJsonConfig() {
        GameConfigOptions options = new GameConfigOptions();
        options.fullScreen = isFullscreen;
        options.apiBaseUrl = NetworkClient.getInstance().getBaseUrl();
        options.appPackageId = NetworkClient.getInstance().getBaseUrl();
        int orientation = getResources().getConfiguration().orientation;
        options.screenOrientation =
                (orientation == Configuration.ORIENTATION_LANDSCAPE) ? "landscape" : "portrait";
        options.userAgent = NetworkClient.getUAString(this);
        String appPackageName = "";
        try {
            appPackageName = getPackageManager().getPackageInfo(getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        options.appPackageId = appPackageName;
        options.sdkVersion = BuildConfig.VERSION_NAME;
        options.apiKey = InAppStoryManager.getInstance().getApiKey();
        options.sessionId = CachedSessionData.getInstance(this).sessionId;
        options.deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        options.placeholders = generatePlaceholders();
        SafeAreaInsets insets = new SafeAreaInsets();
        if (Build.VERSION.SDK_INT >= 28) {
            if (getWindow() != null) {
                WindowInsets windowInsets = getWindow().getDecorView().getRootWindowInsets();
                if (windowInsets != null) {
                    insets.top = Sizes.pxToDpExt(windowInsets.getSystemWindowInsetTop(), this);
                    insets.bottom = Sizes.pxToDpExt(windowInsets.getSystemWindowInsetBottom(), this);
                    insets.left = Sizes.pxToDpExt(windowInsets.getSystemWindowInsetLeft(), this);
                    insets.right = Sizes.pxToDpExt(windowInsets.getSystemWindowInsetRight(), this);
                }
            }
        }
        options.safeAreaInsets = insets;
        try {
            return JsonParser.getJson(options);
        } catch (Exception e) {
            return "";
        }
    }


    private String generateJsonPlaceholders() {
        String st = "[]";
        try {
            st = JsonParser.getJson(generatePlaceholders());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return st;
    }

    private ArrayList<GameDataPlaceholder> generatePlaceholders() {
        Map<String, String> textPlaceholders =
                InAppStoryManager.getInstance().getPlaceholders();
        Map<String, ImagePlaceholderValue> imagePlaceholders =
                InAppStoryManager.getInstance().getImagePlaceholdersValues();
        ArrayList<GameDataPlaceholder> gameDataPlaceholders = new ArrayList<GameDataPlaceholder>();
        for (Map.Entry<String, String> entry : textPlaceholders.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null)
                gameDataPlaceholders.add(new GameDataPlaceholder(
                        "text",
                        entry.getKey(),
                        entry.getValue()));
        }
        for (Map.Entry<String, ImagePlaceholderValue> entry : imagePlaceholders.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null
                    && entry.getValue().getType() == ImagePlaceholderType.URL)
                gameDataPlaceholders.add(new GameDataPlaceholder(
                        "image",
                        entry.getKey(),
                        entry.getValue().getUrl()));
        }
        return gameDataPlaceholders;
    }


    void loadJsApiResponse(String gameResponse, String cb) {
        webView.evaluateJavascript(cb + "('" + gameResponse + "');", null);
    }


    private void initWebView() {

        webView.setWebChromeClient(new WebChromeClient() {
            boolean init = false;

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 10) {
                    if (!init && manager.gameConfig != null) {
                        init = true;
                        initGame(manager.gameConfig);
                    }
                }
            }


            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if (manager != null && webView != null) {
                    webView.sendWebConsoleLog(consoleMessage,
                            manager.storyId,
                            manager.index);
                }
                Log.d("InAppStory_SDK_Game", consoleMessage.messageLevel().name() + ": "
                        + consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                return super.onConsoleMessage(consoleMessage);
            }


            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                // super.onPermissionRequest(request);
                for (String permission : request.getResources()) {
                    switch (permission) {
                        case "android.webkit.resource.AUDIO_CAPTURE": {
                            audioRequest = request;
                            askForPermission(request.getOrigin().toString(),
                                    Manifest.permission.RECORD_AUDIO,
                                    PERMISSIONS_REQUEST_RECORD_AUDIO);
                            break;
                        }
                    }
                }
            }
        });
        webView.addJavascriptInterface(new GameJSInterface(GameActivity.this,
                manager.index, manager.storyId, manager), "Android");
    }


    private void setLoader() {
        if (manager.loaderPath != null && !manager.loaderPath.isEmpty()
                && InAppStoryService.isNotNull())
            ImageLoader.getInstance().displayImage(manager.loaderPath, -1, loader,
                    InAppStoryService.getInstance().getCommonCache());
        else
            loader.setBackgroundColor(Color.BLACK);
    }

    @Override
    protected void onDestroy() {
        if (ScreensManager.getInstance().currentGameActivity == this)
            ScreensManager.getInstance().currentGameActivity = null;
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        GameActivity.this.overridePendingTransition(R.anim.empty_animation, R.anim.alpha_fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState1) {
        super.onCreate(savedInstanceState1);
        ScreensManager.getInstance().currentGameActivity = this;
        setContentView(R.layout.cs_activity_game);
        GameScreenOptions options =
                JsonParser.fromJson(getIntent().getStringExtra("options"), GameScreenOptions.class);
        isFullscreen = options != null && options.fullScreen;
        if (InAppStoryManager.getInstance() == null) {
            finish();
            return;
        }
        manager = new GameManager(this);
        manager.callback = new ZipLoadCallback() {
            @Override
            public void onLoad(String baseUrl, String filePath, String data) {
                manager.gameLoaded = true;
                webView.loadDataWithBaseURL(baseUrl, webView.setDir(data),
                        "text/html; charset=utf-8", "UTF-8",
                        null);
            }

            @Override
            public void onError() {

            }

            @Override
            public void onProgress(int loadedSize, int totalSize) {
                int percent = (int) ((loadedSize * 100) / (totalSize));
                loaderView.setProgress(percent, 100);
            }
        };
        setViews();
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Log.e("jsonConfig", generateJsonConfig());
                if (getIntentValues()) {
                    initWebView();
                    setLoader();
                    manager.loadGame();
                }
            }
        }, 300);

      /*  new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                testJS();
            }
        }, 10000);*/
    }

    private void closeGame() {
        if (closing) return;
        if (manager == null || manager.storyId == null) {
            finish();
            return;
        }
        ZipLoader.getInstance().terminate();
        closing = true;
        CsEventBus.getDefault().post(new CloseGame(Integer.parseInt(manager.storyId),
                manager.title, manager.tags,
                manager.slidesCount, manager.index));
        if (CallbackManager.getInstance().getGameCallback() != null) {
            CallbackManager.getInstance().getGameCallback().closeGame(
                    Integer.parseInt(manager.storyId),
                    manager.title, manager.tags,
                    manager.slidesCount, manager.index);
        }
        if (manager.gameLoaded) {
            webView.evaluateJavascript("closeGameReader();", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    if (!s.equals("true"))
                        gameCompleted(null, null);
                }
            });
        } else {
            gameCompleted(null, null);
        }
    }

    void setAudioManagerMode(String mode) {
        AudioManager audioManager = (AudioManager)
                getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioModes.getModeVal(mode));
    }


    void gameCompleted(String gameState, String link) {
        try {
            Intent intent = new Intent();
            if (Sizes.isTablet()) {
                String observableUID = manager.observableId;
                if (observableUID != null) {
                    MutableLiveData<GameCompleteEvent> liveData =
                            ScreensManager.getInstance().getGameObserver(observableUID);
                    if (liveData != null) {
                        liveData.postValue(new GameCompleteEvent(
                                gameState,
                                Integer.parseInt(manager.storyId),
                                manager.index));
                    }
                }
            } else {
                intent.putExtra("storyId", manager.storyId);
                intent.putExtra("slideIndex", manager.index);
                if (gameState != null)
                    intent.putExtra("gameState", gameState);
            }
            if (link != null)
                manager.tapOnLink(link);
            setResult(RESULT_OK, intent);
            finish();
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            closing = false;
        }
    }


    private void initGame(String data) {
        webView.evaluateJavascript(data, null);
    }

    void testJS() {
        webView.evaluateJavascript("(async () => await fetch('./581568cfc6e82597459a.jpeg', {headers: {Accept: \"image/jpg\"} }))()", null);
    }

}
