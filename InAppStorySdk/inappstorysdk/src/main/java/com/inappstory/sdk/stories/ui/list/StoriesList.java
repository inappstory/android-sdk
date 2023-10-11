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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.callbacks.OnFavoriteItemClick;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListCallback;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListScrollCallback;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.uidomain.list.IStoriesListPresenter;
import com.inappstory.sdk.stories.uidomain.list.StoriesAdapterStoryData;
import com.inappstory.sdk.stories.uidomain.list.StoriesListPresenter;
import com.inappstory.sdk.stories.uidomain.list.items.story.IStoriesListCommonItemClick;
import com.inappstory.sdk.stories.uidomain.list.items.story.IStoriesListDeeplinkItemClick;
import com.inappstory.sdk.stories.uidomain.list.items.story.IStoriesListGameItemClick;
import com.inappstory.sdk.stories.uidomain.list.listnotify.AllStoriesListsNotify;
import com.inappstory.sdk.stories.uidomain.list.listnotify.StoriesListNotify;
import com.inappstory.sdk.stories.uidomain.list.utils.GetStoriesListIds;
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

    IStoriesListPresenter presenter;

    private IStoriesListPresenter getLazyPresenter() {
        synchronized (this) {
            if (presenter == null) {
                if (listNotify == null) throw new RuntimeException("initialization error");
                presenter = new StoriesListPresenter(
                        listNotify,
                        allListsNotify,
                        feed,
                        isFavoriteList ? SourceType.FAVORITE : SourceType.LIST,
                        Story.StoryType.COMMON,
                        uniqueID
                );
                presenter.updateAppearanceManager(appearanceManager);
            }
            return presenter;
        }
    }

    public void notifyByStoryId(int storyId) {

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
                    getLazyPresenter().clearCachedList();
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

    public String getUniqueID() {
        return uniqueID;
    }

    private String uniqueID;

    public void setCallback(ListCallback callback) {
        this.callback = callback;
        presenter.setListCallback(callback);
    }

    ListCallback callback;
    ListScrollCallback scrollCallback;

    public void setScrollCallback(ListScrollCallback scrollCallback) {
        this.scrollCallback = scrollCallback;
    }

    StoriesListNotify listNotify;
    AllStoriesListsNotify allListsNotify;
    boolean isFavoriteList = false;

    public StoriesList(@NonNull Context context, boolean isFavoriteList) {
        super(context);
        init(null);
        this.isFavoriteList = isFavoriteList;
    }

    public StoriesList(@NonNull Context context, String feed) {
        super(context);
        init(null);
        this.feed = feed;
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

    public StoriesList(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        listNotify.unsubscribe();
        allListsNotify.unsubscribe();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        checkAppearanceManager();
        listNotify.bindListAdapter(
                (IStoriesListAdapter) this.getAdapter()
        );
        allListsNotify.bindListAdapter(
                (IStoriesListAdapter) this.getAdapter(), appearanceManager.csCoverQuality()
        );
        listNotify.subscribe();
        allListsNotify.subscribe();
    }

    private void init(AttributeSet attributeSet) {
        uniqueID = randomUUID().toString();
        listNotify = new StoriesListNotify(
                uniqueID,
                Story.StoryType.COMMON
        );
        allListsNotify = new AllStoriesListsNotify(Story.StoryType.COMMON);
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
                    sendPreviewIdsToStatistic();
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
    }

    OnItemTouchListener itemTouchListener;

    HashMap<Integer, ShownStoriesListItem> scrolledItems = new HashMap<>();

    void sendPreviewIdsToStatistic() {
        int hasUgcShift = hasUgc() ? 1 : 0;
        ArrayList<Integer> ids = new ArrayList<>();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            for (int i = linearLayoutManager.findFirstVisibleItemPosition();
                 i <= linearLayoutManager.findLastVisibleItemPosition(); i++) {
                int ind = i - hasUgcShift;
                if (adapter != null && adapter.getStoriesData().size() > ind && ind >= 0) {
                    ids.add(adapter.getStoriesData().get(ind).getId());
                }
            }
        }
        getLazyPresenter().sendPreviewsToStatistic(ids, feed, isFavoriteList);
    }

    boolean hasUgc() {
        checkAppearanceManager();
        return (getLazyPresenter().hasUgcEditor() && !isFavoriteList && appearanceManager.csHasUGC());
    }

    private void getVisibleItems() {
        checkAppearanceManager();
        int hasUgcShift = hasUgc() ? 1 : 0;
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            for (int i = linearLayoutManager.findFirstVisibleItemPosition();
                 i <= linearLayoutManager.findLastVisibleItemPosition(); i++) {
                int ind = i - hasUgcShift;
                if (adapter != null && adapter.getStoriesData().size() > ind && ind >= 0) {
                    View holder = linearLayoutManager.getChildAt(
                            Math.max(0, i - linearLayoutManager.findFirstVisibleItemPosition())
                    );
                    if (holder != null) {
                        float currentPercentage = getItemVisibleArea(holder);
                        ShownStoriesListItem cachedData = scrolledItems.get(i);
                        if (cachedData != null) {
                            cachedData.areaPercent = Math.max(
                                    currentPercentage,
                                    cachedData.areaPercent
                            );
                        } else {
                            ShownStoriesListItem current =
                                    getLazyPresenter().getShownStoriesListItemByStoryId(
                                            adapter.getStoriesData().get(ind).getId(),
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

    private float getItemVisibleArea(View holder) {
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
        return (float) (rectHeight * rectWidth) /
                (holder.getWidth() * holder.getHeight());
    }

    BaseStoriesListAdapter adapter;

    @Override
    public void setLayoutManager(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        super.setLayoutManager(layoutManager);
    }

    private LayoutManager defaultLayoutManager =
            new LinearLayoutManager(getContext(), HORIZONTAL, false) {
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
        checkAppearanceManager();
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

    IStoriesListCommonItemClick commonItemClick = new IStoriesListCommonItemClick() {

        @Override
        public void onClick(List<StoriesAdapterStoryData> storiesData, int index) {
            presenter.commonItemClick(storiesData, index, getContext());
        }
    };

    IStoriesListGameItemClick gameItemClick = new IStoriesListGameItemClick() {


        @Override
        public void onClick(StoriesAdapterStoryData storiesData, int index) {
            presenter.gameItemClick(storiesData, index, getContext());
        }
    };

    IStoriesListDeeplinkItemClick deeplinkItemClick = new IStoriesListDeeplinkItemClick() {
        @Override
        public void onClick(StoriesAdapterStoryData storiesData, int index) {
            presenter.deeplinkItemClick(storiesData, index, getContext());
        }
    };

    public void setOnUGCItemClick(OnUGCItemClick ugcItemClick) {
        this.ugcItemClick = ugcItemClick;
    }


    boolean readerIsOpened = false;

    public void openReader() {
        readerIsOpened = true;
    }

    public void closeReader() {
        readerIsOpened = false;
        sendPreviewIdsToStatistic();
        getVisibleItems();
    }


    public void refreshList() {
        adapter = null;
        loadStories();
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
        if (adapter == null || adapter.getStoriesData() == null) return;
        for (int i = 0; i < adapter.getStoriesData().size(); i++) {
            if (adapter.getStoriesData().get(i).getId() == storyId) {
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
            getLazyPresenter().onWindowFocusChanged();
        }
    }

    public void clearAllFavorites() {
        if (adapter == null) return;
        if (isFavoriteList) {
            adapter.hasFavItem = false;
            adapter.getStoriesData().clear();
        } else {
            adapter.hasFavItem = false;
        }
        if (isFavoriteList)
            adapter.notify(null);
    }

   /* public void favStory(
            StoriesAdapterStoryData data,
            boolean favStatus,
            List<FavoriteImage> favImages,
            boolean isEmpty
    ) {
        if (adapter == null) return;

        checkAppearanceManager();
        if (isFavoriteList) {
            adapter.hasFavItem = false;
            if (favStatus) {
                if (!adapter.getStoriesData().contains(id))
                    adapter.getStoriesData().add(0, id);
            } else {
                if (adapter.getStoriesData().contains(id))
                    adapter.getStoriesData().remove(new Integer(id));
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
    }*/


    LoadStoriesCallback lcallback;

    private final GetStoriesListIds getStoriesListIds = new GetStoriesListIds() {
        @Override
        public void onSuccess(final List<Integer> storiesIds) {
            post(new Runnable() {
                @Override
                public void run() {
                    setOrRefreshAdapter(storiesIds);
                    if (callback != null)
                        callback.storiesLoaded(storiesIds.size(), StringsUtils.getNonNull(getFeed()));
                }
            });
        }

        @Override
        public void onError() {
            if (callback != null)
                callback.loadError(StringsUtils.getNonNull(getFeed()));
        }
    };

    public void loadStories() {

        checkAppearanceManager();

        if (isFavoriteList) {
            getLazyPresenter().loadFavoriteList(getStoriesListIds);
        } else {
            getLazyPresenter().loadFeed(getFeed(), appearanceManager.csHasFavorite(), getStoriesListIds);
        }
    }


    public void setCacheId(String id) {
        getLazyPresenter().setCacheId(id);
    }


    private void checkAppearanceManager() {
        if (this.appearanceManager == null) {
            this.appearanceManager = AppearanceManager.getCommonInstance();
        }

        if (this.appearanceManager == null) {
            this.appearanceManager = new AppearanceManager();
        }

    }

    private void setOrRefreshAdapter(List<StoriesAdapterStoryData> storiesData) {
        setOverScrollMode(getAppearanceManager().csListOverscroll() ?
                OVER_SCROLL_ALWAYS : OVER_SCROLL_NEVER);
        adapter = new BaseStoriesListAdapter(
                getContext(),
                uniqueID,
                storiesData,
                appearanceManager,
                isFavoriteList,
                appearanceManager.csHasFavorite() && !isFavoriteList,
                hasUgc(),
                commonItemClick,
                deeplinkItemClick,
                gameItemClick,
                !isFavoriteList ? favoriteItemClick : null,
                !isFavoriteList ? ugcItemClick : null
        );
        if (callback != null)
            callback.storiesUpdated(storiesData.size(), feed);
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
        } else {
            setLayoutManager(layoutManager);
        }
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
}
