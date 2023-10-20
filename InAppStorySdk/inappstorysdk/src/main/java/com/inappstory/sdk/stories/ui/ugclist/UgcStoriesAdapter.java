package com.inappstory.sdk.stories.ui.ugclist;

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
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.UgcStoryData;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListCallback;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.list.BaseStoryListItem;
import com.inappstory.sdk.stories.ui.list.ClickCallback;
import com.inappstory.sdk.ugc.list.OnUGCItemClick;
import com.inappstory.sdk.ugc.list.UGCListItem;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.List;

public class UgcStoriesAdapter extends RecyclerView.Adapter<BaseStoryListItem> implements ClickCallback {
    public List<Integer> getStoriesIds() {
        return storiesIds;
    }

    private List<Integer> storiesIds = new ArrayList<>();
    OnUGCItemClick ugcItemClick;
    ListCallback callback;

    boolean useUGC;

    public Context context;
    private String listID;

    public UgcStoriesAdapter(Context context,
                             String listID,
                             List<Integer> storiesIds,
                             AppearanceManager manager,
                             ListCallback callback,
                             boolean useUGC,
                             OnUGCItemClick ugcItemClick) {
        this.context = context;
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
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null)
            for (int id : storiesIds) {
                Story story = service.getDownloadManager().getStoryById(id, Story.StoryType.UGC);
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
            return new UGCListItem(v, manager);
        } else
            return new UgcStoryListItem(v, manager, (vType % 5) == 2, vType > 5);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseStoryListItem holder, int position) {
        if (holder == null || InAppStoryService.isNull()) return;
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
            final Story story = InAppStoryService.getInstance().getDownloadManager()
                    .getStoryById(storiesIds.get(position - hasUGC), Story.StoryType.UGC);
            if (story == null) return;
            String imgUrl = (story.getImage() != null && story.getImage().size() > 0) ?
                    story.getProperImage(manager.csCoverQuality()).getUrl() : null;
            holder.bind(story.id,
                    story.getTitle(),
                    story.getTitleColor() != null ? Color.parseColor(story.getTitleColor()) : null,
                    story.getSource(),
                    imgUrl,
                    Color.parseColor(story.getBackgroundColor()),
                    story.isOpened,
                    story.hasAudio(),
                    story.getVideoUrl(), this);
        }
    }

    Long clickTimestamp = -1L;

    @Override
    public void onItemClick(int ind) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        if (System.currentTimeMillis() - clickTimestamp < 1500) {
            return;
        }
        int hasUGC = useUGC ? 1 : 0;
        int index = ind - hasUGC;
        clickTimestamp = System.currentTimeMillis();
        Story current = service.getDownloadManager()
                .getStoryById(storiesIds.get(index), Story.StoryType.UGC);
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
            String gameInstanceId = current.getGameInstanceId();
            if (gameInstanceId != null) {
                service.openGameReaderWithGC(
                        context,
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
                        gameInstanceId);
                return;
            }
            if (current.deeplink != null) {
                StatisticManager.getInstance().sendDeeplinkStory(current.id, current.deeplink, null);
                if (CallbackManager.getInstance().getCallToActionCallback() != null) {
                    CallbackManager.getInstance().getCallToActionCallback().callToAction(
                            context,
                            new SlideData(
                                    new UgcStoryData(
                                            current,
                                            SourceType.LIST
                                    ),
                                    0,
                                    null
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
                current.saveStoryOpened(Story.StoryType.UGC);
                notifyItemChanged(ind);
                return;
            }
            if (current.isHideInReader()) {
                if (CallbackManager.getInstance().getErrorCallback() != null) {
                    CallbackManager.getInstance().getErrorCallback().emptyLinkError();
                }
                return;
            }
        }
        ArrayList<Integer> tempStories = new ArrayList();
        for (Integer storyId : storiesIds) {
            Story story = service.getDownloadManager().getStoryById(storyId, Story.StoryType.UGC);
            if (story == null || !story.isHideInReader())
                tempStories.add(storyId);
        }
        ScreensManager.getInstance().openStoriesReader(
                context, listID,
                manager, tempStories,
                tempStories.indexOf(storiesIds.get(index)), ShowStory.UGC_LIST,
                null, Story.StoryType.UGC);
    }

    @Override
    public int getItemViewType(int position) {
        int hasUGC = useUGC ? 1 : 0;
        if (useUGC && position == 0)
            return -2;
        try {
            int pos = position - hasUGC;
            int pref = pos * 10;
            Story story = InAppStoryService.getInstance().getDownloadManager()
                    .getStoryById(storiesIds.get(pos), Story.StoryType.UGC);
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
        return storiesIds.size() +
                ((!storiesIds.isEmpty() && useUGC) ? 1 : 0);
    }
}
