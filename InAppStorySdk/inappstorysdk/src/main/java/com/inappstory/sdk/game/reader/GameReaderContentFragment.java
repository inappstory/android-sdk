package com.inappstory.sdk.game.reader;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.BuildConfig;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.game.cache.FilePathAndContent;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.inner.share.InnerShareFilesPrepare;
import com.inappstory.sdk.inner.share.ShareFilesPrepareCallback;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.utils.HostFromSecretKey;
import com.inappstory.sdk.network.utils.UserAgent;
import com.inappstory.sdk.share.IASShareData;
import com.inappstory.sdk.share.IASShareManager;
import com.inappstory.sdk.share.IShareCompleteListener;
import com.inappstory.sdk.share.ShareListener;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.api.models.GameCenterData;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderType;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.events.GameCompleteEventObserver;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
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
import com.inappstory.sdk.stories.utils.IASBackPressHandler;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.stories.utils.StoryShareBroadcastReceiver;
import com.inappstory.sdk.utils.ProgressCallback;
import com.inappstory.sdk.utils.ZipLoadCallback;
import com.inappstory.sdk.utils.ZipLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GameReaderContentFragment extends Fragment implements OverlapFragmentObserver, IASBackPressHandler {
    private IASWebView webView;
    private ImageView loader;
    private View closeButton;
    private View webViewContainer;
    private RelativeLayout loaderContainer;
    private IProgressLoaderView loaderView;
    private View baseContainer;
    private View customLoaderView = null;

    public static final int GAME_READER_REQUEST = 405;

    private ImageView refreshGame;

    private boolean onBackPressedLocked = false;

    GameStoryData storyDataModel;

    private PermissionRequest audioRequest;

    private final int PERMISSIONS_REQUEST_RECORD_AUDIO = 101;

    boolean gameReaderGestureBack = false;
    boolean hasSplashFile = false;
    Boolean forceFullscreen = null;
    private boolean isFullscreen = false;

    DownloadInterruption interruption = new DownloadInterruption();
    private boolean closing = false;
    boolean showClose = true;

    GameManager manager;

    public BaseGameReaderScreen getBaseGameReader() {
        BaseGameReaderScreen screen = null;
        if (getActivity() instanceof BaseGameReaderScreen) {
            screen = (BaseGameReaderScreen) getActivity();
        } else if (getParentFragment() instanceof BaseGameReaderScreen) {
            screen = (BaseGameReaderScreen) getParentFragment();
        }
        return screen;
    }


    GameLoadedCallback gameLoadedCallback = new GameLoadedCallback() {
        @Override
        public void complete(final GameCenterData data, String error) {
            if (error == null) {
                setLayout();
                loaderView.setIndeterminate(false);
                manager.loadGame(data);
            } else {
                closeButton.setVisibility(View.VISIBLE);
                GameStoryData dataModel = getStoryDataModel();
                if (CallbackManager.getInstance().getGameReaderCallback() != null) {
                    CallbackManager.getInstance().getGameReaderCallback().gameLoadError(
                            dataModel,
                            gameReaderLaunchData.getGameId()
                    );
                }
                InAppStoryManager.showDLog("Game_Loading", error);
                webView.post(showRefresh);
            }
        }
    };

    private void setLayout() {
    }

    public void shareComplete(String id, boolean success) {
        webView.loadUrl("javascript:(function(){share_complete(\"" + id + "\", " + success + ");})()");
    }

    void updateUI() {
        GameStoryData dataModel = getStoryDataModel();
        if (dataModel != null)
            ProfilingManager.getInstance().setReady("game_init" + dataModel.slideData.story.id + "_" + dataModel.slideData.index);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                closeButton.setVisibility(showClose ? View.VISIBLE : View.GONE);
                refreshGame.removeCallbacks(showRefresh);
                hideView(loaderContainer);
            }
        });
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

    void share(InnerShareData shareObject) {
        final IASShareData shareData = new IASShareData(
                shareObject.getText(), shareObject.getPayload()
        );
        if (!shareObject.getFiles().isEmpty()) {
            new InnerShareFilesPrepare().prepareFiles(
                    getContext(),
                    new ShareFilesPrepareCallback() {
                        @Override
                        public void onPrepared(List<String> files) {
                            shareData.files = files;
                            shareCustomOrDefault(shareData);
                        }
                    },
                    shareObject.getFiles()
            );
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
                storyId = dataModel.slideData.story.id;
                slideIndex = dataModel.slideData.index;
            }
            ScreensManager.getInstance().openOverlapContainerForShare(
                    new ShareListener() {
                        @Override
                        public void onSuccess(boolean shared) {

                        }

                        @Override
                        public void onCancel() {

                        }
                    },
                    getBaseGameReader().getGameReaderFragmentManager(),
                    this,
                    null,
                    storyId,
                    slideIndex,
                    shareObject
            );
        } else {
            new IASShareManager().shareDefault(
                    StoryShareBroadcastReceiver.class,
                    getContext(),
                    shareObject
            );
        }
    }

    int oldOrientation = 0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().setRequestedOrientation(oldOrientation);
    }

    @Override
    public void onDestroyView() {
        if (isFullscreen) {
            InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
            if (inAppStoryManager != null && getActivity() != null)
                inAppStoryManager.getOpenGameReader().onRestoreScreen(getActivity());
        }
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (InAppStoryManager.isNull()) {
            forceFinish();
            return;
        }
        if (getActivity() != null)
            oldOrientation = getActivity().getRequestedOrientation();
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
            public void onError(String error) {
                refreshGame.post(showRefresh);
            }

            @Override
            public void onProgress(long loadedSize, long totalSize) {
                int percent = (int) ((loadedSize * 100) / totalSize);
                loaderView.setProgress(percent, 100);
            }
        };
        initWebView();
        refreshGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interruption.active = false;
                changeView(customLoaderView, refreshGame);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        init = false;
                        downloadGame();
                    }
                }, 500);

            }
        });
        if (Sizes.isTablet() && baseContainer != null) {
            baseContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeGame();
                }
            });
        }
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeGame();
            }
        });
        checkInsets();
        checkIntentValues(gameLoadedCallback);
    }

    public void closeGame() {
        if (closing) return;
        ZipLoader.getInstance().terminate();
        if (manager == null) {
            forceFinish();
            return;
        }
        closing = true;

        if (manager.gameLoaded) {
            webView.evaluateJavascript("closeGameReader();", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    if (!s.equals("true")) {
                        gameCompleted(null, null);
                        if (CallbackManager.getInstance().getGameReaderCallback() != null) {
                            CallbackManager.getInstance().getGameReaderCallback().closeGame(
                                    getStoryDataModel(),
                                    gameReaderLaunchData.getGameId()
                            );
                        }
                    }
                }
            });
        } else {
            gameCompleted(null, null);
            if (CallbackManager.getInstance().getGameReaderCallback() != null) {
                CallbackManager.getInstance().getGameReaderCallback().closeGame(
                        getStoryDataModel(),
                        gameReaderLaunchData.getGameId()
                );
            }
        }
    }

    void setAudioManagerMode(String mode) {
        AudioManager audioManager = (AudioManager)
                getContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioModes.getModeVal(mode));
    }

    void showGoods(final String skusString, final String widgetId) {
        final GameStoryData dataModel = getStoryDataModel();
        if (dataModel == null) return;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
               /* ScreensManager.getInstance().showGoods(
                        skusString,
                        GameActivity.this,
                        new ShowGoodsCallback() {
                            @Override
                            public void onPause() {
                                goodsViewIsShown = true;
                                pauseGame();
                            }

                            @Override
                            public void onResume(String widgetId) {
                                goodsViewIsShown = false;
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
                        dataModel.slideData
                );*/
            }
        });
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

    private void gameReaderAudioFocusChange(int focusChange) {
        if (webView != null) {
            webView.evaluateJavascript("('handleAudioFocusChange' in window) && handleAudioFocusChange(" + focusChange + ");", null);
        }
    }

    void tapOnLinkDefault(String link) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setData(Uri.parse(link));
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.popup_show, R.anim.empty_animation);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "Can't open this url: " + link, Toast.LENGTH_SHORT).show();
        }
    }

    void gameCompleted(String gameState, String link) {
        try {
            if (manager != null && link != null)
                manager.tapOnLink(link, getContext());
            GameStoryData dataModel = getStoryDataModel();
            if (dataModel != null) {
                closing = true;
                String observableUID = gameReaderLaunchData.getObservableUID();
                if (observableUID != null) {
                    GameCompleteEventObserver observer =
                            ScreensManager.getInstance().getGameObserver(observableUID);
                    if (observer != null) {
                        observer.gameComplete(
                                new GameCompleteEvent(
                                        gameState,
                                        dataModel.slideData.story.id,
                                        dataModel.slideData.index
                                )
                        );
                    }
                }
                forceFinish();
            } else {
                forceFinish();
            }
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            closing = false;
        }
    }

    GameReaderLaunchData gameReaderLaunchData;


    private GameStoryData getStoryDataModel() {
        if (storyDataModel == null) {
            SlideData slideData = gameReaderLaunchData.getSlideData();
            if (slideData == null) return null;
            storyDataModel = new GameStoryData(slideData);
        }
        return storyDataModel;
    }

    private void initGame(String data) {
        webView.evaluateJavascript(data, null);
    }

    private boolean init = false;

    private void initWebView() {
        final GameStoryData dataModel = getStoryDataModel();
        webView.setWebViewClient(new IASWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {

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
                            Integer.toString(dataModel.slideData.story.id),
                            dataModel.slideData.index);
                }
                Log.d("InAppStory_SDK_Game", "Console: " + consoleMessage.messageLevel().name() + ": "
                        + consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                return super.onConsoleMessage(consoleMessage);
            }


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
                        manager
                ),
                "Android"
        );
    }

    public void permissionResult(
            int requestCode,
            @NonNull int[] grantResults
    ) {
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (audioRequest != null && grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                audioRequest.grant(audioRequest.getResources());
            } else {
                audioRequest.deny();
            }
        }
    }

    private void askForPermission(String origin, String permission, int requestCode) {
        Activity activity = getActivity();
        if (activity == null) return;
        if (ContextCompat.checkSelfPermission(activity,
                permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    permission)) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{permission},
                        requestCode);
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{permission},
                        requestCode);
            }
        } else {
            if (audioRequest != null)
                audioRequest.grant(audioRequest.getResources());
        }
    }

    private void resumeGame() {
        webView.evaluateJavascript("resumeUI();", null);
    }

    private void pauseGame() {
        webView.evaluateJavascript("pauseUI();", null);
    }

    Runnable showRefresh = new Runnable() {
        @Override
        public void run() {
            changeView(refreshGame, customLoaderView);
        }
    };

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

    public void forceFinish() {
        getBaseGameReader().forceFinish();
    }

    private void checkIntentValues(final GameLoadedCallback callback) {
        manager.gameCenterId = gameReaderLaunchData.getGameId();
        manager.path = gameReaderLaunchData.getGameUrl();
        manager.dataModel = getStoryDataModel();
        if (manager.path == null) {
            if (manager.gameCenterId == null) {
                callback.complete(null, "No game path or id");
                forceFinish();
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
            GameScreenOptions options = gameReaderLaunchData.getOptions();
            setOrientationFromOptions(options);
            setFullScreenFromOptions(options);
            manager.resources = gameReaderLaunchData.getGameResources();
            manager.gameConfig = gameReaderLaunchData.getGameConfig();
            manager.splashImagePath = gameReaderLaunchData.getSplashImagePath();
            replaceConfigs();
            setLoaderOld();
            callback.complete(null, null);
        }
    }

    private void setFullScreenFromOptions(GameScreenOptions options) {
        isFullscreen = options != null && options.fullScreen;
        if (forceFullscreen != null)
            isFullscreen = forceFullscreen;
        if (isFullscreen) {
            InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
            if (inAppStoryManager != null && getActivity() != null)
                inAppStoryManager.getOpenGameReader().onShowInFullscreen(getActivity());
        }
    }

    private void setOrientationFromOptions(GameScreenOptions options) {
        if (options != null) {
            if ("landscape".equals(options.screenOrientation)) {
                setOrientationForNonOreo(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                return;
            }
        }
        setOrientationForNonOreo(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void setOrientationForNonOreo(int requestedOrientation) {
        if (getActivity() == null) return;
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            getActivity().setRequestedOrientation(requestedOrientation);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void downloadGame() {
        downloadGame(
                manager.gameCenterId
        );
    }

    private void downloadGame(
            final String gameId
    ) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null)
            service.gameCacheManager().getGame(
                    gameId,
                    interruption,
                    new ProgressCallback() {
                        @Override
                        public void onProgress(long loadedSize, long totalSize) {
                            if (totalSize == 0) return;
                            final int percent = (int) ((loadedSize * 100) / totalSize);

                            if (customLoaderView != null)
                                customLoaderView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        loaderView.setProgress(percent, 100);
                                    }
                                });
                        }
                    },
                    new UseCaseCallback<File>() {
                        @Override
                        public void onError(String message) {
                            InAppStoryManager.showDLog("Game_Loading", message);
                        }

                        @Override
                        public void onSuccess(File result) {
                            setLoader(result);
                        }
                    },
                    new UseCaseCallback<GameCenterData>() {
                        @Override
                        public void onError(String message) {

                        }

                        @Override
                        public void onSuccess(final GameCenterData gameCenterData) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    setLayout();
                                    loaderView.setIndeterminate(false);
                                    manager.gameConfig = gameCenterData.initCode;
                                    manager.path = gameCenterData.url;
                                    try {
                                        GameScreenOptions options = gameCenterData.options;
                                        manager.resources = gameCenterData.resources;
                                        setOrientationFromOptions(options);
                                        setFullScreenFromOptions(options);
                                    } catch (Exception ignored) {

                                    }
                                    replaceConfigs();
                                }
                            });
                        }
                    },
                    new UseCaseCallback<FilePathAndContent>() {
                        @Override
                        public void onError(String message) {
                            webView.post(new Runnable() {
                                @Override
                                public void run() {
                                    closeButton.setVisibility(View.VISIBLE);
                                    showRefresh.run();
                                }
                            });
                            GameStoryData dataModel = getStoryDataModel();
                            if (CallbackManager.getInstance().getGameReaderCallback() != null) {
                                CallbackManager.getInstance().getGameReaderCallback().gameLoadError(
                                        dataModel,
                                        gameReaderLaunchData.getGameId()
                                );
                            }
                            InAppStoryManager.showDLog("Game_Loading", message);
                        }

                        @Override
                        public void onSuccess(final FilePathAndContent result) {
                            manager.gameLoaded = true;
                            webView.post(new Runnable() {
                                @Override
                                public void run() {
                                    webView.loadDataWithBaseURL(
                                            result.getFilePath(),
                                            webView.setDir(
                                                    result.getFileContent()
                                            ),
                                            "text/html; charset=utf-8", "UTF-8",
                                            null);
                                }
                            });
                            loaderView.setIndeterminate(true);
                            refreshGame.postDelayed(showRefresh, 5000);
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
        Context context = getContext();
        GameConfigOptions options = new GameConfigOptions();
        options.fullScreen = isFullscreen;
        NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (networkClient == null) {
            options.apiBaseUrl = new HostFromSecretKey(
                    ApiSettings.getInstance().getApiKey()
            ).get(inAppStoryManager != null && inAppStoryManager.isSandbox());
        } else {
            options.apiBaseUrl = networkClient.getBaseUrl();
        }
        int orientation = getResources().getConfiguration().orientation;
        options.screenOrientation =
                (orientation == Configuration.ORIENTATION_LANDSCAPE) ? "landscape" : "portrait";
        options.userAgent = new UserAgent().generate(context);
        String appPackageName = "";
        try {
            appPackageName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        options.appPackageId = appPackageName;
        options.sdkVersion = BuildConfig.VERSION_NAME;
        if (inAppStoryManager != null) {
            options.apiKey = inAppStoryManager.getApiKey();
            options.userId = inAppStoryManager.getUserId();
        }
        options.sessionId = CachedSessionData.getInstance(context).sessionId;
        options.lang = Locale.getDefault().toLanguageTag();
        options.deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        options.placeholders = generatePlaceholders();
        SafeAreaInsets insets = new SafeAreaInsets();
        if (Build.VERSION.SDK_INT >= 28) {
            if (getActivity() != null) {
                WindowInsets windowInsets = getActivity().getWindow().getDecorView().getRootWindowInsets();
                if (windowInsets != null) {
                    insets.top = Sizes.pxToDpExt(windowInsets.getSystemWindowInsetTop(), context);
                    insets.bottom = Sizes.pxToDpExt(windowInsets.getSystemWindowInsetBottom(), context);
                    insets.left = Sizes.pxToDpExt(windowInsets.getSystemWindowInsetLeft(), context);
                    insets.right = Sizes.pxToDpExt(windowInsets.getSystemWindowInsetRight(), context);
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

    private void checkInsets() {
        if (Build.VERSION.SDK_INT >= 28) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() == null) return;
                    if (getActivity().getWindow() != null) {
                        WindowInsets windowInsets = getActivity().getWindow().getDecorView().getRootWindowInsets();
                        if (windowInsets != null) {
                            ((RelativeLayout.LayoutParams) closeButton.getLayoutParams()).topMargin =
                                    Math.max(windowInsets.getSystemWindowInsetTop(),
                                            Sizes.dpToPxExt(16, getContext())
                                    );
                            closeButton.requestLayout();
                        }
                        if (Sizes.isTablet()) {

                            View gameContainer = getView().findViewById(R.id.gameContainer);
                            if (gameContainer != null) {
                                Point size = Sizes.getScreenSize();
                                if (windowInsets != null) {
                                    size.y -= (windowInsets.getSystemWindowInsetTop() +
                                            windowInsets.getSystemWindowInsetBottom());
                                }
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


    private void setLoader(File splashFile) {
        if (splashFile == null || !splashFile.exists()) {
            loader.setBackgroundColor(Color.BLACK);
        } else {
            ImageLoader.getInstance().displayImage(splashFile.getAbsolutePath(), -1, loader);
        }
    }

    private void setLoaderOld() {

        InAppStoryService service = InAppStoryService.getInstance();
        if (manager.splashImagePath != null && !manager.splashImagePath.isEmpty()
                && service != null)
            ImageLoader.getInstance().displayImage(
                    manager.splashImagePath,
                    -1,
                    loader,
                    service.getCommonCache()
            );
        else
            loader.setBackgroundColor(Color.BLACK);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.cs_game_reader_content_layout, container, false);
        gameReaderLaunchData = (GameReaderLaunchData) getArguments().getSerializable(
                GameReaderLaunchData.SERIALIZABLE_KEY
        );
        manager = new GameManager(this);
        webView = view.findViewById(R.id.gameWebview);
        loader = view.findViewById(R.id.loader);
        baseContainer = view.findViewById(R.id.draggable_frame);
        loaderContainer = view.findViewById(R.id.loaderContainer);
        refreshGame = view.findViewById(R.id.gameRefresh);
        refreshGame.setImageDrawable(
                getResources().getDrawable(
                        AppearanceManager.getCommonInstance().csRefreshIcon()
                )
        );
        IGameReaderLoaderView gameReaderLoaderView = AppearanceManager.getCommonInstance().csGameReaderLoaderView();
        IGameLoaderView gameLoaderView = AppearanceManager.getCommonInstance().csGameLoaderView();
        if (gameReaderLoaderView != null) {
            loaderView = gameReaderLoaderView;
            customLoaderView = gameReaderLoaderView.getView(getContext());
        } else if (gameLoaderView != null) {
            loaderView = gameLoaderView;
            customLoaderView = gameLoaderView.getView();
        } else {
            GameReaderLoadProgressBar loadProgressBar = new GameReaderLoadProgressBar(getContext());
            loaderView = loadProgressBar;
            customLoaderView = loadProgressBar;
        }
        loaderView.setIndeterminate(true);
        closeButton = view.findViewById(R.id.close_button);
        webViewContainer = view.findViewById(R.id.webViewContainer);
        loaderContainer.addView(customLoaderView);
        return view;
    }

    void loadJsApiResponse(String gameResponse, String cb) {
        webView.evaluateJavascript(cb + "('" + gameResponse + "');", null);
    }

    boolean shareViewIsShown = false;
    boolean goodsViewIsShown = false;

    @Override
    public void closeView(final HashMap<String, Object> data) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                boolean shared = false;
                if (data.containsKey("shared")) shared = (boolean) data.get("shared");
                IShareCompleteListener shareCompleteListener =
                        ScreensManager.getInstance().shareCompleteListener();
                if (shareCompleteListener != null) {
                    shareCompleteListener.complete(shared);
                }
                if (!shared)
                    resumeGame();
                shareViewIsShown = false;

                ScreensManager.getInstance().clearShareIds();

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseGame();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!shareViewIsShown) {
            manager.onResume();
            resumeGame();
        }
    }

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

    private void gameReaderGestureBack() {
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

    @Override
    public boolean onBackPressed() {
        if (!onBackPressedLocked) {
            if (gameReaderGestureBack) {
                gameReaderGestureBack();
            } else {
                closeGame();
            }
        }
        return true;
    }
}
