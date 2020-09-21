package io.casestory.sdk.stories.ui.list;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.casestory.sdk.AppearanceManager;
import io.casestory.sdk.CaseStoryManager;
import io.casestory.sdk.CaseStoryService;
import io.casestory.sdk.eventbus.EventBus;
import io.casestory.sdk.eventbus.Subscribe;
import io.casestory.sdk.eventbus.ThreadMode;
import io.casestory.sdk.exceptions.DataException;
import io.casestory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import io.casestory.sdk.stories.cache.StoryDownloader;
import io.casestory.sdk.stories.events.ChangeStoryEvent;
import io.casestory.sdk.stories.events.OpenStoryByIdEvent;

public class StoriesList extends RecyclerView {
    public StoriesList(@NonNull Context context) {
        super(context);
        init();
    }


    public StoriesList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public interface OnBindFavoriteItem {
        View getFavoriteCell(List<FavoriteImage> favoriteImages);
    }

    public StoriesList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        EventBus.getDefault().register(this);
    }

    StoriesAdapter adapter;
    LayoutManager layoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);

    public void setAppearanceManager(AppearanceManager appearanceManager) {
        this.appearanceManager = appearanceManager;
    }

    public void setOnBindFavoriteItem(OnBindFavoriteItem bindFavoriteItem) {
        this.bindFavoriteItem = bindFavoriteItem;
    }

    AppearanceManager appearanceManager;
    OnBindFavoriteItem bindFavoriteItem;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openStoryByIdEvent(OpenStoryByIdEvent event) {
        StoryDownloader.getInstance().loadStories(StoryDownloader.getInstance().getStories(), event.getIndex());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeStoryEvent(ChangeStoryEvent event) {
        if (layoutManager instanceof LinearLayoutManager) {
            int ind = adapter.getIndexById(event.getId());
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(ind > 0 ? ind : 0, 0);
        }
    }

    public void loadStories() throws DataException {
        if (appearanceManager == null) {
            throw new DataException("Need to set an AppearanceManager", new Throwable("StoriesList data is not valid"));
        }
        if (CaseStoryManager.getInstance().getUserId() == null) {
            throw new DataException("'userId' can't be null", new Throwable("CaseStoryManager data is not valid"));
        }

        if (CaseStoryService.getInstance() != null) {
            CaseStoryService.getInstance().loadStories(new LoadStoriesCallback() {
                @Override
                public void storiesLoaded(List<Integer> storiesIds) {
                    adapter = new StoriesAdapter(storiesIds, appearanceManager, bindFavoriteItem,false);
                    setLayoutManager(layoutManager);
                    setAdapter(adapter);
                }
            });

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    CaseStoryService.getInstance().loadStories(new LoadStoriesCallback() {
                        @Override
                        public void storiesLoaded(List<Integer> storiesIds) {
                            adapter = new StoriesAdapter(storiesIds, appearanceManager, bindFavoriteItem,false);
                            setLayoutManager(layoutManager);
                            setAdapter(adapter);
                        }
                    });
                }
            }, 1000);
        }

    }

}
