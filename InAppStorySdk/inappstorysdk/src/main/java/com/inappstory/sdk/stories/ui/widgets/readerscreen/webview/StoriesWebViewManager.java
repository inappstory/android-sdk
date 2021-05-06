package com.inappstory.sdk.stories.ui.widgets.readerscreen.webview;

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
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Request;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.stories.api.models.ShareObject;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FileCache;
import com.inappstory.sdk.stories.cache.FileType;
import com.inappstory.sdk.stories.cache.StoryDownloader;
import com.inappstory.sdk.stories.events.NoConnectionEvent;
import com.inappstory.sdk.stories.events.PageTaskToLoadEvent;
import com.inappstory.sdk.stories.events.StoryPageStartedEvent;
import com.inappstory.sdk.stories.events.StoryReaderTapEvent;
import com.inappstory.sdk.stories.outerevents.ShowSlide;
import com.inappstory.sdk.stories.ui.dialog.ContactDialog;
import com.inappstory.sdk.stories.ui.widgets.CoreProgressBar;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.StoryShareBroadcastReceiver;
import com.inappstory.sdk.stories.utils.WebPageConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.inappstory.sdk.stories.cache.HtmlParser.fromHtml;

public class StoriesWebViewManager {
    int index = -1;

    public int loadedIndex = -1;
    public int loadedId = -1;

