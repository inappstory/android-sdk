package com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.share.IASShareManager;
import com.inappstory.sdk.stories.api.models.ShareObject;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outerevents.ClickOnShareStory;
import com.inappstory.sdk.stories.outerevents.DislikeStory;
import com.inappstory.sdk.stories.outerevents.FavoriteStory;
import com.inappstory.sdk.stories.outerevents.LikeStory;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;
import com.inappstory.sdk.stories.utils.StoryShareBroadcastReceiver;
import com.inappstory.sdk.utils.StringsUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ButtonsPanelManager {
    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    int storyId;

    public ReaderPageManager getParentManager() {
        return parentManager;
    }

    public void setParentManager(ReaderPageManager parentManager) {
        this.parentManager = parentManager;
    }

    public void unlockShareButton() {
        if (panel != null && panel.share != null) {
            panel.share.setClickable(true);
            panel.share.setEnabled(true);
        }
    }

    public ButtonsPanelManager(ButtonsPanel panel) {
        this.panel = panel;
    }

    ReaderPageManager parentManager;

    public void likeClick(ButtonClickCallback callback) {
        likeDislikeClick(callback, true);
    }

    public void dislikeClick(ButtonClickCallback callback) {
        likeDislikeClick(callback, false);
    }

    private void likeDislikeClick(final ButtonClickCallback callback, boolean like) {
        if (InAppStoryManager.isNull()) return;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId, parentManager.getStoryType());
        final int val;
        if (like) {
            if (story.liked()) {
                CsEventBus.getDefault().post(new LikeStory(story.id, story.statTitle,
                        story.tags, story.getSlidesCount(), story.lastIndex, false));
                if (CallbackManager.getInstance().getLikeDislikeStoryCallback() != null) {
                    CallbackManager.getInstance().getLikeDislikeStoryCallback().likeStory(
                            story.id, StringsUtils.getNonNull(story.statTitle),
                            StringsUtils.getNonNull(story.tags), story.getSlidesCount(),
                            story.lastIndex, false);
                }
                val = 0;
            } else {
                CsEventBus.getDefault().post(new LikeStory(story.id, story.statTitle,
                        story.tags, story.getSlidesCount(), story.lastIndex, true));
                if (CallbackManager.getInstance().getLikeDislikeStoryCallback() != null) {
                    CallbackManager.getInstance().getLikeDislikeStoryCallback().likeStory(
                            story.id, StringsUtils.getNonNull(story.statTitle),
                            StringsUtils.getNonNull(story.tags), story.getSlidesCount(),
                            story.lastIndex, true);
                }
                StatisticManager.getInstance().sendLikeStory(story.id, story.lastIndex,
                        parentManager != null ? parentManager.getFeedId() : null);
                val = 1;
            }
        } else {
            if (story.disliked()) {
                CsEventBus.getDefault().post(new DislikeStory(story.id, story.statTitle,
                        story.tags, story.getSlidesCount(), story.lastIndex, false));
                if (CallbackManager.getInstance().getLikeDislikeStoryCallback() != null) {
                    CallbackManager.getInstance().getLikeDislikeStoryCallback().dislikeStory(
                            story.id, StringsUtils.getNonNull(story.statTitle),
                            StringsUtils.getNonNull(story.tags), story.getSlidesCount(),
                            story.lastIndex, false);
                }
                val = 0;
            } else {
                CsEventBus.getDefault().post(new DislikeStory(story.id, story.statTitle,
                        story.tags, story.getSlidesCount(), story.lastIndex, true));
                if (CallbackManager.getInstance().getLikeDislikeStoryCallback() != null) {
                    CallbackManager.getInstance().getLikeDislikeStoryCallback().dislikeStory(
                            story.id, StringsUtils.getNonNull(story.statTitle),
                            StringsUtils.getNonNull(story.tags), story.getSlidesCount(),
                            story.lastIndex, true);
                }
                StatisticManager.getInstance().sendDislikeStory(story.id, story.lastIndex,
                        parentManager != null ? parentManager.getFeedId() : null);
                val = -1;
            }
        }
        final String likeUID =
                ProfilingManager.getInstance().addTask("api_like");
        NetworkClient.getApi().storyLike(Integer.toString(storyId), val).enqueue(
                new NetworkCallback<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        ProfilingManager.getInstance().setReady(likeUID);
                        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId, parentManager.getStoryType());
                        if (story != null)
                            story.like = val;
                        if (callback != null)
                            callback.onSuccess(val);
                    }


                    @Override
                    public void onError(int code, String message) {

                        ProfilingManager.getInstance().setReady(likeUID);
                        super.onError(code, message);
                        if (callback != null)
                            callback.onError();
                    }

                    @Override
                    public void onTimeout() {
                        super.onTimeout();
                        ProfilingManager.getInstance().setReady(likeUID);
                    }

                    @Override
                    public Type getType() {
                        return null;
                    }
                });
    }

    public void removeStoryFromFavorite() {
        if (panel != null)
            panel.forceRemoveFromFavorite();
    }

    public void favoriteClick(final ButtonClickCallback callback) {
        if (InAppStoryManager.isNull()) return;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId, parentManager.getStoryType());
        final boolean val = story.favorite;
        if (!story.favorite)
            StatisticManager.getInstance().sendFavoriteStory(story.id, story.lastIndex,
                    parentManager != null ? parentManager.getFeedId() : null);
        CsEventBus.getDefault().post(new FavoriteStory(story.id, story.statTitle,
                story.tags, story.getSlidesCount(), story.lastIndex, !story.favorite));
        if (CallbackManager.getInstance().getFavoriteStoryCallback() != null) {
            CallbackManager.getInstance().getFavoriteStoryCallback().favoriteStory(
                    story.id, StringsUtils.getNonNull(story.statTitle),
                    StringsUtils.getNonNull(story.tags), story.getSlidesCount(),
                    story.lastIndex, !story.favorite);
        }
        final String favUID = ProfilingManager.getInstance().addTask("api_favorite");
        NetworkClient.getApi().storyFavorite(Integer.toString(storyId), val ? 0 : 1).enqueue(
                new NetworkCallback<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        ProfilingManager.getInstance().setReady(favUID);
                        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId, parentManager.getStoryType());
                        boolean res = !val;
                        if (story != null)
                            story.favorite = res;
                        if (callback != null)
                            callback.onSuccess(res ? 1 : 0);
                        if (InAppStoryService.isNotNull())
                            InAppStoryService.getInstance().getListReaderConnector().storyFavorite(storyId, res);
                    }

                    @Override
                    public void onError(int code, String message) {

                        ProfilingManager.getInstance().setReady(favUID);
                        super.onError(code, message);
                        if (callback != null)
                            callback.onError();
                    }

                    @Override
                    public void onTimeout() {
                        super.onTimeout();

                        ProfilingManager.getInstance().setReady(favUID);
                    }

                    @Override
                    public Type getType() {
                        return null;
                    }
                });
    }

    ButtonsPanel panel;

    public void soundClick() {//ButtonClickCallback callback) {
        parentManager.changeSoundStatus();
        // CsEventBus.getDefault().post(new SoundOnOffEvent(InAppStoryService.getInstance().isSoundOn(), storyId));
        /*if (callback != null)
            callback.onSuccess(InAppStoryService.getInstance().isSoundOn() ? 1 : 0);*/
    }

    public void refreshSoundStatus() {
        if (panel != null)
            panel.refreshSoundStatus();
    }

    public abstract static class ShareButtonClickCallback implements ButtonClickCallback {
        abstract void onClick();
    }

    public void shareClick(final Context context, final ShareButtonClickCallback callback) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || service.isShareProcess())
            return;
        if (InAppStoryManager.isNull()) return;
        Story story = service.getDownloadManager().getStoryById(storyId, parentManager.getStoryType());
        if (story == null) return;
        StatisticManager.getInstance().sendShareStory(story.id, story.lastIndex,
                story.shareType(story.lastIndex),
                parentManager != null ? parentManager.getFeedId() : null);
        CsEventBus.getDefault().post(new ClickOnShareStory(story.id, story.statTitle,
                story.tags, story.getSlidesCount(), story.lastIndex));

        if (CallbackManager.getInstance().getClickOnShareStoryCallback() != null) {
            CallbackManager.getInstance().getClickOnShareStoryCallback().shareClick(story.id, StringsUtils.getNonNull(story.statTitle),
                    StringsUtils.getNonNull(story.tags), story.getSlidesCount(), story.lastIndex);
        }
        if (story.isScreenshotShare(story.lastIndex)) {
            parentManager.screenshotShare();
            return;
        }
        service.isShareProcess(true);
        if (callback != null)
            callback.onClick();
        final String shareUID = ProfilingManager.getInstance().addTask("api_share");
        NetworkClient.getApi().share(Integer.toString(storyId), story.lastIndex, null)
                .enqueue(new NetworkCallback<ShareObject>() {
                    @Override
                    public void onSuccess(ShareObject response) {
                        ProfilingManager.getInstance().setReady(shareUID);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                            ScreensManager.getInstance().setTempShareId(null);
                            ScreensManager.getInstance().setTempShareStoryId(storyId);
                        } else {
                            ScreensManager.getInstance().setOldTempShareId(null);
                            ScreensManager.getInstance().setOldTempShareStoryId(storyId);
                        }
                        InnerShareData shareData = new InnerShareData();
                        shareData.text = response.getUrl();
                        shareData.payload = response.getPayload();
                        if (parentManager != null) {
                            parentManager.showShareView(shareData);
                        }
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        if (callback != null)
                            callback.onError();
                        InAppStoryService service = InAppStoryService.getInstance();
                        if (service != null) service.isShareProcess(false);
                    }

                    @Override
                    public Type getType() {
                        return ShareObject.class;
                    }
                });
    }
}
