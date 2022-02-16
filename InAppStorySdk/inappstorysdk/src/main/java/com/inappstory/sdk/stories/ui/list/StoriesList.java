package com.inappstory.sdk.stories.ui.list;

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

public class StoriesList extends RecyclerView {
    public StoriesList(@NonNull Context context) {
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


    public StoriesList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        manager = new StoriesListManager();
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
        itemTouchListener = new RecyclerTouchListener(
                getContext());
        addOnItemTouchListener(itemTouchListener);

        //getRecycledViewPool().setMaxRecycledViews(6, 0);
    }

    OnItemTouchListener itemTouchListener;

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


    LoadStoriesCallback lcallback;

    public void loadStories() throws DataException {
        InAppStoryManager.debugSDKCalls("StoriesList_loadStories", "");
        loadStoriesLocal();
    }

    private String cacheId;

    private void setCacheId(String id) {
        this.cacheId = id;
    }

    private void clearLocal() {
        if (cacheId != null && !cacheId.isEmpty() && InAppStoryService.isNotNull()) {
            InAppStoryService.getInstance().listStoriesIds.remove(cacheId);
        }
    }

    private void loadStoriesLocal() throws DataException {
        if (InAppStoryService.isNull()
                || cacheId == null
                || cacheId.isEmpty()) {
            loadStoriesInner();
            return;
        }
        List<Integer> storiesIds = InAppStoryService.getInstance()
                .listStoriesIds.get(cacheId);
        if (storiesIds == null) {
            loadStoriesInner();
            return;
        }
        if (adapter == null) {
            adapter = new StoriesAdapter(getContext(), uniqueID, storiesIds, appearanceManager, favoriteItemClick, isFavoriteList, callback);
            setLayoutManager(layoutManager);
            setAdapter(adapter);
        } else {
            adapter.refresh(storiesIds);
            adapter.notifyDataSetChanged();
        }
    }

    public void loadStoriesInner() throws DataException {

        if (InAppStoryManager.getInstance() == null) {
            throw new DataException("'InAppStoryManager' can't be null", new Throwable("InAppStoryManager data is not valid"));
        }
        if (InAppStoryManager.getInstance().getUserId() == null) {
            throw new DataException("'userId' can't be null", new Throwable("InAppStoryManager data is not valid"));
        }
        if (appearanceManager == null) {
            appearanceManager = AppearanceManager.getCommonInstance();
        }
        if (appearanceManager == null) {
            appearanceManager = new AppearanceManager();
        }
        InAppStoryManager.debugSDKCalls("StoriesList_loadStoriesInner", "");
        final String listUid = ProfilingManager.getInstance().addTask("widget_init");
        boolean hasFavorite = (appearanceManager != null && !isFavoriteList && appearanceManager.csHasFavorite());
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
                    if (adapter == null) {
                        adapter = new StoriesAdapter(getContext(), uniqueID, storiesIds, appearanceManager, favoriteItemClick, isFavoriteList, callback);
                        setLayoutManager(layoutManager);
                        setAdapter(adapter);
                    } else {
                        adapter.refresh(storiesIds);
                        adapter.notifyDataSetChanged();
                    }
                    ProfilingManager.getInstance().setReady(listUid);
                    CsEventBus.getDefault().post(new StoriesLoaded(storiesIds.size()));
                    if (callback != null) callback.storiesLoaded(storiesIds.size());
                }

                @Override
                public void onError() {
                    if (callback != null) callback.loadError();
                }
            };
            InAppStoryService.getInstance().getDownloadManager().loadStories(lcallback, isFavoriteList, hasFavorite);

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (InAppStoryService.isNotNull()) {
                        boolean hasFav = (appearanceManager != null && !isFavoriteList && appearanceManager.csHasFavorite());
                        lcallback = new LoadStoriesCallback() {
                            @Override
                            public void storiesLoaded(List<Integer> storiesIds) {
                                adapter = new StoriesAdapter(getContext(), uniqueID, storiesIds, appearanceManager, favoriteItemClick, isFavoriteList, callback);
                                setLayoutManager(layoutManager);
                                setAdapter(adapter);
                                ProfilingManager.getInstance().setReady(listUid);
                                CsEventBus.getDefault().post(new StoriesLoaded(storiesIds.size()));
                                if (callback != null) callback.storiesLoaded(storiesIds.size());
                            }

                            @Override
                            public void onError() {
                                if (callback != null) callback.loadError();
                            }
                        };
                        InAppStoryService.getInstance().getDownloadManager().loadStories(
                                lcallback, isFavoriteList, hasFav);
                    }
                }
            }, 1000);
        }

    }

}
