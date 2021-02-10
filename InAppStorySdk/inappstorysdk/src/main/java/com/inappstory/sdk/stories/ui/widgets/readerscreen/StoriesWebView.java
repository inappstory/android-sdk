package com.inappstory.sdk.stories.ui.widgets.readerscreen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.inappstory.sdk.R;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.eventbus.CsThreadMode;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Request;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.stories.api.models.ShareObject;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FileCache;
import com.inappstory.sdk.stories.cache.FileType;
import com.inappstory.sdk.stories.cache.StoryDownloader;
import com.inappstory.sdk.stories.events.ChangeIndexEvent;
import com.inappstory.sdk.stories.events.NoConnectionEvent;
import com.inappstory.sdk.stories.events.PageTaskLoadedEvent;
import com.inappstory.sdk.stories.events.PageTaskToLoadEvent;
import com.inappstory.sdk.stories.events.PauseStoryReaderEvent;
import com.inappstory.sdk.stories.events.RestartStoryReaderEvent;
import com.inappstory.sdk.stories.events.ResumeStoryReaderEvent;
import com.inappstory.sdk.stories.events.ShareCompleteEvent;
import com.inappstory.sdk.stories.events.SoundOnOffEvent;
import com.inappstory.sdk.stories.events.StoryOpenEvent;
import com.inappstory.sdk.stories.events.StoryPageLoadedEvent;
import com.inappstory.sdk.stories.events.StoryPageOpenEvent;
import com.inappstory.sdk.stories.events.StoryReaderTapEvent;
import com.inappstory.sdk.stories.events.StorySwipeBackEvent;
import com.inappstory.sdk.stories.outerevents.ShowSlide;
import com.inappstory.sdk.stories.serviceevents.GeneratedWebPageEvent;
import com.inappstory.sdk.stories.ui.dialog.ContactDialog;
import com.inappstory.sdk.stories.ui.widgets.CoreProgressBar;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.StoryShareBroadcastReceiver;
import com.inappstory.sdk.stories.utils.WebPageConverter;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.inappstory.sdk.stories.cache.HtmlParser.fromHtml;

/**
 * Created by Paperrose on 07.06.2018.
 */

public class StoriesWebView extends WebView {
    public void setIndex(int index) {
        this.index = index;
    }

    public int storyId;
    public boolean isVideo = false;

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public int index;

    public int loadedIndex = -1;
    public int loadedId = -1;

    String innerWebText;

    public static final Pattern FONT_SRC = Pattern.compile("@font-face [^}]*src: url\\(['\"](http[^'\"]*)['\"]\\)");
    boolean isLoaded = false;
    public boolean isWebPageLoaded = false;


