package com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel;

import android.content.Intent;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.stories.api.models.ShareObject;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.cache.OldStoryDownloader;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.events.PauseStoryReaderEvent;
import com.inappstory.sdk.stories.events.SoundOnOffEvent;
import com.inappstory.sdk.stories.outerevents.ClickOnShareStory;
import com.inappstory.sdk.stories.outerevents.DislikeStory;
import com.inappstory.sdk.stories.outerevents.FavoriteStory;
import com.inappstory.sdk.stories.outerevents.LikeStory;
import com.inappstory.sdk.stories.serviceevents.LikeDislikeEvent;
import com.inappstory.sdk.stories.serviceevents.StoryFavoriteEvent;

import java.lang.reflect.Type;

public class ButtonsPanelManager {
    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    int storyId;

    public void likeClick(ButtonClickCallback callback) {
        likeDislikeClick(callback, true);
    }

    public void dislikeClick(ButtonClickCallback callback) {
        likeDislikeClick(callback, false);
    }

    private void likeDislikeClick(final ButtonClickCallback callback, boolean like) {
        if (InAppStoryManager.isNull()) return;
        final Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        final int val;
        if (like) {
            if (story.liked()) {
                CsEventBus.getDefault().post(new LikeStory(story.id, story.title,
                        story.tags, story.slidesCount, story.lastIndex, false));
                val = 0;
            } else {
                CsEventBus.getDefault().post(new LikeStory(story.id, story.title,
                        story.tags, story.slidesCount, story.lastIndex, true));
                StatisticManager.getInstance().sendLikeStory(story.id, story.lastIndex);
                val = 1;
            }
        } else {
            if (story.disliked()) {
                CsEventBus.getDefault().post(new DislikeStory(story.id, story.title,
                        story.tags, story.slidesCount, story.lastIndex, false));
                val = 0;
            } else {
                CsEventBus.getDefault().post(new DislikeStory(story.id, story.title,
                        story.tags, story.slidesCount, story.lastIndex, true));
                StatisticManager.getInstance().sendDislikeStory(story.id, story.lastIndex);
                val = -1;
            }
        }
        NetworkClient.getApi().storyLike(Integer.toString(storyId),
                StatisticSession.getInstance().id,
                InAppStoryManager.getInstance().getApiKey(), val).enqueue(
                new NetworkCallback<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        if (story != null)
                            story.like = val;
                        if (callback != null)
                            callback.onSuccess(val);
                        CsEventBus.getDefault().post(new LikeDislikeEvent(storyId, val));
                    }


                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        if (callback != null)
                            callback.onError();
                    }

                    @Override
                    public Type getType() {
                        return null;
                    }
                });
    }

    public void favoriteClick(final ButtonClickCallback callback) {
        if (InAppStoryManager.isNull()) return;
        final Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        final boolean val = story.favorite;
        if (!story.favorite)
            StatisticManager.getInstance().sendFavoriteStory(story.id, story.lastIndex);
        CsEventBus.getDefault().post(new FavoriteStory(story.id, story.title,
                story.tags, story.slidesCount, story.lastIndex, !story.favorite));
        NetworkClient.getApi().storyFavorite(Integer.toString(storyId),
                StatisticSession.getInstance().id,
                InAppStoryManager.getInstance().getApiKey(), val ? 0 : 1).enqueue(
                new NetworkCallback<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        if (story != null)
                            story.favorite = !val;
                        if (callback != null)
                            callback.onSuccess(val ? 1 : 0);
                        CsEventBus.getDefault().post(new StoryFavoriteEvent(storyId, !val));
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        if (callback != null)
                            callback.onError();
                    }

                    @Override
                    public Type getType() {
                        return null;
                    }
                });
    }

    public void soundClick(ButtonClickCallback callback) {
        if (InAppStoryManager.isNull()) return;
        InAppStoryManager.getInstance().soundOn = !InAppStoryManager.getInstance().soundOn;
        CsEventBus.getDefault().post(new SoundOnOffEvent(InAppStoryManager.getInstance().soundOn, storyId));
        if (callback != null)
            callback.onSuccess(InAppStoryManager.getInstance().soundOn ? 1 : 0);
    }

    public void shareClick(final ButtonClickCallback callback) {
        if (InAppStoryManager.isNull()) return;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        StatisticManager.getInstance().sendShareStory(story.id, story.lastIndex);
        CsEventBus.getDefault().post(new ClickOnShareStory(story.id, story.title,
                story.tags, story.slidesCount, story.lastIndex));
        CsEventBus.getDefault().post(new PauseStoryReaderEvent(false));
        NetworkClient.getApi().share(Integer.toString(storyId), StatisticSession.getInstance().id,
                InAppStoryManager.getInstance().getApiKey(), null).enqueue(new NetworkCallback<ShareObject>() {
            @Override
            public void onSuccess(ShareObject response) {
                if (callback != null)
                    callback.onSuccess(0);
                if (CallbackManager.getInstance().getShareCallback() != null) {
                    CallbackManager.getInstance().getShareCallback()
                            .onShare(response.getUrl(), response.getTitle(), response.getDescription(), Integer.toString(storyId));
                } else {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, response.getTitle());
                    sendIntent.putExtra(Intent.EXTRA_TEXT, response.getUrl());
                    sendIntent.setType("text/plain");
                    Intent finalIntent = Intent.createChooser(sendIntent, null);
                    finalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    InAppStoryManager.getInstance().getContext().startActivity(finalIntent);
                }
            }

            @Override
            public void onError(int code, String message) {
                super.onError(code, message);
                if (callback != null)
                    callback.onError();
            }

            @Override
            public Type getType() {
                return ShareObject.class;
            }
        });
    }
}
