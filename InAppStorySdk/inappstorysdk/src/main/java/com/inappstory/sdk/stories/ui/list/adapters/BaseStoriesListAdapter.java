package com.inappstory.sdk.stories.ui.list.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.callbacks.OnFavoriteItemClick;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryData;
import com.inappstory.sdk.stories.ui.list.ClickCallback;
import com.inappstory.sdk.stories.ui.list.IFavoriteCellUpdate;
import com.inappstory.sdk.stories.ui.list.items.IStoriesListCommonItem;
import com.inappstory.sdk.stories.ui.list.items.IStoriesListFavoriteItem;
import com.inappstory.sdk.stories.ui.list.items.IStoriesListUGCEditorItem;
import com.inappstory.sdk.stories.ui.list.items.IStoriesListItemWithCover;
import com.inappstory.sdk.stories.ui.list.items.BaseStoriesListItem;
import com.inappstory.sdk.core.repository.stories.dto.PreviewStoryDTO;
import com.inappstory.sdk.stories.uidomain.list.items.story.IStoriesListCommonItemClick;
import com.inappstory.sdk.stories.uidomain.list.items.story.IStoriesListDeeplinkItemClick;
import com.inappstory.sdk.stories.uidomain.list.items.story.IStoriesListGameItemClick;
import com.inappstory.sdk.ugc.list.OnUGCItemClick;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseStoriesListAdapter
        extends RecyclerView.Adapter<BaseStoriesListItem>
        implements IStoriesListAdapter,
        ClickCallback {
    public List<PreviewStoryDTO> getStoriesData() {
        return storiesData;
    }

    protected List<PreviewStoryDTO> storiesData = new ArrayList<>();
    private boolean isFavoriteList;

    @Override
    public void updateStoriesData(List<PreviewStoryDTO> data) {
        this.storiesData.clear();
        this.storiesData.addAll(data);
        notifyDataSetChanged();
    }

    private final IStoriesListCommonItemClick storiesListCommonItemClick;
    private final IStoriesListDeeplinkItemClick storiesListDeeplinkItemClick;
    private final IStoriesListGameItemClick storiesListGameItemClick;
    private final OnFavoriteItemClick favoriteItemClick;
    private final OnUGCItemClick ugcItemClick;
    boolean hasFavItem = false;

    boolean useFavorite;
    boolean useUGC;

    public Context context;
    private String listID;
    private String feed;

    public BaseStoriesListAdapter(
            Context context,
            String listID,
            String feed,
            AppearanceManager manager,
            boolean isFavoriteList,
            boolean useFavorite,
            boolean useUGC,
            IStoriesListCommonItemClick storiesListCommonItemClick,
            IStoriesListDeeplinkItemClick storiesListDeeplinkItemClick,
            IStoriesListGameItemClick storiesListGameItemClick,
            OnFavoriteItemClick favoriteItemClick,
            OnUGCItemClick ugcItemClick
    ) {
        this.context = context;
        this.feed = feed;
        this.listID = listID;
        this.manager = manager;
        this.isFavoriteList = isFavoriteList;
        this.useFavorite = useFavorite;
        this.useUGC = useUGC;

        this.storiesListCommonItemClick = storiesListCommonItemClick;
        this.storiesListDeeplinkItemClick = storiesListDeeplinkItemClick;
        this.storiesListGameItemClick = storiesListGameItemClick;
        this.favoriteItemClick = favoriteItemClick;
        this.ugcItemClick = ugcItemClick;
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
    }


    @Override
    public void onBindViewHolder(@NonNull final BaseStoriesListItem holder, int position) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        if (holder instanceof IStoriesListFavoriteItem && this instanceof IFavoriteCellUpdate) {
            ((IStoriesListFavoriteItem) holder).bindFavorite(((IFavoriteCellUpdate) this).getFavoriteImages());
            ((IStoriesListFavoriteItem) holder).setImages(((IFavoriteCellUpdate) this).getFavoriteImages());
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
            final PreviewStoryDTO story = storiesData.get(position - hasUGC);
            if (story == null) return;
            String imgUrl = story.getImageUrl(manager.csCoverQuality());
            ((IStoriesListCommonItem) holder).bindCommon(
                    story.getId(),
                    new StoryData(
                            story.getId(),
                            story.getStatTitle(),
                            story.getTags(),
                            story.getSlidesCount(),
                            feed,
                            isFavoriteList ? SourceType.FAVORITE : SourceType.LIST
                    ),
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
                            onItemClick(holder.getBindingAdapterPosition());
                        }
                    });
                }
            });
        }
    }

    Long clickTimestamp = -1L;

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

        final PreviewStoryDTO current = storiesData.get(index);
        String gameInstanceId = current.getGameInstanceId();
        if (gameInstanceId != null) {
            if (storiesListGameItemClick != null)
                storiesListGameItemClick.onClick(
                        current,
                        index
                );
            return;
        } else if (current.getDeeplink() != null) {
            if (storiesListDeeplinkItemClick != null)
                storiesListDeeplinkItemClick.onClick(
                        current,
                        index
                );
        }
        if (storiesListCommonItemClick != null) {
            ArrayList<PreviewStoryDTO> tempStoriesData = new ArrayList();
            for (PreviewStoryDTO storyData : storiesData) {
                if (!storyData.isHideInReader())
                    tempStoriesData.add(storyData);
            }
            storiesListCommonItemClick.onClick(tempStoriesData, tempStoriesData.indexOf(current));
        }
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
        boolean hasFavItem = false;
        if (this instanceof IFavoriteCellUpdate) {
            hasFavItem = ((IFavoriteCellUpdate) this).getFavoriteImages().size() > 0;
        }
        return storiesData.size() + ((!storiesData.isEmpty() && hasFavItem) ? 1 : 0) +
                ((!storiesData.isEmpty() && useUGC) ? 1 : 0);
    }


    @Override
    public void notify(PreviewStoryDTO data) {
        if (data == null) {
            notifyDataSetChanged();
            return;
        }
        int ugcShift = useUGC ? 1 : 0;
        int index = -1;
        for (int i = 0; i < storiesData.size(); i++) {
            if (storiesData.get(i).getId() == data.getId()) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            storiesData.set(index, data);
        }
        if (index >= 0) {
            notifyItemChanged(index + ugcShift);
        }
    }

    @Override
    public List<PreviewStoryDTO> getCurrentStories() {
        return storiesData;
    }

    @Override
    public void refreshList() {
        notifyDataSetChanged();
    }
}
