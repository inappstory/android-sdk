package com.inappstory.sdk.refactoring.stories.ui.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.refactoring.stories.ui.list.viewmodels.BaseStoriesListViewModel;
import com.inappstory.sdk.refactoring.stories.ui.list.viewmodels.StoriesFeedListViewModel;
import com.inappstory.sdk.refactoring.stories.ui.list.viewmodels.StoriesListViewModelCreator;
import com.inappstory.sdk.refactoring.stories.usecases.StoriesFeedParameters;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListCallback;
import com.inappstory.sdk.stories.outercallbacks.storieslist.ListScrollCallback;
import com.inappstory.sdk.stories.ui.list.ShownStoriesListItem;
import com.inappstory.sdk.stories.ui.list.StoryTouchListener;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoriesList extends RecyclerView implements Observer<Boolean> {
    private AppearanceManager appearanceManager;
    StoryTouchListener storyTouchListener = null;
    OnItemTouchListener itemTouchListener;
    private final String DEFAULT_FEED = "default";
    private boolean isFavoriteList = false;
    ListCallback callback;
    ListScrollCallback scrollCallback;
    private boolean readerIsOpened = false;
    HashMap<Integer, ShownStoriesListItem> scrolledItems = new HashMap<>();


    private float mPrevX = 0f;
    private float mPrevY = 0f;

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

    private LayoutManager layoutManager = defaultLayoutManager;

    private String uniqueID;
    private String feed = DEFAULT_FEED;

    private String getUniqueID() {
        if (uniqueID == null || uniqueID.isEmpty()) return feed;
        return uniqueID;
    }

    private final Object lock = new Object();


    public void setAppearanceManager(AppearanceManager appearanceManager) {
        this.appearanceManager = appearanceManager;
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

    public StoriesList(@NonNull Context context, boolean isFavoriteList) {
        super(context);
        init(context, null);
        this.isFavoriteList = isFavoriteList;
    }

    public StoriesList(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public StoriesList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StoriesList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void parseAttrs(@NonNull AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.StoriesList);
        isFavoriteList = typedArray.getBoolean(R.styleable.StoriesList_cs_listIsFavorite, false);
        synchronized (lock) {
            if (!isFavoriteList) {
                feed = typedArray.getString(R.styleable.StoriesList_cs_feed);
                if (feed == null || feed.isEmpty())
                    feed = DEFAULT_FEED;
            } else {
                feed = null;
            }
        }
        typedArray.recycle();
    }

    public void setCallback(ListCallback callback) {
        this.callback = callback;
    }

    public void setScrollCallback(ListScrollCallback scrollCallback) {
        this.scrollCallback = scrollCallback;
    }


    public void setFeed(String feed) {
        if (feed != null && !feed.isEmpty())
            this.feed = feed;
        else
            this.feed = DEFAULT_FEED;
        updateAdapter();
    }

    private void updateAdapter() {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                AppearanceManager appearanceManager = getAppearanceManager();
                IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
                List<String> nonSortedTags = new ArrayList<>(settingsHolder.tags());
                Collections.sort(nonSortedTags);
                String key = (uniqueID != null && !uniqueID.isEmpty()) ? uniqueID :
                        StringsUtils.md5(feed + TextUtils.join(",", nonSortedTags));
                if (getAdapter() != null) {
                    ((StoriesListAdapter) getAdapter()).destroy();
                }
                setAdapter(
                        new StoriesListAdapter(
                                core,
                                appearanceManager,
                                core.widgetViewModels().storiesListViewModels().getOrCreateStoriesListViewModel(
                                        key,
                                        new StoriesListViewModelCreator() {
                                            @Override
                                            public BaseStoriesListViewModel create() {
                                                return new StoriesFeedListViewModel(core,
                                                        new StoriesFeedParameters(
                                                                feed,
                                                                nonSortedTags,
                                                                settingsHolder.options()));
                                            }
                                        }
                                ),
                                null,
                                null
                        )
                );
            }
        });
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
        updateAdapter();
    }


    public void loadStories() {
        Adapter adapter = getAdapter();
        if (adapter instanceof StoriesListAdapter)
            ((StoriesListAdapter) adapter).loadStories();
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        if (attrs != null)
            parseAttrs(attrs);
        itemTouchListener = new RecyclerTouchListener(
                context
        );
        addOnItemTouchListener(itemTouchListener);
        scrollToPosition(0);
        setLayoutManager(defaultLayoutManager);
        updateAdapter();

    }

    private void addListScrollCallback() {
    }

    public void setStoryTouchListener(StoryTouchListener storyTouchListener) {
        this.storyTouchListener = storyTouchListener;
        try {
            removeOnItemTouchListener(itemTouchListener);
        } catch (Exception e) {

        }
        itemTouchListener = new RecyclerTouchListener(
                storyTouchListener,
                getContext()
        );
        addOnItemTouchListener(itemTouchListener);
    }

    private void readerIsOpened() {
        readerIsOpened = true;
    }

    private void readerIsClosed() {
        readerIsOpened = false;
        sendIndexes();
        updateVisibleItems();
    }

    private void sendIndexes() {
        List<Integer> indexes = new ArrayList<>();
    }

    private void updateVisibleItems() {

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            mPrevX = e.getX();
            mPrevY = e.getY();
            updateVisibleItems();
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
    public void onUpdate(@NonNull Boolean storiesReaderIsOpened) {
        if (readerIsOpened != storiesReaderIsOpened) {
            if (storiesReaderIsOpened) readerIsOpened();
            else readerIsClosed();
        }
    }

    private class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private StoryTouchListener touchListener;
        View lastChild = null;

        public RecyclerTouchListener(Context context) {
            this(null, context);
        }

        public RecyclerTouchListener(StoryTouchListener touchListener, Context context) {
            this.touchListener = touchListener;
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
}
