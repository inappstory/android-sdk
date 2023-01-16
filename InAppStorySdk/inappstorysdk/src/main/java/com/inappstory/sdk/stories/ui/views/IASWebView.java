package com.inappstory.sdk.stories.ui.views;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.api.models.logs.WebConsoleLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

public class IASWebView extends WebView {


    public IASWebView(@NonNull Context context) {
        super(context);
        init();
    }

    public IASWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public IASWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    protected void init() {
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        setBackgroundColor(getResources().getColor(R.color.black));

        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        getSettings().setTextZoom(100);
        getSettings().setAllowContentAccess(true);
        getSettings().setAllowFileAccess(true);
        getSettings().setAllowFileAccessFromFileURLs(true);
        getSettings().setAllowUniversalAccessFromFileURLs(true);
        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Log.e("downloadIAS", url + " " + mimetype);
                /* DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "fileName");
                DownloadManager dm = (DownloadManager) getContext().getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);*/
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getSettings().setOffscreenPreRaster(true);
        }

        setClickable(true);
        getSettings().setJavaScriptEnabled(true);
        setWebViewClient(new IASWebViewClient());
    }


    public void sendWebConsoleLog(ConsoleMessage consoleMessage,
                                  String storyId, int slideIndex) {
        WebConsoleLog log = new WebConsoleLog();
        log.timestamp = System.currentTimeMillis();
        log.id = UUID.randomUUID().toString();
        log.logType = consoleMessage.messageLevel().name();
        log.message = consoleMessage.message();
        log.sourceId = consoleMessage.sourceId();
        log.lineNumber = consoleMessage.lineNumber();
        log.storyId = storyId;
        log.slideIndex = slideIndex;
        InAppStoryManager.sendWebConsoleLog(log);
    }

    protected String injectUnselectableStyle(String html) {
        return html.replace("<head>",
                "<head><style>*{" +
                        "-webkit-touch-callout: none;" +
                        "-webkit-user-select: none;" +
                        "-khtml-user-select: none;" +
                        "-moz-user-select: none;" +
                        "-ms-user-select: none;" +
                        "user-select: none;" +
                        "} </style>");
    }

    public String setDir(String html) {
        try {
            int dir = ViewCompat.getLayoutDirection(this);
            String dirString = (dir == View.LAYOUT_DIRECTION_RTL) ? "rtl" : "ltr";
            return html.replace("{{%dir}}", dirString);
        } catch (Exception e) {
            return html;
        }
    }

}
