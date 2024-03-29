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

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.callbacks.OnFavoriteItemClick;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListCallback;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.ugc.list.OnUGCItemClick;
import com.inappstory.sdk.ugc.list.UGCListItem;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.List;

public class StoriesAdapter extends RecyclerView.Adapter<BaseStoryListItem> implements ClickCallback {
    public List<Integer> getStoriesIds() {
        return storiesIds;
    }

    private List<Integer> storiesIds = new ArrayList<>();
    private boolean isFavoriteList;
    OnFavoriteItemClick favoriteItemClick;
    OnUGCItemClick ugcItemClick;
    ListCallback callback;

    boolean hasFavItem = false;

    boolean useFavorite;
    boolean useUGC;

    public Context context;
    private String listID;
    private String feed;
    private String feedID;

    public void setFeedID(String feedID) {
        this.feedID = feedID;
    }

    void notifyChanges() {
        if (callback != null)
            callback.storiesUpdated(storiesIds.size(), feed);
    }

    public StoriesAdapter(Context context,
                          String listID,
                          List<Integer> storiesIds,
                          AppearanceManager manager,
                          boolean isFavoriteList,
                          ListCallback callback,
                          String feed,
                          String feedID,
                          boolean useFavorite,
                          OnFavoriteItemClick favoriteItemClick,
                          boolean useUGC,
                          OnUGCItemClick ugcItemClick) {
        this.context = context;
        this.listID = listID;
        this.feed = feed;
        this.storiesIds = storiesIds;
        this.feedID = feedID;
        this.manager = manager;
        this.favoriteItemClick = favoriteItemClick;
        this.ugcItemClick = ugcItemClick;
        this.callback = callback;
        this.isFavoriteList = isFavoriteList;
        this.useFavorite = useFavorite;
        this.useUGC = useUGC;
        hasFavItem = !isFavoriteList && InAppStoryService.isNotNull()
                && manager != null && manager.csHasFavorite()
                && InAppStoryService.getInstance().getFavoriteImages().size() > 0;
        notifyChanges();
    }

    public void refresh(List<Integer> storiesIds) {
        this.storiesIds = storiesIds;
    }

    public int getIndexById(int id) {
        if (storiesIds == null) return -1;
        return storiesIds.indexOf(id) + (useUGC ? 1 : 0);
    }


    AppearanceManager manager;

