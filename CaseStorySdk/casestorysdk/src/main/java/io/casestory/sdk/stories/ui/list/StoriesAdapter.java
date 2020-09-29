package io.casestory.sdk.stories.ui.list;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.casestory.casestorysdk.R;
import io.casestory.sdk.AppearanceManager;
import io.casestory.sdk.CaseStoryManager;
import io.casestory.sdk.CaseStoryService;
import io.casestory.sdk.stories.api.models.Story;
import io.casestory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import io.casestory.sdk.stories.cache.StoryDownloader;
import io.casestory.sdk.stories.ui.reader.StoriesActivity;

public class StoriesAdapter extends RecyclerView.Adapter<StoryListItem> {
    private List<Integer> storiesIds;
    private boolean isFavoriteList;
    StoriesList.OnFavoriteItemClick favoriteItemClick;

    boolean hasFavItem = false;

    public StoriesAdapter(List<Integer> storiesIds, AppearanceManager manager,
                          StoriesList.OnFavoriteItemClick favoriteItemClick, boolean isFavoriteList) {
        this.storiesIds = storiesIds;
        this.manager = manager;
        this.favoriteItemClick = favoriteItemClick;
        this.isFavoriteList = isFavoriteList;
        hasFavItem = CaseStoryService.getInstance().favoriteImages.size() > 0;
    }

    public int getIndexById(int id) {
        if (storiesIds == null) return -1;
        return storiesIds.indexOf(id);
    }


    AppearanceManager manager;

    @NonNull
    @Override
    public StoryListItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cs_story_list_custom_item, parent, false);
        return new StoryListItem(v, manager, viewType == 2, viewType == 3);
    }

    @Override
    public void onBindViewHolder(@NonNull final StoryListItem holder, final int position) {
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
            CaseStoryService.getInstance().getStoryById(new GetStoryByIdCallback() {
                @Override
                public void getStory(final Story story) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            holder.bind(story.getTitle(), story.getSource(),
                                    (story.getImage() != null && story.getImage().size() > 0) ? story.getImage().get(0).getUrl() : null,
                                    Color.parseColor(story.getBackgroundColor()), holder.isReaded);
                        }
                    });
                }
            }, storiesIds.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(position);
                }
            });
        }

    }

    public void onItemClick(int index) {
        StoryDownloader.getInstance().loadStories(StoryDownloader.getInstance().getStories(),
                StoryDownloader.getInstance().getStories().get(index).id);
        Intent intent2 = new Intent(CaseStoryManager.getInstance().getContext(), StoriesActivity.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2.putExtra("index", index);
        intent2.putIntegerArrayListExtra("stories_ids", new ArrayList<Integer>() {{
            addAll(storiesIds);
        }});
        CaseStoryManager.getInstance().getContext().startActivity(intent2);
    }

    @Override
    public int getItemViewType(int position) {
        if (CaseStoryManager.getInstance().hasFavorite() && position == storiesIds.size()) return 3;
        return StoryDownloader.getInstance().getStoryById(storiesIds.get(position)).isReaded ? 2 : 1;
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
