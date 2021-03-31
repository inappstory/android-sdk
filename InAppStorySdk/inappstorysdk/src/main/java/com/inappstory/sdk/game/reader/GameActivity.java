package com.inappstory.sdk.game.reader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState1) {
        super.onCreate(savedInstanceState1);

        setContentView(R.layout.cs_activity_game);
        webView = findViewById(R.id.gameWebview);
        loader = findViewById(R.id.loader);
        webView.getSettings().setJavaScriptEnabled(true);
        String path = getIntent().getStringExtra("gameUrl");
        storyId = getIntent().getStringExtra("storyId");
        index = getIntent().getIntExtra("slideIndex", 0);
        String loaderPath = getIntent().getStringExtra("preloadPath");
        ImageLoader.getInstance().displayImage(loaderPath, -1, loader);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 90) {
                    loader.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.e("gameLoad", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                return super.onConsoleMessage(consoleMessage);
            }
        });

        webView.addJavascriptInterface(new WebAppInterface(GameActivity.this, index, storyId), "Android");
        FileLoader.downloadAndUnzip(GameActivity.this, path, new GameLoadCallback() {
            @Override
            public void onLoad(final File file) {
                Log.e("GameDownloader", file.getAbsolutePath());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        new File(file.getAbsolutePath() + "/index.html");
                        try {
                            webView.loadDataWithBaseURL(file.getAbsolutePath(), getStringFromFile(file),"text/html; charset=utf-8", "UTF-8", null);
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
