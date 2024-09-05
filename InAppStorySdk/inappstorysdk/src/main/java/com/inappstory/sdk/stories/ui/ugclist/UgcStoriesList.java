package com.inappstory.sdk.stories.ui.ugclist;

import static java.util.UUID.randomUUID;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.callbacks.OnFavoriteItemClick;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryItemCoordinates;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.UgcStoryData;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListCallback;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListScrollCallback;
import com.inappstory.sdk.stories.statistic.GetOldStatisticManagerCallback;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.core.ui.screens.ScreensManager;
import com.inappstory.sdk.stories.ui.list.StoryTouchListener;
import com.inappstory.sdk.stories.ui.reader.ActiveStoryItem;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.ugc.list.OnUGCItemClick;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
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
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                ActiveStoryItem activeStoryItem = manager.getScreensHolder().getStoryScreenHolder().activeStoryItem();
                if (
                        activeStoryItem != null
                                && uniqueID != null
                                && uniqueID.equals(activeStoryItem.getUniqueListId())
                ) {
                    renewCoordinates(activeStoryItem.getListIndex());
                }
            }
        });
        InAppStoryManager.debugSDKCalls("StoriesList_onAttachedToWindow", ""
                + InAppStoryService.isNotNull());
        InAppStoryService.checkAndAddListSubscriber(manager);
        manager.checkCurrentSession();
    }

    private void renewCoordinates(final int index) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                View v = layoutManager.findViewByPosition(index);
                if (v == null) return;
                v.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                final StoryItemCoordinates coordinates = new StoryItemCoordinates(
                        x + v.getWidth() / 2,
                        y + v.getHeight() / 2
                );
                InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
                    @Override
                    public void use(@NonNull InAppStoryManager manager) throws Exception {
                        manager
                                .getScreensHolder()
                                .getStoryScreenHolder()
                                .coordinates(coordinates);
                    }
                });

            }
        }, 950);
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
        InAppStoryService service = InAppStoryService.getInstance();
        return service != null && service.getSession().allowUGC();
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

        adapter = null;
        loadStoriesInner(lastPayload);
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


    public void changeStoryEvent(int storyId, final String listID) {
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
        }
        if (ind >= 0 && this.uniqueID != null && this.uniqueID.equals(listID)) {
            InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryManager manager) throws Exception {
                    manager.getScreensHolder().getStoryScreenHolder().activeStoryItem(
                            new ActiveStoryItem(ind, listID)
                    );
                }
            });
            renewCoordinates(ind);
        }
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            OldStatisticManager.useInstance(
                    manager != null ? manager.currentSessionId : "",
                    new GetOldStatisticManagerCallback() {
                        @Override
                        public void get(@NonNull OldStatisticManager manager) {
                            manager.sendStatistic();
                        }
                    }
            );
        }
    }

    LoadStoriesCallback lcallback;

    public void loadStories(@NonNull String filter) {
        if (filter.isEmpty()) {
            loadStories();
        } else {
            loadStoriesLocal(filter);
        }
    }

    public void loadStories() {
        loadStories(new HashMap<String, Object>());
    }

    public void loadStories(@NonNull HashMap<String, Object> filter) {
        loadStoriesLocal(JsonParser.mapToJsonString(filter));
    }

    public String cacheId;

    public void setCacheId(String id) {
        this.cacheId = id;
    }

    private void loadStoriesLocal(String payload) {
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
        if (callback != null) callback.storiesLoaded(storiesIds.size(), "", getStoriesData(storiesIds));
    }

    private List<StoryData> getStoriesData(List<Integer> storiesIds) {
        List<StoryData> data = new ArrayList<>();
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null)
            for (int id : storiesIds) {
                Story story = service.getStoryDownloadManager().getStoryById(id, Story.StoryType.UGC);
                if (story != null) {
                    data.add(new UgcStoryData(story, SourceType.LIST));
                }
            }
        return data;
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
                manager != null ? manager.currentSessionId : "",
                storiesIds,
                appearanceManager,
                callback,
                hasSessionUGC() && appearanceManager.csHasUGC(),
                ugcItemClick);
        setLayoutManager(layoutManager);
        setAdapter(adapter);
    }

    private void loadStoriesInner(final String payload) {

        lastPayload = payload;
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager == null) {
            InAppStoryManager.showELog(
                    InAppStoryManager.IAS_ERROR_TAG,
                    StringsUtils.getErrorStringFromContext(
                            getContext(),
                            R.string.ias_npe_manager
                    )
            );
            return;
        }
        if (manager.noCorrectUserIdOrDevice()) return;

        final InAppStoryService service = InAppStoryService.getInstance();

        checkAppearanceManager();
        final String listUid = ProfilingManager.getInstance().addTask("widget_init");
        if (service != null) {
            lcallback = new LoadStoriesCallback() {
                @Override
                public void storiesLoaded(final List<Integer> storiesIds) {
                    if (cacheId != null && !cacheId.isEmpty()) {
                       service.listStoriesIds.put(cacheId, storiesIds);
                    }
                    post(new Runnable() {
                        @Override
                        public void run() {
                            setOrRefreshAdapter(storiesIds);
                            if (callback != null)
                                callback.storiesLoaded(
                                        storiesIds.size(),
                                        "",
                                        getStoriesData(storiesIds)
                                );
                        }
                    });
                    ProfilingManager.getInstance().setReady(listUid);
                }

                @Override
                public void setFeedId(String feedId) {

                }

                @Override
                public void onError() {
                    if (callback != null) callback.loadError("");
                }
            };
            service.getStoryDownloadManager().loadUgcStories(lcallback, payload);

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    InAppStoryService service1 = InAppStoryService.getInstance();
                    if (service1 != null) {
                        lcallback = new LoadStoriesCallback() {
                            @Override
                            public void storiesLoaded(final List<Integer> storiesIds) {
                                post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setOrRefreshAdapter(storiesIds);
                                        if (callback != null)
                                            callback.storiesLoaded(
                                                    storiesIds.size(),
                                                    "",
                                                    getStoriesData(storiesIds)
                                            );
                                    }
                                });
                                ProfilingManager.getInstance().setReady(listUid);

                            }

                            @Override
                            public void setFeedId(String feedId) {

                            }

                            @Override
                            public void onError() {
                                if (callback != null) callback.loadError("");
                            }
                        };
                        service1.getStoryDownloadManager().loadUgcStories(lcallback, payload);
                    }
                }
            }, 1000);
        }

    }

}