    public void loadStory(final int id, final int index) {
        if (loadedId == id && loadedIndex == index) return;
        if (InAppStoryManager.getInstance() == null)
            return;
        if (!InAppStoryService.getInstance().isConnected()) {
            CsEventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.READER));
            return;
        }
        Log.e("PageTaskToLoadEvent", "loadStory " + id + " " + index);
        final Story story = StoryDownloader.getInstance().getStoryById(id);
        if (story == null || story.getLayout() == null || story.pages == null || story.pages.isEmpty()) {
            return;
        }
        if (story.slidesCount <= index) return;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadStoryInner(id, index, story);
            }
        });

    }

    private void loadStoryInner(final int id, final int index, Story story) {
        Log.e("PageTaskToLoadEvent", "loadStoryInner " + id + " " + index);
        isWebPageLoaded = false;
        StoriesWebView.this.storyId = id;
        StoriesWebView.this.loadedIndex = index;
        StoriesWebView.this.index = index;
        StoriesWebView.this.loadedId = id;
        isLoaded = StoryDownloader.getInstance().checkIfPageLoaded(new Pair<>(id, index));

        final String layout = story.getLayout();

        Log.e("PageTaskToLoadEvent", "" + isLoaded + " " + index + " " + id);

        // EventBus.getDefault().post(new PageTaskToLoadEvent(storyId, index, false));

        if (!isLoaded) {
            CsEventBus.getDefault().post(new PageTaskToLoadEvent(storyId, index, false));
            if (Build.VERSION.SDK_INT >= 19) {
                setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                setLayerType(View.LAYER_TYPE_NONE, null);
            }

            //WebPageConverter.replaceEmptyAndLoad("", storyId, index, layout);

            return;
        } else {

            pageTaskLoaded(null);
        }
    }

    boolean emptyLoaded = false;

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void pageTaskLoaded(PageTaskLoadedEvent event) {
        if (event != null) {
            if (storyId != event.getId() || index != event.getIndex()) return;
        }
        Log.e("LoadHtml", "PageTaskLoadedEvent");
        Story story = StoryDownloader.getInstance().getStoryById(storyId);
        String layout = story.getLayout();
        List<String> fonturls = new ArrayList<>();

        Matcher urlMatcher = FONT_SRC.matcher(layout);
        while (urlMatcher.find()) {
            if (urlMatcher.groupCount() == 1) {
                fonturls.add(fromHtml(urlMatcher.group(1)).toString());
            }
        }
        for (String fonturl : fonturls) {
            String fileLink = Downloader.getFontFile(getContext(), fonturl);
            if (fileLink != null)
                layout = layout.replaceFirst(fonturl, "file://" + fileLink);
        }

        String innerWebData = story.pages.get(index);
        innerWebText = innerWebData;
        if (InAppStoryService.getInstance().isConnected()) {
            if (innerWebData.contains("<video")) {
                isVideo = true;
                getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                setLayerType(View.LAYER_TYPE_HARDWARE, null);
                WebPageConverter.replaceVideoAndLoad(innerWebData, storyId, index, layout);
                return;
            } else {
                isVideo = false;
                if (Build.VERSION.SDK_INT >= 19) {
                    setLayerType(View.LAYER_TYPE_HARDWARE, null);
                } else {
                    getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                    setLayerType(View.LAYER_TYPE_NONE, null);
                }
                WebPageConverter.replaceImagesAndLoad(innerWebData, storyId, index, layout);

                return;
            }
        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void generatedWebPageEvent(final GeneratedWebPageEvent event) {
        if (storyId != event.getStoryId()) return;
        boolean high = storyId == InAppStoryService.getInstance().getCurrentId();
        //getSettings().setUseWideViewPort(true);
        getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        String tmpData = event.getWebData();
        String tmpLayout = event.getLayout();
        for (String key : InAppStoryManager.getInstance().getPlaceholders().keySet()) {
            tmpData = tmpData.replace(key, InAppStoryManager.getInstance().getPlaceholders().get(key));
            tmpLayout = tmpLayout.replace(key, InAppStoryManager.getInstance().getPlaceholders().get(key));
        }
        final String data = tmpData;
        final String lt = tmpLayout;

        if (!emptyLoaded) {

            Log.e("LoadHtml", "first");
            emptyLoaded = true;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    String s0 = injectUnselectableStyle(lt);
                    loadDataWithBaseURL("", s0, "text/html; charset=utf-8", "UTF-8", null);
                }
            });
        } else {

            Log.e("LoadHtml", "next");
            replaceHtml(data);
        }
    }

    public static String injectUnselectableStyle(String html) {
        return html.replace("<head>",
                "<head><style>*{" +
                        "-webkit-touch-callout: none;" +
                        "-webkit-user-select: none;" +
                        "-khtml-user-select: none;" +
                        "-moz-user-select: none;" +
                        "-ms-user-select: none;" +
                        "user-select: none;" +
                        "} </style>");
        //.replaceAll("muted", "");
    }

    public int getCurrentItem() {
        return index;
    }

    public void setCurrentItem(int index, boolean withAnimation) {
        loadStory(this.storyId, index);
    }

    public void setCurrentItem(int index) {
        loadStory(this.storyId, index);
    }

    boolean loadingFinished = true;
    boolean redirect = false;

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeStoryPageEvent(StoryPageOpenEvent event) {
        if (this.storyId != event.getStoryId()) return;
        setCurrentItem(event.getIndex(), false);
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeStoryEvent(StoryOpenEvent event) {
        if (event.getStoryId() != storyId) {
            stopVideo();
        } else {

            playVideo();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    resumeVideo();
                }
            }, 200);
        }
    }

    @CsSubscribe
    public void storySwipeBackEvent(StorySwipeBackEvent event) {
        if (event.getStoryId() == storyId)
            resumeVideo();
    }


    public void restartVideo() {
        //if (isVideo) {
        stopVideo();
        playVideo();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                resumeVideo();
            }
        }, 200);
        // }
    }


    public void replaceHtml(String page) {
        // if (!isVideo) return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("(function(){show_slide(\"" + oldEscape(page) + "\");})()", null);
        } else {
            loadUrl("javascript:(function(){show_slide(\"" + oldEscape(page) + "\");})()");
        }
    }

    private String oldEscape(String raw) {
        String escaped = raw
                .replaceAll("\"", "\\\\\"")
                .replaceAll("\n", " ")
                .replaceAll("\r", " ");
        //.replaceAll("muted", "");
        return escaped;
    }

    private String escape(String raw) {
        String escaped = raw;
        escaped = escaped.replaceAll("\"", "\\\"");
        escaped = escaped.replaceAll("\b", "\\b");
        escaped = escaped.replaceAll("\f", "\\f");
        escaped = escaped.replaceAll("\n", "\\n");
        escaped = escaped.replaceAll("\r", "\\r");
        escaped = escaped.replaceAll("\t", "\\t");
        // TODO: escape other non-printing characters using uXXXX notation
        return escaped;
    }

    public void pauseVideo() {
        // if (!isVideo) return;
        //Log.e("playVideo", storyId + " pause");
        loadUrl("javascript:(function(){story_slide_pause();})()");
    }


    public void playVideo() {
        // if (!isVideo) return;
        //Log.e("playVideo", storyId + " play");
        boolean withSound = InAppStoryManager.getInstance().soundOn;
        if (withSound) {
            loadUrl("javascript:(function(){story_slide_start('{\"muted\": false}');})()");
        } else {
            loadUrl("javascript:(function(){story_slide_start('{\"muted\": true}');})()");
        }
    }

    public void stopVideo() {
        // if (!isVideo) return;
        //loadUrl("javascript:(function(){window.Android.defaultTap('test');})()");
        //Log.e("playVideo", storyId + " stop");
        loadUrl("javascript:(function(){story_slide_stop();})()");

    }

    public void resumeVideo() {


        // Log.e("playVideo", storyId + " resume");
        loadUrl("javascript:(function(){story_slide_resume();})()");
    }

    @Override
    public void loadUrl(final String url) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //Log.e("replaceHtml", url);
                StoriesWebView.super.loadUrl(url);
            }
        });
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeSoundStatus(SoundOnOffEvent event) {
        if (InAppStoryManager.getInstance().soundOn) {
            loadUrl("javascript:(function(){story_slide_enable_audio();})()");
        } else {
            loadUrl("javascript:(function(){story_slide_disable_audio();})()");
        }
    }

    public void cancelDialog(String id) {
        loadUrl("javascript:(function(){story_send_text_input_result(\"" + id + "\", \"\");})()");
    }

    public void sendDialog(String id, String data) {
        data = data.replaceAll("\n", "<br>");
        loadUrl("javascript:story_send_text_input_result(\"" + id + "\", \"" + data + "\")");
    }

    public StoriesWebView(Context context) {
        super(context);

        init();
    }

    String emptyJSString = "javascript:document.body.style.setProperty(\"color\", \"black\"); ";

    private CoreProgressBar progressBar;

    public void destroyWebView() {
        final Runtime runtime = Runtime.getRuntime();
        try {
            CsEventBus.getDefault().unregister(this);
        } catch (Exception e) {
        }
        final long usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        final long maxHeapSizeInMB = runtime.maxMemory() / 1048576L;
        final long availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB;
        Log.e("destroyWebView", "destroyWebView" + usedMemInMB + " " + availHeapSizeInMB);
        loadedIndex = -1;
        loadedId = -1;
        emptyLoaded = false;
        removeAllViews();
        clearHistory();
        clearCache(true);
        loadUrl("about:blank");

        onPause();
        removeAllViews();
        destroyDrawingCache();
        //  pauseTimers();
        //  destroy();
    }

    public void setProgressBar(CoreProgressBar progressBar) {
        this.progressBar = progressBar;
    }


    private void init() {

        loadedIndex = -1;
        loadedId = -1;
        CsEventBus.getDefault().register(this);
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        setBackgroundColor(getResources().getColor(R.color.black));
        if (Build.VERSION.SDK_INT >= 19) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            setLayerType(View.LAYER_TYPE_NONE, null);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            getSettings().setTextZoom(100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getSettings().setOffscreenPreRaster(true);
        }

        setWebViewClient(new WebViewClient() {

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                String img = url;
                Context con = InAppStoryManager.getInstance().getContext();
                FileCache cache = FileCache.INSTANCE;
                File file = cache.getStoredFile(con, img, FileType.STORY_IMAGE, storyId, null);

                Log.e("storyLoaded", url + " " + storyId + " " + index);
                if (file.exists()) {
                    try {
                        Response response = new Request.Builder().head().url(url).build().execute();
                        String ctType = response.headers.get("Content-Type");
                        Log.e("storyLoaded", ctType + " " + storyId + " " + index);
                        return new WebResourceResponse(ctType, "BINARY",
                                new FileInputStream(file));
                    } catch (FileNotFoundException e) {
                        return super.shouldInterceptRequest(view, url);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return super.shouldInterceptRequest(view, url);
                    } catch (Exception e) {

                        return super.shouldInterceptRequest(view, url);
                    }
                } else
                    return super.shouldInterceptRequest(view, url);
            }



            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String img = request.getUrl().toString();
                Context con = InAppStoryManager.getInstance().getContext();
                FileCache cache = FileCache.INSTANCE;
                File file = cache.getStoredFile(con, img, FileType.STORY_IMAGE, storyId, null);

                if (file.exists()) {
                    try {
                        Response response = new Request.Builder().head().url(request.getUrl().toString()).build().execute();
                        String ctType = response.headers.get("Content-Type");
                        return new WebResourceResponse(ctType, "BINARY",
                                new FileInputStream(file));
                    } catch (FileNotFoundException e) {
                        return super.shouldInterceptRequest(view, request);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return super.shouldInterceptRequest(view, request);
                    } catch (Exception e) {
                        return super.shouldInterceptRequest(view, request);
                    }
                } else
                    return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

            }
        });
        setWebChromeClient(new WebChromeClient() {
            @Nullable
            @Override
            public Bitmap getDefaultVideoPoster() {
                if (super.getDefaultVideoPoster() == null) {
                    Bitmap bmp = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bmp);
                    canvas.drawColor(Color.BLACK);
                    return bmp;
                } else {
                    return super.getDefaultVideoPoster();
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (progressBar != null)
                    progressBar.setProgress(newProgress);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("MyApplication", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                return super.onConsoleMessage(consoleMessage);
            }
        });

        setClickable(true);
        getSettings().setJavaScriptEnabled(true);
        // getSettings().setMediaPlaybackRequiresUserGesture(false);
        // getSettings().setPluginState(WebSettings.PluginState.ON);
        addJavascriptInterface(new WebAppInterface(getContext(), index), "Android");
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void shareComplete(ShareCompleteEvent event) {
        if (storyId != event.storyId) return;
        loadUrl("javascript:(function(){share_complete(\"" + event.getId() + "\", " + event.isSuccess() + ");})()");
    }

    public class WebAppInterface {
        Context mContext;
        int lindex;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c, int lindex) {
            this.lindex = lindex;
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void storyClick(String payload) {
            Log.e("JSEvent", "storyClick");
            InAppStoryService.getInstance().lastTapEventTime = System.currentTimeMillis();
            if (payload == null || payload.isEmpty() || payload.equals("test")) {
                if (InAppStoryService.getInstance().isConnected()) {
                    CsEventBus.getDefault().post(new StoryReaderTapEvent((int) coordinate1));
                } else {
                    CsEventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.READER));
                }
            } else if (payload.equals("forbidden")) {
                if (InAppStoryService.getInstance().isConnected()) {
                    CsEventBus.getDefault().post(new StoryReaderTapEvent((int) coordinate1, true));
                } else {
                    CsEventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.READER));
                }
            } else {
                CsEventBus.getDefault().post(new StoryReaderTapEvent(payload));
            }
        }

        @JavascriptInterface
        public void storyShowSlide(int index) {
            if (StoriesWebView.this.index != index) {
                CsEventBus.getDefault().post(new ChangeIndexEvent(index));
            }
        }

        @JavascriptInterface
        public void storyShowNextSlide(final long delay) {
            Log.e("storyShowNextSlide", delay + "");
            if (delay != 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CsEventBus.getDefault().post(new RestartStoryReaderEvent(StoriesWebView.this.storyId, StoriesWebView.this.index, delay));
                    }
                }, 100);
            } else {
                CsEventBus.getDefault().post(new ChangeIndexEvent(StoriesWebView.this.index + 1));
            }
        }

        @JavascriptInterface
        public void storyShowTextInput(String id, String data) {

            ContactDialog alert = new ContactDialog(storyId, id, data,
                    new ContactDialog.SendListener() {
                        @Override
                        public void onSend(final String id, final String data) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    sendDialog(id, data);
                                }
                            });
                        }
                    },
                    new ContactDialog.CancelListener() {
                        @Override
                        public void onCancel(final String id) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    cancelDialog(id);
                                }
                            });
                        }
                    });
            alert.showDialog((Activity) getContext());
        }

        @JavascriptInterface
        public void storyLoaded() {
            Log.e("storyLoaded", storyId + " " + index);
            isWebPageLoaded = true;
            if (InAppStoryService.getInstance() == null) return;
            if (InAppStoryService.getInstance().getCurrentId() != storyId) {
                stopVideo();
            } else {
                playVideo();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resumeVideo();
                    }
                }, 200);
            }
            Story story = StoryDownloader.getInstance().getStoryById(storyId);
            CsEventBus.getDefault().post(new ShowSlide(story.id, story.title,
                    story.tags, story.slidesCount, index));
            CsEventBus.getDefault().post(new StoryPageLoadedEvent(storyId, index));
            CsEventBus.getDefault().post(new PageTaskToLoadEvent(storyId, index, true));
        }

        @JavascriptInterface
        public void storyStatisticEvent(String name, String data) {
            StatisticManager.getInstance().sendWidgetStoryEvent(name, data);
        }

        @JavascriptInterface
        public void emptyLoaded() {
        }

        @JavascriptInterface
        public void share(String id, String data) {
            ShareObject shareObj = JsonParser.fromJson(data, ShareObject.class);
            if (InAppStoryManager.getInstance().shareCallback != null) {
                InAppStoryManager.getInstance().shareCallback.onShare(shareObj.getUrl(), shareObj.getTitle(), shareObj.getDescription(), id);
            } else {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, shareObj.getTitle());
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareObj.getUrl());
                sendIntent.setType("text/plain");
                PendingIntent pi = PendingIntent.getBroadcast(getContext(), 989,
                        new Intent(getContext(), StoryShareBroadcastReceiver.class),
                        FLAG_UPDATE_CURRENT);
                Intent finalIntent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    finalIntent = Intent.createChooser(sendIntent, null, pi.getIntentSender());
                    finalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    InAppStoryManager.getInstance().setTempShareId(id);
                    InAppStoryManager.getInstance().setTempShareStoryId(storyId);
                    InAppStoryManager.getInstance().getContext().startActivity(finalIntent);
                } else {
                    finalIntent = Intent.createChooser(sendIntent, null);
                    finalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    InAppStoryManager.getInstance().getContext().startActivity(finalIntent);
                    InAppStoryManager.getInstance().setOldTempShareId(id);
                    InAppStoryManager.getInstance().setOldTempShareStoryId(storyId);
                }
            }
        }

        @JavascriptInterface
        public void storyFreezeUI() {
            touchSlider = true;
            Log.e("JSEvent", "storyFreezeUI");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                getParentForAccessibility().requestDisallowInterceptTouchEvent(true);
            }
        }


        @JavascriptInterface
        public void storySendData(String data) {

            Log.d("quiz", "storySendData" + " " + data);
            if (!InAppStoryManager.getInstance().sendStatistic) return;
            NetworkClient.getApi().sendStoryData(Integer.toString(storyId), data, StatisticSession.getInstance().id)
                    .enqueue(new NetworkCallback<com.inappstory.sdk.network.Response>() {
                        @Override
                        public void onSuccess(com.inappstory.sdk.network.Response response) {

                        }

                        @Override
                        public Type getType() {
                            return null;
                        }
                    });
        }

        @JavascriptInterface
        public void storySetLocalData(String data, boolean sendToServer) {

            Log.d("quiz", "storySetLocalData" + " " + data + " " + sendToServer);
            KeyValueStorage.saveString("story" + storyId
                    + "__" + InAppStoryManager.getInstance().getUserId(), data);

            if (!InAppStoryManager.getInstance().sendStatistic) return;
            if (sendToServer) {
                NetworkClient.getApi().sendStoryData(Integer.toString(storyId), data, StatisticSession.getInstance().id)
                        .enqueue(new NetworkCallback<Response>() {
                            @Override
                            public void onSuccess(Response response) {

                            }

                            @Override
                            public Type getType() {
                                return null;
                            }
                        });
            }
        }


        @JavascriptInterface
        public String storyGetLocalData() {
            Log.d("quiz", "storyGetLocalData");
            String res = KeyValueStorage.getString("story" + storyId
                    + "__" + InAppStoryManager.getInstance().getUserId());
            return res == null ? "" : res;
        }


        @JavascriptInterface
        public void defaultTap(String val) {
            Log.d("MyApplication", val);
        }
    }

    public StoriesWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StoriesWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    float coordinate1;

    long lastTap;


    private float pressedY;


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void pauseStoryEvent(PauseStoryReaderEvent event) {
        if (storyId == InAppStoryService.getInstance().getCurrentId())
            pauseVideo();
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void pauseStoryEvent(ResumeStoryReaderEvent event) {
        if (storyId == InAppStoryService.getInstance().getCurrentId())
            resumeVideo();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        float pressedEndY = 0f;
        if (InAppStoryService.getInstance().cubeAnimation) return false;
        boolean distanceY = false;
        if (!InAppStoryService.getInstance().isConnected()) return true;
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                pressedY = motionEvent.getY();
                coordinate1 = motionEvent.getX();
              //  CsEventBus.getDefault().post(new PauseStoryReaderEvent(false));
                //StoriesWebView.this.pauseVideo();
                break;
            case MotionEvent.ACTION_UP:
               // CsEventBus.getDefault().post(new ResumeStoryReaderEvent(false));
               // StoriesWebView.this.resumeVideo();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        boolean c = super.dispatchTouchEvent(motionEvent);
        return c;
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (InAppStoryService.getInstance().cubeAnimation) return false;
        boolean c = super.onTouchEvent(motionEvent);
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - lastTap < 1500) {
                return true;
            }
        }
        return c;
    }

    boolean touchSlider = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (InAppStoryService.getInstance().cubeAnimation) return false;
        boolean c = super.onInterceptTouchEvent(motionEvent);
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - lastTap < 1500) {
                return false;
            }

            lastTap = System.currentTimeMillis();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            touchSlider = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                getParentForAccessibility().requestDisallowInterceptTouchEvent(false);
            }
        }
        return c || touchSlider;
    }


}
