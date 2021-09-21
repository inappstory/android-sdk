package com.inappstory.sdk.game.reader;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
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
import com.inappstory.sdk.game.loader.GameLoader;
import com.inappstory.sdk.game.loader.GameLoadCallback;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.stories.api.models.ShareObject;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.outerevents.CloseGame;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.views.IGameLoaderView;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.stories.utils.StoryShareBroadcastReceiver;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.inappstory.sdk.network.JsonParser.toMap;

public class GameActivity extends AppCompatActivity {
    private WebView webView;
    private ImageView loader;
    private View closeButton;
    private RelativeLayout loaderContainer;
    private IGameLoaderView loaderView;
    private View blackTop;
    private View blackBottom;
    private View baseContainer;
    GameManager manager;

    public static final int GAME_READER_REQUEST = 878;

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

    public void close() {
        closeGame();
    }

    void gameReaderGestureBack() {
        if (webView != null) {
            if (manager.gameLoaded) {
                webView.evaluateJavascript("gameReaderGestureBack();", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                      //  if (!s.equals("true"))
                      //      closeGame();
                    }
                });
            } else {
                gameCompleted(null, null);
            }
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
                if (loaderContainer != null)
                    loaderContainer.setVisibility(View.GONE);
            }
        });

    }

    private void setViews() {
        webView = findViewById(R.id.gameWebview);
        webView.setBackgroundColor(Color.BLACK);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
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
        if (requestCode == SHARE_EVENT && resultCode == RESULT_CANCELED) {
            closeGame();
        }
    }

    public static final int SHARE_EVENT = 909;

    @Override
    public void onResume() {
        manager.onResume();
        super.onResume();
        resumeGame();
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

    public void shareDefault(ShareObject shareObject) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, shareObject.getTitle());
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareObject.getUrl());
        sendIntent.setType("text/plain");
        PendingIntent pi = PendingIntent.getBroadcast(GameActivity.this, SHARE_EVENT,
                new Intent(GameActivity.this, StoryShareBroadcastReceiver.class),
                FLAG_UPDATE_CURRENT);
        Intent finalIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            finalIntent = Intent.createChooser(sendIntent, null, pi.getIntentSender());
            //finalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(finalIntent, SHARE_EVENT);
        } else {
            finalIntent = Intent.createChooser(sendIntent, null);
            finalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            InAppStoryService.getInstance().getContext().startActivity(finalIntent);

        }
    }

    public void shareComplete(String id, boolean success) {
        webView.loadUrl("javascript:(function(){share_complete(\"" + id + "\", " + success + ");})()");
    }

    private void getIntentValues() {
        manager.path = getIntent().getStringExtra("gameUrl");
        manager.resources = getIntent().getStringExtra("gameResources");
        manager.storyId = getIntent().getStringExtra("storyId");
        manager.index = getIntent().getIntExtra("slideIndex", 0);
        manager.slidesCount = getIntent().getIntExtra("slidesCount", 0);
        manager.title = getIntent().getStringExtra("title");
        manager.tags = getIntent().getStringExtra("tags");
        manager.gameConfig = getIntent().getStringExtra("gameConfig");
        manager.gameConfig = manager.gameConfig.replace("{{%sdkVersion}}", BuildConfig.VERSION_NAME);
        manager.loaderPath = getIntent().getStringExtra("preloadPath");
    }


    void loadGameResponse(String gameResponse, String cb) {
        webView.evaluateJavascript(cb + "('" + gameResponse + "');", null);
    }

    private void initWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
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
                Log.e("MyApplication", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                return super.onConsoleMessage(consoleMessage);
            }
        });
        webView.addJavascriptInterface(new GameJSInterface(GameActivity.this,
                manager.index, manager.storyId, manager), "Android");
    }

    private void setLoader() {
        if (manager.loaderPath != null && !manager.loaderPath.isEmpty()
                && InAppStoryService.getInstance() != null)
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
    protected void onCreate(Bundle savedInstanceState1) {
        super.onCreate(savedInstanceState1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        ScreensManager.getInstance().currentGameActivity = this;
        setContentView(R.layout.cs_activity_game);
       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (webView != null) {
                    webView.evaluateJavascript("Android.gameComplete(\"\", \"\", \"dodo://category/100\");", null);
                }
            }
        }, 10000);*/
        manager = new GameManager(this);
        manager.callback = new GameLoadCallback() {
            @Override
            public void onLoad(String baseUrl, String data) {
                webView.loadDataWithBaseURL(baseUrl, data,
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
        getIntentValues();
        initWebView();
        setLoader();
        manager.loadGame();
    }

    private void closeGame() {
        if (closing) return;
        GameLoader.getInstance().terminate();
        closing = true;
        CsEventBus.getDefault().post(new CloseGame(Integer.parseInt(manager.storyId),
                manager.title, manager.tags,
                manager.slidesCount, manager.index));
        if (manager.gameLoaded) {
            webView.evaluateJavascript("closeGameReader();", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                  //  if (!s.equals("true"))
                 //       gameCompleted(null, null);
                }
            });
        } else {
            gameCompleted(null, null);
        }
    }


    void gameCompleted(String gameState, String link) {
        try {
            Intent intent = new Intent();
            if (Sizes.isTablet()) {
                CsEventBus.getDefault().post(new GameCompleteEvent(
                        gameState,
                        Integer.parseInt(manager.storyId),
                        manager.index));
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

}
