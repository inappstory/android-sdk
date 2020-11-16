package io.casestory.sdk.stories.ui.widgets.readerscreen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.casestory.casestorysdk.R;
import io.casestory.sdk.CaseStoryManager;
import io.casestory.sdk.CaseStoryService;
import io.casestory.sdk.eventbus.CsEventBus;
import io.casestory.sdk.eventbus.CsSubscribe;
import io.casestory.sdk.eventbus.CsThreadMode;
import io.casestory.sdk.network.NetworkCallback;
import io.casestory.sdk.network.NetworkClient;
import io.casestory.sdk.network.Request;
import io.casestory.sdk.network.Response;
import io.casestory.sdk.stories.api.models.StatisticSession;
import io.casestory.sdk.stories.api.models.Story;
import io.casestory.sdk.stories.cache.Downloader;
import io.casestory.sdk.stories.cache.FileCache;
import io.casestory.sdk.stories.cache.FileType;
import io.casestory.sdk.stories.cache.StoryDownloader;
import io.casestory.sdk.stories.events.ChangeIndexEvent;
import io.casestory.sdk.stories.events.NoConnectionEvent;
import io.casestory.sdk.stories.events.PageTaskLoadedEvent;
import io.casestory.sdk.stories.events.PageTaskToLoadEvent;
import io.casestory.sdk.stories.events.PauseStoryReaderEvent;
import io.casestory.sdk.stories.events.RestartStoryReaderEvent;
import io.casestory.sdk.stories.events.ResumeStoryReaderEvent;
import io.casestory.sdk.stories.events.StoryOpenEvent;
import io.casestory.sdk.stories.events.StoryPageLoadedEvent;
import io.casestory.sdk.stories.events.StoryPageOpenEvent;
import io.casestory.sdk.stories.events.StoryReaderTapEvent;
import io.casestory.sdk.stories.events.StorySwipeBackEvent;
import io.casestory.sdk.stories.outerevents.ClickOnButton;
import io.casestory.sdk.stories.outerevents.ShowSlide;
import io.casestory.sdk.stories.serviceevents.GeneratedWebPageEvent;
import io.casestory.sdk.stories.ui.dialog.ContactDialog;
import io.casestory.sdk.stories.ui.widgets.CoreProgressBar;
import io.casestory.sdk.stories.utils.KeyValueStorage;
import io.casestory.sdk.stories.utils.WebPageConverter;

