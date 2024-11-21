package com.inappstory.sdk.stories.ui.ugclist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
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
import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryItemCoordinates;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.UgcStoryData;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListCallback;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.ui.list.BaseStoryListItem;
import com.inappstory.sdk.stories.ui.list.ClickCallback;
import com.inappstory.sdk.stories.ui.reader.ActiveStoryItem;
import com.inappstory.sdk.ugc.list.OnUGCItemClick;
import com.inappstory.sdk.ugc.list.UGCListItem;

import java.util.ArrayList;
import java.util.List;

public class UgcStoriesAdapter extends RecyclerView.Adapter<BaseStoryListItem> implements ClickCallback {
    public List<Integer> getStoriesIds() {
        return storiesIds;
    }

    private List<Integer> storiesIds = new ArrayList<>();
    OnUGCItemClick ugcItemClick;
    ListCallback callback;
    private final IASCore core;

    boolean useUGC;

    public Context context;
    private String listID;
    private String sessionId;

    public UgcStoriesAdapter(
            IASCore core,
            Context context,
            String listID,
            String sessionId,
            List<Integer> storiesIds,
            AppearanceManager manager,
            ListCallback callback,
            boolean useUGC,
            OnUGCItemClick ugcItemClick
    ) {
        this.core = core;
        this.context = context;
        this.sessionId = sessionId;
        this.listID = listID;
        this.storiesIds = storiesIds;
        this.manager = manager;
        this.ugcItemClick = ugcItemClick;
        this.callback = callback;
        this.useUGC = useUGC;
        if (callback != null)
            callback.storiesUpdated(storiesIds.size(), null, getStoriesData(storiesIds));
    }

    private List<StoryData> getStoriesData(List<Integer> storiesIds) {
        List<StoryData> data = new ArrayList<>();
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager != null)
            for (int id : storiesIds) {
                IListItemContent story = inAppStoryManager
                        .iasCore()
                        .contentHolder()
                        .listsContent()
                        .getByIdAndType(
                                id,
                                ContentType.UGC
                        );
                if (story != null) {
                    data.add(new UgcStoryData(story, SourceType.LIST));
                }
            }
        return data;
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
        if (vType == -2) {
            return new UGCListItem(v, parent, manager);
        } else
            return new UgcStoryListItem(v, parent, manager, (vType % 5) == 2, vType > 5);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseStoryListItem holder, int position) {
        if (holder.isUGC) {
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
                    .getByIdAndType(storiesIds.get(position - hasUGC), ContentType.UGC);
            if (story == null) return;
            String imgUrl = story.imageCoverByQuality(manager.csCoverQuality());
            holder.bind(story.id(),
                    new StringWithPlaceholders().replace(story.title(), core),
                    story.titleColor() != null ? ColorUtils.parseColorRGBA(story.titleColor()) : null,
                    imgUrl,
                    ColorUtils.parseColorRGBA(story.backgroundColor()),
                    story.isOpened(),
                    story.hasAudio(),
                    story.videoCover(),
                    StoryData.getStoryData(story, null, SourceType.LIST, ContentType.UGC),
                    this
            );
        }
    }

    Long clickTimestamp = -1L;

    @Override
    public void onItemClick(final int ind, final StoryItemCoordinates coordinates) {
        if (System.currentTimeMillis() - clickTimestamp < 1500) {
            return;
        }
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull final IASCore core) {
                InAppStoryService service = InAppStoryService.getInstance();
                if (service == null) return;
                core.screensManager().getStoryScreenHolder().activeStoryItem(new ActiveStoryItem(ind, listID));
                int hasUGC = useUGC ? 1 : 0;
                int index = ind - hasUGC;
                clickTimestamp = System.currentTimeMillis();
                final IListItemContent current = core.contentHolder().listsContent()
                        .getByIdAndType(storiesIds.get(index), ContentType.UGC);
                if (current != null) {
                    if (callback != null) {
                        callback.itemClick(
                                new UgcStoryData(
                                        current,
                                        SourceType.LIST
                                ),
                                index
                        );
                    }
                    if (current.gameInstanceId() != null) {

                        core.statistic().storiesV1(
                                sessionId,
                                new GetStatisticV1Callback() {
                                    @Override
                                    public void get(@NonNull IASStatisticStoriesV1 manager) {
                                        manager.addGameClickStatistic(current.id());
                                    }
                                }
                        );
                        core.screensManager().openScreen(
                                context,
                                new LaunchGameScreenStrategy(
                                        core,
                                        false
                                )
                                        .data(new LaunchGameScreenData(
                                                null,
                                                new GameStoryData(
                                                        new SlideData(
                                                                new UgcStoryData(
                                                                        current,
                                                                        SourceType.LIST
                                                                ),
                                                                0,
                                                                null
                                                        )
                                                ),
                                                current.gameInstanceId()
                                        ))
                        );
                        return;
                    } else if (current.deeplink() != null) {
                        core.statistic().storiesV2().sendDeeplinkStory(current.id(),
                                current.deeplink(),
                                null);
                        core.callbacksAPI().useCallback(
                                IASCallbackType.CALL_TO_ACTION,
                                new UseIASCallback<CallToActionCallback>() {
                                    @Override
                                    public void use(@NonNull CallToActionCallback callback) {
                                        callback.callToAction(
                                                context,
                                                new SlideData(
                                                        new UgcStoryData(
                                                                current,
                                                                SourceType.LIST
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
                                                            InAppStoryService.createExceptionLog(ignored);
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                }
                        );
                        current.setOpened(true);
                        core.storyListCache().saveStoryOpened(current.id(), ContentType.UGC);
                        notifyItemChanged(ind);
                        return;
                    } else if (current.hideInReader()) {
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
                            .getByIdAndType(storyId, ContentType.UGC);
                    if (story == null || !story.hideInReader())
                        tempStories.add(storyId);
                }
                LaunchStoryScreenData launchData = new LaunchStoryScreenData(
                        listID,
                        null,
                        sessionId,
                        tempStories,
                        tempStories.indexOf(storiesIds.get(index)),
                        false,
                        ShowStory.ACTION_OPEN,
                        SourceType.LIST,
                        0,
                        ContentType.UGC,
                        coordinates
                );
                core.screensManager().openScreen(context,
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
        });

    }

    @Override
    public int getItemViewType(int position) {
        int hasUGC = useUGC ? 1 : 0;
        if (useUGC && position == 0)
            return -2;
        try {
            int pos = position - hasUGC;
            int pref = pos * 10;
            InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
            IListItemContent story = inAppStoryManager.iasCore().contentHolder().listsContent(
            ).getByIdAndType(storiesIds.get(pos), ContentType.UGC);
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
        return storiesIds.size() +
                ((!storiesIds.isEmpty() && useUGC) ? 1 : 0);
    }
}
