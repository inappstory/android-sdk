package com.inappstory.sdk.stories.ui.ugclist;

import static java.util.UUID.randomUUID;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.exceptions.DataException;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.callbacks.OnFavoriteItemClick;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListCallback;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListScrollCallback;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.list.StoryTouchListener;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.ugc.list.OnUGCItemClick;

import java.util.HashMap;
import java.util.List;

public class UgcStoriesList extends RecyclerView {
    public UgcStoriesList(@NonNull Context context) {
        super(context);
        init(null);
    }

    public String getUniqueID() {
        return uniqueID;
    }

    private String uniqueID;

    public void setCallback(ListCallback callback) {
        this.callback = callback;
    }

    ListCallback callback;

    UgcStoriesListManager manager;

    public UgcStoriesList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AppearanceManager getAppearanceManager() {
        return appearanceManager;
    }

    public void setStoryTouchListener(StoryTouchListener storyTouchListener) {
        this.storyTouchListener = storyTouchListener;
        try {
            removeOnItemTouchListener(itemTouchListener);
        } catch (Exception e) {

        }
        itemTouchListener = new RecyclerTouchListener(storyTouchListener,
                getContext());
        addOnItemTouchListener(itemTouchListener);
    }

    StoryTouchListener storyTouchListener = null;

    ListScrollCallback scrollCallback;

    public void setScrollCallback(ListScrollCallback scrollCallback) {
        this.scrollCallback = scrollCallback;
    }


