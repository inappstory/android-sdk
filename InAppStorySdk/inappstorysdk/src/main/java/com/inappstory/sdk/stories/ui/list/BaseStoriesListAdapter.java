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
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListCallback;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.list.items.IStoriesListCommonItem;
import com.inappstory.sdk.stories.ui.list.items.IStoriesListFavoriteItem;
import com.inappstory.sdk.stories.ui.list.items.IStoriesListUGCEditorItem;
import com.inappstory.sdk.stories.ui.list.items.IStoriesListItemWithCover;
import com.inappstory.sdk.stories.ui.list.items.BaseStoriesListItem;
import com.inappstory.sdk.stories.uidomain.list.StoriesAdapterStoryData;
import com.inappstory.sdk.stories.uidomain.list.items.story.IStoriesListItemClick;
import com.inappstory.sdk.stories.uidomain.list.listnotify.IStoriesListNotify;
import com.inappstory.sdk.ugc.list.OnUGCItemClick;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.List;

abstract class BaseStoriesListAdapter
        extends RecyclerView.Adapter<BaseStoriesListItem>
        implements IStoriesListAdapter, ClickCallback {
    public List<StoriesAdapterStoryData> getStoriesData() {
        return storiesData;
    }

    private List<StoriesAdapterStoryData> storiesData = new ArrayList<>();
    private boolean isFavoriteList;

    private final IStoriesListItemClick storiesListItemClick;
    private final OnFavoriteItemClick favoriteItemClick;
    private final OnUGCItemClick ugcItemClick;
    ListCallback callback;

    boolean hasFavItem = false;

    boolean useFavorite;
    boolean useUGC;

    public Context context;
    private String listID;
    private String feed;

    private IStoriesListNotify storiesListNotify;

    private void addStoryData() {

    }

    private void removeStoryDataById() {

    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    void notifyChanges() {
        if (callback != null)
            callback.storiesUpdated(storiesData.size(), feed);
    }

    public BaseStoriesListAdapter(Context context,
                                  String listID,
                                  IStoriesListNotify storiesListNotify,
                                  List<StoriesAdapterStoryData> storiesData,
                                  AppearanceManager manager,
                                  boolean isFavoriteList,
                                  ListCallback callback,
                                  String feed,
                                  boolean useFavorite,
                                  boolean useUGC,
                                  IStoriesListItemClick storiesListItemClick,
                                  OnFavoriteItemClick favoriteItemClick,
                                  OnUGCItemClick ugcItemClick) {
        this.storiesListNotify = storiesListNotify;
        this.context = context;
        this.listID = listID;
        this.feed = feed;
        this.storiesData = storiesData;
        this.manager = manager;
        this.callback = callback;
        this.isFavoriteList = isFavoriteList;
        this.useFavorite = useFavorite;
        this.useUGC = useUGC;

        this.storiesListItemClick = storiesListItemClick;
        this.favoriteItemClick = favoriteItemClick;
        this.ugcItemClick = ugcItemClick;

        InAppStoryService service = InAppStoryService.getInstance();
        hasFavItem = !isFavoriteList && service != null
                && manager != null && manager.csHasFavorite()
                && service.getFavoriteImages().size() > 0;
        notifyChanges();
    }

    public int getIndexById(int id) {
        if (storiesData == null) return -1;
        for (int i = 0; i < storiesData.size(); i++) {
            if (storiesData.get(i).getId() == id) return i + (useUGC ? 1 : 0);
        }
        return -1;
    }


    protected AppearanceManager manager;

    @NonNull
    @Override
    public BaseStoriesListItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return getViewHolderItem(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.cs_story_list_custom_item,
                        parent,
                        false
                ),
                viewType
        );
      /*  if (vType == -1) {
            return new StoriesListFavoriteItem(v, manager);
        } else if (vType == -2) {
            return new StoriesListUgcEditorItem(v, manager);
        } else {
            return new StoriesListItem(v, manager, (vType % 5) == 2);
        }*/
    }


    @Override
    public void onBindViewHolder(@NonNull BaseStoriesListItem holder, int position) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        if (holder instanceof IStoriesListFavoriteItem) {
            ((IStoriesListFavoriteItem) holder).bindFavorite();
            ((IStoriesListFavoriteItem) holder).setImages();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickWithDelay(new Runnable() {
                        @Override
                        public void run() {
                            if (favoriteItemClick != null) {
                                favoriteItemClick.onClick();
                            }
                        }
                    });

                }
            });
        } else if (holder instanceof IStoriesListUGCEditorItem) {
            ((IStoriesListUGCEditorItem) holder).bindUGC();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickWithDelay(new Runnable() {
                        @Override
                        public void run() {
                            if (ugcItemClick != null) {
                                ugcItemClick.onClick();
                            }
                        }
                    });

                }
            });
        } else if (holder instanceof IStoriesListCommonItem) {
            int hasUGC = useUGC ? 1 : 0;
            final StoriesAdapterStoryData story = storiesData.get(position - hasUGC);
            if (story == null) return;
            String imgUrl = story.getImageUrl();
            ((IStoriesListCommonItem) holder).bindCommon(
                    story.getId(),
                    story.getTitle(),
                    story.getTitleColor() != null ? Color.parseColor(story.getTitleColor()) : null,
                    Color.parseColor(story.getBackgroundColor()),
                    story.isOpened() || isFavoriteList,
                    story.hasAudio(),
                    this
            );
            if (holder instanceof IStoriesListItemWithCover) {
                ((IStoriesListItemWithCover) holder).setImage(imgUrl);
                ((IStoriesListItemWithCover) holder).setVideo(story.getVideoUrl());
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickWithDelay(new Runnable() {
                        @Override
                        public void run() {
                            if (storiesListItemClick != null) {
                                storiesListItemClick.onClick(story);
                            }
                        }
                    });
                }
            });
        }
    }

    Long clickTimestamp = -1L;

    private SourceType getListSourceType() {
        return isFavoriteList ? SourceType.FAVORITE : SourceType.LIST;
    }

    private void clickWithDelay(Runnable runnable) {
        if (System.currentTimeMillis() - clickTimestamp < 1500) {
            return;
        }
        clickTimestamp = System.currentTimeMillis();
        runnable.run();
    }

    @Override
    public void onItemClick(int ind) {
        if (InAppStoryService.isNull()) return;


        int hasUGC = useUGC ? 1 : 0;
        int index = ind - hasUGC;
        InAppStoryService service = InAppStoryService.getInstance();

        final StoriesAdapterStoryData current = storiesData.get(index);
        if (callback != null) {
            callback.itemClick(
                    new StoryData(
                            current.getId(),
                            StringsUtils.getNonNull(current.getStatTitle()),
                            StringsUtils.getNonNull(current.getTags()),
                            current.getSlidesCount(),
                            feed,
                            getListSourceType()
                    ),
                    index
            );
        }
        String gameInstanceId = current.getGameInstanceId();
        if (gameInstanceId != null) {
            storiesListNotify.openStory(current.getId(), Story.StoryType.COMMON, listID);
            service.openGameReaderWithGC(
                    context,
                    new GameStoryData(
                            new SlideData(
                                    new StoryData(
                                            current.getId(),
                                            Story.StoryType.COMMON,
                                            StringsUtils.getNonNull(current.getStatTitle()),
                                            StringsUtils.getNonNull(current.getTags()),
                                            current.getSlidesCount(),
                                            feed,
                                            getListSourceType()
                                    ),
                                    0
                            )

                    ),
                    gameInstanceId);
            return;
        } else if (current.getDeeplink() != null) {
            storiesListNotify.openStory(current.getId(), Story.StoryType.COMMON, listID);
            StatisticManager.getInstance().sendDeeplinkStory(current.getId(), current.getDeeplink(), feed);
            OldStatisticManager.getInstance().addDeeplinkClickStatistic(current.getId());
            if (CallbackManager.getInstance().getCallToActionCallback() != null) {
                CallbackManager.getInstance().getCallToActionCallback().callToAction(
                        context,
                        new SlideData(
                                new StoryData(
                                        current.getId(),
                                        StringsUtils.getNonNull(current.getStatTitle()),
                                        StringsUtils.getNonNull(current.getTags()),
                                        current.getSlidesCount(),
                                        feed,
                                        getListSourceType()
                                ),
                                0
                        ),
                        current.getDeeplink(),
                        ClickAction.DEEPLINK
                );
            } else if (CallbackManager.getInstance().getUrlClickCallback() != null) {
                CallbackManager.getInstance().getUrlClickCallback().onUrlClick(current.getDeeplink());
            } else {
                if (!InAppStoryService.isConnected()) {
                    if (CallbackManager.getInstance().getErrorCallback() != null) {
                        CallbackManager.getInstance().getErrorCallback().noConnection();
                    }
                    return;
                }
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(current.getDeeplink()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                } catch (Exception ignored) {
                    InAppStoryService.createExceptionLog(ignored);
                }
            }
            return;
        }
        if (current.isHideInReader()) {

            if (CallbackManager.getInstance().getErrorCallback() != null) {
                CallbackManager.getInstance().getErrorCallback().emptyLinkError();
            }
            return;
        }
        ArrayList<Integer> tempStories = new ArrayList();
        for (Integer storyId : storiesIds) {
            Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId, Story.StoryType.COMMON);
            if (story == null || !story.isHideInReader())
                tempStories.add(storyId);
        }
        ScreensManager.getInstance().openStoriesReader(context, listID, manager, tempStories,
                tempStories.indexOf(storiesIds.get(index)),
                isFavoriteList ? ShowStory.FAVORITE : ShowStory.LIST, feed, Story.StoryType.COMMON);
    }

    public static final int FAVORITE_ITEM_TYPE = -1;
    public static final int UGC_ITEM_TYPE = -2;
    public static final int IMAGE_ITEM_TYPE = 1;
    public static final int VIDEO_ITEM_TYPE = 2;
    public static final int WRONG_ITEM_TYPE = 0;

    @Override
    public int getItemViewType(int position) {
        int ugcItemShift = useUGC ? 1 : 0;
        if (useUGC && position == 0)
            return UGC_ITEM_TYPE;
        if (position == storiesData.size() + ugcItemShift)
            if (useFavorite) return FAVORITE_ITEM_TYPE;
            else return WRONG_ITEM_TYPE;
        int pos = position - ugcItemShift;
        if (storiesData.get(pos).getVideoUrl() != null)
            return VIDEO_ITEM_TYPE;
        else
            return IMAGE_ITEM_TYPE;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return storiesData.size() + ((!storiesData.isEmpty() && hasFavItem) ? 1 : 0) +
                ((!storiesData.isEmpty() && useUGC) ? 1 : 0);
    }


    @Override
    public void notify(StoriesAdapterStoryData data) {
        if (data == null) notifyDataSetChanged();
        int ugcShift = useUGC ? 1 : 0;
        int index = storiesData.indexOf(data);
        if (index >= 0) {
            notifyItemChanged(index + ugcShift);
        }
    }

    @Override
    public List<StoriesAdapterStoryData> getCurrentStories() {
        return storiesData;
    }


    @Override
    public void changeStoryEvent(int storyId) {

    }

    @Override
    public void closeReader() {

    }

    @Override
    public void openReader() {

    }

    @Override
    public void refreshList() {

    }

    @Override
    public void clearAllFavorites() {

    }
}
