package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.game.reader.GameActivity;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.stories.api.models.ShareObject;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.slidestructure.SlideStructure;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.events.NoConnectionEvent;
import com.inappstory.sdk.stories.outerevents.ShowSlide;
import com.inappstory.sdk.stories.outerevents.StartGame;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.dialog.ContactDialog;
import com.inappstory.sdk.stories.ui.widgets.CoreProgressBar;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.generated.SimpleStoriesGeneratedView;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.webview.SimpleStoriesWebView;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.StoryShareBroadcastReceiver;
import com.inappstory.sdk.stories.utils.WebPageConvertCallback;
import com.inappstory.sdk.stories.utils.WebPageConverter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Intent.EXTRA_CHOSEN_COMPONENT;
import static com.inappstory.sdk.InAppStoryManager.testGenerated;
import static com.inappstory.sdk.game.reader.GameActivity.GAME_READER_REQUEST;

public class StoriesViewManager {
    public int index = -1;

    public int loadedIndex = -1;
    public int loadedId = -1;

    public void setIndex(int index) {
        this.index = index;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    void gameComplete(String data) {
        if (storiesView instanceof SimpleStoriesWebView) {
            ((SimpleStoriesWebView)storiesView).gameComplete(data);
        }
    }

    public void changeSoundStatus() {
        storiesView.changeSoundStatus();
    }

    public int storyId;
    boolean slideInCache = false;

    public void setPageManager(ReaderPageManager pageManager) {
        this.pageManager = pageManager;
    }

    public ReaderPageManager getPageManager() {
        return pageManager;
    }

    ReaderPageManager pageManager;

    public float getClickCoordinate() {
        return storiesView.getCoordinate();//storiesWebView.coordinate1;
    }


    public static final Pattern FONT_SRC = Pattern.compile("@font-face [^}]*src: url\\(['\"](http[^'\"]*)['\"]\\)");

    boolean lock = true;

    public void storyLoaded(int oId, int oInd) {
        this.index = oInd;
        loadedIndex = oInd;
        loadedId = oId;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        innerLoad(story);
    }

    public StoriesViewManager() {
    }

    Context context;

    public StoriesViewManager(Context context) {
        this.context = context;
    }

    void innerLoad(Story story) {
        if (InAppStoryService.isConnected()) {
            if (testGenerated) {
                initViews(story.slidesStructure.get(index));
            } else {
                try {
                    setWebViewSettings(story);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {

        }
    }

    public void loadWebData(String layout, String webdata) {
        if (!(storiesView instanceof SimpleStoriesWebView)) return;
        ((SimpleStoriesWebView) storiesView).loadWebData(layout, webdata);
    }

    private void initViews(SlideStructure slideStructure) {

        ((SimpleStoriesGeneratedView) storiesView).initViews(slideStructure);
    }

    public void loadStory(final int id, final int index) {
        if (loadedId == id && loadedIndex == index) return;

        if (InAppStoryService.isNull())
            return;
        if (!InAppStoryService.isConnected()) {
            CsEventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.READER));
            return;
        }
        final Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(id);
        if (story == null || story.checkIfEmpty()) {
            return;
        }
        if (story.slidesCount <= index) return;
        storyId = id;
        this.index = index;
        loadedIndex = index;
        loadedId = id;
        slideInCache = InAppStoryService.getInstance().getDownloadManager().checkIfPageLoaded(id, index);
        if (slideInCache) {
            innerLoad(story);
            pageManager.slideLoadedInCache(index);
        } else {
            pageManager.storyLoadStart();
        }
    }

    void setWebViewSettings(Story story) throws IOException {
        String innerWebData = story.pages.get(index);
        String layout = getLayoutWithFonts(story.getLayout());
        if (storiesView == null || !(storiesView instanceof SimpleStoriesWebView)) return;

        WebPageConvertCallback callback = new WebPageConvertCallback() {
            @Override
            public void onConvert(String webData, String webLayout, int lastIndex) {
             //   Log.e("changePriority", "loadWebData" + storyId + " " + index);
                if (index != lastIndex) return;
               // Log.e("changePriority", "loadWebData" + storyId + " " + index);
                loadWebData(webLayout, webData);
            }
        };
        ((SimpleStoriesWebView) storiesView).setLayerType(View.LAYER_TYPE_HARDWARE, null);
        if (innerWebData.contains("<video")) {
            isVideo = true;
            converter.replaceVideoAndLoad(innerWebData, story, index, layout, callback);
        } else {
            isVideo = false;
            converter.replaceImagesAndLoad(innerWebData, story, index, layout, callback);
        }
    }

    WebPageConverter converter = new WebPageConverter();

    String getLayoutWithFonts(String layout) {
        List<String> fonturls = new ArrayList<>();

        Matcher urlMatcher = FONT_SRC.matcher(layout);
        while (urlMatcher.find()) {
            if (urlMatcher.groupCount() == 1) {
                fonturls.add(converter.fromHtml(urlMatcher.group(1)).toString());
            }
        }
        for (String fonturl : fonturls) {
            String fileLink = Downloader.getFontFile(fonturl);
            if (fileLink != null)
                layout = layout.replaceFirst(fonturl, "file://" + fileLink);
        }
        return layout;
    }


    boolean isVideo = false;

    void loadStoryInner(final int id, final int index, Story story) {

    }

    public void setStoriesView(SimpleStoriesView storiesWebView) {
        this.storiesView = storiesWebView;
        storiesWebView.checkIfClientIsSet();
    }


    public CoreProgressBar getProgressBar() {
        return progressBar;
    }

    public File getCurrentFile(String img) {
        try {
            return InAppStoryService.getInstance().getCommonCache().get(img);
        } catch (IOException e) {
            return null;
        }
    }

    private CoreProgressBar progressBar;

    public void setProgressBar(CoreProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    SimpleStoriesView storiesView;

    public void storyShowTextInput(String id, String data) {
        ContactDialog alert = new ContactDialog(storyId, id, data,
                new ContactDialog.SendListener() {
                    @Override
                    public void onSend(final String id, final String data) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                storiesView.sendDialog(id, data);
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
                                storiesView.cancelDialog(id);
                            }
                        });
                    }
                });
        alert.showDialog((Activity) storiesView.getContext());
    }

    public void storyClick(String payload) {
        if (payload == null || payload.isEmpty() || payload.equals("test")) {
            if (InAppStoryService.isConnected()) {
                pageManager.storyClick(null, (int)getClickCoordinate(), false);
            } else {
                CsEventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.READER));
            }
        } else if (payload.equals("forbidden")) {
            if (InAppStoryService.isConnected()) {
                pageManager.storyClick(null, (int)getClickCoordinate(), true);
            } else {
                CsEventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.READER));
            }
        } else {
            pageManager.storyClick(payload, (int)getClickCoordinate(), false);
        }
    }


    public void shareComplete(String stId, boolean success) {
        storiesView.shareComplete(stId, success);
    }

    /*public class StoryShareBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ComponentName clickedComponent = intent.getParcelableExtra(EXTRA_CHOSEN_COMPONENT);
            if (clickedComponent != null && ScreensManager.getInstance().getTempShareId() != null) {
                shareComplete(Integer.toString(ScreensManager.getInstance().getTempShareStoryId()),
                        true);
            }
        }
    }*/

    public void share(String id, String data) {
        ShareObject shareObj = JsonParser.fromJson(data, ShareObject.class);
        if (CallbackManager.getInstance().getShareCallback() != null) {
            CallbackManager.getInstance().getShareCallback()
                    .onShare(shareObj.getUrl(), shareObj.getTitle(), shareObj.getDescription(), id);
        } else {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, shareObj.getTitle());
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareObj.getUrl());
            sendIntent.setType("text/plain");
            PendingIntent pi = PendingIntent.getBroadcast(storiesView.getContext(), 989,
                    new Intent(storiesView.getContext(), StoryShareBroadcastReceiver.class),
                    FLAG_UPDATE_CURRENT);
            Intent finalIntent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                finalIntent = Intent.createChooser(sendIntent, null, pi.getIntentSender());
               // finalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ScreensManager.getInstance().setTempShareId(id);
                ScreensManager.getInstance().setTempShareStoryId(storyId);
                storiesView.getContext().startActivity(finalIntent);
            } else {
                finalIntent = Intent.createChooser(sendIntent, null);
                //finalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                storiesView.getContext().startActivity(finalIntent);
                ScreensManager.getInstance().setOldTempShareId(id);
                ScreensManager.getInstance().setOldTempShareStoryId(storyId);
            }
        }
    }

    public void storyStartedEvent() {
        if (InAppStoryService.isNull()) return;
        pageManager.startStoryTimers();
    }


    public void storyResumedEvent(double startTime) {
        if (InAppStoryService.isNull()) return;
    }

    public void openGameReader(String gameUrl, String preloadPath, String gameConfig, String resources) {
        ScreensManager.getInstance().openGameReader(context, storyId, index, gameUrl,
                preloadPath, gameConfig, resources);
    }

    public void storyLoaded(int slideIndex) {
        if (InAppStoryService.isNull()) return;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        if ((slideIndex >= 0 && story.lastIndex != slideIndex)
                || InAppStoryService.getInstance().getCurrentId() != storyId) {
            stopStory();
        } else {
            playStory();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    resumeStory();
                }
            }, 200);
        }
        CsEventBus.getDefault().post(new ShowSlide(story.id, story.title,
                story.tags, story.slidesCount, index));
    }

    public void freezeUI() {
        storiesView.freezeUI();
    }

    public void storySetLocalData(String data, boolean sendToServer) {
        KeyValueStorage.saveString("story" + storyId
                + "__" + InAppStoryService.getInstance().getUserId(), data);

        if (!InAppStoryService.getInstance().getSendStatistic()) return;
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
        if (!InAppStoryService.getInstance().getSendStatistic()) return;
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

    public void stopStory() {
        storiesView.stopVideo();
    }

    public void restartStory() {
        storiesView.restartVideo();
    }

    public void playStory() {
        storiesView.playVideo();
    }

    public void pauseByClick() {

    }

    public void pauseStory() {
        storiesView.pauseVideo();
    }

    public void resumeStory() {
        storiesView.resumeVideo();
    }

    public void changeIndex(int index) {
        pageManager.openSlideByIndex(index);
    }

    public void restartStoryWithDuration(long duration) {
        pageManager.restartCurrentWithDuration(duration);
    }

    public void resetTimers() {
        pageManager.resetCurrentDuration();
    }
}
