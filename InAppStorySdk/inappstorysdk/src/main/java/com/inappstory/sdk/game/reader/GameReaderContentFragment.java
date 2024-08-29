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
import android.graphics.Bitmap;
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

import com.inappstory.iasutilsconnector.filepicker.IFilePicker;
import com.inappstory.iasutilsconnector.filepicker.OnFilesChooseCallback;
import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.BuildConfig;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.core.ui.screens.ShareProcessHandler;
import com.inappstory.sdk.core.ui.screens.gamereader.BaseGameScreen;
import com.inappstory.sdk.core.ui.screens.gamereader.GameReaderOverlapContainerDataForShare;
import com.inappstory.sdk.game.cache.FilePathAndContent;
import com.inappstory.sdk.game.cache.GameCacheManager;
import com.inappstory.sdk.game.cache.SetGameLoggerCallback;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.game.cache.UseCaseWarnCallback;
import com.inappstory.sdk.game.reader.logger.GameLoggerLvl1;
import com.inappstory.sdk.game.ui.GameProgressLoader;
import com.inappstory.sdk.game.utils.GameConstants;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.inner.share.InnerShareFilesPrepare;
import com.inappstory.sdk.inner.share.ShareFilesPrepareCallback;
import com.inappstory.sdk.memcache.GetBitmapFromCacheWithFilePath;
import com.inappstory.sdk.memcache.IGetBitmapFromMemoryCache;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.utils.HostFromSecretKey;
import com.inappstory.sdk.network.utils.UserAgent;
import com.inappstory.sdk.share.IASShareData;
import com.inappstory.sdk.share.IASShareManager;
import com.inappstory.sdk.share.IShareCompleteListener;
import com.inappstory.sdk.share.ShareListener;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;
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
import com.inappstory.sdk.stories.outercallbacks.game.GameLoadedError;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.ui.OverlapFragmentObserver;
import com.inappstory.sdk.stories.ui.views.IASWebView;
import com.inappstory.sdk.stories.ui.views.IASWebViewClient;
import com.inappstory.sdk.stories.utils.AudioModes;
import com.inappstory.sdk.stories.utils.IASBackPressHandler;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.stories.utils.StoryShareBroadcastReceiver;
import com.inappstory.sdk.utils.ProgressCallback;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class GameReaderContentFragment extends Fragment implements OverlapFragmentObserver, IASBackPressHandler {
    private IASWebView webView;
    private ImageView loader;
    private View closeButton;
    private View webViewContainer;
    private RelativeLayout loaderContainer;
    private GameProgressLoader progressLoader;
    private View baseContainer;

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

    public BaseGameScreen getBaseGameReader() {
        BaseGameScreen screen = null;
        if (getActivity() instanceof BaseGameScreen) {
            screen = (BaseGameScreen) getActivity();
        } else if (getParentFragment() instanceof BaseGameScreen) {
            screen = (BaseGameScreen) getParentFragment();
        }
        return screen;
    }

    void jsEvent(String name, String data) {
        GameStoryData dataModel = getStoryDataModel();
        if (CallbackManager.getInstance().getGameReaderCallback() != null) {
            CallbackManager.getInstance().getGameReaderCallback().eventGame(
                    dataModel,
                    gameReaderLaunchData.getGameId(),
                    name,
                    data
            );
        }
    }

    GameLoadedError gameLoadedErrorCallback = new GameLoadedError() {
        @Override
        public void onError(final GameCenterData data, String error) {
            GameStoryData dataModel = getStoryDataModel();
            if (CallbackManager.getInstance().getGameReaderCallback() != null) {
                CallbackManager.getInstance().getGameReaderCallback().gameLoadError(
                        dataModel,
                        gameReaderLaunchData.getGameId()
                );
            }
            InAppStoryManager.showDLog("Game_Loading", error);
            webView.post(new Runnable() {
                @Override
                public void run() {
                    closeButton.setVisibility(View.VISIBLE);
                    showRefresh.run();
                }
            });
        }
    };

    private void setLayout() {
    }

    public void shareComplete(String id, boolean success) {
        webView.loadUrl("javascript:(function(){share_complete(\"" + id + "\", " + success + ");})()");
    }

    void restartGame() {
        InAppStoryService service = InAppStoryService.getInstance();
        final GameCacheManager cacheManager;
        if (service != null) {
            cacheManager = service.gameCacheManager();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    closeButton.setVisibility(View.VISIBLE);
                    loaderContainer.setVisibility(View.VISIBLE);
                    if (webView != null) {
                        webView.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        synchronized (initLock) {
                                            initWithEmpty = true;
                                        }
                                        if (webView != null)
                                            webView.loadUrl("about:blank");
                                    }
                                }
                        );
                        webView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (initLock) {
                                    init = false;
                                    initWithEmpty = false;
                                }
                                FilePathAndContent filePathAndContent = cacheManager.getCurrentFilePathAndContent();
                                if (webView != null)
                                    webView.loadDataWithBaseURL(
                                            filePathAndContent.getFilePath(),
                                            webView.setDir(
                                                    filePathAndContent.getFileContent()
                                            ),
                                            "text/html; charset=utf-8", "UTF-8",
                                            null
                                    );
                            }
                        }, 200);
                    }

                }
            });
        }

    }


    void gameShouldForeground() {
        long leftTime = Math.max(0, 2000 - (System.currentTimeMillis() - startDownloadTime));
        webView.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressLoader.launchFinalAnimation();
            }
        }, leftTime);
        webView.postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript("gameShouldForeground();", null);
            }
        }, leftTime + 100);
    }

    void updateUI() {
        GameStoryData dataModel = getStoryDataModel();
        if (dataModel != null)
            ProfilingManager.getInstance().setReady("game_init" + dataModel.slideData.story.id + "_" + dataModel.slideData.index);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                closeButton.setVisibility(showClose ? View.VISIBLE : View.GONE);
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

    void openFilePicker(final String data) {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                IFilePicker filePicker = manager.utilModulesHolder.getFilePicker();
                filePicker.setPickerSettings(data);
                filePicker.show(
                        getContext(),
                        getBaseGameReader().getScreenFragmentManager(),
                        R.id.ias_file_picker_container,
                        new OnFilesChooseCallback() {
                            @Override
                            public void onChoose(String cbName, String cbId, String[] filesWithTypes) {
                                uploadFilesFromFilePicker(cbName, cbId, filesWithTypes);
                            }

                            @Override
                            public void onCancel(String cbName, String cbId) {
                                uploadFilesFromFilePicker(cbName, cbId, new String[0]);
                            }

                            @Override
                            public void onError(String cbName, String cbId, String reason) {
                                uploadFilesFromFilePicker(cbName, cbId, new String[0]);
                            }
                        }
                );
            }
        });
    }

    private void uploadFilesFromFilePicker(String cbName, String cbId, String[] filesWithTypes) {
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("id", cbId);
        payloadMap.put("response", filesWithTypes);
        String payload = JsonParser.mapToJsonString(payloadMap).replaceAll(Pattern.quote("'"), "\\'");
        String webString = "window." + cbName + "('" + payload + "');";
        Log.e("webString", webString);
        webView.evaluateJavascript(webString, null);
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
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) return;
        ShareProcessHandler shareProcessHandler = inAppStoryManager
                .getScreensHolder()
                .getShareProcessHandler();
        shareProcessHandler.isShareProcess(false);
        if (CallbackManager.getInstance().getShareCallback() != null) {
            int storyId = -1;
            int slideIndex = 0;
            GameStoryData dataModel = getStoryDataModel();
            if (dataModel != null) {
                storyId = dataModel.slideData.story.id;
                slideIndex = dataModel.slideData.index;
            }
            inAppStoryManager
                    .getScreensHolder()
                    .getStoryScreenHolder()
                    .openOverlapContainer(
                            new GameReaderOverlapContainerDataForShare()
                                    .shareData(shareObject)
                                    .slideIndex(slideIndex)
                                    .storyId(storyId)
                                    .shareListener(
                                            new ShareListener() {
                                                @Override
                                                public void onSuccess(boolean shared) {

                                                }

                                                @Override
                                                public void onCancel() {

                                                }
                                            }
                                    ),
                            getBaseGameReader()
                                    .getScreenFragmentManager(),
                            this
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

        initWebView();
        refreshGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interruption.active = false;
                changeView(progressLoader, refreshGame);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (initLock) {
                            init = false;
                        }
                        if (manager != null) {
                            manager.statusHolder.clearGameStatus();
                            manager.logger.gameLoaded(false);
                        }
                        downloadGame();
                    }
                }, 500);

            }
        });
        if (Sizes.isTablet(getContext()) && baseContainer != null) {
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
        checkIntentValues(gameLoadedErrorCallback);
    }

    public void closeGame() {
        if (closing) return;
        if (manager == null) {
            forceFinish();
            return;
        }
        closing = true;

        if (manager.statusHolder.gameLoaded()) {
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
            InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
            if (manager != null && link != null)
                manager.tapOnLink(link, getContext());
            GameStoryData dataModel = getStoryDataModel();
            if (dataModel != null) {
                closing = true;
                String observableUID = gameReaderLaunchData.getObservableUID();
                if (observableUID != null) {
                    GameCompleteEventObserver observer =
                            inAppStoryManager.getScreensHolder().getGameScreenHolder().getGameObserver(observableUID);
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
    private boolean initWithEmpty = false;

    private final Object initLock = new Object();

    private void initWebView() {
        final GameStoryData dataModel = getStoryDataModel();
        webView.setWebViewClient(new IASWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                boolean canInit = false;
                if (newProgress >= 100) {
                    synchronized (initLock) {
                        if (!init && !initWithEmpty) {
                            init = true;
                            canInit = true;
                        }
                    }
                }
                if (canInit && manager.gameConfig != null) {
                    initGame(manager.gameConfig);
                }
            }


            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if (dataModel != null && webView != null) {
                    webView.sendWebConsoleLog(consoleMessage,
                            Integer.toString(dataModel.slideData.story.id),
                            dataModel.slideData.index);
                }
                String msg = consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId();
                if (manager != null && manager.logger != null) {
                    switch (consoleMessage.messageLevel()) {
                        case ERROR:
                            manager.logger.sendConsoleError(msg);
                            break;
                        case WARNING:
                            manager.logger.sendConsoleWarn(msg);
                            break;
                        default:
                            manager.logger.sendConsoleInfo(msg);
                            break;
                    }
                }
                Log.d("InAppStory_SDK_Game", "Console: " +
                        consoleMessage.messageLevel().name() + ": " + msg);
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
        webView.loadUrl("javascript:(function() {" +
                "if ('resumeUI' in window) " +
                "{" +
                " window.resumeUI(); " +
                "}" +
                "})()");
    }

    private void pauseGame() {
        webView.loadUrl("javascript:(function() {" +
                "if ('pauseUI' in window) " +
                "{" +
                " window.pauseUI(); " +
                "}" +
                "})()");
        // webView.evaluateJavascript("pauseUI();", null);
    }

    Runnable showRefresh = new Runnable() {
        @Override
        public void run() {
            changeView(refreshGame, progressLoader);
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

    private void checkIntentValues(final GameLoadedError callback) {
        manager.gameCenterId = gameReaderLaunchData.getGameId();
        manager.dataModel = getStoryDataModel();
        if (manager.gameCenterId == null) {
            callback.onError(null, "No game id");
            forceFinish();
            return;
        }
        InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        Map<String, String> splashKeys = GameConstants.getSplashesKeys(
                inAppStoryService != null && inAppStoryService.hasLottieAnimation()
        );
        Map<String, File> splashPaths = new HashMap<>();
        for (Map.Entry<String, String> entry : splashKeys.entrySet()) {
            String path = KeyValueStorage.getString(entry.getValue() + manager.gameCenterId);
            if (path != null) {
                if (!path.isEmpty()) {
                    File splash = new File(path);
                    splashPaths.put(entry.getKey(), splash);
                    hasSplashFile = hasSplashFile || splash.exists();
                } else {
                    splashPaths.put(entry.getKey(), null);
                }
            }
        }
        if (!splashPaths.isEmpty()) {
            File staticFile = splashPaths.get(GameConstants.SPLASH_STATIC);
            File animFile = splashPaths.get(GameConstants.SPLASH_ANIM);
            if (staticFile != null)
                setStaticSplashScreen(staticFile);
            if (animFile != null)
                setLoader(animFile);
        }
        downloadGame();
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

    private long startDownloadTime;

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
        startDownloadTime = System.currentTimeMillis();
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull final InAppStoryService service) throws Exception {
                service.getGamePreloader().pause();
                service.gameCacheManager().getGame(
                        gameId,
                        service.hasLottieAnimation(),
                        service.getFilesDownloadManager(),
                        interruption,
                        new ProgressCallback() {
                            @Override
                            public void onProgress(long loadedSize, long totalSize) {
                                if (totalSize == 0) return;
                                final int percent = (int) ((loadedSize * 100) / totalSize);

                                if (progressLoader != null)
                                    progressLoader.setProgress(percent, 100);
                            }
                        },
                        new UseCaseWarnCallback<File>() {
                            @Override
                            public void onWarn(String message) {
                                if (manager != null && manager.logger != null) {
                                    manager.logger.sendSdkWarn(message);
                                }
                                InAppStoryManager.showDLog("Game_Loading", message);
                            }

                            @Override
                            public void onError(String message) {
                                InAppStoryManager.showDLog("Game_Loading", message);
                            }

                            @Override
                            public void onSuccess(File result) {
                                setStaticSplashScreen(result);
                            }
                        },
                        new UseCaseWarnCallback<File>() {
                            @Override
                            public void onWarn(String message) {
                                if (manager != null && manager.logger != null) {
                                    manager.logger.sendSdkWarn(message);
                                }
                                InAppStoryManager.showDLog("Game_Loading", message);
                            }

                            @Override
                            public void onError(String message) {
                                progressLoader.launchLoaderAnimation(null);
                                InAppStoryManager.showDLog("Game_Loading", message);
                            }

                            @Override
                            public void onSuccess(File result) {
                                setLoader(result);
                                //progressLoader.launchLoaderAnimation(result);
                            }
                        },
                        new UseCaseCallback<IGameCenterData>() {
                            @Override
                            public void onError(String message) {
                                if (manager != null && manager.logger != null) {
                                    manager.logger.sendSdkError(message, null);
                                }
                                gameLoadedErrorCallback.onError(null, message);
                            }

                            @Override
                            public void onSuccess(final IGameCenterData iGameCenterData) {
                                if (manager == null) return;


                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setLayout();
                                        GameCenterData gameCenterData = (GameCenterData) iGameCenterData;
                                        progressLoader.setIndeterminate(false);
                                        manager.statusHolder.setTotalReloadTries(
                                                gameCenterData.canTryReloadCount()
                                        );
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
                            public void onError(final String message) {
                                webView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (manager != null && manager.logger != null) {
                                            manager.logger.sendSdkError(message, null);
                                        }
                                        gameLoadedErrorCallback.onError(null, message);
                                    }
                                });
                            }

                            @Override
                            public void onSuccess(final FilePathAndContent result) {
                                service.getGamePreloader().restart();
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
                                        progressLoader.setIndeterminate(true);
                                    }
                                });
                            }
                        },
                        new SetGameLoggerCallback() {
                            @Override
                            public void setLogger(int loggerLevel) {
                                if (manager != null) {
                                    manager.setLogger(loggerLevel);
                                }
                            }
                        }
                );
            }
        });

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
        options.userAgent = StringsUtils.getEscapedString(
                new UserAgent().generate(context)
        );
        String appPackageName = "";
        try {
            appPackageName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        options.appPackageId = appPackageName;
        options.sdkVersion = StringsUtils.getEscapedString(
                BuildConfig.VERSION_NAME
        );
        if (inAppStoryManager != null) {
            options.apiKey = inAppStoryManager.getApiKey();
            options.userId = StringsUtils.getEscapedString(
                    inAppStoryManager.getUserId()
            );
            options.lang = inAppStoryManager.getCurrentLocale().toLanguageTag();
        } else {
            options.lang = Locale.getDefault().toLanguageTag();
        }
        options.sessionId = CachedSessionData.getInstance(context).sessionId;
        options.deviceId = "";
        if (inAppStoryManager == null || inAppStoryManager.isDeviceIDEnabled()) {
            options.deviceId = StringsUtils.getEscapedString(
                    Settings.Secure.getString(
                            context.getContentResolver(),
                            Settings.Secure.ANDROID_ID
                    )
            );
        }
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
                        if (Sizes.isTablet(getContext())) {

                            View gameContainer = getView().findViewById(R.id.gameContainer);
                            if (gameContainer != null) {
                                Point size = Sizes.getScreenSize(getContext());
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
                gameDataPlaceholders.add(
                        new GameDataPlaceholder(
                                "text",
                                StringsUtils.getEscapedString(
                                        entry.getKey()
                                ),
                                StringsUtils.getEscapedString(
                                        entry.getValue()
                                )
                        )
                );
        }
        for (Map.Entry<String, ImagePlaceholderValue> entry : imagePlaceholders.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null
                    && entry.getValue().getType() == ImagePlaceholderType.URL)
                gameDataPlaceholders.add(
                        new GameDataPlaceholder(
                                "image",
                                StringsUtils.getEscapedString(
                                        entry.getKey()
                                ),
                                StringsUtils.getEscapedString(
                                        entry.getValue().getUrl()
                                )
                        )
                );
        }
        return gameDataPlaceholders;
    }

    private void setLoader(File splashFile) {
        Log.e("LoaderFile", "Anim " + (splashFile != null ? splashFile.getAbsolutePath() : "empty"));
        progressLoader.launchLoaderAnimation(splashFile);
    }

    private void setStaticSplashScreen(File splashFile) {

        if (splashFile == null || !splashFile.exists()) {
            loader.setBackgroundColor(Color.BLACK);
        } else {
            Log.e("Loader", "Static " + splashFile.getAbsolutePath());
            new GetBitmapFromCacheWithFilePath(
                    splashFile.getAbsolutePath(),
                    new IGetBitmapFromMemoryCache() {
                        @Override
                        public void get(Bitmap bitmap) {
                            loader.setImageBitmap(bitmap);
                        }
                    }
            ).get();
            //     ImageLoader.getInstance().displayImage(splashFile.getAbsolutePath(), -1, loader);
        }
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
        manager.logger = new GameLoggerLvl1(gameReaderLaunchData.getGameId());
        webView = view.findViewById(R.id.gameWebview);
        loader = view.findViewById(R.id.loader);
        baseContainer = view.findViewById(R.id.draggable_frame);
        loaderContainer = view.findViewById(R.id.loaderContainer);
        progressLoader = view.findViewById(R.id.gameProgressLoader);
        refreshGame = view.findViewById(R.id.gameRefresh);
        refreshGame.setImageDrawable(
                getResources().getDrawable(
                        AppearanceManager.getCommonInstance().csRefreshIcon()
                )
        );
        closeButton = view.findViewById(R.id.close_button);
        webViewContainer = view.findViewById(R.id.webViewContainer);
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
                ShareProcessHandler shareProcessHandler = ShareProcessHandler.getInstance();
                if (shareProcessHandler == null) return;
                boolean shared = false;
                if (data.containsKey("shared")) shared = (boolean) data.get("shared");
                IShareCompleteListener shareCompleteListener =
                        shareProcessHandler.shareCompleteListener();
                if (shareCompleteListener != null) {
                    shareCompleteListener.complete(shared);
                }
                if (!shared)
                    resumeGame();
                shareViewIsShown = false;

                shareProcessHandler.clearShareIds();

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
        if (manager.statusHolder.gameLoaded()) {
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
