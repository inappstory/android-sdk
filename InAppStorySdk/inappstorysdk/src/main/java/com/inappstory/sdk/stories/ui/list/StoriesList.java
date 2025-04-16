package com.inappstory.sdk.stories.ui.list;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASStatisticStoriesV1;
import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.core.dataholders.StoriesRequestKey;
import com.inappstory.sdk.core.storieslist.StoriesRequestResult;
import com.inappstory.sdk.core.ui.screens.storyreader.StoryScreenHolder;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.core.network.content.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.callbacks.OnFavoriteItemClick;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryItemCoordinates;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListCallback;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListScrollCallback;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.statistic.IASStatisticStoriesV2Impl;
import com.inappstory.sdk.stories.ui.reader.ActiveStoryItem;
import com.inappstory.sdk.ugc.list.OnUGCItemClick;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class StoriesList extends RecyclerView {
    public StoriesList(@NonNull Context context) {
        super(context);
        init(null);
    }

    public static String DEFAULT_FEED = "default";

    public void updateVisibleArea(boolean triggerScrollCallback) {
        getVisibleItems();
        if (triggerScrollCallback && scrollCallback != null && !scrolledItems.isEmpty()) {
            scrollCallback.onVisibleAreaUpdated(
                    new ArrayList<>(scrolledItems.values())
            );
            scrolledItems.clear();
        }
    }

    public String getFeed() {
        synchronized (feedLock) {
            if (isFavoriteList) return null;
            return feed;
        }
    }

    private final Object feedLock = new Object();

    public void setFeed(String feed) {
        boolean reloadStories = false;
        synchronized (feedLock) {
            if (!isFavoriteList && feed != null && !feed.isEmpty()) {
                if (this.feed != null && !this.feed.isEmpty() && !this.feed.equals(feed)) {
                    reloadStories = true;
                }
                this.feed = feed;
            }
        }
        if (reloadStories) {
            loadStories();
        }
    }

    private String feed = DEFAULT_FEED;
    private String feedId = null;

    public String getFeedId() {
        return feedId;
    }

    void setListFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    private String uniqueID;

    public void setCallback(ListCallback callback) {
        this.callback = callback;
    }

    ListCallback callback;
    ListScrollCallback scrollCallback;

    public void setScrollCallback(ListScrollCallback scrollCallback) {
        this.scrollCallback = scrollCallback;
    }

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


    private int mTouchSlop = 0;
    private float mPrevX = 0f;
    private float mPrevY = 0f;


    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            mPrevX = e.getX();
            mPrevY = e.getY();
            if (scrollCallback != null) {
                // scrollCallback.userInteractionStart();
            }
            getVisibleItems();
        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
            if (layoutManager != null && layoutManager instanceof LinearLayoutManager) {
                if (((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    if (Math.abs(e.getX() - mPrevX) > Math.abs(e.getY() - mPrevY)) {
                        if (scrollCallback != null) {
                            scrollCallback.scrollStart();
                        }
                    }
                } else {
                    if (Math.abs(e.getY() - mPrevY) > Math.abs(e.getX() - mPrevX)) {
                        if (scrollCallback != null) {
                            scrollCallback.scrollStart();
                        }
                    }
                }
            }

        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL) {
            if (scrollCallback != null) {
                //         scrollCallback.userInteractionEnd();
            }
        }
        return super.onTouchEvent(e);
    }

    public StoriesList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) {
                service.removeListSubscriber(manager);
            }

            @Override
            public void error() {
                manager.clear();
            }
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        manager.list = this;
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                StoryScreenHolder storyScreenHolder = core.screensManager().getStoryScreenHolder();
                ActiveStoryItem activeStoryItem = storyScreenHolder.activeStoryItem();
                if (
                        activeStoryItem != null
                                && uniqueID != null
                                && uniqueID.equals(activeStoryItem.getUniqueListId())
                ) {
                    renewCoordinates(activeStoryItem.getListIndex(), storyScreenHolder);
                }
            }
        });

        InAppStoryService.checkAndAddListSubscriber(manager);
        manager.checkCurrentSession();
    }

    private void init(AttributeSet attributeSet) {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        manager = new StoriesListManager();
        setDecorator();
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.StoriesList);
            isFavoriteList = typedArray.getBoolean(R.styleable.StoriesList_cs_listIsFavorite, false);
            synchronized (feedLock) {
                if (!isFavoriteList) {
                    feed = typedArray.getString(R.styleable.StoriesList_cs_feed);
                    if (feed == null || feed.isEmpty()) feed = StoriesList.DEFAULT_FEED;
                } else {
                    feed = null;
                }
            }
            typedArray.recycle();
        }
        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!readerIsOpened) {
                    sendIndexes();
                    getVisibleItems();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    if (scrollCallback != null && !scrolledItems.isEmpty()) {
                        scrollCallback.onVisibleAreaUpdated(
                                new ArrayList<>(scrolledItems.values())
                        );
                        scrollCallback.scrollEnd();
                    }
                    scrolledItems.clear();
                }
            }
        });
        itemTouchListener = new RecyclerTouchListener(
                getContext());
        addOnItemTouchListener(itemTouchListener);
        scrollToPosition(0);
        //getRecycledViewPool().setMaxRecycledViews(6, 0);
    }

    OnItemTouchListener itemTouchListener;

    private boolean hasSessionUGC() {
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) return false;
        return inAppStoryManager.iasCore().sessionManager().getSession().allowUGC();
    }

    HashMap<Integer, ShownStoriesListItem> scrolledItems = new HashMap<>();

    void sendIndexes() {
        int hasUgc = hasUgc();
        final ArrayList<Integer> indexes = new ArrayList<>();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            for (int i = linearLayoutManager.findFirstVisibleItemPosition();
                 i <= linearLayoutManager.findLastVisibleItemPosition(); i++) {
                int ind = i - hasUgc;
                if (adapter != null && adapter.getStoriesIds().size() > ind && ind >= 0) {
                    View holder = linearLayoutManager.getChildAt(
                            i - linearLayoutManager.findFirstVisibleItemPosition()
                    );
                    if (holder != null) {
                        Rect rect = new Rect();
                        holder.getGlobalVisibleRect(rect);
                        Rect rect2 = new Rect();
                        getGlobalVisibleRect(rect2);
                        int rectLeft = Math.max(rect.left, rect2.left);
                        int rectRight = Math.min(rect.right, rect2.right);
                        int rectWidth = Math.max(0, rectRight - rectLeft);
                        if (rectWidth > 0) {
                            indexes.add(adapter.getStoriesIds().get(ind));
                        }
                    }
                }
            }
        }
        InAppStoryManager.useCore(
                new UseIASCoreCallback() {
                    @Override
                    public void use(@NonNull final IASCore core) {
                        final List<Integer> newIndexes = core.statistic().newStatisticPreviews(indexes);
                        core.sessionManager().useOrOpenSession(
                                new OpenSessionCallback() {
                                    @Override
                                    public void onSuccess(RequestLocalParameters requestLocalParameters) {
                                        core.statistic().storiesV2().sendViewStory(
                                                newIndexes,
                                                isFavoriteList ?
                                                        IASStatisticStoriesV2Impl.FAVORITE :
                                                        IASStatisticStoriesV2Impl.LIST,
                                                feedId
                                        );
                                        core.statistic().storiesV1(
                                                requestLocalParameters.sessionId,
                                                new GetStatisticV1Callback() {
                                                    @Override
                                                    public void get(@NonNull IASStatisticStoriesV1 manager) {
                                                        manager.previewStatisticEvent(newIndexes);
                                                    }
                                                }
                                        );
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                }
                        );
                    }
                }
        );


    }

    int hasUgc() {
        return (hasSessionUGC() && !isFavoriteList && getAppearanceManager().csHasUGC()) ? 1 : 0;
    }

    private void getVisibleItems() {
        int hasUgc = hasUgc();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            for (int i = linearLayoutManager.findFirstVisibleItemPosition();
                 i <= linearLayoutManager.findLastVisibleItemPosition(); i++) {
                int ind = i - hasUgc;
                if (adapter != null && adapter.getStoriesIds().size() > ind && ind >= 0) {
                    View holder = linearLayoutManager.getChildAt(
                            i - linearLayoutManager.findFirstVisibleItemPosition()
                    );
                    if (holder != null) {
                        Rect rect = new Rect();
                        holder.getGlobalVisibleRect(rect);
                        Rect rect2 = new Rect();
                        getGlobalVisibleRect(rect2);
                        int rectTop = Math.max(rect.top, rect2.top);
                        int rectBottom = Math.min(rect.bottom, rect2.bottom);
                        int rectLeft = Math.max(rect.left, rect2.left);
                        int rectRight = Math.min(rect.right, rect2.right);
                        int rectHeight = Math.max(0, rectBottom - rectTop);
                        int rectWidth = Math.max(0, rectRight - rectLeft);
                        float currentPercentage =
                                (float) (rectHeight * rectWidth) /
                                        (holder.getWidth() * holder.getHeight());
                        ShownStoriesListItem cachedData = scrolledItems.get(i);
                        if (cachedData != null) {
                            currentPercentage = Math.max(currentPercentage, cachedData.areaPercent);
                        }
                        IListItemContent current = null;
                        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
                        if (inAppStoryManager != null)
                            current = inAppStoryManager
                                    .iasCore().contentHolder().listsContent()
                                    .getByIdAndType(
                                            adapter.getStoriesIds().get(ind),
                                            ContentType.STORY
                                    );
                        if (current != null && currentPercentage > 0) {
                            scrolledItems.put(i, new ShownStoriesListItem(
                                    new StoryData(
                                            current,
                                            feed,
                                            isFavoriteList ? SourceType.FAVORITE : SourceType.LIST
                                    ),
                                    i,
                                    currentPercentage
                            ));
                        }
                    }
                }
            }
        }
    }

    StoriesAdapter adapter;
    boolean loadStoriesLaunched = false;

    @Override
    public void setLayoutManager(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        super.setLayoutManager(layoutManager);
    }

    private LayoutManager defaultLayoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false) {
        @Override
        public int scrollHorizontallyBy(int dx, Recycler recycler, State state) {
            int scrollRange = super.scrollHorizontallyBy(dx, recycler, state);
            int overScroll = dx - scrollRange;
            if (overScroll != 0 && scrollCallback != null) {
                //  scrollCallback.onOverscroll(overScroll, 0);
            }
            return scrollRange;
        }
    };
    LayoutManager layoutManager = defaultLayoutManager;

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
        getVisibleItems();
    }

    void sessionIsOpened() {

    }

    public void refresh() {
        loadStoriesLaunched = true;
        adapter = null;
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                StoriesRequestKey key = null;
                if (isFavoriteList) {
                    key = new StoriesRequestKey(true);
                } else {
                    IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
                    List<String> nonSortedTags = new ArrayList<>(settingsHolder.tags());
                    Collections.sort(nonSortedTags);
                    final String tagsHash = StringsUtils.md5(TextUtils.join(",", nonSortedTags));
                    key = new StoriesRequestKey(cacheId, feed, tagsHash);
                }
                loadStoriesInner(key);
            }
        });
    }

    public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
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
            if (touchListener == null)
                touchListener = getAppearanceManager().csStoryTouchListener();
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
        if (!Objects.equals(listID, getUniqueID())) return;
        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(Math.max(ind, 0), 0);
        }
        if (ind >= 0) {
            InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    StoryScreenHolder storyScreenHolder = core.screensManager().getStoryScreenHolder();
                    storyScreenHolder.activeStoryItem(
                            new ActiveStoryItem(ind, listID)
                    );
                    renewCoordinates(ind, storyScreenHolder);
                }
            });
        }
    }

    private void renewCoordinates(final int index, @NonNull final StoryScreenHolder screenHolder) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                View v = layoutManager.findViewByPosition(index);
                if (v == null) return;
                v.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                screenHolder.coordinates(
                        new StoryItemCoordinates(
                                x + v.getWidth() / 2,
                                y + v.getHeight() / 2
                        )
                );

            }
        }, 950);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    core.statistic().storiesV1(
                            manager != null ?
                                    manager.currentSessionId :
                                    core.sessionManager().getSession().getSessionId(),
                            new GetStatisticV1Callback() {
                                @Override
                                public void get(@NonNull IASStatisticStoriesV1 manager) {
                                    manager.sendStatistic();
                                }
                            }
                    );
                }
            });
        }
    }

    public void clearAllFavorites() {
        if (adapter == null) return;
        if (isFavoriteList) {
            adapter.hasFavItem = false;
            adapter.getStoriesIds().clear();
        } else {
            adapter.hasFavItem = false;
        }
        adapter.notifyDataSetChanged();
        if (isFavoriteList)
            adapter.notifyChanges();
    }

    public void favStory(int id, boolean favStatus, boolean isEmpty) {
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
            updateVisibleArea(true);
        } else if (!adapter.hasFavItem) {
            if (!isEmpty) {
                adapter.hasFavItem = (getAppearanceManager().csHasFavorite());
                // adapter.refresh();
                adapter.notifyDataSetChanged();
            }
        } else if (isEmpty) {
            adapter.hasFavItem = false;
            adapter.notifyDataSetChanged();
        } else {
            adapter.notifyItemChanged(getAdapter().getItemCount() - 1);
        }
        if (isFavoriteList)
            adapter.notifyChanges();
    }


    LoadStoriesCallback lcallback;

    public void loadStories() {
        loadStoriesLaunched = true;
        InAppStoryManager.debugSDKCalls("StoriesList_loadStories", "");
        try {
            loadStoriesLocal();
        } catch (Throwable e) {
            InAppStoryManager.handleException(e);
        }
    }


    private String cacheId;

    public void setCacheId(String id) {
        this.cacheId = id;
    }

    public void clearCachedData() {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                if (isFavoriteList) {
                    core.storiesListVMHolder().removeFavoriteResult();
                } else {
                    core.storiesListVMHolder().removeResultByIdAndFeed(cacheId, feed);
                }
            }
        });
    }

    private void loadStoriesLocal() {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                StoriesRequestKey key = null;
                if (isFavoriteList) {
                    key = new StoriesRequestKey(true);
                } else {
                    IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
                    List<String> nonSortedTags = new ArrayList<>(settingsHolder.tags());
                    Collections.sort(nonSortedTags);
                    final String tagsHash = StringsUtils.md5(TextUtils.join(",", nonSortedTags));
                    key = new StoriesRequestKey(cacheId, feed, tagsHash);
                }
                List<Integer> storiesIds;
                StoriesRequestResult state = core.storiesListVMHolder().getStoriesRequestResult(key);
                if (state == null || (storiesIds = state.getStoriesIds()) == null) {
                    loadStoriesInner(key);
                    return;
                }
                setOrRefreshAdapter(storiesIds);
                if (callback != null) {
                    callback.storiesLoaded(
                            storiesIds.size(),
                            StringsUtils.getNonNull(getFeed()),
                            getStoriesData(storiesIds)
                    );
                }
            }
        });
    }

    private List<StoryData> getStoriesData(List<Integer> storiesIds) {
        List<StoryData> data = new ArrayList<>();
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager != null)
            for (int id : storiesIds) {
                IListItemContent story = inAppStoryManager.iasCore().contentHolder()
                        .listsContent().getByIdAndType(id, ContentType.STORY);
                if (story != null) {
                    data.add(new StoryData(story, feed, SourceType.LIST));
                }
            }
        return data;
    }

    private AppearanceManager getAppearanceManager() {
        if (this.appearanceManager == null) {
            this.appearanceManager = AppearanceManager.getCommonInstance();
        }

        if (this.appearanceManager == null) {
            this.appearanceManager = new AppearanceManager();
        }
        return this.appearanceManager;
    }

    private void setOrRefreshAdapter(final List<Integer> storiesIds) {
        post(
                new Runnable() {
                    @Override
                    public void run() {
                        final AppearanceManager appearanceManager = getAppearanceManager();
                        setOverScrollMode(appearanceManager.csListOverscroll() ?
                                OVER_SCROLL_ALWAYS : OVER_SCROLL_NEVER);
                        adapter = new StoriesAdapter(
                                InAppStoryManager.getInstance().iasCore(),
                                getContext(),
                                uniqueID,
                                manager != null ? manager.currentSessionId : "",
                                storiesIds,
                                appearanceManager,
                                isFavoriteList,
                                callback,
                                getFeed(),
                                getFeedId(),
                                appearanceManager.csHasFavorite() && !isFavoriteList,
                                !isFavoriteList ? favoriteItemClick : null,
                                hasSessionUGC() && appearanceManager.csHasUGC() && !isFavoriteList,
                                !isFavoriteList ? ugcItemClick : null);
                        if (layoutManager == defaultLayoutManager && appearanceManager.csColumnCount() != null) {
                            setLayoutManager(new GridLayoutManager(getContext(), appearanceManager.csColumnCount()) {
                                @Override
                                public int scrollVerticallyBy(int dy, Recycler recycler, State state) {
                                    int scrollRange = super.scrollVerticallyBy(dy, recycler, state);
                                    int overScroll = dy - scrollRange;
                                    if (overScroll != 0 && scrollCallback != null) {
                                        //  scrollCallback.onOverscroll(0, overScroll);
                                    }
                                    return scrollRange;
                                }
                            });

                        } else
                            setLayoutManager(layoutManager);
                        setAdapter(adapter);
                        if (scrollCallback != null && !scrolledItems.isEmpty()) {

                            scrollCallback.onVisibleAreaUpdated(
                                    new ArrayList<>(scrolledItems.values())
                            );
                        }
                        scrolledItems.clear();
                    }
                }
        );
    }

    private void setDecorator() {
        addItemDecoration(new ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int margin = appearanceManager.csListItemMargin(getContext());
                outRect.bottom = margin;
                outRect.top = margin;
                int position = parent.getChildAdapterPosition(view);
                int itemCount = state.getItemCount();
                if (position == 0)
                    outRect.left = margin;
                if (position == itemCount - 1) {
                    outRect.right = margin;
                }
            }
        });
    }

    private void loadStoriesInner(final StoriesRequestKey key) {
        InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull final IASCore core) {
                if (((IASDataSettingsHolder) core.settingsAPI()).noCorrectUserIdOrDevice()) {
                    InAppStoryManager.showELog(
                            InAppStoryManager.IAS_ERROR_TAG,
                            StringsUtils.getErrorStringFromContext(
                                    getContext(),
                                    R.string.ias_usage_without_user_and_device
                            )
                    );
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            setOrRefreshAdapter(new ArrayList<Integer>());
                        }
                    });
                    return;
                }

                InAppStoryManager.debugSDKCalls("StoriesList_loadStoriesInner", "");
                final String listUid = core.statistic().profiling().addTask("widget_init");
                final boolean hasFavorite = (!isFavoriteList && getAppearanceManager().csHasFavorite());

                lcallback = new LoadStoriesCallback() {
                    @Override
                    public void storiesLoaded(final List<Integer> storiesIds) {
                        core.storiesListVMHolder().setStoriesRequestResult(
                                key,
                                new StoriesRequestResult(storiesIds)
                        );
                        core.statistic().profiling().setReady(listUid);
                        post(new Runnable() {
                            @Override
                            public void run() {
                                setOrRefreshAdapter(storiesIds);
                                if (callback != null)
                                    callback.storiesLoaded(
                                            storiesIds.size(),
                                            StringsUtils.getNonNull(getFeed()),
                                            getStoriesData(storiesIds)
                                    );
                            }
                        });


                    }

                    @Override
                    public void setFeedId(String feedId) {
                        setListFeedId(feedId);
                    }

                    @Override
                    public void onError() {
                        setOrRefreshAdapter(new ArrayList<Integer>());
                        if (callback != null)
                            callback.loadError(StringsUtils.getNonNull(getFeed()));
                    }
                };
                core.contentLoader().storyDownloadManager().loadStories(
                        getFeed(),
                        lcallback,
                        null,
                        isFavoriteList,
                        hasFavorite
                );
            }

            @Override
            public void error() {
                setOrRefreshAdapter(new ArrayList<Integer>());
                InAppStoryManager.showELog(
                        InAppStoryManager.IAS_ERROR_TAG,
                        StringsUtils.getErrorStringFromContext(
                                getContext(),
                                R.string.ias_npe_manager
                        )
                );
            }
        });
    }

}
