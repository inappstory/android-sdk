package com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel;

import android.os.Build;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.repository.stories.IStoriesRepository;
import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.core.repository.stories.interfaces.IChangeStatusReaderCallback;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.core.utils.network.NetworkClient;
import com.inappstory.sdk.core.utils.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.models.api.ShareObject;
import com.inappstory.sdk.core.models.api.Story.StoryType;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryData;
import com.inappstory.sdk.core.repository.statistic.ProfilingManager;
import com.inappstory.sdk.core.repository.statistic.StatisticV2Manager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;
import com.inappstory.sdk.usecase.callbacks.IUseCaseCallback;
import com.inappstory.sdk.usecase.callbacks.UseCaseCallbackFavoriteStory;
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

    public void likeClick() {
        likeDislikeClick(true);
    }

    public void dislikeClick() {
        likeDislikeClick(false);
    }

    private void likeDislikeClick(boolean like) {
        IStoriesRepository storiesRepository =
                IASCore.getInstance().getStoriesRepository(parentManager.getStoryType());
        final IStoryDTO story = storiesRepository.getStoryById(storyId);
        if (story == null) return;
        NetworkClient networkClient = IASCore.getInstance().getNetworkClient();
        if (networkClient == null) {
            return;
        }
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
        if (like && !liked) {
            storiesRepository.likeStory(storyId);
        } else if (!like && !disliked) {
            storiesRepository.dislikeStory(storyId);
        } else {
            storiesRepository.clearLikeDislikeStoryStatus(storyId);
        }
    }

    public void removeStoryFromFavorite() {
        if (panel != null)
            panel.forceRemoveFromFavorite();
    }

    public void favoriteClick() {
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
        IUseCaseCallback favoriteUseCaseCallback = new UseCaseCallbackFavoriteStory(
                slideData,
                !story.getFavorite()
        );
        favoriteUseCaseCallback.invoke();
        if (!story.getFavorite()) {
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


    public void shareClick(final @NonNull IChangeStatusReaderCallback callback) {
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
        StatisticV2Manager.getInstance().sendShareStory(
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
        callback.onProcess();
        IASCore.getInstance().isShareProcess(true);
        final String shareUID = ProfilingManager.getInstance().addTask("api_share");
        networkClient.enqueue(
                networkClient.getApi().share(
                        Integer.toString(storyId),
                        null
                ),
                new NetworkCallback<ShareObject>() {
                    @Override
                    public void onSuccess(ShareObject response) {
                        callback.onSuccess(1);
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
