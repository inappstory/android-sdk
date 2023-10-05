package com.inappstory.sdk.stories.ui.list;

import static java.util.UUID.randomUUID;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
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
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.callbacks.OnFavoriteItemClick;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListCallback;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListScrollCallback;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.uidomain.list.IStoriesListPresenter;
import com.inappstory.sdk.stories.uidomain.list.StoriesListPresenter;
import com.inappstory.sdk.stories.uidomain.list.readerconnector.StoriesListNotify;
import com.inappstory.sdk.ugc.list.OnUGCItemClick;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoriesList extends RecyclerView {
    public StoriesList(@NonNull Context context) {
        super(context);
        init(null);
    }

    IStoriesListPresenter presenter = new StoriesListPresenter();

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

    public Object feedLock = new Object();

    public void setFeed(String feed) {
        boolean reloadStories = false;
        synchronized (feedLock) {
            if (!isFavoriteList && feed != null && !feed.isEmpty()) {
                if (this.feed != null && !this.feed.isEmpty() && !this.feed.equals(feed)) {
                    InAppStoryManager manager = InAppStoryManager.getInstance();
                    if (manager != null && cacheId != null)
                        manager.clearCachedList(cacheId);
                    reloadStories = true;
                }
                this.feed = feed;
            }
        }
        if (this.adapter != null && reloadStories) {
            refreshList();
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

    private String uniqueID;

    public void setCallback(ListCallback callback) {
        this.callback = callback;
    }

    ListCallback callback;
    ListScrollCallback scrollCallback;

    public void setScrollCallback(ListScrollCallback scrollCallback) {
        this.scrollCallback = scrollCallback;
    }

    StoriesListNotify manager;
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
        manager.unsubscribe();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        manager.bindList(this);
        manager.subscribe();
    }

    private void init(AttributeSet attributeSet) {
        uniqueID = randomUUID().toString();
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        manager = new StoriesListNotify();
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
                    Log.e("onVisibleAreaUpdated", scrolledItems.entrySet().toString());
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
        synchronized (Session.class) {
            return (!Session.needToUpdate()
                    && Session.getInstance().editor != null
                    && Session.getInstance().editor.url != null
                    && !Session.getInstance().editor.url.isEmpty());
        }
    }

    HashMap<Integer, ShownStoriesListItem> scrolledItems = new HashMap<>();

    void sendIndexes() {
        int hasUgc = hasUgc();
        ArrayList<Integer> indexes = new ArrayList<>();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            for (int i = linearLayoutManager.findFirstVisibleItemPosition();
                 i <= linearLayoutManager.findLastVisibleItemPosition(); i++) {
                int ind = i - hasUgc;
                if (adapter != null && adapter.getStoriesIds().size() > ind && ind >= 0) {
                    indexes.add(adapter.getStoriesIds().get(ind));
                }
            }
        }
        ArrayList<Integer> newIndexes =
                OldStatisticManager.getInstance().newStatisticPreviews(indexes);
        try {
            if (StatisticManager.getInstance() != null) {
                StatisticManager.getInstance().sendViewStory(newIndexes,
                        isFavoriteList ? StatisticManager.FAVORITE : StatisticManager.LIST, feedId);
            }
        } catch (Exception e) {

        }
        OldStatisticManager.getInstance().previewStatisticEvent(indexes);
    }

    int hasUgc() {
        checkAppearanceManager();
        return (hasSessionUGC() && !isFavoriteList && appearanceManager.csHasUGC()) ? 1 : 0;
    }

    private void getVisibleItems() {
        checkAppearanceManager();
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
                            cachedData.areaPercent = Math.max(
                                    currentPercentage,
                                    cachedData.areaPercent
                            );
                        } else {
                            ShownStoriesListItem current = presenter.getShownStoriesListItemByStoryId(
                                    adapter.getStoriesIds().get(ind),
                                    i,
                                    currentPercentage,
                                    feed,
                                    isFavoriteList ? SourceType.FAVORITE : SourceType.LIST
                            );
                            if (current != null) {
                                scrolledItems.put(i, current);
                            }

                        }

                    }
                }
            }
        }
    }

    StoriesAdapter adapter;

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


    public void refreshList() {
        adapter = null;
        loadStoriesInner();
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


    public void changeStoryEvent(int storyId) {
        if (adapter == null || adapter.getStoriesIds() == null) return;
        for (int i = 0; i < adapter.getStoriesIds().size(); i++) {
            if (adapter.getStoriesIds().get(i) == storyId) {
                adapter.notifyItemChanged(i);
                break;
            }
        }
        if (layoutManager == null) return;
        final int ind = adapter.getIndexById(storyId);
        if (ind < 0) return;
        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(ind, 0);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                View v = layoutManager.findViewByPosition(ind);
                if (v == null) return;
                v.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                ScreensManager.getInstance().coordinates = new Point(x + v.getWidth() / 2,
                        y + v.getHeight() / 2);

            }
        }, 950);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            OldStatisticManager.getInstance().sendStatistic();
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

    public void favStory(int id, boolean favStatus, List<FavoriteImage> favImages, boolean isEmpty) {
        if (adapter == null) return;

        checkAppearanceManager();
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
        if (isFavoriteList)
            adapter.notifyChanges();
    }


    LoadStoriesCallback lcallback;

    public void loadStories() {
        InAppStoryManager.debugSDKCalls("StoriesList_loadStories", "");
        loadStoriesLocal();
    }

    private String cacheId;

    public void setCacheId(String id) {
        this.cacheId = id;
    }

    private void loadStoriesLocal() {
        if (cacheId == null
                || cacheId.isEmpty()) {
            loadStoriesInner();
            return;
        }
        List<Integer> storiesIds = presenter.getCachedStoriesPreviews(cacheId);
        if (storiesIds == null) {
            loadStoriesInner();
            return;
        }
        checkAppearanceManager();
        setOrRefreshAdapter(storiesIds);
        if (callback != null)
            callback.storiesLoaded(storiesIds.size(), StringsUtils.getNonNull(getFeed()));
    }

    private void checkAppearanceManager() {
        if (this.appearanceManager == null) {
            this.appearanceManager = AppearanceManager.getCommonInstance();
        }

        if (this.appearanceManager == null) {
            this.appearanceManager = new AppearanceManager();
        }

    }

    private void setOrRefreshAdapter(List<Integer> storiesIds) {
        checkAppearanceManager();

        setOverScrollMode(getAppearanceManager().csListOverscroll() ?
                OVER_SCROLL_ALWAYS : OVER_SCROLL_NEVER);
        adapter = new StoriesAdapter(getContext(),
                uniqueID,
                storiesIds,
                appearanceManager,
                isFavoriteList,
                callback,
                getFeed(),
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
            addItemDecoration(new ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.bottom = appearanceManager.csListItemMargin(getContext());
                    outRect.top = appearanceManager.csListItemMargin(getContext());
                }
            });
        } else
            setLayoutManager(layoutManager);
        setAdapter(adapter);
        post(
                new Runnable() {
                    @Override
                    public void run() {

                        Log.e("onVisibleAreaUpdated", scrolledItems.entrySet().toString());
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


    public void loadStoriesInner() {

        if (InAppStoryManager.getInstance() == null) {
            InAppStoryManager.showELog(InAppStoryManager.IAS_ERROR_TAG, "'InAppStoryManager' cannot be null");
            return;
        }
        if (InAppStoryManager.getInstance().getUserId() == null) {
            InAppStoryManager.showELog(InAppStoryManager.IAS_ERROR_TAG, "Parameter 'userId' cannot be null");
            return;
        }

        checkAppearanceManager();
        InAppStoryManager.debugSDKCalls("StoriesList_loadStoriesInner", "");
        final String listUid = ProfilingManager.getInstance().addTask("widget_init");
        boolean hasFavorite = (appearanceManager != null && !isFavoriteList && appearanceManager.csHasFavorite());
        if (InAppStoryService.isNotNull()) {
            lcallback = new LoadStoriesCallback() {
                @Override
                public void storiesLoaded(final List<Integer> storiesIds) {
                    if (cacheId != null && !cacheId.isEmpty()) {
                        if (InAppStoryService.isNotNull()) {
                            InAppStoryService.getInstance()
                                    .listStoriesIds.put(cacheId, storiesIds);
                        }
                    }
                    post(new Runnable() {
                        @Override
                        public void run() {
                            setOrRefreshAdapter(storiesIds);
                            if (callback != null)
                                callback.storiesLoaded(storiesIds.size(), StringsUtils.getNonNull(getFeed()));
                        }
                    });
                    ProfilingManager.getInstance().setReady(listUid);

                }

                @Override
                public void setFeedId(String feedId) {
                    setListFeedId(feedId);
                }

                @Override
                public void onError() {
                    if (callback != null) callback.loadError(StringsUtils.getNonNull(getFeed()));
                }
            };
            InAppStoryService.getInstance().getDownloadManager().loadStories(getFeed(), lcallback, isFavoriteList, hasFavorite);

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (InAppStoryService.isNotNull()) {
                        boolean hasFav = (appearanceManager != null && !isFavoriteList && appearanceManager.csHasFavorite());
                        lcallback = new LoadStoriesCallback() {
                            @Override
                            public void storiesLoaded(final List<Integer> storiesIds) {
                                post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setOrRefreshAdapter(storiesIds);
                                        if (callback != null)
                                            callback.storiesLoaded(storiesIds.size(), StringsUtils.getNonNull(getFeed()));
                                    }
                                });
                                ProfilingManager.getInstance().setReady(listUid);

                            }

                            @Override
                            public void setFeedId(String feedId) {
                                setListFeedId(feedId);
                            }

                            @Override
                            public void onError() {
                                if (callback != null)
                                    callback.loadError(StringsUtils.getNonNull(getFeed()));
                            }
                        };
                        InAppStoryService.getInstance().getDownloadManager().loadStories(getFeed(),
                                lcallback, isFavoriteList, hasFav);
                    }
                }
            }, 1000);
        }

    }

}
