package com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.ui.screens.ShareProcessHandler;
import com.inappstory.sdk.core.utils.CallbackTypesConverter;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.share.IShareCompleteListener;
import com.inappstory.sdk.stories.api.models.ShareObject;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickOnShareStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.FavoriteStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.LikeDislikeStoryCallback;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;

import java.lang.reflect.Type;

public class ButtonsPanelManager {
    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    int storyId;

    private final IASCore core;

    public ReaderPageManager getPageManager() {
        return pageManager;
    }

    public void setPageManager(ReaderPageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void unlockShareButton() {
        if (panel != null && panel.share != null) {
            panel.share.post(new Runnable() {
                @Override
                public void run() {
                    panel.share.setClickable(true);
                    panel.share.setEnabled(true);
                }
            });
        }
    }

    public ButtonsPanelManager(ButtonsPanel panel, IASCore core) {
        this.core = core;
        this.panel = panel;
    }

    ReaderPageManager pageManager;

    public void likeClick(ButtonClickCallback callback) {
        likeDislikeClick(callback, true);
    }

    public void dislikeClick(ButtonClickCallback callback) {
        likeDislikeClick(callback, false);
    }

    private void likeDislikeClick(final ButtonClickCallback callback, final boolean like) {
        InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        if (inAppStoryService == null) return;
        NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            return;
        }
        final Story story =
                inAppStoryService.getStoryDownloadManager().getStoryById(
                        storyId, pageManager.getStoryType()
                );
        if (story == null) return;
        final int val;
        core.callbacksAPI().useCallback(
                IASCallbackType.CLICK_SHARE,
                new UseIASCallback<LikeDislikeStoryCallback>() {
                    @Override
                    public void use(@NonNull LikeDislikeStoryCallback callback) {
                        if (like) {
                            callback.likeStory(
                                    pageManager.getSlideData(story),
                                    !story.liked()
                            );
                        } else {
                            callback.dislikeStory(
                                    pageManager.getSlideData(story),
                                    !story.disliked()
                            );
                        }
                    }
                }
        );
        if (like && !story.liked()) {
            StatisticManager.getInstance().sendLikeStory(story.id, story.lastIndex,
                    pageManager != null ? pageManager.getFeedId() : null);
            val = 1;
        } else if (!like && !story.disliked()) {
            StatisticManager.getInstance().sendDislikeStory(story.id, story.lastIndex,
                    pageManager != null ? pageManager.getFeedId() : null);
            val = -1;
        } else {
            val = 0;
        }
        final String likeUID =
                ProfilingManager.getInstance().addTask("api_like");
        networkClient.enqueue(
                networkClient.getApi().storyLike(
                        Integer.toString(storyId),
                        val
                ),
                new NetworkCallback<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        ProfilingManager.getInstance().setReady(likeUID);
                        Story story = InAppStoryService.getInstance().getStoryDownloadManager().getStoryById(storyId, pageManager.getStoryType());
                        if (story != null)
                            story.like = val;
                        if (callback != null)
                            callback.onSuccess(val);
                    }


                    @Override
                    public void errorDefault(String message) {

                        ProfilingManager.getInstance().setReady(likeUID);
                        if (callback != null)
                            callback.onError();
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
        final InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        if (inAppStoryService == null) return;
        NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            return;
        }
        final Story story = inAppStoryService.getStoryDownloadManager().getStoryById(storyId, pageManager.getStoryType());
        if (story == null) return;
        final boolean val = story.favorite;
        if (!story.favorite)
            StatisticManager.getInstance().sendFavoriteStory(story.id, story.lastIndex,
                    pageManager != null ? pageManager.getFeedId() : null);
        core.callbacksAPI().useCallback(
                IASCallbackType.FAVORITE,
                new UseIASCallback<FavoriteStoryCallback>() {
                    @Override
                    public void use(@NonNull FavoriteStoryCallback callback) {
                        callback.favoriteStory(
                                pageManager.getSlideData(story),
                                !story.favorite
                        );
                    }
                });
        final String favUID = ProfilingManager.getInstance().addTask("api_favorite");
        networkClient.enqueue(
                networkClient.getApi().storyFavorite(
                        Integer.toString(storyId),
                        val ? 0 : 1
                ),
                new NetworkCallback<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        ProfilingManager.getInstance().setReady(favUID);
                        Story story = inAppStoryService.getStoryDownloadManager().getStoryById(storyId, pageManager.getStoryType());
                        boolean res = !val;
                        if (story != null)
                            story.favorite = res;
                        if (callback != null)
                            callback.onSuccess(res ? 1 : 0);
                        inAppStoryService.getListReaderConnector().storyFavorite(storyId, res);
                    }


                    @Override
                    public void errorDefault(String message) {

                        ProfilingManager.getInstance().setReady(favUID);
                        if (callback != null)
                            callback.onError();
                    }

                    @Override
                    public Type getType() {
                        return null;
                    }
                }
        );
    }

    ButtonsPanel panel;

    public void soundClick() {
        pageManager.changeSoundStatus();
    }

    public void refreshSoundStatus() {
        if (panel != null)
            panel.refreshSoundStatus();
    }

    public abstract static class ShareButtonClickCallback implements ButtonClickCallback {
        abstract void onClick();
    }

    public void shareClick(final ShareButtonClickCallback callback) {
        InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            return;
        }
        if (inAppStoryService == null)
            return;
        final ShareProcessHandler shareProcessHandler = core.screensManager().getShareProcessHandler();
        if (shareProcessHandler == null || shareProcessHandler.isShareProcess()) return;
        final Story story = inAppStoryService.getStoryDownloadManager().getStoryById(storyId, pageManager.getStoryType());
        if (story == null) return;
        final int slideIndex = story.lastIndex;
        StatisticManager.getInstance().sendShareStory(story.id, slideIndex,
                story.shareType(slideIndex),
                pageManager != null ? pageManager.getFeedId() : null);
        core.callbacksAPI().useCallback(
                IASCallbackType.CLICK_SHARE,
                new UseIASCallback<ClickOnShareStoryCallback>() {
                    @Override
                    public void use(@NonNull ClickOnShareStoryCallback callback) {
                        callback.shareClick(
                                pageManager.getSlideData(story)
                        );
                    }
                });
        if (story.isScreenshotShare(slideIndex)) {
            pageManager.screenshotShare();
            return;
        }
        shareProcessHandler.isShareProcess(true);
        if (callback != null)
            callback.onClick();
        final String shareUID = ProfilingManager.getInstance().addTask("api_share");
        networkClient.enqueue(
                networkClient.getApi().share(
                        Integer.toString(storyId),
                        null
                ),
                new NetworkCallback<ShareObject>() {
                    @Override
                    public void onSuccess(ShareObject response) {
                        ProfilingManager.getInstance().setReady(shareUID);
                        shareProcessHandler.shareCompleteListener(new IShareCompleteListener(
                                null, storyId
                        ) {
                            @Override
                            public void complete(String shareId, boolean shared) {
                                pageManager.shareComplete(shareId, shared);
                            }
                        });
                        InnerShareData shareData = new InnerShareData();
                        shareData.text = response.getUrl();
                        shareData.payload = story.getSlideEventPayload(slideIndex);
                        if (pageManager != null) {
                            pageManager.showShareView(shareData);
                        }
                    }

                    @Override
                    public void errorDefault(String message) {
                        if (callback != null)
                            callback.onError();
                        shareProcessHandler.isShareProcess(false);
                    }

                    @Override
                    public Type getType() {
                        return ShareObject.class;
                    }
                }
        );
    }
}
