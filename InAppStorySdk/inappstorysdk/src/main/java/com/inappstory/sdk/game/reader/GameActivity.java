package com.inappstory.sdk.game.reader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
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

import androidx.appcompat.app.AppCompatActivity;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.game.loader.GameLoadCallback;
import com.inappstory.sdk.game.loader.GameLoader;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.outerevents.CloseGame;
import com.inappstory.sdk.stories.outerevents.FinishGame;
import com.inappstory.sdk.stories.ui.views.IGameLoaderView;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    String storyId;
    String title;
    String tags;
    int index;
    int slidesCount;

    WebView webView;
    ImageView loader;
    View closeButton;
    RelativeLayout loaderContainer;
    IGameLoaderView loaderView;
    View blackTop;
    View blackBottom;
    View baseContainer;

    @Override
    public void onBackPressed() {
        closeGame();
    }

    private void setViews() {
        webView = findViewById(R.id.gameWebview);
        loader = findViewById(R.id.loader);
        baseContainer = findViewById(R.id.draggable_frame);
        loaderContainer = findViewById(R.id.loaderContainer);
        blackTop = findViewById(R.id.blackTop);
        blackBottom = findViewById(R.id.blackBottom);
        if (AppearanceManager.getInstance() == null || AppearanceManager.getInstance().csGameLoaderView() == null) {
            loaderView = new GameLoadProgressBar(GameActivity.this,
                    null,
                    android.R.attr.progressBarStyleHorizontal);
        } else {
            loaderView = AppearanceManager.getInstance().csGameLoaderView();
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
                Point screenSize = Sizes.getScreenSize();
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
    String path;
    String resources;
    String loaderPath;

    private void getIntentValues() {
        path = getIntent().getStringExtra("gameUrl");
        resources = getIntent().getStringExtra("gameResources");
        storyId = getIntent().getStringExtra("storyId");
        index = getIntent().getIntExtra("slideIndex", 0);
        slidesCount = getIntent().getIntExtra("slidesCount", 0);
        title = getIntent().getStringExtra("title");
        tags = getIntent().getStringExtra("tags");
        gameConfig = getIntent().getStringExtra("gameConfig");
        loaderPath = getIntent().getStringExtra("preloadPath");
    }

    private void initWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
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
            ImageLoader.getInstance().displayImage(loaderPath, -1, loader);
        else
            loader.setBackgroundColor(Color.BLACK);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState1) {
        super.onCreate(savedInstanceState1);

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
                final int ls = loadedSize;
                final int ts = totalSize;
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int percent = ((ls * 100) / (ts));
                        loaderView.setProgress(percent, 100);
                    }
                });
            }
        });
    }

    boolean closing = false;

    void closeGame() {
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



    public String[] urlParts(String url) {
        String[] parts = url.split("/");
        String fName = parts[parts.length - 1].split("\\.")[0];
        return fName.split("_");
    }

    public static final int GAME_READER_REQUEST = 878;

    String gameConfig;

    public void gameCompleted(String gameState) {
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

    boolean gameLoaded;

    public void initGame(String data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(data, null);
        } else {
            webView.loadUrl("javascript:" + data);
        }
    }

    public class WebAppInterface {
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
        public void gameLoaded() {
            gameLoaded = true;
            if (loaderContainer != null)
                loaderContainer.setVisibility(View.GONE);
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

    }


}
