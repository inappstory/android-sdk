package com.inappstory.sdk.stories.ui.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.inappstory.sdk.R;
import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.exceptions.DataException;
import com.inappstory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListCallback;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.callbacks.OnFavoriteItemClick;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.outerevents.StoriesLoaded;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.utils.Sizes;

import static java.util.UUID.randomUUID;

public class StoriesList extends RecyclerView {
    public StoriesList(@NonNull Context context) {
        super(context);
        init(null);

    }

    public void setCallback(ListCallback callback) {
        this.callback = callback;
    }

    ListCallback callback;

    StoriesListManager manager;
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


    public StoriesList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        InAppStoryManager.debugSDKCalls("StoriesList_onDetachedFromWindow",
                toString() + " " + InAppStoryService.isNotNull() + " " + manager);
        InAppStoryService.checkAndRemoveListSubscriber(manager);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            InAppStoryManager.debugSDKCalls("StoriesList_onVisibilityChanged",
                    toString() + " " + InAppStoryService.isNotNull() + " VISIBLE " + manager);
        //    InAppStoryService.checkAndAddListSubscriber(manager);
        } else {
            InAppStoryManager.debugSDKCalls("StoriesList_onVisibilityChanged",
                    toString() + " " + InAppStoryService.isNotNull() + " INVISIBLE " + manager);
        //    InAppStoryService.checkAndRemoveListSubscriber(manager);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        InAppStoryManager.debugSDKCalls("StoriesList_onAttachedToWindow", toString() + " " + InAppStoryService.isNotNull() + manager);
        InAppStoryService.checkAndAddListSubscriber(manager);

    }

    private void init(AttributeSet attributeSet) {
        manager = new StoriesListManager(this);
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

    void sendIndexes() {
        ArrayList<Integer> indexes = new ArrayList<>();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            for (int i = linearLayoutManager.findFirstVisibleItemPosition();
                 i <= linearLayoutManager.findLastVisibleItemPosition(); i++) {
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

    /**
     * Use to interact with the favorite cell (for example, to open a new window with a list of favorite stories)
     *
     * @param favoriteItemClick (favoriteItemClick) - instance of OnFavoriteItemClick.
     */
    public void setOnFavoriteItemClick(OnFavoriteItemClick favoriteItemClick) {
        this.favoriteItemClick = favoriteItemClick;
    }

    AppearanceManager appearanceManager;
    OnFavoriteItemClick favoriteItemClick;

    boolean readerIsOpened = false;

    public void openReader() {
        readerIsOpened = true;
    }

    public void closeReader() {
        readerIsOpened = false;
        sendIndexes();
    }


    void refreshList() {
        try {
            adapter = null;
            loadStoriesInner();
        } catch (DataException e) {
            e.printStackTrace();
        }
    }

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
                appearanceManager = AppearanceManager.getCommonInstance();
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


    public void changeStoryEvent(int storyId) {
        if (adapter == null || adapter.getStoriesIds() == null) return;
        for (int i = 0; i < adapter.getStoriesIds().size(); i++) {
            if (adapter.getStoriesIds().get(i) == storyId) {
                adapter.notifyItemChanged(i);
                break;
            }
        }
        if (layoutManager == null) return;
        if (layoutManager instanceof LinearLayoutManager) {
            final int ind = adapter.getIndexById(storyId);
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
                        ScreensManager.getInstance().coordinates = new Point(x + v.getWidth() / 2 - Sizes.dpToPxExt(8),
                                y + v.getHeight() / 2);
                    }
                }, 950);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            OldStatisticManager.getInstance().sendStatistic();
        }
    }

    public void favStory(int id, boolean favStatus, List<FavoriteImage> favImages, boolean isEmpty) {
        if (InAppStoryService.isNull()) return;
        if (adapter == null) return;
        if (isFavoriteList) {
            adapter.hasFavItem = false;
            if (favStatus) {
                if (!adapter.getStoriesIds().contains(id))
                    adapter.getStoriesIds().add(0, id);
            } else {
                if (adapter.getStoriesIds().contains(id))
                    adapter.getStoriesIds().remove(new Integer(id));
            }
            adapter.notifyDataSetChanged();
        } else if (isEmpty && !favImages.isEmpty()) {
            adapter.hasFavItem = (appearanceManager != null && appearanceManager.csHasFavorite());
            // adapter.refresh();
            adapter.notifyDataSetChanged();
        } else if ((!isEmpty && favImages.isEmpty()) || (adapter.hasFavItem && favImages.isEmpty())) {
            adapter.hasFavItem = false;
            adapter.notifyDataSetChanged();
            // adapter.refresh();
        } else {
            adapter.notifyItemChanged(getAdapter().getItemCount() - 1);
        }

    }


    public void loadStories() throws DataException {
        InAppStoryManager.debugSDKCalls("StoriesList_loadStories", toString() + "");
        loadStoriesInner();
    }


    public void loadStoriesInner() throws DataException {
        if (appearanceManager == null) {
            appearanceManager = AppearanceManager.getCommonInstance();
        }
        if (appearanceManager == null) {
            throw new DataException("Need to set an AppearanceManager", new Throwable("StoriesList data is not valid"));
        }
        if (InAppStoryManager.getInstance() == null) {
            throw new DataException("'InAppStoryManager' can't be null", new Throwable("InAppStoryManager data is not valid"));
        }
        if (InAppStoryManager.getInstance().getUserId() == null) {
            throw new DataException("'userId' can't be null", new Throwable("InAppStoryManager data is not valid"));
        }
        InAppStoryManager.debugSDKCalls("StoriesList_loadStoriesInner", toString() + "");
        final String listUid = ProfilingManager.getInstance().addTask("widget_init");
        final boolean hasFavorite = (appearanceManager != null && !isFavoriteList && appearanceManager.csHasFavorite());
        if (InAppStoryService.isNotNull()) {
            InAppStoryService.getInstance().getDownloadManager().loadStories(new LoadStoriesCallback() {
                @Override
                public void storiesLoaded(List<Integer> storiesIds) {
                    InAppStoryManager.debugSDKCalls("StoriesList_loadStoriesInner", StoriesList.this.toString() + " loaded");
                    if (adapter == null) {
                        adapter = new StoriesAdapter(getContext(), storiesIds, appearanceManager, favoriteItemClick, isFavoriteList, callback);
                        setLayoutManager(layoutManager);
                        setAdapter(adapter);
                    } else {
                        adapter.refresh(storiesIds);
                        adapter.notifyDataSetChanged();
                    }
                    InAppStoryManager.debugSDKCalls("StoriesList_loadStoriesInner", StoriesList.this.toString() + " setAdapter");
                    CsEventBus.getDefault().post(new StoriesLoaded(storiesIds.size()));
                    if (callback != null) callback.storiesLoaded(storiesIds.size());
                    InAppStoryManager.debugSDKCalls("StoriesList_loadStoriesInner", StoriesList.this.toString() + " callback " + (callback != null));
                    ProfilingManager.getInstance().setReady(listUid);
                    InAppStoryManager.debugSDKCalls("StoriesList_loadStoriesInner", StoriesList.this.toString() + " ProfilingManager");
                }

                @Override
                public void onError() {
                    if (callback != null) callback.loadError();
                }
            }, isFavoriteList, hasFavorite);

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (InAppStoryService.isNotNull())
                        InAppStoryService.getInstance().getDownloadManager().loadStories(
                                new LoadStoriesCallback() {
                                    @Override
                                    public void storiesLoaded(List<Integer> storiesIds) {
                                        InAppStoryManager.debugSDKCalls("StoriesList_loadStoriesInner", StoriesList.this.toString() + " loaded delay");
                                        CsEventBus.getDefault().post(new StoriesLoaded(storiesIds.size()));
                                        if (callback != null)
                                            callback.storiesLoaded(storiesIds.size());
                                        InAppStoryManager.debugSDKCalls("StoriesList_loadStoriesInner", StoriesList.this.toString() + " callback delay " + (callback != null));
                                        adapter = new StoriesAdapter(getContext(), storiesIds, appearanceManager, favoriteItemClick, isFavoriteList, callback);
                                        setLayoutManager(layoutManager);
                                        setAdapter(adapter);
                                        InAppStoryManager.debugSDKCalls("StoriesList_loadStoriesInner", StoriesList.this.toString() + " setAdapter delay");
                                        ProfilingManager.getInstance().setReady(listUid);
                                        InAppStoryManager.debugSDKCalls("StoriesList_loadStoriesInner", StoriesList.this.toString() + " ProfilingManager delay");
                                    }

                                    @Override
                                    public void onError() {
                                        if (callback != null) callback.loadError();
                                    }
                                }, isFavoriteList, hasFavorite);
                }
            }, 1000);
        }

    }

}
