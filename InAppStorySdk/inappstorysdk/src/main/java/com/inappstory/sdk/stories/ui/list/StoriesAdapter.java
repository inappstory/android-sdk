package com.inappstory.sdk.stories.ui.list;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASStatisticStoriesV1;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenData;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenStrategy;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenData;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenStrategy;
import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.core.utils.StringWithPlaceholders;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.callbacks.OnFavoriteItemClick;
import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryItemCoordinates;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListCallback;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.ui.reader.ActiveStoryItem;
import com.inappstory.sdk.ugc.list.OnUGCItemClick;
import com.inappstory.sdk.ugc.list.UGCListItem;

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
    private String sessionId;
    private String feedID;

    public void setFeedID(String feedID) {
        this.feedID = feedID;
    }

    void notifyChanges() {
        if (callback != null)
            callback.storiesUpdated(storiesIds.size(), feed, getStoriesData(storiesIds));
    }

    private List<StoryData> getStoriesData(List<Integer> storiesIds) {
        List<StoryData> data = new ArrayList<>();
        for (int id : storiesIds) {
            IListItemContent story =
                    core.contentHolder().listsContent().getByIdAndType(id, ContentType.STORY);
            if (story != null) {
                data.add(new StoryData(story, feed, SourceType.LIST));
            }
        }
        return data;
    }

    private final IASCore core;

    public StoriesAdapter(
            IASCore core,
            Context context,
            String listID,
            String sessionId,
            List<Integer> storiesIds,
            AppearanceManager manager,
            boolean isFavoriteList,
            ListCallback callback,
            String feed,
            String feedID,
            boolean useFavorite,
            OnFavoriteItemClick favoriteItemClick,
            boolean useUGC,
            OnUGCItemClick ugcItemClick
    ) {
        this.core = core;
        this.context = context;
        this.listID = listID;
        this.feed = feed;
        this.sessionId = sessionId;
        this.storiesIds = storiesIds;
        this.feedID = feedID;
        this.manager = manager;
        this.favoriteItemClick = favoriteItemClick;
        this.ugcItemClick = ugcItemClick;
        this.callback = callback;
        this.isFavoriteList = isFavoriteList;
        this.useFavorite = useFavorite;
        this.useUGC = useUGC;
        hasFavItem = !isFavoriteList
                && manager != null
                && manager.csHasFavorite()
                && !core
                .contentHolder()
                .favoriteItems().isEmpty(ContentType.STORY);
        notifyChanges();
    }

    public void refresh(List<Integer> storiesIds) {
        this.storiesIds = storiesIds;
    }

    public int getIndexById(int id) {
        if (storiesIds == null) return -1;
        int indInList = storiesIds.indexOf(id);
        if (indInList == -1) return -1;
        return indInList + (useUGC ? 1 : 0);
    }


    AppearanceManager manager;

    @NonNull
    @Override
    public BaseStoryListItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int vType = viewType % 10;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cs_story_list_custom_item, parent, false);
        if (vType == -1) {
            return new StoryFavoriteListItem(v, parent, manager);
        } else if (vType == -2) {
            return new UGCListItem(v, parent, manager);
        } else {
            return new StoryListItem(v, parent, manager, (vType % 5) == 2, vType > 5);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseStoryListItem holder, int position) {
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
            final IListItemContent story = core.contentHolder().listsContent()
                    .getByIdAndType(storiesIds.get(position - hasUGC), ContentType.STORY);
            if (story == null) return;
            String imgUrl = story.imageCoverByQuality(manager.csCoverQuality());
            holder.bind(story.id(),
                    new StringWithPlaceholders().replace(story.title(), core),
                    story.titleColor() != null ? ColorUtils.parseColorRGBA(story.titleColor()) : null,
                    imgUrl,
                    ColorUtils.parseColorRGBA(story.backgroundColor()),
                    story.isOpened() || isFavoriteList,
                    story.hasAudio(),
                    story.videoCover(),
                    StoryData.getStoryData(story, feed, getListSourceType(), ContentType.STORY),
                    this
            );
        }
    }

    Long clickTimestamp = -1L;

    private SourceType getListSourceType() {
        return isFavoriteList ? SourceType.FAVORITE : SourceType.LIST;
    }

    @Override
    public void onItemClick(final int ind, StoryItemCoordinates coordinates) {


        if (System.currentTimeMillis() - clickTimestamp < 1500) {
            return;
        }
        core.screensManager().getStoryScreenHolder().activeStoryItem(new ActiveStoryItem(ind, listID));
        int hasUGC = useUGC ? 1 : 0;
        int index = ind - hasUGC;
        clickTimestamp = System.currentTimeMillis();
        final IListItemContent current = core.contentHolder().listsContent()
                .getByIdAndType(storiesIds.get(index), ContentType.STORY);
        if (current != null) {
            if (callback != null) {
                callback.itemClick(
                        new StoryData(
                                current,
                                feed,
                                getListSourceType()
                        ),
                        index
                );
            }
            String gameInstanceId = current.gameInstanceId();
            if (gameInstanceId != null) {
                core.statistic().storiesV1(
                        sessionId,
                        new GetStatisticV1Callback() {
                            @Override
                            public void get(@NonNull IASStatisticStoriesV1 manager) {
                                manager.addGameClickStatistic(current.id());
                            }
                        }
                );
                core.screensManager().openScreen(context,
                        new LaunchGameScreenStrategy(core, false)
                                .data(new LaunchGameScreenData(
                                        null,
                                        new GameStoryData(
                                                new SlideData(
                                                        new StoryData(
                                                                current,
                                                                feed,
                                                                getListSourceType()
                                                        ),
                                                        0,
                                                        null
                                                )

                                        ),
                                        gameInstanceId
                                ))
                );

                current.setOpened(true);
                core.storyListCache().saveStoryOpened(current.id(), ContentType.STORY);
                core.inAppStoryService().getListReaderConnector().changeStory(current.id(),
                        listID,
                        false
                );
                // notifyItemChanged(ind);
                return;
            } else if (current.deeplink() != null) {
                core.statistic().storiesV1(
                        sessionId,
                        new GetStatisticV1Callback() {
                            @Override
                            public void get(@NonNull IASStatisticStoriesV1 manager) {
                                manager.addDeeplinkClickStatistic(current.id());
                            }
                        }
                );
                core.statistic().storiesV2().sendDeeplinkStory(current.id(), current.deeplink(), feedID);
                core.callbacksAPI().useCallback(
                        IASCallbackType.CALL_TO_ACTION,
                        new UseIASCallback<CallToActionCallback>() {
                            @Override
                            public void use(@NonNull CallToActionCallback callback) {
                                callback.callToAction(
                                        context,
                                        new SlideData(
                                                new StoryData(
                                                        current,
                                                        feed,
                                                        getListSourceType()
                                                ),
                                                0,
                                                null
                                        ),
                                        current.deeplink(),
                                        ClickAction.DEEPLINK
                                );
                            }

                            @Override
                            public void onDefault() {
                                new ConnectionCheck().check(
                                        context,
                                        new ConnectionCheckCallback(core) {
                                            @Override
                                            public void success() {
                                                try {
                                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                                    i.setData(Uri.parse(current.deeplink()));
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    context.startActivity(i);
                                                } catch (Exception ignored) {
                                                    core.exceptionManager().createExceptionLog(ignored);
                                                }
                                            }
                                        }
                                );
                            }
                        }
                );
                current.setOpened(true);
                core.storyListCache().saveStoryOpened(current.id(), ContentType.STORY);
                core.inAppStoryService().getListReaderConnector().changeStory(
                        current.id(),
                        listID,
                        false
                );
                return;
            }
            if (current.hideInReader()) {
                core.callbacksAPI().useCallback(IASCallbackType.ERROR,
                        new UseIASCallback<ErrorCallback>() {
                            @Override
                            public void use(@NonNull ErrorCallback callback) {
                                callback.emptyLinkError();
                            }
                        }
                );
                return;
            }
        }
        ArrayList<Integer> tempStories = new ArrayList();
        for (Integer storyId : storiesIds) {
            IListItemContent story = core.contentHolder().listsContent()
                    .getByIdAndType(storyId, ContentType.STORY);
            if (story == null || !story.hideInReader())
                tempStories.add(storyId);
        }
        LaunchStoryScreenData launchData = new LaunchStoryScreenData(
                listID,
                feed,
                sessionId,
                tempStories,
                tempStories.indexOf(storiesIds.get(index)),
                false,
                ShowStory.ACTION_OPEN,
                isFavoriteList ? SourceType.FAVORITE : SourceType.LIST,
                0,
                ContentType.STORY,
                coordinates
        );
        core.screensManager().openScreen(
                context,
                new LaunchStoryScreenStrategy(core, false).
                        launchStoryScreenData(launchData).
                        readerAppearanceSettings(
                                new LaunchStoryScreenAppearance(
                                        AppearanceManager.checkOrCreateAppearanceManager(manager),
                                        context
                                )
                        )
        );
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
            IListItemContent story = core.contentHolder().listsContent()
                    .getByIdAndType(storiesIds.get(pos), ContentType.STORY);
            if (story == null) return 0;
            if (story.videoCover() != null) pref += 5;
            return story.isOpened() ? (pref + 2) : (pref + 1);
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
