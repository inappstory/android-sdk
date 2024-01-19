package com.inappstory.sdk.stories.ui.list;

import static java.util.UUID.randomUUID;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
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
import com.inappstory.sdk.UseServiceInstanceCallback;
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
import com.inappstory.sdk.stories.utils.Sizes;
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
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                service.removeListSubscriber(manager);
            }

            @Override
            public void error() throws Exception {
                manager.clear();
            }
        });
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
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        manager = new StoriesListManager();
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
                            currentPercentage = Math.max(currentPercentage, cachedData.areaPercent);
                        }
                        InAppStoryService service = InAppStoryService.getInstance();
                        if (service == null) return;
                        Story current = service.getDownloadManager()
                                .getStoryById(adapter.getStoriesIds().get(ind), Story.StoryType.COMMON);
                        if (current != null && currentPercentage > 0) {
                            scrolledItems.put(i, new ShownStoriesListItem(
                                    new StoryData(
                                            current.id,
                                            StringsUtils.getNonNull(current.statTitle),
                                            StringsUtils.getNonNull(current.tags),
                                            current.getSlidesCount(),
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


    void refreshList() {
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
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(Math.max(ind, 0), 0);
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
                    ScreensManager.getInstance().coordinates = new Point(x + v.getWidth() / 2,
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

    public void clearAllFavorites() {
        if (InAppStoryService.isNull()) return;
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
        if (InAppStoryService.isNull()) return;
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
            updateVisibleArea(true);
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
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                if (cacheId == null || cacheId.isEmpty()) {
                    loadStoriesInner();
                    return;
                }
                List<Integer> storiesIds = service.listStoriesIds.get(cacheId);
                if (storiesIds == null) {
                    loadStoriesInner();
                    return;
                }
                checkAppearanceManager();
                setOrRefreshAdapter(storiesIds);
                if (callback != null)
                    callback.storiesLoaded(storiesIds.size(), StringsUtils.getNonNull(getFeed()));
            }

            @Override
            public void error() throws Exception {
                loadStoriesInner();
            }
        });
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
        final boolean hasFavorite = (appearanceManager != null && !isFavoriteList && appearanceManager.csHasFavorite());
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull final InAppStoryService service) throws Exception {
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
                service.getDownloadManager().loadStories(getFeed(), lcallback, isFavoriteList, hasFavorite);
            }

            @Override
            public void error() throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
                            @Override
                            public void use(@NonNull InAppStoryService service) throws Exception {
                                boolean hasFav = (appearanceManager != null && !isFavoriteList &&
                                        appearanceManager.csHasFavorite());
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
                                service.getDownloadManager().loadStories(getFeed(),
                                        lcallback, isFavoriteList, hasFav);
                            }
                        });
                    }
                }, 1000);
            }
        });


    }

}
