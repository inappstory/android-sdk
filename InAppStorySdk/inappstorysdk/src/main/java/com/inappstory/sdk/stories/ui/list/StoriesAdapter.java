package com.inappstory.sdk.stories.ui.list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.inappstory.sdk.R;
import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListCallback;
import com.inappstory.sdk.stories.outerevents.CallToAction;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.callbacks.OnFavoriteItemClick;
import com.inappstory.sdk.stories.events.NoConnectionEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.outerevents.ClickOnStory;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.ui.ScreensManager;

public class StoriesAdapter extends RecyclerView.Adapter<StoryListItem> {
    public List<Integer> getStoriesIds() {
        return storiesIds;
    }

    private List<Integer> storiesIds = new ArrayList<>();
    private boolean isFavoriteList;
    OnFavoriteItemClick favoriteItemClick;
    ListCallback callback;

    boolean hasFavItem = false;

    public Context context;

    public StoriesAdapter(Context context, List<Integer> storiesIds, AppearanceManager manager,
                          OnFavoriteItemClick favoriteItemClick, boolean isFavoriteList, ListCallback callback) {
        this.context = context;
        this.storiesIds = storiesIds;
        this.manager = manager;
        this.favoriteItemClick = favoriteItemClick;
        this.callback = callback;
        this.isFavoriteList = isFavoriteList;
        hasFavItem = !isFavoriteList && InAppStoryService.isNotNull()
                && manager != null && manager.csHasFavorite()
                && InAppStoryService.getInstance().getFavoriteImages().size() > 0;
    }

    public void refresh(List<Integer> storiesIds) {
        this.storiesIds = storiesIds;
    }

    public int getIndexById(int id) {
        if (storiesIds == null) return -1;
        return storiesIds.indexOf(id);
    }


    AppearanceManager manager;

    @NonNull
    @Override
    public StoryListItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int vType = viewType % 10;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cs_story_list_custom_item, parent, false);
        return new StoryListItem(v, manager, (vType % 5) == 2, vType == 3, vType > 5);
    }

    @Override
    public void onBindViewHolder(@NonNull final StoryListItem holder, final int position) {
        if (holder == null) return;
        if (holder.isFavorite) {
            holder.bindFavorite();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favoriteItemClick != null) {
                        favoriteItemClick.onClick();
                    }
                }
            });
        } else {
            if (InAppStoryService.isNotNull()) {
                final Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storiesIds.get(position));
                if (story == null) return;
                holder.bind(story.getTitle(),
                        story.getTitleColor() != null ? Color.parseColor(story.getTitleColor()) : null,
                        story.getSource(),
                        (story.getImage() != null && story.getImage().size() > 0) ?
                                story.getProperImage(manager.csCoverQuality()).getUrl() : null,
                        Color.parseColor(story.getBackgroundColor()),
                        story.isOpened || isFavoriteList, story.hasAudio(),
                        story.getVideoUrl());
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(position);
                }
            });
        }

    }

    public void onItemClick(int index) {
        if (InAppStoryService.isNull()) return;
        Story current = InAppStoryService.getInstance().getDownloadManager().getStoryById(storiesIds.get(index));
        if (current != null) {
            CsEventBus.getDefault().post(new ClickOnStory(current.id, index, current.title,
                    current.tags, current.getSlidesCount(),
                    isFavoriteList ? ClickOnStory.FAVORITE : ClickOnStory.LIST));
            if (callback != null) {
                callback.itemClick(current.id, index, current.title, current.tags,
                        current.getSlidesCount(), isFavoriteList);
            }
            if (current.deeplink != null) {
                StatisticManager.getInstance().sendDeeplinkStory(current.id, current.deeplink);
                OldStatisticManager.getInstance().addDeeplinkClickStatistic(current.id);
                CsEventBus.getDefault().post(new CallToAction(current.id, current.title,
                        current.tags, current.getSlidesCount(), 0,
                        current.deeplink, CallToAction.DEEPLINK));
                if (CallbackManager.getInstance().getCallToActionCallback() != null) {
                    CallbackManager.getInstance().getCallToActionCallback().callToAction(
                            current.id, current.title,
                            current.tags, current.getSlidesCount(), 0,
                            current.deeplink, ClickAction.DEEPLINK);
                }
                if (CallbackManager.getInstance().getUrlClickCallback() != null) {
                    CallbackManager.getInstance().getUrlClickCallback().onUrlClick(current.deeplink);
                    current.isOpened = true;
                    current.saveStoryOpened();
                    notifyItemChanged(index);
                } else {
                    if (!InAppStoryService.isConnected()) {
                        if (CallbackManager.getInstance().getErrorCallback() != null) {
                            CallbackManager.getInstance().getErrorCallback().noConnection();
                        }
                        CsEventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.LINK));
                        return;
                    }
                    current.isOpened = true;
                    current.saveStoryOpened();
                    notifyItemChanged(index);
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(current.deeplink));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    } catch (Exception ignored) {
                    }
                }
                return;
            }
            if (current.isHideInReader()) {

                if (CallbackManager.getInstance().getErrorCallback() != null) {
                    CallbackManager.getInstance().getErrorCallback().emptyLinkError();
                }
                CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.EMPTY_LINK));
                return;
            }
        } else {
            CsEventBus.getDefault().post(new ClickOnStory(storiesIds.get(index), index, null,
                    null, 0, isFavoriteList ? ClickOnStory.FAVORITE : ClickOnStory.LIST));

            if (callback != null) {
                callback.itemClick(storiesIds.get(index), index, null, null, 0,
                        isFavoriteList);
            }
        }
        ArrayList<Integer> tempStories = new ArrayList();
        for (Integer storyId : storiesIds) {
            Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
            if (story == null || !story.isHideInReader())
                tempStories.add(storyId);
        }
        ScreensManager.getInstance().openStoriesReader(context, manager, tempStories,
                tempStories.indexOf(storiesIds.get(index)),
                isFavoriteList ? ShowStory.FAVORITE : ShowStory.LIST);
        InAppStoryService.getInstance().getDownloadManager().putStories(
                InAppStoryService.getInstance().getDownloadManager().getStories());
    }

    @Override
    public int getItemViewType(int position) {
        int pref = position * 10;
        if (manager != null && manager.csHasFavorite() && position == storiesIds.size())
            return pref + 3;
        try {
            Story story = InAppStoryService.getInstance().getDownloadManager()
                    .getStoryById(storiesIds.get(position));
            if (story.getVideoUrl() != null) pref += 5;
            return story.isOpened ? (pref + 2) : (pref + 1);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return storiesIds.size() + (hasFavItem ? 1 : 0);
    }
}
