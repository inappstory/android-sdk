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
import android.util.Log;
import android.util.SizeF;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.inappstory.iasutilsconnector.filepicker.IFilePicker;
import com.inappstory.iasutilsconnector.filepicker.OnFilesChooseCallback;
import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.BuildConfig;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.ui.widgets.customicons.CustomIconWithoutStates;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.ui.screens.ScreenType;
import com.inappstory.sdk.core.ui.screens.ShareProcessHandler;
import com.inappstory.sdk.core.ui.screens.gamereader.BaseGameScreen;
import com.inappstory.sdk.core.ui.screens.gamereader.GameReaderOverlapContainerDataForShare;
import com.inappstory.sdk.game.cache.FilePathAndContent;
import com.inappstory.sdk.game.cache.SetGameLoggerCallback;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.game.cache.UseCaseWarnCallback;
import com.inappstory.sdk.game.ui.GameProgressLoader;
import com.inappstory.sdk.game.utils.GameConstants;
import com.inappstory.sdk.imageloader.CustomFileLoader;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.inner.share.InnerShareFilesPrepare;
import com.inappstory.sdk.inner.share.ShareFilesPrepareCallback;
import com.inappstory.sdk.memcache.IGetBitmapFromMemoryCache;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ContentData;
import com.inappstory.sdk.inappmessage.InAppMessageData;
import com.inappstory.sdk.stories.ui.widgets.TouchFrameLayout;
import com.inappstory.sdk.utils.IAcceleratorSubscriber;
import com.inappstory.sdk.utils.UrlEncoder;
import com.inappstory.sdk.network.utils.UserAgent;
import com.inappstory.sdk.share.IASShareData;
import com.inappstory.sdk.share.IASShareManager;
import com.inappstory.sdk.share.IShareCompleteListener;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;
import com.inappstory.sdk.stories.api.models.GameCenterData;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderType;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.callbacks.ShareCallback;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.events.GameCompleteEventObserver;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenGameReader;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.game.GameLoadedError;
import com.inappstory.sdk.stories.ui.OverlapFragmentObserver;
import com.inappstory.sdk.stories.ui.views.IASWebView;
import com.inappstory.sdk.stories.ui.views.IASWebViewClient;
import com.inappstory.sdk.stories.utils.AudioModes;
import com.inappstory.sdk.stories.utils.IASBackPressHandler;
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

public class GameReaderContentFragment extends Fragment implements OverlapFragmentObserver, IASBackPressHandler, IAcceleratorSubscriber {
    private IASWebView webView;
    private ImageView loader;
    private FrameLayout gameWebViewContainer;
    private TouchFrameLayout closeButton;
    private View blackTop;
    private View blackBottom;
    private View webViewAndLoaderContainer;
    private RelativeLayout loaderContainer;
    private GameProgressLoader progressLoader;
    private View baseContainer;

    public static final int GAME_READER_REQUEST = 405;

    private TouchFrameLayout refreshGame;

    private boolean onBackPressedLocked = false;

    ContentData storyDataModel;

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


