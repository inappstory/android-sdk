package com.inappstory.sdk.stories.ui.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.inappstory.sdk.R;
import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.eventbus.CsThreadMode;
import com.inappstory.sdk.exceptions.DataException;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.cache.StoryDownloader;
import com.inappstory.sdk.stories.events.ChangeStoryEvent;
import com.inappstory.sdk.stories.events.ChangeUserIdForListEvent;
import com.inappstory.sdk.stories.events.CloseStoryReaderEvent;
import com.inappstory.sdk.stories.events.OpenStoriesScreenEvent;
import com.inappstory.sdk.stories.events.OpenStoryByIdEvent;
import com.inappstory.sdk.stories.managers.OldStatisticManager;
import com.inappstory.sdk.stories.outerevents.StoriesLoaded;
import com.inappstory.sdk.stories.serviceevents.StoryFavoriteEvent;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.utils.Sizes;

public class StoriesList extends RecyclerView {
    public StoriesList(@NonNull Context context) {
        super(context);
        init(null);
    }

    boolean isFavoriteList = false;

    public StoriesList(@NonNull Context context, boolean isFavoriteList) {
        super(context);
        init(null);
        this.isFavoriteList = isFavoriteList;
    }


    public StoriesList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    StoryTouchListener storyTouchListener = null;

    public interface OnFavoriteItemClick {
        void onClick();
    }


    public StoriesList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    public void onDetachedFromWindow() {
        CsEventBus.getDefault().unregister(this);
        super.onDetachedFromWindow();
        Log.e("cslistEvent", "detached");
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        CsEventBus.getDefault().register(this);

        Log.e("cslistEvent", "attached");
    }

