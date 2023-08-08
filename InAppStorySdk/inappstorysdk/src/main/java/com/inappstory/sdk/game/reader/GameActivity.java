package com.inappstory.sdk.game.reader;

import static com.inappstory.sdk.share.IASShareManager.SHARE_EVENT;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
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
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.inappstory.sdk.game.cache.GameCacheManager;
import com.inappstory.sdk.game.cache.GameLoadCallback;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.inner.share.InnerShareFilesPrepare;
import com.inappstory.sdk.inner.share.ShareFilesPrepareCallback;
import com.inappstory.sdk.lrudiskcache.FileManager;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.share.IASShareData;
import com.inappstory.sdk.share.IASShareManager;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.api.models.GameCenterData;
import com.inappstory.sdk.stories.api.models.GameSplashScreen;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderType;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FileLoadProgressCallback;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.callbacks.GameDownloadCallback;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.outercallbacks.game.GameLoadedCallback;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.ui.OverlapFragmentObserver;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.views.IASWebView;
import com.inappstory.sdk.stories.ui.views.IASWebViewClient;
import com.inappstory.sdk.stories.ui.views.IGameLoaderView;
import com.inappstory.sdk.stories.ui.views.IGameReaderLoaderView;
import com.inappstory.sdk.stories.ui.views.IProgressLoaderView;
import com.inappstory.sdk.stories.utils.AudioModes;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.stories.utils.StoryShareBroadcastReceiver;
import com.inappstory.sdk.utils.StringsUtils;
import com.inappstory.sdk.utils.ZipLoadCallback;
import com.inappstory.sdk.utils.ZipLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class GameActivity extends AppCompatActivity implements OverlapFragmentObserver {


    GameCacheManager gameCacheManager = InAppStoryService.getInstance().gameCacheManager();
    private IASWebView webView;
    private ImageView loader;
    private View closeButton;
    private View webViewContainer;
    private RelativeLayout loaderContainer;
    private IProgressLoaderView loaderView;
    private View baseContainer;

    private ImageView refreshGame;
    GameManager manager;
    private PermissionRequest audioRequest;
    private boolean isFullscreen = false;

    public static final int GAME_READER_REQUEST = 878;

    private boolean closing = false;
    boolean showClose = true;

    private boolean onBackPressedLocked = false;

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

    private void hideView(final View view) {
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

    private void changeView(final View view1, final View view2) {
        if (view1 == null || view2 == null) return;
        onBackPressedLocked = true;
        view1.setAlpha(0f);
        view1.setVisibility(View.VISIBLE);
        view2.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view1.setAlpha((float) animation.getAnimatedValue());
                view2.setAlpha(1f - (float) animation.getAnimatedValue());
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view2.setVisibility(View.GONE);
                onBackPressedLocked = false;
            }
        });
        valueAnimator.start();
    }

    void showGoods(final String skusString, final String widgetId) {
        final GameStoryData dataModel = getStoryDataModel();
        if (dataModel == null) return;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ScreensManager.getInstance().showGoods(
                        skusString,
                        GameActivity.this,
                        new ShowGoodsCallback() {
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
                        },
                        true,
                        widgetId,
                        dataModel.storyId,
                        dataModel.slideIndex,
                        dataModel.feedId
                );
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
        GameStoryData dataModel = getStoryDataModel();
        if (dataModel != null)
            ProfilingManager.getInstance().setReady("game_init" + dataModel.storyId + "_" + dataModel.slideIndex);
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                closeButton.setVisibility(showClose ? View.VISIBLE : View.GONE);
                refreshGame.removeCallbacks(showRefresh);
                hideView(loaderContainer);
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setViews() {
        webView = findViewById(R.id.gameWebview);
        loader = findViewById(R.id.loader);
        baseContainer = findViewById(R.id.draggable_frame);
        loaderContainer = findViewById(R.id.loaderContainer);
        refreshGame = findViewById(R.id.gameRefresh);
        refreshGame.setImageDrawable(getResources().getDrawable(AppearanceManager.getCommonInstance().csRefreshIcon()));
        IGameReaderLoaderView gameReaderLoaderView = AppearanceManager.getCommonInstance().csGameReaderLoaderView();
        IGameLoaderView gameLoaderView = AppearanceManager.getCommonInstance().csGameLoaderView();
        if (gameReaderLoaderView != null) {
            loaderView = gameReaderLoaderView;
            customLoaderView = gameReaderLoaderView.getView(GameActivity.this);
        } else if (gameLoaderView != null) {
            loaderView = gameLoaderView;
            customLoaderView = gameLoaderView.getView();
        } else {
            GameReaderLoadProgressBar loadProgressBar = new GameReaderLoadProgressBar(GameActivity.this);
            loaderView = loadProgressBar;
            customLoaderView = loadProgressBar;
        }
        loaderView.setIndeterminate(true);
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

        initWebView();
        if (Sizes.isTablet()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.BLACK);
            }
        }

        refreshGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeView(customLoaderView, refreshGame);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadGame();
                    }
                }, 500);

            }
        });
        loaderContainer.addView(customLoaderView);

    }

    Boolean forceFullscreen = false;

    private void setLayout() {
        if (isFullscreen) {
            int systemUiVisibility = 0;
            int navigationBarColor = Color.TRANSPARENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                systemUiVisibility = systemUiVisibility | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            systemUiVisibility = systemUiVisibility |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

            getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
            getWindow().getAttributes().flags = getWindow().getAttributes().flags |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(navigationBarColor);
            }
        }
    }

    private void checkInsets() {
        if (Build.VERSION.SDK_INT >= 28) {
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (getWindow() != null) {
                        WindowInsets windowInsets = getWindow().getDecorView().getRootWindowInsets();
                        if (windowInsets != null) {
                            ((RelativeLayout.LayoutParams) closeButton.getLayoutParams()).topMargin =
                                    Math.max(windowInsets.getSystemWindowInsetTop(), Sizes.dpToPxExt(16));
                            closeButton.requestLayout();
                        }
                        if (Sizes.isTablet()) {

                            View gameContainer = findViewById(R.id.gameContainer);
                            if (gameContainer != null) {
                                Point size = Sizes.getScreenSize();
                                size.y -= (windowInsets.getSystemWindowInsetTop() +
                                        windowInsets.getSystemWindowInsetBottom());
                                gameContainer.getLayoutParams().height = size.y;
                                gameContainer.getLayoutParams().width = (int) (size.y / 1.5f);
                                gameContainer.requestLayout();
                            }
                        }
                    }

                    closeButton.setVisibility(View.VISIBLE);
                }
            });
        } else {
            closeButton.setVisibility(View.VISIBLE);
        }
    }

    public void tapOnLinkDefault(String link) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setData(Uri.parse(link));
            startActivity(i);
            overridePendingTransition(R.anim.popup_show, R.anim.empty_animation);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Can't open this url: " + link, Toast.LENGTH_SHORT).show();
        }
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
        super.onResume();
        if (!shareViewIsShown) {
            manager.onResume();
            resumeGame();
        }
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

    public void share(InnerShareData shareObject) {
        final IASShareData shareData = new IASShareData(
                shareObject.getText(), shareObject.getPayload()
        );
        if (!shareObject.getFiles().isEmpty()) {
            new InnerShareFilesPrepare().prepareFiles(this, new ShareFilesPrepareCallback() {
                @Override
                public void onPrepared(List<String> files) {
                    shareData.files = files;
                    shareCustomOrDefault(shareData);
                }
            }, shareObject.getFiles());
        } else {
            shareCustomOrDefault(shareData);
        }

    }

    private void shareCustomOrDefault(IASShareData shareObject) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null)
            service.isShareProcess(false);
        if (CallbackManager.getInstance().getShareCallback() != null) {
            int storyId = -1;
            int slideIndex = 0;
            GameStoryData dataModel = getStoryDataModel();
            if (dataModel != null) {
                storyId = dataModel.storyId;
                slideIndex = dataModel.slideIndex;
            }
            ScreensManager.getInstance().openOverlapContainerForShare(
                    this, this, null, storyId, slideIndex, shareObject
            );
        } else {
            new IASShareManager().shareDefault(
                    StoryShareBroadcastReceiver.class,
                    GameActivity.this,
                    shareObject
            );
        }
    }

    public void shareComplete(String id, boolean success) {
        webView.loadUrl("javascript:(function(){share_complete(\"" + id + "\", " + success + ");})()");
    }

    @Override
    public void finish() {
        super.finish();
        GameActivity.this.overridePendingTransition(R.anim.empty_animation, R.anim.alpha_fade_out);
    }

    private void replaceGameInstanceStorageData(Map<String, Object> serverData) throws JSONException {
        if (serverData == null || serverData.isEmpty()) return;
        String storageId = "gameInstance_" + manager.gameCenterId
                + "__" + InAppStoryService.getInstance().getUserId();
        String localStringData = KeyValueStorage.getString(storageId);
        if (localStringData == null) {
            KeyValueStorage.saveString(storageId, JsonParser.mapToJsonString(serverData));
        } else {
            Map<String, Object> localData = JsonParser.toObjectMap(new JSONObject(localStringData));
            HashMap<String, Object> newData = new HashMap<>(localData);
            for (String key : serverData.keySet()) {
                newData.put(key, serverData.get(key));
            }
            KeyValueStorage.saveString(storageId, JsonParser.mapToJsonString(newData));
        }
    }

    boolean hasSplashFile = false;

    private void checkIntentValues(final GameLoadedCallback callback) {
        manager.gameCenterId = getIntent().getStringExtra("gameId");
        manager.path = getIntent().getStringExtra("gameUrl");
        manager.dataModel = getStoryDataModel();
        if (manager.path == null) {
            if (manager.gameCenterId == null) {
                callback.complete(false, null);
                finish();
                return;
            }
            String splashPath = KeyValueStorage.getString("gameInstanceSplash_" + manager.gameCenterId);
            if (splashPath != null) {
                File splash = new File(splashPath);
                hasSplashFile = splash.exists();
                setLoader(splash);
            }
            downloadGame();
        } else {
            GameScreenOptions options =
                    JsonParser.fromJson(getIntent().getStringExtra("options"), GameScreenOptions.class);
            isFullscreen = options != null && options.fullScreen;
            if (forceFullscreen != null)
                isFullscreen = forceFullscreen;
            manager.resources = getIntent().getStringExtra("gameResources");
            manager.gameConfig = getIntent().getStringExtra("gameConfig");
            manager.splashImagePath = getIntent().getStringExtra("splashImagePath");
            replaceConfigs();
            setLoaderOld();
            callback.complete(true, null);
        }
    }

    private void downloadSplash(final GameSplashScreen splashScreen) {
        boolean needToDownload = URLUtil.isValidUrl(splashScreen.url);
        final String oldSplashPath = KeyValueStorage.getString("gameInstanceSplash_" + manager.gameCenterId);
        if (oldSplashPath != null) {
            File splash = new File(oldSplashPath);
            if (splash.exists()) {
                if (FileManager.checkShaAndSize(splash, splashScreen.size, splashScreen.sha1))
                    needToDownload = false;
            }
            setLoader(splash);
        }
        if (needToDownload) {
            Downloader.downloadFileBackground(splashScreen.url, InAppStoryService.getInstance().getCommonCache(), new FileLoadProgressCallback() {
                @Override
                public void onProgress(int loadedSize, int totalSize) {

                }

                @Override
                public void onSuccess(File file) {
                    if (file != null && file.exists()) {
                        if (FileManager.checkShaAndSize(file, splashScreen.size, splashScreen.sha1)) {
                            KeyValueStorage.saveString("gameInstanceSplash_" + manager.gameCenterId, file.getAbsolutePath());
                            if (!hasSplashFile) {
                                setLoader(file);
                            } else {
                                if (oldSplashPath != null) {
                                    File splash = new File(oldSplashPath);
                                    if (splash.exists()) {
                                        splash.delete();
                                    }
                                    setLoader(splash);
                                }
                            }
                        } else {
                            file.delete();
                        }
                    }
                }

                @Override
                public void onError() {

                }
            });
        }
    }

    private void downloadGame() {
        downloadGame(
                manager.gameCenterId,
                new GameDownloadCallback() {
                    @Override
                    public void complete(GameCenterData gameCenterData) {
                        if (gameCenterData.splashScreen != null)
                            downloadSplash(gameCenterData.splashScreen);
                        try {
                            replaceGameInstanceStorageData(gameCenterData.instanceUserData);
                        } catch (JSONException ignored) {

                        }
                        manager.resources = getIntent().getStringExtra("gameResources");
                        manager.gameConfig = gameCenterData.initCode;
                        manager.path = gameCenterData.url;
                        try {
                            GameScreenOptions options = gameCenterData.options;
                            manager.resources = JsonParser.getJson(gameCenterData.resources);
                            isFullscreen = options != null && options.fullScreen;
                            if (forceFullscreen != null)
                                isFullscreen = forceFullscreen;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        replaceConfigs();
                        gameLoadedCallback.complete(true, gameCenterData);
                    }

                    @Override
                    public void error() {

                        gameLoadedCallback.complete(false, null);
                    }
                }
        );
    }

    private void replaceConfigs() {
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
    }

    private String generateJsonConfig() {
        GameConfigOptions options = new GameConfigOptions();
        options.fullScreen = isFullscreen;
        options.apiBaseUrl = NetworkClient.getInstance().getBaseUrl();
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
        String language;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            language = Locale.getDefault().toLanguageTag();
        } else {
            language = Locale.getDefault().getLanguage();
        }
        options.lang = language;
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
        if (manager.gameCenterId != null)
            options.gameInstanceId = manager.gameCenterId;
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
        final GameStoryData dataModel = getStoryDataModel();
        webView.setWebViewClient(new IASWebViewClient());
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

                if (dataModel != null && webView != null) {
                    webView.sendWebConsoleLog(consoleMessage,
                            Integer.toString(dataModel.storyId),
                            dataModel.slideIndex);
                }
                Log.d("InAppStory_SDK_Game", "Console: " + consoleMessage.messageLevel().name() + ": "
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
        webView.addJavascriptInterface(
                new GameJSInterface(
                        GameActivity.this,
                        manager
                ),
                "Android"
        );
    }

    private void downloadGame(
            final String gameId,
            final GameDownloadCallback callback
    ) {
        gameCacheManager.getGame(gameId, new GameLoadCallback() {
            @Override
            public void onSuccess(GameCenterData gameCenterData) {
                if (callback != null) callback.complete(gameCenterData);
            }

            @Override
            public void onError() {
                if (callback != null) callback.error();
            }
        });

    }

    private void setLoader(File splashFile) {
        if (splashFile == null || !splashFile.exists()) {
            loader.setBackgroundColor(Color.BLACK);
        } else {
            ImageLoader.getInstance().displayImage(splashFile.getAbsolutePath(), -1, loader);
        }
    }

    private void setLoaderOld() {
        if (manager.splashImagePath != null && !manager.splashImagePath.isEmpty()
                && InAppStoryService.isNotNull())
            ImageLoader.getInstance().displayImage(manager.splashImagePath, -1, loader,
                    InAppStoryService.getInstance().getCommonCache());
        else
            loader.setBackgroundColor(Color.BLACK);
    }

    @Override
    protected void onDestroy() {
        refreshGame.removeCallbacks(showRefresh);
        if (ScreensManager.getInstance().currentGameActivity == this)
            ScreensManager.getInstance().currentGameActivity = null;
        super.onDestroy();
    }


    Runnable showRefresh = new Runnable() {
        @Override
        public void run() {
            changeView(refreshGame, customLoaderView);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState1) {
        super.onCreate(savedInstanceState1);
        setContentView(R.layout.cs_activity_game);
        if (InAppStoryManager.isNull()) {
            finish();
            return;
        }
        ScreensManager.getInstance().currentGameActivity = this;
        manager = new GameManager(this);
        manager.callback = new ZipLoadCallback() {
            @Override
            public void onLoad(String baseUrl, String data) {
                manager.gameLoaded = true;
                webView.loadDataWithBaseURL(baseUrl, webView.setDir(data),
                        "text/html; charset=utf-8", "UTF-8",
                        null);
                refreshGame.postDelayed(showRefresh, 5000);
            }

            @Override
            public void onError() {
                refreshGame.post(showRefresh);
            }

            @Override
            public void onProgress(int loadedSize, int totalSize) {
                int percent = (int) ((loadedSize * 100) / (totalSize));
                loaderView.setProgress(percent, 100);
            }
        };
        setViews();
        checkInsets();
        checkIntentValues(gameLoadedCallback);
    }

    GameLoadedCallback gameLoadedCallback = new GameLoadedCallback() {
        @Override
        public void complete(boolean success, final GameCenterData data) {
            if (success) {
                setLayout();
                loaderView.setIndeterminate(false);
                manager.loadGame(data);
            } else {
                closeButton.setVisibility(View.VISIBLE);
                GameStoryData dataModel = getStoryDataModel();
                if (CallbackManager.getInstance().getGameReaderCallback() != null) {
                    CallbackManager.getInstance().getGameReaderCallback().gameLoadError(
                            dataModel,
                            getIntent().getStringExtra("gameId")
                    );
                }
                webView.post(showRefresh);
            }
        }
    };

    View customLoaderView = null;


    private void closeGame() {
        if (closing) return;
        GameStoryData dataModel = getStoryDataModel();
        ZipLoader.getInstance().terminate();
        if (manager == null) {
            finish();
            return;
        }
        closing = true;
        if (dataModel != null) {
            if (CallbackManager.getInstance().getGameCallback() != null) {
                CallbackManager.getInstance().getGameCallback().closeGame(
                        dataModel.storyId,
                        StringsUtils.getNonNull(dataModel.title),
                        StringsUtils.getNonNull(dataModel.tags),
                        dataModel.slidesCount,
                        dataModel.slideIndex
                );
            }
        }
        if (CallbackManager.getInstance().getGameReaderCallback() != null) {
            CallbackManager.getInstance().getGameReaderCallback().closeGame(
                    dataModel,
                    getIntent().getStringExtra("gameId")
            );
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

    GameStoryData storyDataModel;

    private GameStoryData getStoryDataModel() {
        if (storyDataModel == null) {
            if (getIntent().getStringExtra("storyId") == null) return null;
            storyDataModel = new GameStoryData(
                    Integer.parseInt(getIntent().getStringExtra("storyId")),
                    getIntent().getIntExtra("slideIndex", 0),
                    getIntent().getIntExtra("slidesCount", 0),
                    getIntent().getStringExtra("title"),
                    getIntent().getStringExtra("tags"),
                    getIntent().getStringExtra("feedId"),
                    Story.storyTypeFromName(getIntent().getStringExtra("storyType"))
            );
        }
        return storyDataModel;
    }

    void gameCompleted(String gameState, String link) {
        try {
            if (manager != null && link != null)
                manager.tapOnLink(link);
            GameStoryData dataModel = getStoryDataModel();
            if (dataModel != null) {
                Intent intent = new Intent();
                closing = true;
                intent.putExtra("storyId", dataModel.storyId);
                intent.putExtra("slideIndex", dataModel.slideIndex);
                if (gameState != null)
                    intent.putExtra("gameState", gameState);
                if (Sizes.isTablet()) {
                    String observableUID = getIntent().getStringExtra("observableUID");
                    if (observableUID != null) {
                        MutableLiveData<GameCompleteEvent> liveData =
                                ScreensManager.getInstance().getGameObserver(observableUID);
                        if (liveData != null) {
                            liveData.postValue(
                                    new GameCompleteEvent(
                                            gameState,
                                            dataModel.storyId,
                                            dataModel.slideIndex
                                    )
                            );
                        }
                    }
                }

                setResult(RESULT_OK, intent);
                finish();
            } else {
                finish();
            }
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            closing = false;
        }
    }


    private void initGame(String data) {
        webView.evaluateJavascript(data, null);
    }

    @Override
    public void closeView(final HashMap<String, Object> data) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                boolean shared = false;
                if (data.containsKey("shared")) shared = (boolean) data.get("shared");
                String id;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    id = ScreensManager.getInstance().getTempShareId();
                } else {
                    id = ScreensManager.getInstance().getOldTempShareId();
                }
                shareComplete(id, shared);
                if (!shared)
                    resumeGame();
                shareViewIsShown = false;

                ScreensManager.getInstance().clearShareIds();

            }
        });

    }


    boolean shareViewIsShown = false;

    @Override
    public void viewIsOpened() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                shareViewIsShown = true;
                pauseGame();
            }
        });
    }
}
