package com.inappstory.sdk.game.reader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.inappstory.sdk.R;
import com.inappstory.sdk.game.loader.FileLoader;
import com.inappstory.sdk.game.loader.GameLoadCallback;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.stories.api.models.StatisticManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.security.AccessController.getContext;

public class GameActivity extends AppCompatActivity {
    String storyId;
    int index;
    WebView webView;
    ImageView loader;
    View closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState1) {
        super.onCreate(savedInstanceState1);

        setContentView(R.layout.cs_activity_game);
        webView = findViewById(R.id.gameWebview);
        loader = findViewById(R.id.loader);
        webView.getSettings().setJavaScriptEnabled(true);
        closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameCompleted(null);
            }
        });
        String path = getIntent().getStringExtra("gameUrl");
        storyId = getIntent().getStringExtra("storyId");
        gameConfig = getIntent().getStringExtra("gameConfig");
        index = getIntent().getIntExtra("slideIndex", 0);
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
        FileLoader.downloadAndUnzip(GameActivity.this, path, urlParts[0], new GameLoadCallback() {
            @Override
            public void onLoad(final File file) {
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
            if (loader != null)
                loader.setVisibility(View.GONE);
        }


        @JavascriptInterface
        public void gameComplete(String data) {
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
