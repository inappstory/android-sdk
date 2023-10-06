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
import com.inappstory.sdk.stories.ui.list.items.favorite.StoriesListFavoriteItem;
import com.inappstory.sdk.stories.ui.list.items.story.StoriesListItem;
import com.inappstory.sdk.stories.uidomain.list.StoriesAdapterStoryData;
import com.inappstory.sdk.ugc.list.OnUGCItemClick;
import com.inappstory.sdk.stories.ui.list.items.ugceditor.StoriesListUgcEditorItem;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.List;

public class StoriesAdapter
        extends RecyclerView.Adapter<BaseStoriesListItem>
        implements ClickCallback {
    public List<StoriesAdapterStoryData> getStoriesIds() {
        return storiesData;
    }

    private List<StoriesAdapterStoryData> storiesData = new ArrayList<>();
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

    public void setFeed(String feed) {
        this.feed = feed;
    }

    void notifyChanges() {
        if (callback != null)
            callback.storiesUpdated(storiesData.size(), feed);
    }

    public StoriesAdapter(Context context,
                          String listID,
                          List<StoriesAdapterStoryData> storiesData,
                          AppearanceManager manager,
                          boolean isFavoriteList,
                          ListCallback callback,
                          String feed,
                          boolean useFavorite,
                          OnFavoriteItemClick favoriteItemClick,
                          boolean useUGC,
                          OnUGCItemClick ugcItemClick) {
        this.context = context;
        this.listID = listID;
        this.feed = feed;
        this.storiesData = storiesData;
        this.manager = manager;
        this.favoriteItemClick = favoriteItemClick;
        this.ugcItemClick = ugcItemClick;
        this.callback = callback;
        this.isFavoriteList = isFavoriteList;
        this.useFavorite = useFavorite;
        this.useUGC = useUGC;
        InAppStoryService service = InAppStoryService.getInstance();
        hasFavItem = !isFavoriteList && service != null
                && manager != null && manager.csHasFavorite()
                && service.getFavoriteImages().size() > 0;
        notifyChanges();
    }

    public int getIndexById(int id) {
        if (storiesData == null) return -1;
        for (int i = 0; i < storiesData.size(); i++) {
            if (storiesData.get(i).getId() == id) return i + (useUGC ? 1 : 0) ;
        }
        return -1;
    }


    AppearanceManager manager;

    @NonNull
    @Override
    public BaseStoriesListItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int vType = viewType % 10;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cs_story_list_custom_item,
                parent,
                false
        );
        if (vType == -1) {
            return new StoriesListFavoriteItem(v, manager);
        } else if (vType == -2) {
            return new StoriesListUgcEditorItem(v, manager);
        } else {
            return new StoriesListItem(v, manager, (vType % 5) == 2);
        }
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
                    if (favoriteItemClick != null) {
                        favoriteItemClick.onClick();
                    }
                }
            });
        } else if (holder instanceof IStoriesListUGCEditorItem) {
            ((IStoriesListUGCEditorItem) holder).bindUGC();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ugcItemClick != null) {
                        ugcItemClick.onClick();
                    }
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
        }
    }

    Long clickTimestamp = -1L;

    private SourceType getListSourceType() {
        return isFavoriteList ? SourceType.FAVORITE : SourceType.LIST;
    }

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

        final StoriesAdapterStoryData current = storiesData.get(index);
        if (current != null) {
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

                current.isOpened = true;
                current.saveStoryOpened(Story.StoryType.COMMON);
                notifyItemChanged(ind);
                return;
            } else if (current.getDeeplink() != null) {
                StatisticManager.getInstance().sendDeeplinkStory(current.id, current.deeplink, feed);
                OldStatisticManager.getInstance().addDeeplinkClickStatistic(current.id);
                if (CallbackManager.getInstance().getCallToActionCallback() != null) {
                    CallbackManager.getInstance().getCallToActionCallback().callToAction(
                            context,
                            new SlideData(
                                    new StoryData(
                                            current.id,
                                            StringsUtils.getNonNull(current.statTitle),
                                            StringsUtils.getNonNull(current.tags),
                                            current.getSlidesCount(),
                                            feed,
                                            getListSourceType()
                                    ),
                                    0
                            ),
                            current.deeplink,
                            ClickAction.DEEPLINK
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
                    callback.itemClick(
                            new StoryData(
                                    lStory.id,
                                    StringsUtils.getNonNull(lStory.statTitle),
                                    StringsUtils.getNonNull(lStory.tags),
                                    lStory.getSlidesCount(),
                                    feed,
                                    getListSourceType()
                            ),
                            index
                    );
                } else {
                    callback.itemClick(
                            new StoryData(
                                    storiesIds.get(index),
                                    "",
                                    "",
                                    0,
                                    feed,
                                    getListSourceType()
                            ),
                            index
                    );
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
                isFavoriteList ? ShowStory.FAVORITE : ShowStory.LIST, feed, Story.StoryType.COMMON);
    }

    public static final int FAVORITE_ITEM_TYPE = -1;
    public static final int UGC_ITEM_TYPE = -2;
    public static final int IMAGE_ITEM_TYPE = 1;
    public static final int VIDEO_ITEM_TYPE = 1;
    public static final int WRONG_ITEM_TYPE = 0;

    @Override
    public int getItemViewType(int position) {
        int ugcItemShift = useUGC ? 1 : 0;
        if (useUGC && position == 0)
            return UGC_ITEM_TYPE;
        if (position == storiesIds.size() + ugcItemShift)
            if (useFavorite) return FAVORITE_ITEM_TYPE;
            else return WRONG_ITEM_TYPE;
        try {
            int pos = position - ugcItemShift;
            Story story = InAppStoryService.getInstance().getDownloadManager()
                    .getStoryById(storiesIds.get(pos), Story.StoryType.COMMON);
            if (story.getVideoUrl() != null) return VIDEO_ITEM_TYPE;
            return IMAGE_ITEM_TYPE;
        } catch (Exception e) {
            return WRONG_ITEM_TYPE;
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