    private void init(AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.StoriesList);
            isFavoriteList = typedArray.getBoolean(R.styleable.StoriesList_cs_listIsFavorite, false);
            typedArray.recycle();
        }
        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!readerIsOpened)
                    sendIndexes();

            }
        });
        addOnItemTouchListener(
                new RecyclerTouchListener(
                        getContext()));
        //getRecycledViewPool().setMaxRecycledViews(6, 0);
    }

    public void sendIndexes() {
        ArrayList<Integer> indexes = new ArrayList<>();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            for (int i = linearLayoutManager.findFirstVisibleItemPosition();
                 i <= linearLayoutManager.findLastVisibleItemPosition(); i++) {
                if (adapter != null && adapter.getStoriesIds().size() > i && i >= 0)
                    indexes.add(adapter.getStoriesIds().get(i));
            }
        } else if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            for (int i = gridLayoutManager.findFirstVisibleItemPosition();
                 i <= gridLayoutManager.findLastVisibleItemPosition(); i++) {
                if (adapter != null && adapter.getStoriesIds().size() > i && i >= 0)
                    indexes.add(adapter.getStoriesIds().get(i));
            }
        }

        OldStatisticManager.getInstance().previewStatisticEvent(indexes);
        try {
            if (StatisticManager.getInstance() != null) {
                StatisticManager.getInstance().sendViewStory(indexes,
                        isFavoriteList ? StatisticManager.FAVORITE : StatisticManager.LIST);
            }
        } catch (Exception e) {

        }
    }

    StoriesAdapter adapter;

    @Override
    public void setLayoutManager(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        super.setLayoutManager(layoutManager);
    }

    LayoutManager layoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);

    public void setAppearanceManager(AppearanceManager appearanceManager) {
        this.appearanceManager = appearanceManager;
    }

    public void setOnFavoriteItemClick(OnFavoriteItemClick favoriteItemClick) {
        this.favoriteItemClick = favoriteItemClick;
    }

    AppearanceManager appearanceManager;
    OnFavoriteItemClick favoriteItemClick;

    boolean readerIsOpened = false;

    @CsSubscribe
    public void openReaderEvent(OpenStoriesScreenEvent event) {
        readerIsOpened = true;
    }

    @CsSubscribe
    public void openReaderEvent(CloseStoryReaderEvent event) {
        readerIsOpened = false;
        sendIndexes();
    }

    @CsSubscribe
    public void openStoryByIdEvent(OpenStoryByIdEvent event) {
        StoryDownloader.getInstance().loadStories(StoryDownloader.getInstance().getStories(), event.getIndex());
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeUserId(ChangeUserIdForListEvent event) {
        try {
            adapter = null;
            loadStories();
        } catch (DataException e) {
            e.printStackTrace();
        }
    }

    public Object touchLock = new Object();

    public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private StoryTouchListener touchListener;
        View lastChild = null;

        public RecyclerTouchListener(Context context) {
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (appearanceManager == null) {
                appearanceManager = AppearanceManager.getInstance();
            }
            if (touchListener == null)
                touchListener = appearanceManager.csStoryTouchListener();
            if (touchListener != null) {
                if (child != null && e.getAction() == MotionEvent.ACTION_DOWN) {
                    touchListener.touchDown(child, rv.getChildPosition(child));
                    lastChild = child;
                } else if (lastChild != null && (e.getAction() == MotionEvent.ACTION_CANCEL ||
                        e.getAction() == MotionEvent.ACTION_UP)) {
                    touchListener.touchUp(lastChild, rv.getChildPosition(lastChild));
                } else if (e.getAction() == MotionEvent.ACTION_MOVE && lastChild != null) {
                    if (child == null || child != lastChild) {
                        touchListener.touchUp(lastChild, rv.getChildPosition(lastChild));
                        lastChild = null;
                    }
                }
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeStoryEvent(final ChangeStoryEvent event) {
        Story st = StoryDownloader.getInstance().getStoryById(event.getId());
        st.isOpened = true;
        st.saveStoryOpened();
        for (int i = 0; i < adapter.getStoriesIds().size(); i++) {
            if (adapter.getStoriesIds().get(i) == event.getId()) {
                adapter.notifyItemChanged(i);
                break;
            }
        }
        if (layoutManager instanceof LinearLayoutManager) {
            final int ind = adapter.getIndexById(event.getId());
            if (ind == -1) return;
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(ind > 0 ? ind : 0, 0);

            if (ind >= 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int[] location = new int[2];
                        View v = layoutManager.findViewByPosition(ind);
                        if (v == null) return;
                        v.getLocationOnScreen(location);
                        int x = location[0];
                        int y = location[1];
                        InAppStoryManager.getInstance().coordinates = new Point(x + v.getWidth() / 2 - Sizes.dpToPxExt(8), y + v.getHeight() / 2);
                    }
                }, 950);
            }
        }
    }

    boolean hasFavItem;

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void favItem(StoryFavoriteEvent event) {
        if (InAppStoryService.getInstance() == null) return;
        if (InAppStoryService.getInstance().favoriteImages == null)
            InAppStoryService.getInstance().favoriteImages = new ArrayList<>();
        List<FavoriteImage> favImages = InAppStoryService.getInstance().favoriteImages;
        boolean isEmpty = favImages.isEmpty();
        Story story = StoryDownloader.getInstance().getStoryById(event.getId());
        if (event.favStatus) {
            FavoriteImage favoriteImage = new FavoriteImage(Integer.valueOf(event.getId()), story.getImage(), story.getBackgroundColor());
            if (!favImages.contains(favoriteImage))
                favImages.add(0, favoriteImage);
        } else {
            for (FavoriteImage favoriteImage : favImages) {
                if (favoriteImage.getId() == Integer.valueOf(event.getId())) {
                    favImages.remove(favoriteImage);
                    break;
                }
            }
        }
        if (isFavoriteList) {
            adapter.hasFavItem = false;
            if (event.favStatus) {
                if (!adapter.getStoriesIds().contains(event.getId()))
                    adapter.getStoriesIds().add(0, event.getId());
            } else {
                if (adapter.getStoriesIds().contains(event.getId()))
                    adapter.getStoriesIds().remove(new Integer(event.getId()));
            }
            adapter.notifyDataSetChanged();
        } else if (isEmpty && !favImages.isEmpty()) {
            adapter.hasFavItem = (true && InAppStoryManager.getInstance().hasFavorite());
            // adapter.refresh();
            adapter.notifyDataSetChanged();
        } else if (!isEmpty && favImages.isEmpty()) {
            adapter.hasFavItem = false;
            adapter.notifyDataSetChanged();
            // adapter.refresh();
        } else {
            adapter.notifyItemChanged(getAdapter().getItemCount() - 1);
        }

    }

    public void loadStories() throws DataException {
        if (appearanceManager == null) {
            appearanceManager = AppearanceManager.getInstance();
        }
        if (appearanceManager == null) {
            throw new DataException("Need to set an AppearanceManager", new Throwable("StoriesList data is not valid"));
        }
        if (InAppStoryManager.getInstance().getUserId() == null) {
            throw new DataException("'userId' can't be null", new Throwable("InAppStoryManager data is not valid"));
        }

        if (InAppStoryService.getInstance() != null) {
            InAppStoryService.getInstance().loadStories(new LoadStoriesCallback() {
                @Override
                public void storiesLoaded(List<Integer> storiesIds) {
                    CsEventBus.getDefault().post(new StoriesLoaded(storiesIds.size()));
                    if (adapter == null) {
                        adapter = new StoriesAdapter(getContext(), storiesIds, appearanceManager, favoriteItemClick, isFavoriteList);
                        setLayoutManager(layoutManager);
                        setAdapter(adapter);
                    } else {
                        adapter.refresh(storiesIds);
                        adapter.notifyDataSetChanged();
                    }
                }
            }, isFavoriteList);

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (InAppStoryService.getInstance() != null)
                        InAppStoryService.getInstance().loadStories(new LoadStoriesCallback() {
                            @Override
                            public void storiesLoaded(List<Integer> storiesIds) {
                                CsEventBus.getDefault().post(new StoriesLoaded(storiesIds.size()));
                                adapter = new StoriesAdapter(getContext(), storiesIds, appearanceManager, favoriteItemClick, isFavoriteList);
                                setLayoutManager(layoutManager);
                                setAdapter(adapter);
                            }
                        }, isFavoriteList);
                }
            }, 1000);
        }

    }

}
