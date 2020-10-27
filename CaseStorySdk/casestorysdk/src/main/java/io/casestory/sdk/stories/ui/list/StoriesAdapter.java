package io.casestory.sdk.stories.ui.list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.casestory.casestorysdk.R;
import io.casestory.sdk.AppearanceManager;
import io.casestory.sdk.CaseStoryManager;
import io.casestory.sdk.CaseStoryService;
import io.casestory.sdk.eventbus.EventBus;
import io.casestory.sdk.stories.api.models.Story;
import io.casestory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import io.casestory.sdk.stories.cache.StoryDownloader;
import io.casestory.sdk.stories.events.NoConnectionEvent;
import io.casestory.sdk.stories.events.StoriesErrorEvent;
import io.casestory.sdk.stories.ui.reader.StoriesActivity;
import io.casestory.sdk.stories.ui.reader.StoriesDialogFragment;
import io.casestory.sdk.stories.utils.Sizes;

import static io.casestory.sdk.AppearanceManager.CS_CLOSE_POSITION;
import static io.casestory.sdk.AppearanceManager.CS_STORY_READER_ANIMATION;
import static java.security.AccessController.getContext;

public class StoriesAdapter extends RecyclerView.Adapter<StoryListItem> {
    public List<Integer> getStoriesIds() {
        return storiesIds;
    }

    private List<Integer> storiesIds = new ArrayList<>();
    private boolean isFavoriteList;
    StoriesList.OnFavoriteItemClick favoriteItemClick;

    boolean hasFavItem = false;

    public Context context;

    public StoriesAdapter(Context context, List<Integer> storiesIds, AppearanceManager manager,
                          StoriesList.OnFavoriteItemClick favoriteItemClick, boolean isFavoriteList) {
        this.context = context;
        this.storiesIds = storiesIds;
        this.manager = manager;
        this.favoriteItemClick = favoriteItemClick;
        this.isFavoriteList = isFavoriteList;
        hasFavItem = !isFavoriteList && CaseStoryService.getInstance().favoriteImages.size() > 0;
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
        return new StoryListItem(v, manager, vType == 2, vType == 3);
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
                                    Color.parseColor(story.getBackgroundColor()), story.isReaded || isFavoriteList);
                        }
                    });
                }

                @Override
                public void loadError(int type) {

                }

                @Override
                public void getPartialStory(Story story) {

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

        Story current = StoryDownloader.getInstance().getStoryById(storiesIds.get(index));
        if (current != null) {
            if (current.deeplink != null) {
                CaseStoryService.getInstance().addDeeplinkClickStatistic(current.id);
                if (CaseStoryManager.getInstance().getUrlClickCallback() != null) {
                    CaseStoryManager.getInstance().getUrlClickCallback().onUrlClick(current.deeplink);
                } else {
                    if (!CaseStoryService.getInstance().isConnected()) {
                        EventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.LINK));
                        return;
                    }
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(current.deeplink));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    } catch (Exception e) {
                    }
                }
                return;
            }
            if (current.isHideInReader()) {
                EventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.EMPTY_LINK));
                return;
            }
        }
        ArrayList<Integer> tempStories = new ArrayList();
        for (Integer storyId : storiesIds) {
            Story story = StoryDownloader.getInstance().getStoryById(storyId);
            if (story == null || !story.isHideInReader())
                tempStories.add(storyId);
        }
        if (Sizes.isTablet()) {
            DialogFragment settingsDialogFragment = new StoriesDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("index", tempStories.indexOf(storiesIds.get(index)));
            bundle.putInt(CS_CLOSE_POSITION, manager.csClosePosition());
            bundle.putInt(CS_STORY_READER_ANIMATION, manager.csStoryReaderAnimation());
            bundle.putIntegerArrayList("stories_ids", tempStories);
            settingsDialogFragment.setArguments(bundle);
            settingsDialogFragment.show(
                    ((AppCompatActivity) context).getSupportFragmentManager(),
                    "DialogFragment");
        } else {
            StoryDownloader.getInstance().loadStories(StoryDownloader.getInstance().getStories(),
                    StoryDownloader.getInstance().getStories().get(index).id);
            Intent intent2 = new Intent(CaseStoryManager.getInstance().getContext(), StoriesActivity.class);
            // intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2.putExtra(CS_CLOSE_POSITION, manager.csClosePosition());
            intent2.putExtra(CS_STORY_READER_ANIMATION, manager.csStoryReaderAnimation());
            intent2.putExtra("index", tempStories.indexOf(storiesIds.get(index)));
            intent2.putIntegerArrayListExtra("stories_ids", tempStories);
            context.startActivity(intent2);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int pref = position*10;
        if (CaseStoryManager.getInstance().hasFavorite() && position == storiesIds.size()) return pref + 3;
        return StoryDownloader.getInstance().getStoryById(storiesIds.get(position)).isReaded ? (pref + 2) : (pref + 1);
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
