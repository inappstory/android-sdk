package io.casestory.sdk.stories.ui.widgets.readerscreen;

import android.animation.ValueAnimator;
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
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.casestory.casestorysdk.R;
import io.casestory.sdk.CaseStoryManager;
import io.casestory.sdk.CaseStoryService;
import io.casestory.sdk.eventbus.EventBus;
import io.casestory.sdk.eventbus.Subscribe;
import io.casestory.sdk.stories.api.models.StatisticSession;
import io.casestory.sdk.stories.api.models.Story;
import io.casestory.sdk.stories.api.networkclient.ApiClient;
import io.casestory.sdk.stories.api.networkclient.RetrofitCallback;
import io.casestory.sdk.stories.cache.Downloader;
import io.casestory.sdk.stories.cache.FileCache;
import io.casestory.sdk.stories.cache.FileType;
import io.casestory.sdk.stories.cache.StoryDownloader;
import io.casestory.sdk.stories.events.ChangeIndexEvent;
import io.casestory.sdk.stories.events.NoConnectionEvent;
import io.casestory.sdk.stories.events.PauseStoryReaderEvent;
import io.casestory.sdk.stories.events.RestartStoryReaderEvent;
import io.casestory.sdk.stories.events.ResumeStoryReaderEvent;
import io.casestory.sdk.stories.events.StoryOpenEvent;
import io.casestory.sdk.stories.events.StoryPageLoadedEvent;
import io.casestory.sdk.stories.events.StoryPageOpenEvent;
import io.casestory.sdk.stories.events.StoryReaderTapEvent;
import io.casestory.sdk.stories.events.StorySwipeBackEvent;
import io.casestory.sdk.stories.serviceevents.GeneratedWebPageEvent;
import io.casestory.sdk.stories.ui.dialog.ContactDialog;
import io.casestory.sdk.stories.ui.widgets.CoreProgressBar;
import io.casestory.sdk.stories.utils.KeyValueStorage;
import io.casestory.sdk.stories.utils.WebPageConverter;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

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

    public int loadedIndex;
    public int loadedId;

    String innerWebText;

    public static final Pattern FONT_SRC = Pattern.compile("@font-face [^}]*src: url\\(['\"](http[^'\"]*)['\"]\\)");

    public void loadStory(int id, int index) {
        if (loadedId == id && loadedIndex == index) return;
        if (CaseStoryManager.getInstance() == null)
            return;
        if (!CaseStoryService.getInstance().isConnected()) {
            EventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.READER));
            return;
        }
        Story story = StoryDownloader.getInstance().getStoryById(id);
        if (story == null || story.getLayout() == null || story.pages == null || story.pages.isEmpty()) {
            return;
        }
        if (story.pages.size() <= index) return;
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
        this.storyId = id;
        this.loadedIndex = index;
        this.index = index;
        this.loadedId = id;
        String innerWebData = story.pages.get(index);
        innerWebText = innerWebData;
        if (CaseStoryService.getInstance().isConnected()) {

            boolean exists = false;
            if (innerWebData.contains("<video")) {
                isVideo = true;
                setLayerType(View.LAYER_TYPE_HARDWARE, null);
                WebPageConverter.replaceVideoAndLoad(innerWebData, storyId, layout);
            } else {
                if (Build.VERSION.SDK_INT >= 19) {
                    setLayerType(View.LAYER_TYPE_NONE, null);
                } else {
                    getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                    setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                WebPageConverter.replaceImagesAndLoad(innerWebData, storyId, layout);
            }


            final String finalWebData = layout
                    .replace("//_ratio = 0.66666666666,", "")
                    .replace("{{%content}}", innerWebData);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new GeneratedWebPageEvent(finalWebData));
                }
            }, 200);


        }
    }

    @Subscribe
    public void generatedWebPageEvent(GeneratedWebPageEvent event) {
        loadDataWithBaseURL("", injectUnselectableStyle(event.getWebData()), "text/html; charset=utf-8", "UTF-8", null);
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

    @Subscribe
    public void changeNarrativePageEvent(StoryPageOpenEvent event) {

    }

    @Subscribe
    public void changeNarrativeEvent(StoryOpenEvent event) {
        if (event.getNarrativeId() != storyId)
            stopVideo();
        else {
            playVideo();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    resumeVideo();
                }
            }, 200);
        }
    }

    @Subscribe
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
        loadUrl("javascript:(function(){narrative_slide_start();})()");
    }

    public void pauseVideo() {
        // if (!isVideo) return;
        loadUrl("javascript:(function(){narrative_slide_pause();})()");
    }

    public void stopVideo() {
        // if (!isVideo) return;
        //loadUrl("javascript:(function(){window.Android.defaultTap('test');})()");
        loadUrl("javascript:(function(){narrative_slide_stop();})()");

    }

    public void resumeVideo() {
        //  if (!isVideo) return;
        loadUrl("javascript:(function(){narrative_slide_resume();})()");
    }

    public void cancelDialog(String id) {
        loadUrl("javascript:(function(){narrative_send_text_input_result(\"" + id + "\", \"\");})()");
    }

    public void sendDialog(String id, String data) {
        data = data.replaceAll("\n", "<br>");
        loadUrl("javascript:narrative_send_text_input_result(\"" + id + "\", \"" + data + "\")");
    }

    public StoriesWebView(Context context) {
        super(context);

        init();
    }

    String emptyJSString = "javascript:document.body.style.setProperty(\"color\", \"black\"); ";

    private CoreProgressBar progressBar;
    ValueAnimator animation2;
    ValueAnimator animationIn;
    ValueAnimator animationOut;

    public void destroyWebView() {
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
        EventBus.getDefault().register(this);
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        setBackgroundColor(getResources().getColor(R.color.black));
        if (Build.VERSION.SDK_INT >= 19) {
            setLayerType(View.LAYER_TYPE_NONE, null);
        } else {
            getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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
                        Log.e("loadVideoFile", url);
                        Response response = ApiClient.getImageApiOk().newCall(
                                new Request.Builder().head().url(url).build()).execute();
                        String ctType = response.header("Content-Type");
                        return new WebResourceResponse(ctType, "BINARY",
                                new FileInputStream(file));
                    } catch (FileNotFoundException e) {
                        Log.e("loadVideoFile", e.getMessage());
                        return super.shouldInterceptRequest(view, url);
                    } catch (IOException e) {
                        e.printStackTrace();
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
                        Response response = ApiClient.getImageApiOk().newCall(
                                new Request.Builder().head().url(request.getUrl().toString()).build()).execute();
                        String ctType = response.header("Content-Type");
                        return new WebResourceResponse(ctType, "BINARY",
                                new FileInputStream(file));
                    } catch (FileNotFoundException e) {
                        Log.e("loadVideoFile", e.getMessage());
                        return super.shouldInterceptRequest(view, request);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return super.shouldInterceptRequest(view, request);
                    }
                } else
                    return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
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
                if (animation2 != null) {
                    animation2.cancel();
                    animation2 = null;
                }
                EventBus.getDefault().post(new StoryPageLoadedEvent(storyId, index));
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
        public void narrativeClick(String payload) {
            if (System.currentTimeMillis() - CaseStoryService.getInstance().lastTapEventTime < 400) {
                return;
            }
            CaseStoryService.getInstance().lastTapEventTime = System.currentTimeMillis();
            if (payload == null || payload.isEmpty() || payload.equals("test")) {
                if (CaseStoryService.getInstance().isConnected()) {
                    EventBus.getDefault().post(new StoryReaderTapEvent((int) coordinate1));
                } else {
                    EventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.READER));
                }
            } else if (payload.equals("forbidden")) {
                if (CaseStoryService.getInstance().isConnected()) {
                    EventBus.getDefault().post(new StoryReaderTapEvent((int) coordinate1, true));
                } else {
                    EventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.READER));
                }
            } else {
                EventBus.getDefault().post(new StoryReaderTapEvent(payload));
            }
        }

        @JavascriptInterface
        public void narrativeShowSlide(int index) {
            Log.d("quiz", "narrativeShowSlide" + " " + index);
            if (StoriesWebView.this.index != index) {
                EventBus.getDefault().post(new ChangeIndexEvent(index));
            }
        }

        @JavascriptInterface
        public void narrativeShowNextSlide(long delay) {
            Log.d("quiz", "narrativeShowNextSlide" + " " + delay);
            if (delay != 0) {
                EventBus.getDefault().post(new RestartStoryReaderEvent(StoriesWebView.this.storyId, StoriesWebView.this.index, delay));
            } else {
                EventBus.getDefault().post(new ChangeIndexEvent(StoriesWebView.this.index + 1));
            }
        }

        @JavascriptInterface
        public void narrativeShowTextInput(String id, String data) {
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
        public void narrativeFreezeUI() {
            touchSlider = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                getParentForAccessibility().requestDisallowInterceptTouchEvent(true);
            }
        }


        @JavascriptInterface
        public void narrativeSendData(String data) {

            Log.d("quiz", "narrativeSendData" + " " + data);
            ApiClient.getApi().sendStoryData(Integer.toString(storyId), data, StatisticSession.getInstance().id)
                    .enqueue(new RetrofitCallback<ResponseBody>() {
                        @Override
                        public void onSuccess(ResponseBody response) {

                        }
                    });
        }

        @JavascriptInterface
        public void narrativeSetLocalData(String data, boolean sendToServer) {

            Log.d("quiz", "narrativeSetLocalData" + " " + data + " " + sendToServer);
            KeyValueStorage.saveString("narrative" + storyId
                    + "__" + CaseStoryManager.getInstance().getUserId(), data);

            if (sendToServer) {
                ApiClient.getApi().sendStoryData(Integer.toString(storyId), data, StatisticSession.getInstance().id)
                        .enqueue(new RetrofitCallback<ResponseBody>() {
                            @Override
                            public void onSuccess(ResponseBody response) {

                            }
                        });
            }
        }


        @JavascriptInterface
        public String narrativeGetLocalData() {
            Log.d("quiz", "narrativeGetLocalData");
            String res = KeyValueStorage.getString("narrative" + storyId
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
                EventBus.getDefault().post(new PauseStoryReaderEvent(false));
                StoriesWebView.this.pauseVideo();
                break;
            case MotionEvent.ACTION_UP:
                EventBus.getDefault().post(new ResumeStoryReaderEvent(false));
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
