package com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.dataholders.IReaderContent;
import com.inappstory.sdk.core.dataholders.IReaderContentWithStatus;
import com.inappstory.sdk.core.ui.screens.ShareProcessHandler;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.share.IShareCompleteListener;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.api.models.ShareObject;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickOnShareStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.FavoriteStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.LikeDislikeStoryCallback;
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

        final IReaderContent story =
                core.contentHolder().readerContent().getByIdAndType(
                        storyId, pageManager.getViewContentType()
                );
        if (!(story instanceof IReaderContentWithStatus)) return;
        final IReaderContentWithStatus storyWithStatus = (IReaderContentWithStatus) story;
        final int val;
        ContentIdWithIndex idWithIndex = pageManager.getParentManager().getByIdAndIndex(storyId);
        core.callbacksAPI().useCallback(
                IASCallbackType.CLICK_SHARE,
                new UseIASCallback<LikeDislikeStoryCallback>() {
                    @Override
                    public void use(@NonNull LikeDislikeStoryCallback callback) {
                        if (like) {
                            callback.likeStory(
                                    pageManager.getSlideData(story),
                                    storyWithStatus.like() != 1
                            );
                        } else {
                            callback.dislikeStory(
                                    pageManager.getSlideData(story),
                                    storyWithStatus.like() != -1
                            );
                        }
                    }
                }
        );
        if (like && storyWithStatus.like() != 1) {
            core.statistic().v2().sendLikeStory(idWithIndex.id(), idWithIndex.index(),
                    pageManager != null ? pageManager.getFeedId() : null);
            val = 1;
        } else if (!like && storyWithStatus.like() != -1) {
            core.statistic().v2().sendDislikeStory(idWithIndex.id(), idWithIndex.index(),
                    pageManager != null ? pageManager.getFeedId() : null);
            val = -1;
        } else {
            val = 0;
        }
        final String likeUID =
                core.statistic().profiling().addTask("api_like");
        core.network().enqueue(
                core.network().getApi().storyLike(
                        Integer.toString(storyId),
                        val
                ),
                new NetworkCallback<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        core.statistic().profiling().setReady(likeUID);
                        Story story = core.contentLoader().storyDownloadManager()
                                .getStoryById(storyId, pageManager.getViewContentType());
                        if (story != null)
                            story.like(val);
                        if (callback != null)
                            callback.onSuccess(val);
                    }


                    @Override
                    public void errorDefault(String message) {
                        core.statistic().profiling().setReady(likeUID);
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
        final IReaderContent story =
                core.contentHolder().readerContent().getByIdAndType(
                        storyId, pageManager.getViewContentType()
                );
        if (!(story instanceof IReaderContentWithStatus))return;
        final IReaderContentWithStatus storyWithStatus = (IReaderContentWithStatus) story;
        ContentIdWithIndex idWithIndex = pageManager.getParentManager().getByIdAndIndex(storyId);
        final boolean val = storyWithStatus.favorite();
        if (!val)
            core.statistic().v2().sendFavoriteStory(idWithIndex.id(), idWithIndex.index(),
                    pageManager != null ? pageManager.getFeedId() : null);
        core.callbacksAPI().useCallback(
                IASCallbackType.FAVORITE,
                new UseIASCallback<FavoriteStoryCallback>() {
                    @Override
                    public void use(@NonNull FavoriteStoryCallback callback) {
                        callback.favoriteStory(
                                pageManager.getSlideData(story),
                                !val
                        );
                    }
                });
        final String favUID = core.statistic().profiling().addTask("api_favorite");
        core.network().enqueue(
                core.network().getApi().storyFavorite(
                        Integer.toString(storyId),
                        val ? 0 : 1
                ),
                new NetworkCallback<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        core.statistic().profiling().setReady(favUID);
                        boolean res = !val;
                        storyWithStatus.favorite(res);
                        if (callback != null)
                            callback.onSuccess(res ? 1 : 0);
                        core.inAppStoryService()
                                .getListReaderConnector().storyFavorite(storyId, res);
                    }


                    @Override
                    public void errorDefault(String message) {
                        core.statistic().profiling().setReady(favUID);
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
            panel.refreshSoundStatus(core);
    }

    public abstract static class ShareButtonClickCallback implements ButtonClickCallback {
        abstract void onClick();
    }

    public void shareClick(final ShareButtonClickCallback callback) {

        final ShareProcessHandler shareProcessHandler = core.screensManager().getShareProcessHandler();
        if (shareProcessHandler == null || shareProcessHandler.isShareProcess()) return;
        ContentIdWithIndex idWithIndex = pageManager.getParentManager().getByIdAndIndex(storyId);
        final IReaderContent story = core.contentLoader().storyDownloadManager()
                .getStoryById(storyId, pageManager.getViewContentType());
        if (story == null) return;
        final int slideIndex = idWithIndex.index();
        final int shareType = story.shareType(slideIndex);
        core.statistic().v2().sendShareStory(storyId, slideIndex,
                shareType,
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
        if (shareType == 1) {
            pageManager.screenshotShare();
            return;
        }
        shareProcessHandler.isShareProcess(true);
        if (callback != null)
            callback.onClick();
        final String shareUID = core.statistic().profiling().addTask("api_share");
        core.network().enqueue(
                core.network().getApi().share(
                        Integer.toString(storyId),
                        null
                ),
                new NetworkCallback<ShareObject>() {
                    @Override
                    public void onSuccess(ShareObject response) {
                        core.statistic().profiling().setReady(shareUID);
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
                        shareData.payload = story.slideEventPayload(slideIndex);
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
