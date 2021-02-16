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
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

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
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.cache.FileCache;
import com.inappstory.sdk.stories.cache.FileType;
import com.inappstory.sdk.stories.cache.StoryDownloader;
import com.inappstory.sdk.stories.events.NoConnectionEvent;
import com.inappstory.sdk.stories.events.PageTaskToLoadEvent;
import com.inappstory.sdk.stories.events.PauseStoryReaderEvent;
import com.inappstory.sdk.stories.events.ResumeStoryReaderEvent;
import com.inappstory.sdk.stories.events.StoryPageLoadedEvent;
import com.inappstory.sdk.stories.events.StoryReaderTapEvent;
import com.inappstory.sdk.stories.outerevents.ShowSlide;
import com.inappstory.sdk.stories.ui.dialog.ContactDialog;
import com.inappstory.sdk.stories.ui.widgets.CoreProgressBar;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.StoryShareBroadcastReceiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class StoriesWebViewManager {
    int index;
    int storyId;

    public float getClickCoordinate() {
        return storiesWebView.coordinate1;
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
        alert.showDialog((Activity)storiesWebView.getContext());
    }

    public void storyClick(String payload) {
        InAppStoryService.getInstance().lastTapEventTime = System.currentTimeMillis();
        if (payload == null || payload.isEmpty() || payload.equals("test")) {
            if (InAppStoryService.getInstance().isConnected()) {
                CsEventBus.getDefault().post(new StoryReaderTapEvent((int)getClickCoordinate()));
            } else {
                CsEventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.READER));
            }
        } else if (payload.equals("forbidden")) {
            if (InAppStoryService.getInstance().isConnected()) {
                CsEventBus.getDefault().post(new StoryReaderTapEvent((int)getClickCoordinate(), true));
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
        CsEventBus.getDefault().post(new StoryPageLoadedEvent(storyId, index));
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

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void pauseStoryEvent(PauseStoryReaderEvent event) {
        if (storyId == InAppStoryService.getInstance().getCurrentId())
            storiesWebView.pauseVideo();
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void pauseStoryEvent(ResumeStoryReaderEvent event) {
        if (storyId == InAppStoryService.getInstance().getCurrentId())
            storiesWebView.resumeVideo();
    }
}