    public UgcStoriesList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (InAppStoryService.getInstance() != null) {
            InAppStoryService.getInstance().removeListSubscriber(manager);
        } else
            manager.clear();
    }

    private float mPrevX = 0f;
    private float mPrevY = 0f;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            mPrevX = e.getX();
            mPrevY = e.getY();
        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
            if (Math.abs(e.getX() - mPrevX) > Math.abs(e.getY() - mPrevY)) {
                if (scrollCallback != null) {
                    scrollCallback.scrollStart();
                }
            }
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL) {
            if (scrollCallback != null) {
                scrollCallback.scrollEnd();
            }
        }
        return super.onTouchEvent(e);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        manager.list = this;
        InAppStoryManager.debugSDKCalls("StoriesList_onAttachedToWindow", ""
                + InAppStoryService.isNotNull());
        InAppStoryService.checkAndAddListSubscriber(manager);
    }

    private void init(AttributeSet attributeSet) {
        uniqueID = randomUUID().toString();
        manager = new UgcStoriesListManager();
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!readerIsOpened)
                    sendIndexes();

            }
        });
        itemTouchListener = new RecyclerTouchListener(
                getContext());
        addOnItemTouchListener(itemTouchListener);

        //getRecycledViewPool().setMaxRecycledViews(6, 0);
    }

    OnItemTouchListener itemTouchListener;

    private boolean hasSessionUGC() {
        synchronized (Session.class) {
            return (!Session.needToUpdate()
                    && Session.getInstance().editor != null
                    && Session.getInstance().editor.url != null
                    && !Session.getInstance().editor.url.isEmpty());
        }
    }

    void sendIndexes() {
    }

    UgcStoriesAdapter adapter;

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
    OnUGCItemClick ugcItemClick;

    public void setOnUGCItemClick(OnUGCItemClick ugcItemClick) {
        this.ugcItemClick = ugcItemClick;
    }


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
            loadStoriesInner(lastPayload);
        } catch (DataException e) {
            e.printStackTrace();
        }
    }

    String lastPayload = "";

    public class RecyclerTouchListener implements OnItemTouchListener {
        private GestureDetector gestureDetector;
        private StoryTouchListener touchListener;
        View lastChild = null;

        public RecyclerTouchListener(Context context) {
            this(null, context);
        }

        public RecyclerTouchListener(StoryTouchListener touchListener, Context context) {
            this.touchListener = touchListener;
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
            checkAppearanceManager();
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


    public void changeStoryEvent(int storyId, String listID) {
        if (adapter == null || adapter.getStoriesIds() == null) return;
        for (int i = 0; i < adapter.getStoriesIds().size(); i++) {
            if (adapter.getStoriesIds().get(i) == storyId) {
                adapter.notifyItemChanged(i);
                break;
            }
        }
        if (layoutManager == null) return;
        final int ind = adapter.getIndexById(storyId);
        if (ind == -1) return;
        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(ind > 0 ? ind : 0, 0);
        } else if (layoutManager instanceof GridLayoutManager) {
            ((GridLayoutManager) layoutManager).scrollToPositionWithOffset(ind > 0 ? ind : 0, 0);
        }
        if (ind >= 0 && listID != null && this.uniqueID != null && this.uniqueID.equals(listID)) {
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

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            OldStatisticManager.getInstance().sendStatistic();
        }
    }

    LoadStoriesCallback lcallback;

    public void loadStories(@NonNull String filter) throws DataException {
        if (filter.isEmpty()) {
            loadStories();
        } else {
            loadStoriesLocal(filter);
        }
    }

    public void loadStories() throws DataException {
        loadStories(new HashMap<String, Object>());
    }

    public void loadStories(@NonNull HashMap<String, Object> filter) throws DataException {
        loadStoriesLocal(JsonParser.mapToJsonString(filter));
    }

    public String cacheId;

    public void setCacheId(String id) {
        this.cacheId = id;
    }

    private void loadStoriesLocal(String payload) throws DataException {
        if (InAppStoryService.isNull()
                || cacheId == null
                || cacheId.isEmpty()) {
            loadStoriesInner(payload);
            return;
        }
        List<Integer> storiesIds = InAppStoryService.getInstance()
                .listStoriesIds.get(cacheId);
        if (storiesIds == null) {
            loadStoriesInner(payload);
            return;
        }
        checkAppearanceManager();
        setOrRefreshAdapter(storiesIds);
        if (callback != null) callback.storiesLoaded(storiesIds.size(), "");
    }

    private void checkAppearanceManager() {
        if (this.appearanceManager == null) {
            this.appearanceManager = AppearanceManager.getCommonInstance();
        }

        if (this.appearanceManager == null) {
            this.appearanceManager = new AppearanceManager();
        }
        this.appearanceManager
                .csHasFavorite(false)
                .csHasLike(false)
                .csHasShare(false);
    }

    private void setOrRefreshAdapter(List<Integer> storiesIds) {
        checkAppearanceManager();
        setOverScrollMode(getAppearanceManager().csListOverscroll() ?
                OVER_SCROLL_ALWAYS : OVER_SCROLL_NEVER);
        adapter = new UgcStoriesAdapter(getContext(),
                uniqueID,
                storiesIds,
                appearanceManager,
                callback,
                hasSessionUGC() && appearanceManager.csHasUGC(),
                ugcItemClick);
        setLayoutManager(layoutManager);
        setAdapter(adapter);
    }


    private void loadStoriesInner(final String payload) throws DataException {
        lastPayload = payload;
        if (InAppStoryManager.getInstance() == null) {
            throw new DataException("'InAppStoryManager' can't be null", new Throwable("InAppStoryManager data is not valid"));
        }
        if (InAppStoryManager.getInstance().getUserId() == null) {
            throw new DataException("'userId' can't be null", new Throwable("InAppStoryManager data is not valid"));
        }

        checkAppearanceManager();
        final String listUid = ProfilingManager.getInstance().addTask("widget_init");
        if (InAppStoryService.isNotNull()) {
            lcallback = new LoadStoriesCallback() {
                @Override
                public void storiesLoaded(List<Integer> storiesIds) {
                    if (cacheId != null && !cacheId.isEmpty()) {
                        if (InAppStoryService.isNotNull()) {
                            InAppStoryService.getInstance()
                                    .listStoriesIds.put(cacheId, storiesIds);
                        }
                    }
                    setOrRefreshAdapter(storiesIds);
                    ProfilingManager.getInstance().setReady(listUid);
                    if (callback != null) callback.storiesLoaded(storiesIds.size(), "");
                }

                @Override
                public void setFeedId(String feedId) {

                }

                @Override
                public void onError() {
                    if (callback != null) callback.loadError("");
                }
            };
            InAppStoryService.getInstance().getDownloadManager().loadUgcStories(lcallback, payload);

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (InAppStoryService.isNotNull()) {
                        lcallback = new LoadStoriesCallback() {
                            @Override
                            public void storiesLoaded(List<Integer> storiesIds) {
                                setOrRefreshAdapter(storiesIds);
                                ProfilingManager.getInstance().setReady(listUid);
                                if (callback != null)
                                    callback.storiesLoaded(storiesIds.size(), "");
                            }

                            @Override
                            public void setFeedId(String feedId) {

                            }

                            @Override
                            public void onError() {
                                if (callback != null) callback.loadError("");
                            }
                        };
                        InAppStoryService.getInstance().getDownloadManager().loadUgcStories(lcallback, payload);
                    }
                }
            }, 1000);
        }

    }

}
