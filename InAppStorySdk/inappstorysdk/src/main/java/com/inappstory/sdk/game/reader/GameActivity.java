package com.inappstory.sdk.game.reader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.game.loader.FileLoader;
import com.inappstory.sdk.game.loader.GameLoadCallback;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.outerevents.CloseGame;
import com.inappstory.sdk.stories.outerevents.FinishGame;
import com.inappstory.sdk.stories.ui.views.IGameLoaderView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    @Override
    protected void onCreate(Bundle savedInstanceState1) {
        super.onCreate(savedInstanceState1);

        setContentView(R.layout.cs_activity_game);
        webView = findViewById(R.id.gameWebview);
        loader = findViewById(R.id.loader);
        loaderContainer = findViewById(R.id.loaderContainer);
        if (AppearanceManager.getInstance().csGameLoaderView() == null) {
            loaderView = new GameLoadProgressBar(GameActivity.this,
                    null,
                    android.R.attr.progressBarStyleHorizontal);
        } else {
            loaderView = AppearanceManager.getInstance().csGameLoaderView();
        }
        loaderContainer.addView(loaderView.getView());
        webView.getSettings().setJavaScriptEnabled(true);
        closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CsEventBus.getDefault().post(new CloseGame(Integer.parseInt(storyId), title, tags,
                        slidesCount, index));
                gameCompleted(null);
            }
        });
        String path = getIntent().getStringExtra("gameUrl");
        String resources = getIntent().getStringExtra("gameResources");
        ArrayList<WebResource> resourceList = new ArrayList<>();
        if (resources != null) {
            resourceList = JsonParser.listFromJson(resources, WebResource.class);
        }
        gameConfig = getIntent().getStringExtra("gameConfig");

        storyId = getIntent().getStringExtra("storyId");
        index = getIntent().getIntExtra("slideIndex", 0);
        slidesCount = getIntent().getIntExtra("slidesCount", 0);
        title = getIntent().getStringExtra("title");
        tags = getIntent().getStringExtra("tags");

        String loaderPath = getIntent().getStringExtra("preloadPath");
        if (!loaderPath.isEmpty())
            ImageLoader.getInstance().displayImage(loaderPath, -1, loader);
        else
            loader.setBackgroundColor(Color.BLACK);
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

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
               /* if (gameConfig != null)
                    initGame(gameConfig);*/
            }
        });

        webView.addJavascriptInterface(new WebAppInterface(GameActivity.this, index, storyId), "Android");
        String[] urlParts = urlParts(path);
        final ArrayList<WebResource> finalResourceList = resourceList;
        final long[] tSize = {0};
        long rSize = 0;
        for (WebResource resource : finalResourceList) {
            rSize += resource.size;
        }
        final long finalRSize = rSize;
        FileLoader.downloadAndUnzip(GameActivity.this, path, urlParts[0], new GameLoadCallback() {
            @Override
            public void onLoad(final File file) {
                FileLoader.downloadResources(finalResourceList, file.getAbsolutePath(), new GameLoadCallback() {
                    @Override
                    public void onLoad(File file2) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                File fl = new File(file.getAbsolutePath() + "/index.html");
                                try {
                                    webView.loadDataWithBaseURL("file://" + fl.getAbsolutePath(), getStringFromFile(fl), "text/html; charset=utf-8", "UTF-8", null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onProgress(int loadedSize, int totalSize) {
                        long sz = finalRSize + tSize[0];
                        long lz = tSize[0] + loadedSize;
                        int percent = (int)((lz * 100)/(sz));
                        loaderView.setProgress(percent, 100);
                    }
                });
            }

            @Override
            public void onError() {

            }

            @Override
            public void onProgress(int loadedSize, int totalSize) {
                tSize[0] = totalSize;
                int percent = (int)((loadedSize * 100)/(totalSize + finalRSize));
                loaderView.setProgress(percent, 100);
            }
        });
    }

    public String[] urlParts(String url) {
        String[] parts = url.split("/");
        String fName = parts[parts.length - 1].split("\\.")[0];
        return fName.split("_");
    }

    public static final int GAME_READER_REQUEST = 878;

    String gameConfig;

    public void gameCompleted(String gameState) {
        Intent intent = new Intent();
        intent.putExtra("storyId", storyId);
        intent.putExtra("slideIndex", index);
        if (gameState != null)
            intent.putExtra("gameState", gameState);
        setResult(RESULT_OK, intent);
        finish();
    }

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

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(File fl) throws Exception {
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }
}