import static io.casestory.sdk.stories.cache.HtmlParser.fromHtml;

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
        if (CaseStoryManager.getInstance() == null)
            return;
        if (!CaseStoryService.getInstance().isConnected()) {
            CsEventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.READER));
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Story story = StoryDownloader.getInstance().getStoryById(id);
                if (story == null || story.getLayout() == null || story.pages == null || story.pages.isEmpty()) {
                    return;
                }
                if (story.slidesCount <= index) return;
                loadStoryInner(id, index, story);
            }
        }).start();


    }

    private void loadStoryInner(final int id, final int index, Story story) {
        isWebPageLoaded = false;
        StoriesWebView.this.storyId = id;
        StoriesWebView.this.loadedIndex = index;
        StoriesWebView.this.index = index;
        StoriesWebView.this.loadedId = id;
        isLoaded = StoryDownloader.getInstance().checkIfPageLoaded(new Pair<>(id, index));

        String layout = story.getLayout();


        // EventBus.getDefault().post(new PageTaskToLoadEvent(storyId, index, false));
        if (!isLoaded) {
            CsEventBus.getDefault().post(new PageTaskToLoadEvent(storyId, index, false));
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= 19) {
                        setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    } else {
                        setLayerType(View.LAYER_TYPE_NONE, null);
                    }
                }
            });

            WebPageConverter.replaceEmptyAndLoad("", storyId, index, layout);

            return;
        } else {
            pageTaskLoaded(null);
        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void pageTaskLoaded(PageTaskLoadedEvent event) {
        if (event != null) {
            if (storyId != event.getId() || index != event.getIndex()) return;
        }
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
        if (CaseStoryService.getInstance().isConnected()) {
            final String finalLayout = layout;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (innerWebText.contains("<video")) {
                        isVideo = true;
                        setLayerType(View.LAYER_TYPE_HARDWARE, null);
                        WebPageConverter.replaceVideoAndLoad(innerWebText, storyId, index, finalLayout);
                        return;
                    } else {
                        if (Build.VERSION.SDK_INT >= 19) {
                            setLayerType(View.LAYER_TYPE_HARDWARE, null);
                        } else {
                            getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                            setLayerType(View.LAYER_TYPE_NONE, null);
                        }
                        WebPageConverter.replaceImagesAndLoad(innerWebText, storyId, index, finalLayout);

                        return;
                    }
                }
            });

        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void generatedWebPageEvent(GeneratedWebPageEvent event) {
        if (storyId != event.getStoryId()) return;
        boolean high = storyId == CaseStoryService.getInstance().getCurrentId();
        //getSettings().setUseWideViewPort(true);
        getSettings().setRenderPriority(high ? WebSettings.RenderPriority.HIGH : WebSettings.RenderPriority.LOW);

        final String data = event.getWebData();
        loadDataWithBaseURL("", injectUnselectableStyle(data), "text/html; charset=utf-8", "UTF-8", null);

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

    public void playVideo() {
        // if (!isVideo) return;
        loadUrl("javascript:(function(){story_slide_start();})()");
    }

    public void pauseVideo() {
        // if (!isVideo) return;
        loadUrl("javascript:(function(){story_slide_pause();})()");
    }

    public void stopVideo() {
        // if (!isVideo) return;
        //loadUrl("javascript:(function(){window.Android.defaultTap('test');})()");
        loadUrl("javascript:(function(){story_slide_stop();})()");

    }

    public void resumeVideo() {
        //  if (!isVideo) return;

        loadUrl("javascript:(function(){story_slide_resume();})()");
    }

    @Override
    public void loadUrl(final String url) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                StoriesWebView.super.loadUrl(url);
            }
        });
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
                Context con = CaseStoryManager.getInstance().getContext();
                FileCache cache = FileCache.INSTANCE;
                File file = cache.getStoredFile(con, img, FileType.STORY_IMAGE, storyId, null);

                if (file.exists()) {
                    try {
                        Response response = new Request.Builder().head().url(url).build().execute();
                        String ctType = response.headers.get("Content-Type");
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
                Context con = CaseStoryManager.getInstance().getContext();
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
           /* if (System.currentTimeMillis() - CaseStoryService.getInstance().lastTapEventTime < 700) {
                return;
            }*/

            Log.e("JSEvent", "storyClick");
            CaseStoryService.getInstance().lastTapEventTime = System.currentTimeMillis();
            if (payload == null || payload.isEmpty() || payload.equals("test")) {
                if (CaseStoryService.getInstance().isConnected()) {
                    CsEventBus.getDefault().post(new StoryReaderTapEvent((int) coordinate1));
                } else {
                    CsEventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.READER));
                }
            } else if (payload.equals("forbidden")) {
                if (CaseStoryService.getInstance().isConnected()) {
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
                // EventBus.getDefault().post(new ChangeIndexEvent(index));
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
            isWebPageLoaded = true;
            if (CaseStoryService.getInstance() == null) return;
            if (CaseStoryService.getInstance().getCurrentId() != storyId) {
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
        public void emptyLoaded() {
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
            NetworkClient.getApi().sendStoryData(Integer.toString(storyId), data, StatisticSession.getInstance().id)
                    .enqueue(new NetworkCallback<io.casestory.sdk.network.Response>() {
                        @Override
                        public void onSuccess(io.casestory.sdk.network.Response response) {

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
                    + "__" + CaseStoryManager.getInstance().getUserId(), data);

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
                    + "__" + CaseStoryManager.getInstance().getUserId());
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


    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        float pressedEndY = 0f;
        if (CaseStoryService.getInstance().cubeAnimation) return false;
        boolean distanceY = false;
        if (!CaseStoryService.getInstance().isConnected()) return true;
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                pressedY = motionEvent.getY();
                coordinate1 = motionEvent.getX();
                CsEventBus.getDefault().post(new PauseStoryReaderEvent(false));
                StoriesWebView.this.pauseVideo();
                break;
            case MotionEvent.ACTION_UP:
                CsEventBus.getDefault().post(new ResumeStoryReaderEvent(false));
                StoriesWebView.this.resumeVideo();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        boolean c = super.dispatchTouchEvent(motionEvent);
        return c;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (CaseStoryService.getInstance().cubeAnimation) return false;
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
        if (CaseStoryService.getInstance().cubeAnimation) return false;
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
