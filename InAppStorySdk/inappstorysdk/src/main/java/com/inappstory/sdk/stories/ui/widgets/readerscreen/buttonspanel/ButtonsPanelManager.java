package com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel;

import android.os.Build;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.repository.stories.IStoriesRepository;
import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.core.network.NetworkClient;
import com.inappstory.sdk.core.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.network.models.Response;
import com.inappstory.sdk.stories.api.models.ShareObject;
import com.inappstory.sdk.stories.api.models.Story.StoryType;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryData;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;
import com.inappstory.sdk.usecase.callbacks.IUseCaseCallback;
import com.inappstory.sdk.usecase.callbacks.UseCaseCallbackLikeDislikeStory;
import com.inappstory.sdk.usecase.callbacks.UseCaseCallbackShareClick;

import java.lang.reflect.Type;

public class ButtonsPanelManager {
    public void setStoryIdAndType(int storyId, StoryType type) {
        this.storyId = storyId;
        this.type = type;
    }

    int storyId;

    StoryType type;

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
        IStoriesRepository storiesRepository =
                IASCore.getInstance().getStoriesRepository(parentManager.getStoryType());
        final IStoryDTO story = storiesRepository.getStoryById(storyId);
        if (story == null) return;
        NetworkClient networkClient = IASCore.getInstance().getNetworkClient();
        if (networkClient == null) {
            return;
        }
        final int val;
        final int slideIndex = storiesRepository.getStoryLastIndex(storyId);
        boolean liked = story.getLike() == 1;
        boolean disliked = story.getLike() == -1;
        SlideData slideData = new SlideData(
                new StoryData(
                        story,
                        getParentManager().getFeedId(),
                        getParentManager().getSourceType()

                ),
                slideIndex
        );
        IUseCaseCallback likeDislikeUseCaseCallback = new UseCaseCallbackLikeDislikeStory(
                slideData,
                like,
                like ? liked : disliked
        );
        likeDislikeUseCaseCallback.invoke();
        if (like) {
            if (liked) {
                val = 0;
            } else {
                StatisticManager.getInstance().sendLikeStory(story.getLike(), slideIndex,
                        parentManager != null ? parentManager.getFeedId() : null);
                val = 1;
            }
        } else {
            if (disliked) {
                val = 0;
            } else {
                StatisticManager.getInstance().sendDislikeStory(story.getId(), slideIndex,
                        parentManager != null ? parentManager.getFeedId() : null);
                val = -1;
            }
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
                        story.setLike(val);
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
        IStoriesRepository storiesRepository =
                IASCore.getInstance().getStoriesRepository(parentManager.getStoryType());
        final IStoryDTO story = storiesRepository.getStoryById(storyId);
        if (story == null) return;
        final int slideIndex = storiesRepository.getStoryLastIndex(storyId);
        SlideData slideData = new SlideData(
                new StoryData(
                       story,
                        getParentManager().getFeedId(),
                        getParentManager().getSourceType()
                ),
                slideIndex
        );
        if (story.getFavorite()) {
            storiesRepository.addToFavorite(storyId);
        } else {
            storiesRepository.removeFromFavorite(storyId);
        }
    }

    ButtonsPanel panel;

    public void soundClick() {
        parentManager.changeSoundStatus();
    }

    public void refreshSoundStatus() {
        if (panel != null)
            panel.refreshSoundStatus();
    }

    public abstract static class ShareButtonClickCallback implements ButtonClickCallback {
        abstract void onClick();
    }

    public void shareClick(final ShareButtonClickCallback callback) {
        if (IASCore.getInstance().isShareProcess()) return;
        IStoriesRepository storiesRepository =
                IASCore.getInstance().getStoriesRepository(parentManager.getStoryType());
        final IStoryDTO story = storiesRepository.getStoryById(storyId);
        if (story == null) return;
        NetworkClient networkClient = IASCore.getInstance().getNetworkClient();
        if (networkClient == null) {
            return;
        }
        final int slideIndex = storiesRepository.getStoryLastIndex(storyId);
        StatisticManager.getInstance().sendShareStory(
                story.getId(),
                slideIndex,
                story.shareType(slideIndex),
                parentManager != null ? parentManager.getFeedId() : null
        );
        IUseCaseCallback shareClick = new UseCaseCallbackShareClick(
                new SlideData(
                        new StoryData(
                                story,
                                getParentManager().getFeedId(),
                                getParentManager().getSourceType()
                        ),
                        slideIndex
                )
        );
        shareClick.invoke();
        if (story.isScreenshotShare(slideIndex)) {
            parentManager.screenshotShare();
            return;
        }
        IASCore.getInstance().isShareProcess(true);
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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                            ScreensManager.getInstance().setTempShareId(null);
                            ScreensManager.getInstance().setTempShareStoryId(storyId);
                        } else {
                            ScreensManager.getInstance().setOldTempShareId(null);
                            ScreensManager.getInstance().setOldTempShareStoryId(storyId);
                        }
                        InnerShareData shareData = new InnerShareData();
                        shareData.text = response.getUrl();
                        shareData.payload = story.getSlideEventPayload(slideIndex);
                        if (parentManager != null) {
                            parentManager.showShareView(shareData);
                        }
                    }

                    @Override
                    public void errorDefault(String message) {
                        if (callback != null)
                            callback.onError();

                        IASCore.getInstance().isShareProcess(false);
                    }

                    @Override
                    public Type getType() {
                        return ShareObject.class;
                    }
                }
        );
    }
}
