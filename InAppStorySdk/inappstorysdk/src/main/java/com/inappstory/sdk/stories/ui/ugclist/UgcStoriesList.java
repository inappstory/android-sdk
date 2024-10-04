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
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASStatisticV1;
import com.inappstory.sdk.core.stories.StoriesListVMState;
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
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.statistic.IASStatisticProfilingImpl;
import com.inappstory.sdk.stories.ui.list.StoryTouchListener;
import com.inappstory.sdk.stories.ui.reader.ActiveStoryItem;
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
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                ActiveStoryItem activeStoryItem = core.screensManager().getStoryScreenHolder().activeStoryItem();
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
                InAppStoryManager.useCore(new UseIASCoreCallback() {
                    @Override
                    public void use(@NonNull IASCore core) {
                        core.screensManager().getStoryScreenHolder()
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
            InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    core.screensManager().getStoryScreenHolder().activeStoryItem(
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

            InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    core.statistic().v1(
                            manager != null ?
                                    manager.currentSessionId :
                                    core.sessionManager().getSession().getSessionId(),
                            new GetStatisticV1Callback() {
                                @Override
                                public void get(@NonNull IASStatisticV1 manager) {
                                    manager.sendStatistic();
                                }
                            }
                    );
                }
            });

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

    private void loadStoriesLocal(final String payload) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                if (cacheId == null
                        || cacheId.isEmpty()) {
                    loadStoriesInner(payload);
                    return;
                }
                StoriesListVMState state = core.storiesListVMHolder().getVMState(cacheId);
                List<Integer> storiesIds;
                if (state == null || (storiesIds = state.getStoriesIds()) == null) {
                    loadStoriesInner(payload);
                    return;
                }
                checkAppearanceManager();
                setOrRefreshAdapter(storiesIds);
                if (callback != null) callback.storiesLoaded(
                        storiesIds.size(),
                        "",
                        getStoriesData(storiesIds)
                );
            }
        });
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
        adapter = new UgcStoriesAdapter(
                InAppStoryManager.getInstance().iasCore(),
                getContext(),
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

        final IASCore core = manager.iasCore();
        if (((IASDataSettingsHolder)core.settingsAPI()).noCorrectUserIdOrDevice()) return;
        final InAppStoryService service = InAppStoryService.getInstance();

        checkAppearanceManager();
        final String listUid = core.statistic().profiling().addTask("widget_init");
        lcallback = new LoadStoriesCallback() {
            @Override
            public void storiesLoaded(final List<Integer> storiesIds) {
                if (cacheId != null && !cacheId.isEmpty()) {
                    core.storiesListVMHolder().setVMState(
                            cacheId,
                            new StoriesListVMState(storiesIds)
                    );
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
                core.statistic().profiling().setReady(listUid);
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

    }

}