    GameLoadedError gameLoadedErrorCallback = new GameLoadedError() {
        @Override
        public void onError(final GameCenterData data, String error) {
            manager.gameLoadError();
            InAppStoryManager.showDLog(LoggerTags.IAS_GAME_LOADING, error);

            if (webView != null)
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        closeButton.setVisibility(View.VISIBLE);
                        showRefresh.run();
                    }
                });
        }
    };


    public void shareComplete(String id, boolean success) {

        if (webView != null)
            webView.loadUrl("javascript:(function(){share_complete(\"" + id + "\", " + success + ");})()");
    }

    void restartGame() {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull final IASCore core) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        closeButton.setVisibility(View.VISIBLE);
                        loaderContainer.setVisibility(View.VISIBLE);
                        synchronized (initLock) {
                            initWithEmpty = true;
                        }

                        clearWebView();
                        if (webView != null) {
                            webView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    synchronized (initLock) {
                                        init = false;
                                        initWithEmpty = false;
                                    }
                                    FilePathAndContent filePathAndContent =
                                            core
                                                    .contentLoader()
                                                    .gameCacheManager()
                                                    .getCurrentFilePathAndContent();
                                    if (webView != null)
                                        webView.loadDataWithBaseURL(
                                                filePathAndContent.getFilePath(),
                                                webView.setDir(
                                                        filePathAndContent.getFileContent(),
                                                        getContext()
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
        });

    }


    void gameShouldForeground() {
        long leftTime = Math.max(0, 2000 - (System.currentTimeMillis() - startDownloadTime));
        if (webView == null) return;
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
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                // ContentData dataModel = getStoryDataModel();
                /*if (dataModel instanceof SlideData) {
                    SlideData slideData = (SlideData) dataModel;
                    core.statistic().profiling().setReady(
                            "game_init" +
                                    + slideData.story().id()
                                    + "_"
                                    + slideData.index()
                    );
                } else if (dataModel instanceof InAppMessageData) {
                    InAppMessageData iamData = (InAppMessageData) dataModel;
                }
                if (dataModel != null && dataModel.storyData() != null) {

                }*/


            }
        });
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
        InAppStoryManager.useCore(
                new UseIASCoreCallback() {
                    @Override
                    public void use(@NonNull IASCore core) {
                        IFilePicker filePicker = core.externalUtilsAPI().getUtilsAPI().getFilePicker();
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
                }
        );
    }

    private void uploadFilesFromFilePicker(String cbName, String cbId, String[] filesWithTypes) {
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("id", cbId);
        payloadMap.put("response", filesWithTypes);
        String payload = JsonParser.mapToJsonString(payloadMap);
        String webString = "window." + cbName + "('" + StringsUtils.escapeSingleQuotes(payload) + "');";
        Log.e("webString", webString);
        if (webView != null)
            webView.evaluateJavascript(webString, null);
    }

    void share(InnerShareData shareObject) {
        final IASShareData shareData = new IASShareData(
                shareObject.getText(),
                shareObject.getUrl(),
                shareObject.getPayload()
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

    private void shareCustomOrDefault(final IASShareData shareObject) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull final IASCore core) {
                core.screensManager().getShareProcessHandler().isShareProcess(false);
                core.callbacksAPI().useCallback(
                        IASCallbackType.SHARE_ADDITIONAL,
                        new UseIASCallback<ShareCallback>() {
                            @Override
                            public void use(@NonNull ShareCallback callback) {
                                int storyId = -1;
                                int slideIndex = 0;
                                ContentData dataModel = getStoryDataModel();
                                if (dataModel instanceof SlideData) {
                                    storyId = ((SlideData) dataModel).story().id();
                                    slideIndex = ((SlideData) dataModel).index();
                                }
                                core.screensManager().getGameScreenHolder()
                                        .openShareOverlapContainer(
                                                new GameReaderOverlapContainerDataForShare()
                                                        .shareData(shareObject)
                                                        .slideIndex(slideIndex)
                                                        .storyId(storyId),
                                                getBaseGameReader()
                                                        .getScreenFragmentManager(),
                                                GameReaderContentFragment.this
                                        );
                            }

                            @Override
                            public void onDefault() {
                                new IASShareManager().shareDefault(
                                        StoryShareBroadcastReceiver.class,
                                        getContext(),
                                        shareObject
                                );
                            }
                        }
                );
            }
        });

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
            InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    Activity activity = getActivity();
                    if (activity != null) {
                        ((IOpenGameReader) core.screensManager()
                                .getOpenReader(ScreenType.GAME))
                                .onRestoreScreen(getActivity());
                    }
                }
            });
        }
        if (manager != null && manager.logger != null)
            manager.logger.stopQueue();
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.acceleratorUtils().unsubscribe(GameReaderContentFragment.this);
            }
        });
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                manager = new GameManager(
                        GameReaderContentFragment.this,
                        InAppStoryManager.getInstance().iasCore(),
                        gameReaderLaunchData.getGameId(),
                        getStoryDataModel()
                );
                Activity activity = getActivity();
                if (activity != null)
                    oldOrientation = activity.getRequestedOrientation();
                initWebView();
                refreshGame.setClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        interruption.active = false;
                        progressLoader.setProgress(0, 100);
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
                                downloadGame(true);
                            }
                        }, 500);
                        try {
                            final CustomIconWithoutStates refreshIconInterface =
                                    AppearanceManager.getCommonInstance().csCustomIcons().refreshIcon();
                            refreshIconInterface.clickEvent(refreshGame.getChildAt(0));
                        } catch (Exception e) {

                        }
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
                closeButton.setClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            final CustomIconWithoutStates closeIconInterface =
                                    AppearanceManager.getCommonInstance().csCustomIcons().closeIcon();
                            closeIconInterface.clickEvent(closeButton.getChildAt(0));
                        } catch (Exception e) {

                        }
                        closeGame();
                    }
                });
                checkInsets();
                checkIntentValues(core, gameLoadedErrorCallback);
                downloadGame(false);
            }

            @Override
            public void error() {
                forceFinish();
            }
        });
    }

    public void closeGame() {
        if (closing) return;
        if (manager == null) {
            forceFinish();
            return;
        }
        closing = true;

        if (manager.statusHolder.gameLoaded()) {
            if (webView != null)
                webView.evaluateJavascript("closeGameReader();", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        if (!s.equals("true")) {
                            closeGameReader();
                        }
                    }
                });
        } else {
            closeGameReader();
        }
    }

    private void closeGameReader() {
        gameCompleted(null, null);
        manager.closeGameReader();
    }

    void setAudioManagerMode(String mode) {
        AudioManager audioManager = (AudioManager)
                getContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioModes.getModeVal(mode));
    }

    void showGoods(final String skusString, final String widgetId) {
        final ContentData dataModel = getStoryDataModel();
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

                    if (webView != null)
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

    void gameCompleted(final String gameState, String link) {
        try {
            if (manager != null && link != null)
                manager.tapOnLink(link, getContext());
            final ContentData dataModel = getStoryDataModel();
            if (dataModel instanceof SlideData) {
                closing = true;
                final String observableUID = gameReaderLaunchData.getObservableUID();
                if (observableUID != null) {
                    InAppStoryManager.useCore(new UseIASCoreCallback() {
                        @Override
                        public void use(@NonNull IASCore core) {
                            GameCompleteEventObserver observer =
                                    core.screensManager().getGameScreenHolder()
                                            .getGameObserver(observableUID);
                            if (observer != null) {
                                observer.gameComplete(
                                        new GameCompleteEvent(
                                                gameState,
                                                ((SlideData) dataModel).story().id(),
                                                ((SlideData) dataModel).index()
                                        )
                                );
                            }
                        }
                    });

                }
            }
            forceFinish();
        } catch (Exception e) {
            InAppStoryManager.handleException(e);
            closing = false;
        }
    }

    GameReaderLaunchData gameReaderLaunchData;


    private ContentData getStoryDataModel() {
        if (storyDataModel == null) {
            ContentData slideData = gameReaderLaunchData.getContentData();
            if (slideData == null) return null;
            storyDataModel = slideData;
        }
        return storyDataModel;
    }

    private void initGame(String data) {
        if (webView != null)
            webView.evaluateJavascript(data, null);
    }

    private boolean init = false;
    private boolean initWithEmpty = false;

    private final Object initLock = new Object();

    private void initWebView() {
        if (getContext() == null) return;
        try {
            webView = new IASWebView(getContext());
            gameWebViewContainer.addView(webView);
        } catch (Exception e) {
            forceFinish();
            return;
        }
        final ContentData dataModel = getStoryDataModel();
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
                    if (dataModel instanceof SlideData) {
                        webView.sendWebConsoleLog(
                                consoleMessage,
                                Integer.toString(((SlideData) dataModel).story().id()),
                                0,
                                ((SlideData) dataModel).index()
                        );
                    } else if (dataModel instanceof InAppMessageData) {
                        webView.sendWebConsoleLog(
                                consoleMessage,
                                Integer.toString(((InAppMessageData) dataModel).id()),
                                1,
                                0
                        );
                    }

                }
                String msg = consoleMessage.message();
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
                InAppStoryManager.showDLog(
                        LoggerTags.IAS_GAME_CONSOLE,
                        "Console: " +
                                consoleMessage.messageLevel().name() + ": " + msg
                );
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

    void showLoaders(final IASWebView oldWebView, final IASCore core) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                checkIntentValues(core, gameLoadedErrorCallback);
                closeButton.setVisibility(View.VISIBLE);
                loaderContainer.setVisibility(View.VISIBLE);

            }
        });
    }

    void recreateGameView(final IRecreateWebViewCallback chain) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (progressLoader != null)
                    progressLoader.clearLoader();
                if (loader != null) {
                    loader.setImageBitmap(null);
                    loader.setBackgroundColor(Color.BLACK);
                }
                IASWebView oldWebView = webView;
                synchronized (initLock) {
                    initWithEmpty = true;
                }
                removeOldWebView(oldWebView);
                initWebView();
                chain.invoke(null);

            }
        });

    }

    private void removeOldWebView(IASWebView webView) {
        if (webView != null) {
            webView.destroyView();
            gameWebViewContainer.removeView(webView);
            gameWebViewContainer.removeAllViews();
        }
    }

    private void clearWebView() {
        if (webView != null) {
            webView.loadUrl("about:blank");
        }
    }

    public void changeGameToAnother(String newGameId) {
        gameReaderLaunchData = new GameReaderLaunchData(
                newGameId,
                gameReaderLaunchData.getObservableUID(),
                gameReaderLaunchData.getContentData()
        );
        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
        }
        args.putSerializable(GameReaderLaunchData.SERIALIZABLE_KEY, gameReaderLaunchData);
        setArguments(args);
        manager.gameCenterId = newGameId;
        interruption.active = false;
        //downloadGame();
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

    private void checkIntentValues(IASCore core, final GameLoadedError callback) {
        String gameId = gameReaderLaunchData.getGameId();
        if (gameId == null) {
            callback.onError(null, "No game id");
            forceFinish();
            return;
        }

        Map<String, String> splashKeys = GameConstants.getSplashesKeys(
                core.externalUtilsAPI().hasLottieAnimation()
        );
        Map<String, File> splashPaths = new HashMap<>();
        for (Map.Entry<String, String> entry : splashKeys.entrySet()) {
            String path = core.keyValueStorage().getString(entry.getValue() + gameId);
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
    }

    private void setFullScreenFromOptions(GameScreenOptions options) {
        isFullscreen = options != null && options.fullScreen;
        if (forceFullscreen != null)
            isFullscreen = forceFullscreen;
        if (isFullscreen) {
            InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    Activity activity = getActivity();
                    if (activity != null) {
                        ((IOpenGameReader) core.screensManager()
                                .getOpenReader(ScreenType.GAME))
                                .onShowInFullscreen(getActivity());
                    }
                }
            });
        }
        setOffsets(isFullscreen);
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

    private void downloadGame(boolean forceReloadArchive) {
        downloadGame(
                gameReaderLaunchData.getGameId(),
                forceReloadArchive
        );
    }

    void downloadGame(
            final String gameId,
            final boolean forceReloadArchive
    ) {
        startDownloadTime = System.currentTimeMillis();
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull final IASCore core) {
                Log.e("ArchiveUseCase", "downloadGame");
                core.contentPreload().pauseGamePreloader();
                core.contentLoader().gameCacheManager().getGame(
                        gameId,
                        core.externalUtilsAPI().hasLottieAnimation(),
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
                                        core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
                                            @Override
                                            public void onSuccess(RequestLocalParameters sessionId) {
                                                replaceConfigs((IASDataSettingsHolder) core.settingsAPI());
                                            }

                                            @Override
                                            public void onError() {

                                            }
                                        });
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
                                CachedSessionData sessionData = ((IASDataSettingsHolder) core.settingsAPI()).sessionData();
                                if (sessionData == null) {
                                    gameLoadedErrorCallback.onError(null, "No session found");
                                    return;
                                }
                                if (sessionData.preloadGames)
                                    core.contentPreload().resumeGamePreloader();
                                webView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        synchronized (initLock) {
                                            init = false;
                                            initWithEmpty = false;
                                        }
                                        webView.loadDataWithBaseURL(
                                                result.getFilePath(),
                                                webView.setDir(
                                                        result.getFileContent(),
                                                        getContext()
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
                        },
                        forceReloadArchive
                );
            }
        });

    }

    void clearGameView() {
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
                }

            }
        });
    }

    private void replaceConfigs(IASDataSettingsHolder dataSettingsHolder) {
        if (manager.gameConfig != null) {
            if (manager.gameConfig.contains("{{%sdkVersion}}"))
                manager.gameConfig = manager.gameConfig.replace("{{%sdkVersion}}", BuildConfig.VERSION_NAME);
            if (manager.gameConfig.contains("{{%sdkConfig}}") || manager.gameConfig.contains("\"{{%sdkConfig}}\"")) {
                String replacedConfig = generateJsonConfig();
                manager.gameConfig = manager.gameConfig.replace("\"{{%sdkConfig}}\"", replacedConfig);
            } else if (manager.gameConfig.contains("{{%sdkPlaceholders}}") || manager.gameConfig.contains("\"{{%sdkPlaceholders}}\"")) {
                String replacedPlaceholders = generateJsonPlaceholders(dataSettingsHolder);
                manager.gameConfig = manager.gameConfig.replace("\"{{%sdkPlaceholders}}\"", replacedPlaceholders);
                manager.gameConfig = manager.gameConfig.replace("{{%sdkPlaceholders}}", replacedPlaceholders);
            }
        }
    }


    private String generateJsonConfig() {
        Context context = getContext();
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        final IASCore core = inAppStoryManager != null ? inAppStoryManager.iasCore() : null;

        GameConfigOptions options = new GameConfigOptions();
        if (!isAdded()) return "{}";
        options.fullScreen = isFullscreen;
        if (core != null) {
            if (context == null)
                context = core.appContext();
            if (context == null) {
                return "{}";
            }
            IASDataSettingsHolder dataSettingsHolder = ((IASDataSettingsHolder) core.settingsAPI());
            CachedSessionData sessionData = dataSettingsHolder.sessionData();
            options.apiBaseUrl = core.network().getBaseUrl();
            options.deviceId = dataSettingsHolder.deviceId();
            if (sessionData != null && sessionData.userId != null)
                options.userId = StringsUtils.getEscapedString(
                        new UrlEncoder().encode(sessionData.userId)
                );
            else
                options.userId = "";
            options.lang = dataSettingsHolder.lang().toLanguageTag();
            options.userAgent = StringsUtils.getEscapedString(
                    core.network().userAgent()
            );
            options.sessionId = sessionData != null && sessionData.sessionId != null ? sessionData.sessionId : "";
            options.apiKey = core.projectSettingsAPI().apiKey();
            options.placeholders = generatePlaceholders(dataSettingsHolder);
            options.userExtraOptions = JsonParser.stringMapToEscapedObjMap(
                    dataSettingsHolder.options()
            );
        } else {
            if (context == null) {
                return "{}";
            }
            options.lang = Locale.getDefault().toLanguageTag();
            options.deviceId = "";
            options.apiBaseUrl = "";
            options.userAgent = StringsUtils.getEscapedString(
                    new UserAgent().generate(context)
            );
            options.sessionId = "";
        }
        int orientation = context.getResources().getConfiguration().orientation;
        options.screenOrientation =
                (orientation == Configuration.ORIENTATION_LANDSCAPE) ? "landscape" : "portrait";
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
        SafeAreaInsets insets = new SafeAreaInsets();
        if (Build.VERSION.SDK_INT >= 28) {
            if (getActivity() != null) {
                WindowInsets windowInsets = getActivity().getWindow().getDecorView().getRootWindowInsets();
                if (windowInsets != null) {
                    insets.top = Sizes.pxToDpExt(windowInsets.getStableInsetTop(), context);
                    insets.bottom = Sizes.pxToDpExt(windowInsets.getStableInsetBottom(), context);
                    insets.left = Sizes.pxToDpExt(windowInsets.getStableInsetLeft(), context);
                    insets.right = Sizes.pxToDpExt(windowInsets.getStableInsetRight(), context);
                }
            }
        }
        options.safeAreaInsets = insets;
        String gameId = gameReaderLaunchData.getGameId();
        if (gameId != null)
            options.gameInstanceId = gameId;
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
                                    Math.max(windowInsets.getStableInsetTop(),
                                            Sizes.dpToPxExt(16, getContext())
                                    );
                            closeButton.requestLayout();
                        }
                        if (Sizes.isTablet(getContext())) {

                            View gameContainer = getView().findViewById(R.id.gameContainer);
                            if (gameContainer != null) {
                                Point size = Sizes.getScreenSize(getContext());
                                if (windowInsets != null) {
                                    size.y -= (windowInsets.getStableInsetTop() +
                                            windowInsets.getStableInsetBottom());
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


    private String generateJsonPlaceholders(IASDataSettingsHolder dataSettingsHolder) {
        String st = "[]";
        try {
            st = JsonParser.getJson(generatePlaceholders(dataSettingsHolder));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return st;
    }

    private ArrayList<GameDataPlaceholder> generatePlaceholders(IASDataSettingsHolder dataSettingsHolder) {
        Map<String, String> textPlaceholders =
                dataSettingsHolder.placeholders();
        Map<String, ImagePlaceholderValue> imagePlaceholders =
                dataSettingsHolder.imagePlaceholders();
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
            new CustomFileLoader().getBitmapFromFilePath(
                    splashFile.getAbsolutePath(),
                    new IGetBitmapFromMemoryCache() {
                        @Override
                        public void get(Bitmap bitmap) {
                            loader.setImageBitmap(bitmap);
                        }
                    },
                    null
            );
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
        gameWebViewContainer = view.findViewById(R.id.gameWebviewContainer);
        loader = view.findViewById(R.id.loader);
        baseContainer = view.findViewById(R.id.draggable_frame);
        loaderContainer = view.findViewById(R.id.loaderContainer);
        progressLoader = view.findViewById(R.id.gameProgressLoader);
        blackTop = view.findViewById(R.id.ias_black_top);
        blackBottom = view.findViewById(R.id.ias_black_bottom);
        Context context = view.getContext();

        refreshGame = view.findViewById(R.id.gameRefresh);
        int maxRefreshSize = Sizes.dpToPxExt(40, context);
        final CustomIconWithoutStates refreshIconInterface = AppearanceManager.getCommonInstance().csCustomIcons().refreshIcon();
        final View refreshView = refreshIconInterface.createIconView(context, new SizeF(maxRefreshSize, maxRefreshSize));
        refreshView.setClickable(false);
        refreshGame.addView(refreshView);
        refreshGame.setTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                refreshIconInterface.touchEvent(refreshView, event);
                return false;
            }
        });

        closeButton = view.findViewById(R.id.close_button);
        int maxCloseSize = Sizes.dpToPxExt(30, context);
        final CustomIconWithoutStates closeIconInterface = AppearanceManager.getCommonInstance().csCustomIcons().closeIcon();
        final View closeView = closeIconInterface.createIconView(context, new SizeF(maxCloseSize, maxCloseSize));
        closeView.setClickable(false);
        closeButton.addView(closeView);
        closeButton.setTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeIconInterface.touchEvent(closeView, event);
                return false;
            }
        });


        return view;
    }

    private void setOffsets(boolean isFullscreen) {
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity == null) return;
        if (!Sizes.isTablet(fragmentActivity)) {

            if (blackTop != null) {
                int phoneHeight = Sizes.getFullPhoneHeight(fragmentActivity);
                View window = getView();
                int windowHeight = Sizes.getScreenSize(fragmentActivity).y;

                int[] location = new int[2];
                // int[] location2 = new int[2];
                if (window != null) {
                    windowHeight = window.getHeight();
                    window.getLocationOnScreen(location);
                    // window.getLocationInWindow(location2);
                }

                int topInsetOffset = 0;
                int bottomInsetOffset = 0;
                if (Build.VERSION.SDK_INT >= 23) {
                    if (fragmentActivity.getWindow() != null) {
                        WindowInsets windowInsets = fragmentActivity.getWindow().getDecorView().getRootWindowInsets();
                        if (windowInsets != null) {
                            topInsetOffset = Math.max(0, windowInsets.getStableInsetTop());
                            bottomInsetOffset = Math.max(0, windowInsets.getStableInsetBottom());
                        }
                    }
                }

                if (!isFullscreen) {
                    if (location[1] < topInsetOffset) {
                        LinearLayout.LayoutParams topLp = (LinearLayout.LayoutParams) blackTop.getLayoutParams();
                        topLp.height = topInsetOffset;
                        blackTop.requestLayout();
                    }
                    if (phoneHeight - location[1] - bottomInsetOffset < windowHeight) {
                        LinearLayout.LayoutParams bottomLp = (LinearLayout.LayoutParams) blackBottom.getLayoutParams();
                        bottomLp.height = bottomInsetOffset;
                        blackBottom.requestLayout();
                    }
                }
            }
        }
    }

    void loadJsApiResponse(String gameResponse, String cb) {
        if (webView != null)
            webView.evaluateJavascript(cb + "('" + StringsUtils.escapeSingleQuotes(gameResponse) + "');", null);
    }

    boolean shareViewIsShown = false;
    boolean goodsViewIsShown = false;

    @Override
    public void closeView(final HashMap<String, Object> data) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull final IASCore core) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ShareProcessHandler shareProcessHandler = core.screensManager().getShareProcessHandler();
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
            if (webView != null)
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


    public void acceleratorSensorIsActive() {
        if (webView == null) return;
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript("userAccelerationSensorCbActivate();", null);
            }
        });
    }

    public void acceleratorSensorActivationError(final String type, final String message) {
        if (webView == null) return;
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript(
                        "userAccelerationSensorCbError("
                                + type + ","
                                + message + ");",
                        null);
            }
        });
    }

    @Override
    public void onEvent(final float x, final float y, final float z) {
        if (webView == null) return;
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript(
                        "userAccelerationSensorCbRead("
                                + x + ","
                                + y + ","
                                + z + ");",
                        null);
            }
        });
    }
}