    @NonNull
    @Override
    public BaseStoryListItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int vType = viewType % 10;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cs_story_list_custom_item, parent, false);
        if (vType == -1) {
            return new StoryFavoriteListItem(v, manager);
        } else if (vType == -2) {
            return new UGCListItem(v, manager);
        } else {
            return new StoryListItem(v, manager, (vType % 5) == 2, vType > 5);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseStoryListItem holder, int position) {
        if (holder == null || InAppStoryService.isNull()) return;
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
        } else if (holder.isUGC) {
            holder.bindUGC();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ugcItemClick != null) {
                        ugcItemClick.onClick();
                    }
                }
            });
        } else {
            int hasUGC = useUGC ? 1 : 0;
            final Story story = InAppStoryService.getInstance().getDownloadManager()
                    .getStoryById(storiesIds.get(position - hasUGC), Story.StoryType.COMMON);
            if (story == null) return;
            String imgUrl = (story.getImage() != null && story.getImage().size() > 0) ?
                    story.getProperImage(manager.csCoverQuality()).getUrl() : null;

            holder.bind(story.id,
                    story.getTitle(),
                    story.getTitleColor() != null ? Color.parseColor(story.getTitleColor()) : null,
                    story.getSource(),
                    imgUrl,
                    Color.parseColor(story.getBackgroundColor()),
                    story.isOpened || isFavoriteList,
                    story.hasAudio(),
                    story.getVideoUrl(), this);
        }
    }

    Long clickTimestamp = -1L;

    @Override
    public void onItemClick(int ind) {
        if (InAppStoryService.isNull()) return;

        if (System.currentTimeMillis() - clickTimestamp < 1500) {
            return;
        }
        int hasUGC = useUGC ? 1 : 0;
        int index = ind - hasUGC;
        clickTimestamp = System.currentTimeMillis();
        InAppStoryService service = InAppStoryService.getInstance();
        Story current = service.getDownloadManager().getStoryById(storiesIds.get(index), Story.StoryType.COMMON);
        if (current != null) {
            if (callback != null) {
                callback.itemClick(current.id, index,
                        StringsUtils.getNonNull(current.statTitle),
                        StringsUtils.getNonNull(current.tags),
                        current.getSlidesCount(),
                        isFavoriteList,
                        StringsUtils.getNonNull(feed));
            }
            String gameInstanceId = current.getGameInstanceId();
            if (gameInstanceId != null) {
                if (!InAppStoryService.isConnected()) {

                    if (CallbackManager.getInstance().getErrorCallback() != null) {
                        CallbackManager.getInstance().getErrorCallback().noConnection();
                    }
                    return;
                }
                service.openGameReaderWithGC(
                        context,
                        new GameStoryData(
                                current.id,
                                0,
                                current.slidesCount,
                                current.title,
                                current.tags,
                                feed,
                                Story.StoryType.COMMON
                        ),
                        gameInstanceId);

                current.isOpened = true;
                current.saveStoryOpened(Story.StoryType.COMMON);
                notifyItemChanged(ind);
                notifyChanges();
                return;
            } else if (current.deeplink != null) {
                StatisticManager.getInstance().sendDeeplinkStory(current.id, current.deeplink, feedID);
                OldStatisticManager.getInstance().addDeeplinkClickStatistic(current.id);
                if (CallbackManager.getInstance().getCallToActionCallback() != null) {
                    CallbackManager.getInstance().getCallToActionCallback().callToAction(
                            new SlideData(
                                    new StoryData(
                                            current.id,
                                            StringsUtils.getNonNull(current.statTitle),
                                            StringsUtils.getNonNull(current.tags),
                                            current.getSlidesCount()
                                    ),
                                    0
                            ),
                            current.deeplink,
                            ClickAction.STORY_FEED_DEEPLINK
                    );
                } else if (CallbackManager.getInstance().getUrlClickCallback() != null) {
                    CallbackManager.getInstance().getUrlClickCallback().onUrlClick(current.deeplink);
                } else {
                    if (!InAppStoryService.isConnected()) {
                        if (CallbackManager.getInstance().getErrorCallback() != null) {
                            CallbackManager.getInstance().getErrorCallback().noConnection();
                        }
                        return;
                    }
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(current.deeplink));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    } catch (Exception ignored) {
                        InAppStoryService.createExceptionLog(ignored);
                    }
                }

                current.isOpened = true;
                current.saveStoryOpened(Story.StoryType.COMMON);
                notifyItemChanged(ind);
                notifyChanges();
                return;
            }
            if (current.isHideInReader()) {

                if (CallbackManager.getInstance().getErrorCallback() != null) {
                    CallbackManager.getInstance().getErrorCallback().emptyLinkError();
                }
                return;
            }
        } else {
            if (callback != null) {
                Story lStory = InAppStoryService.getInstance().getDownloadManager()
                        .getStoryById(storiesIds.get(index), Story.StoryType.COMMON);
                if (lStory != null) {
                    callback.itemClick(lStory.id, index, StringsUtils.getNonNull(lStory.statTitle), "", 0,
                            false, "");
                } else {
                    callback.itemClick(storiesIds.get(index), index, "", "", 0,
                            false, "");
                }
            }
        }
        ArrayList<Integer> tempStories = new ArrayList();
        for (Integer storyId : storiesIds) {
            Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId, Story.StoryType.COMMON);
            if (story == null || !story.isHideInReader())
                tempStories.add(storyId);
        }
        ScreensManager.getInstance().openStoriesReader(context, listID, manager, tempStories,
                tempStories.indexOf(storiesIds.get(index)),
                isFavoriteList ? ShowStory.FAVORITE : ShowStory.LIST, feed, feedID, Story.StoryType.COMMON);
    }


    @Override
    public int getItemViewType(int position) {
        int hasUGC = useUGC ? 1 : 0;
        if (useUGC && position == 0)
            return -2;
        if (useFavorite && position == storiesIds.size() + hasUGC)
            return -1;
        try {
            int pos = position - hasUGC;
            int pref = pos * 10;
            Story story = InAppStoryService.getInstance().getDownloadManager()
                    .getStoryById(storiesIds.get(pos), Story.StoryType.COMMON);
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
        return storiesIds.size() + ((!storiesIds.isEmpty() && hasFavItem) ? 1 : 0) +
                ((!storiesIds.isEmpty() && useUGC) ? 1 : 0);
    }
}