    public void setIndex(int index) {
        this.index = index;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    int storyId;
    boolean slideInCache = false;

    public float getClickCoordinate() {
        return storiesWebView.coordinate1;
    }


    public static final Pattern FONT_SRC = Pattern.compile("@font-face [^}]*src: url\\(['\"](http[^'\"]*)['\"]\\)");

    boolean lock = true;

    public void storyLoaded(int oId, int oInd) {
        if (storyId != oId || index != oInd) return;
        this.index = oInd;
        loadedIndex = oInd;
        loadedId = oId;
        Story story = StoryDownloader.getInstance().getStoryById(storyId);
        innerLoad(story);
    }

    void innerLoad(Story story) {
        String layout = getLayoutWithFonts(story.getLayout());
        String innerWebData = story.pages.get(index);
        if (InAppStoryService.isConnected()) {
            setWebViewSettings(innerWebData, layout);
        }
    }

    public void loadStory(final int id, final int index) {
        if (loadedId == id && loadedIndex == index) return;
        if (InAppStoryManager.getInstance() == null)
            return;
        if (!InAppStoryService.isConnected()) {
            CsEventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.READER));
            return;
        }
        final Story story = StoryDownloader.getInstance().getStoryById(id);
        if (story == null || story.getLayout() == null || story.pages == null || story.pages.isEmpty()) {
            return;
        }
        if (story.slidesCount <= index) return;
        storyId = id;
        this.index = index;
        loadedIndex = index;
        loadedId = id;
        slideInCache = StoryDownloader.getInstance().checkIfPageLoaded(new Pair<>(id, index));
        if (!slideInCache) {
            CsEventBus.getDefault().post(new PageTaskToLoadEvent(storyId, index, false)); //animation
        } else {
            innerLoad(story);
        }
    }

    void setWebViewSettings(String innerWebData, String layout) {
        if (innerWebData.contains("<video")) {
            isVideo = true;
            storiesWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            WebPageConverter.replaceVideoAndLoad(innerWebData, storyId, index, layout);
        } else {
            isVideo = false;
            if (Build.VERSION.SDK_INT >= 19) {
                storiesWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                storiesWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                storiesWebView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
            WebPageConverter.replaceImagesAndLoad(innerWebData, storyId, index, layout);
        }
    }

    String getLayoutWithFonts(String layout) {
        List<String> fonturls = new ArrayList<>();

        Matcher urlMatcher = FONT_SRC.matcher(layout);
        while (urlMatcher.find()) {
            if (urlMatcher.groupCount() == 1) {
                fonturls.add(fromHtml(urlMatcher.group(1)).toString());
            }
        }
        for (String fonturl : fonturls) {
            String fileLink = Downloader.getFontFile(storiesWebView.getContext(), fonturl);
            if (fileLink != null)
                layout = layout.replaceFirst(fonturl, "file://" + fileLink);
        }
        return layout;
    }

    boolean isVideo = false;

    void loadStoryInner(final int id, final int index, Story story) {

    }

    public void setStoriesWebView(SimpleStoriesWebView storiesWebView) {
        this.storiesWebView = storiesWebView;
        if (!this.storiesWebView.clientIsSet) {
            this.storiesWebView.addJavascriptInterface(new WebAppInterface(this.storiesWebView.getContext(),
                    this), "Android");
            this.storiesWebView.setWebViewClient(new WebViewClient() {

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                    String img = url;
                    Context con = InAppStoryManager.getInstance().getContext();
                    FileCache cache = FileCache.INSTANCE;
                    File file = cache.getStoredFile(con, img, FileType.STORY_IMAGE, Integer.toString(storyId), null);
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
                    Context con = InAppStoryManager.getInstance().getContext();
                    FileCache cache = FileCache.INSTANCE;
                    File file = cache.getStoredFile(con, img, FileType.STORY_IMAGE, Integer.toString(storyId), null);
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
            this.storiesWebView.setWebChromeClient(new WebChromeClient() {
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

        }
    }


    private CoreProgressBar progressBar;

    public void setProgressBar(CoreProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    SimpleStoriesWebView storiesWebView;

    public void storyShowTextInput(String id, String data) {
        ContactDialog alert = new ContactDialog(storyId, id, data,
                new ContactDialog.SendListener() {
                    @Override
                    public void onSend(final String id, final String data) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                storiesWebView.sendDialog(id, data);
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
                                storiesWebView.cancelDialog(id);
                            }
                        });
                    }
                });
        alert.showDialog((Activity) storiesWebView.getContext());
    }

    public void storyClick(String payload) {
        InAppStoryService.getInstance().lastTapEventTime = System.currentTimeMillis();
        if (payload == null || payload.isEmpty() || payload.equals("test")) {
            if (InAppStoryService.isConnected()) {
                CsEventBus.getDefault().post(new StoryReaderTapEvent((int) getClickCoordinate()));
            } else {
                CsEventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.READER));
            }
        } else if (payload.equals("forbidden")) {
            if (InAppStoryService.isConnected()) {
                CsEventBus.getDefault().post(new StoryReaderTapEvent((int) getClickCoordinate(), true));
            } else {
                CsEventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.READER));
            }
        } else {
            CsEventBus.getDefault().post(new StoryReaderTapEvent(payload));
        }
    }

    public void shareComplete(int stId, boolean success) {
        if (storyId != stId) return;
        storiesWebView.shareComplete(stId, success);
    }

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
            PendingIntent pi = PendingIntent.getBroadcast(storiesWebView.getContext(), 989,
                    new Intent(storiesWebView.getContext(), StoryShareBroadcastReceiver.class),
                    FLAG_UPDATE_CURRENT);
            Intent finalIntent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
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

    public void storyLoaded() {
        if (InAppStoryService.getInstance() == null) return;
        if (InAppStoryService.getInstance().getCurrentId() != storyId) {
            storiesWebView.stopVideo();
        } else {
            storiesWebView.playVideo();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    storiesWebView.resumeVideo();
                }
            }, 200);
        }
        Story story = StoryDownloader.getInstance().getStoryById(storyId);
        CsEventBus.getDefault().post(new ShowSlide(story.id, story.title,
                story.tags, story.slidesCount, index));
        CsEventBus.getDefault().post(new StoryPageStartedEvent(storyId, index));
        CsEventBus.getDefault().post(new PageTaskToLoadEvent(storyId, index, true));
    }

    public void freezeUI() {
        storiesWebView.freezeUI();
    }

    public void storySetLocalData(String data, boolean sendToServer) {
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

    public void storySendData(String data) {
        if (!InAppStoryManager.getInstance().sendStatistic) return;
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

    public void stopVideo() {
        storiesWebView.stopVideo();
    }

    public void playVideo() {
        storiesWebView.playVideo();
    }

    public void pauseVideo() {
        storiesWebView.pauseVideo();
    }

    public void resumeVideo() {
        storiesWebView.resumeVideo();
    }

    public void pauseStory() {
        pauseVideo();
    }

    public void resumeStory() {
        resumeVideo();
    }
}
