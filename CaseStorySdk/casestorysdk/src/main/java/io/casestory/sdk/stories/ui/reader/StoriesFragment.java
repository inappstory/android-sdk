package io.casestory.sdk.stories.ui.reader;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.casestory.casestorysdk.R;
import io.casestory.sdk.CaseStoryManager;
import io.casestory.sdk.CaseStoryService;
import io.casestory.sdk.eventbus.EventBus;
import io.casestory.sdk.eventbus.Subscribe;
import io.casestory.sdk.eventbus.ThreadMode;
import io.casestory.sdk.stories.api.models.Story;
import io.casestory.sdk.stories.cache.StoryDownloader;
import io.casestory.sdk.stories.events.ChangeIndexEvent;
import io.casestory.sdk.stories.events.ChangeStoryEvent;
import io.casestory.sdk.stories.events.ChangeUserIdEvent;
import io.casestory.sdk.stories.events.CloseStoryReaderEvent;
import io.casestory.sdk.stories.events.NextStoryPageEvent;
import io.casestory.sdk.stories.events.NextStoryReaderEvent;
import io.casestory.sdk.stories.events.PageByIdSelectedEvent;
import io.casestory.sdk.stories.events.PageSelectedEvent;
import io.casestory.sdk.stories.events.PauseStoryReaderEvent;
import io.casestory.sdk.stories.events.PrevStoryPageEvent;
import io.casestory.sdk.stories.events.PrevStoryReaderEvent;
import io.casestory.sdk.stories.events.ResumeStoryReaderEvent;
import io.casestory.sdk.stories.events.StoriesNextPageEvent;
import io.casestory.sdk.stories.events.StoryOpenEvent;
import io.casestory.sdk.stories.events.StoryPageOpenEvent;
import io.casestory.sdk.stories.events.StoryReaderTapEvent;
import io.casestory.sdk.stories.events.StoryTimerReverseEvent;
import io.casestory.sdk.stories.events.StorySwipeBackEvent;
import io.casestory.sdk.stories.events.StoryTimerSkipEvent;
import io.casestory.sdk.stories.serviceevents.ChangeIndexEventInFragment;
import io.casestory.sdk.stories.serviceevents.DestroyStoriesFragmentEvent;
import io.casestory.sdk.stories.serviceevents.PrevStoryFragmentEvent;
import io.casestory.sdk.stories.storieslistenerevents.OnNextEvent;
import io.casestory.sdk.stories.storieslistenerevents.OnPrevEvent;
import io.casestory.sdk.stories.ui.widgets.readerscreen.StoriesProgressView;
import io.casestory.sdk.stories.ui.widgets.readerscreen.StoriesReaderPager;
import io.casestory.sdk.stories.ui.widgets.readerscreen.StoriesReaderPagerAdapter;
import io.casestory.sdk.stories.utils.BackPressHandler;
import io.casestory.sdk.stories.utils.Sizes;
import io.casestory.sdk.stories.utils.StatusBarController;

import static io.casestory.sdk.AppearanceManager.CS_CLOSE_ON_SWIPE;
import static io.casestory.sdk.AppearanceManager.CS_CLOSE_POSITION;
import static io.casestory.sdk.AppearanceManager.CS_STORY_READER_ANIMATION;

public class StoriesFragment extends Fragment implements BackPressHandler, ViewPager.OnPageChangeListener, StoriesProgressView.StoriesListener {

    public StoriesFragment() {
        super();
    }

    boolean isDestroyed = false;

    boolean created = false;

    boolean backPaused = false;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (isDestroyed) return;
        if (positionOffset == 0f) {
            CaseStoryService.getInstance().cubeAnimation = false;
            storiesViewPager.requestDisallowInterceptTouchEvent(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    invMask.setVisibility(View.GONE);
                }
            }, 100);
        } else {
            if (invMask.getVisibility() != View.VISIBLE)
                invMask.setVisibility(View.VISIBLE);
            CaseStoryService.getInstance().cubeAnimation = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                storiesViewPager.requestDisallowInterceptTouchEvent(true);
            }
        }
        CaseStoryService.getInstance().lastTapEventTime = System.currentTimeMillis() + 100;
    }

    List<Integer> currentIds = new ArrayList<>();


    StoriesReaderPager storiesViewPager;
    View invMask;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        if (CaseStoryService.getInstance() == null || CaseStoryManager.getInstance() == null) {
            if (getActivity() != null) getActivity().finish();
            return;
        }
        configurationChanged = false;
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (isDestroyed) return;
        storiesViewPager = view.findViewById(R.id.stories);
        storiesViewPager.canUseNotLoaded = getArguments().getBoolean("canUseNotLoaded", false);
        invMask = view.findViewById(R.id.invMask);
        storiesViewPager.setParameters(
                getArguments().getInt(CS_STORY_READER_ANIMATION, 0),
                getArguments().getBoolean(CS_CLOSE_ON_SWIPE, false));
        int closePosition = getArguments().getInt(CS_CLOSE_POSITION, 1);
        currentIds = getArguments().getIntegerArrayList("stories_ids");
        if (currentIds == null) {
            if (getActivity() != null) getActivity().finish();
            return;
        }
        StoriesReaderPagerAdapter outerViewPagerAdapter =
                new StoriesReaderPagerAdapter(
                        getChildFragmentManager(),
                        closePosition,
                        getArguments().getBoolean(CS_CLOSE_ON_SWIPE, false), currentIds);
        storiesViewPager.setAdapter(outerViewPagerAdapter);
        storiesViewPager.addOnPageChangeListener(this);
        int ind = getArguments().getInt("index", 0);
        if (ind > 0) {
            storiesViewPager.setCurrentItem(ind);
        } else {
            try {
                onPageSelected(0);
            } catch (Exception e) {

            }
        }
        storiesViewPager.getAdapter().notifyDataSetChanged();

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (!isDestroyed) {
            Log.d("AndroidEvent", "onConfigurationChanged");
            configurationChanged = true;
        }
        super.onConfigurationChanged(newConfig);
    }

    boolean configurationChanged = false;

    @Override
    public void onDestroyView() {
        if (!isDestroyed) {
            EventBus.getDefault().unregister(this);
            if (CaseStoryService.getInstance() != null) {
                CaseStoryService.getInstance().currentEvent = null;
                if (!configurationChanged) {
                    EventBus.getDefault().post(new DestroyStoriesFragmentEvent());
                }
                configurationChanged = false;
            }
        }
        getArguments().putBoolean("isDestroyed", true);
        super.onDestroyView();
    }


    @Override
    public void onPause() {
        if (!isDestroyed) {
            backPaused = true;
            EventBus.getDefault().post(new PauseStoryReaderEvent(true));
        }
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isDestroyed = getArguments().getBoolean("isDestroyed");
        created = true;
        return inflater.inflate(R.layout.cs_fragment_stories, container, false);
    }


    @Override
    public void onResume() {
        backPaused = false;
        if (!isDestroyed) {
            if (!created)
                Log.e("resumeTimer", "SFOnResume");
                EventBus.getDefault().post(new ResumeStoryReaderEvent(true));
            if (!Sizes.isTablet())
                StatusBarController.hideStatusBar(getActivity(), true);
            created = false;
        }
        super.onResume();
    }


    @Override
    public void onPageSelected(int position) {
        if (isDestroyed) return;
        ArrayList<Integer> adds = new ArrayList<>();
        if (currentIds.size() > 1) {
            if (position == 0) {
                adds.add(currentIds.get(position + 1));
            } else if (position == currentIds.size() - 1) {
                adds.add(currentIds.get(position - 1));
            } else {
                adds.add(currentIds.get(position + 1));
                adds.add(currentIds.get(position - 1));
            }
        }
        StoryDownloader.getInstance().addStoryTask(currentIds.get(position), adds);
        if (getArguments() != null) {
            getArguments().putInt("index", position);
        }

        if (currentIds != null && currentIds.size() > position) {
            CaseStoryService.getInstance().addStatisticBlock(currentIds.get(position),
                    StoryDownloader.getInstance().findItemByStoryId(currentIds.get(position)).lastIndex);
            EventBus.getDefault().post(new ChangeStoryEvent(currentIds.get(position), position));
        }
        final int pos = position;

        EventBus.getDefault().post(new PageSelectedEvent(pos));
        EventBus.getDefault().post(new PageByIdSelectedEvent(currentIds.get(pos), false));
        if (pos > 0) {
            EventBus.getDefault().post(new PageByIdSelectedEvent(currentIds.get(pos - 1), true));
        }
        if (pos < currentIds.size() - 1) {
            EventBus.getDefault().post(new PageByIdSelectedEvent(currentIds.get(pos + 1), true));
        }
    }


    private int getCurIndexById(int id) {
        return StoryDownloader.getInstance().findItemByStoryId(id).lastIndex;
    }

    int currentIndex = 0;


    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (getCurIndexById(CaseStoryService.getInstance().getCurrentId()) == currentIndex) {
                Log.e("resumeTimer", "pageScroll");
                EventBus.getDefault().post(new ResumeStoryReaderEvent(false));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new StorySwipeBackEvent(CaseStoryService.getInstance().getCurrentId()));
                    }
                }, 50);
            }

        }
        currentIndex = getCurIndexById(CaseStoryService.getInstance().getCurrentId());

        Log.e("currentIndex", "OnPrevEvent " + currentIndex);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeUserId(ChangeUserIdEvent event) {
        EventBus.getDefault().post(new CloseStoryReaderEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void narrativePageTapEvent(StoryReaderTapEvent event) {
        if (!isDestroyed) {
            if (event.getLink() == null || event.getLink().isEmpty()) {
                int real = event.getCoordinate();
                int sz = (!Sizes.isTablet() ? Sizes.getScreenSize().x : Sizes.dpToPxExt(400));
                if (real >= 0.3 * sz && !event.isForbidden()) {
                    EventBus.getDefault().post(new NextStoryPageEvent(currentIds.get(storiesViewPager.getCurrentItem())));
                } else if (real < 0.3 * sz) {
                    EventBus.getDefault().post(new PrevStoryPageEvent(currentIds.get(storiesViewPager.getCurrentItem())));
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void nextStoryPageEvent(OnNextEvent event) {
        if (isDestroyed) return;
        if (currentIndex < StoryDownloader.getInstance().findItemByStoryId(CaseStoryService.getInstance().getCurrentId()).pages.size() - 1) {
            currentIndex++;
        }

        EventBus.getDefault().post(new StoryPageOpenEvent(
                CaseStoryService.getInstance().getCurrentId(),
                currentIndex
        ));

        Story st = StoryDownloader.getInstance().findItemByStoryId(CaseStoryService.getInstance().getCurrentId());
        if (st.lastIndex < st.slidesCount) {
            st.lastIndex++;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        invMask.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                invMask.setVisibility(View.GONE);
            }
        }, 600);
    }


    /**
     * Открываем предыдущий слайд нарратива
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void prevStoryPageEvent(StoryTimerReverseEvent event) {
        EventBus.getDefault().post(new OnPrevEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setCurrentIndexEvent(ChangeIndexEvent event) {
        if (!isDestroyed) {
            int curItem = storiesViewPager.getCurrentItem();
            StoryDownloader.getInstance().findItemByStoryId(currentIds.get(curItem)).lastIndex = event.getIndex();
            EventBus.getDefault().post(new ChangeIndexEventInFragment(event.getIndex(), currentIds.get(curItem)));
        }
    }

    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void onNext(OnNextEvent event) {

    }*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeIndexEvent(ChangeIndexEventInFragment event) {
        Log.e("loadStory", "SFchangeIndexEvent " + event.getIndex());
        currentIndex = event.getIndex();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPrev(OnPrevEvent event) {
        if (isDestroyed) return;

        Handler handler = new Handler(Looper.getMainLooper());
        invMask.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                invMask.setVisibility(View.GONE);
            }
        }, 600);
        if (StoryDownloader.getInstance().findItemByStoryId(CaseStoryService.getInstance().getCurrentId()).lastIndex > 0) {
            StoryDownloader.getInstance().findItemByStoryId(CaseStoryService.getInstance().getCurrentId()).lastIndex--;
        }
        if (currentIndex > 0) {
            currentIndex--;
            EventBus.getDefault().post(new StoryPageOpenEvent(
                    CaseStoryService.getInstance().getCurrentId(),
                    currentIndex
            ));
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeStoryEvent(ChangeStoryEvent event) {
        CaseStoryService.getInstance().setCurrentId(currentIds.get(event.getIndex()));
        currentIndex = StoryDownloader.getInstance().findItemByStoryId(currentIds.get(event.getIndex())).lastIndex;
        Log.e("currentIndex", "ChangeStoryEvent " + currentIndex);
        if (isDestroyed) return;
        invMask.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                invMask.setVisibility(View.GONE);
            }
        }, 600);

        EventBus.getDefault().post(new StoryOpenEvent(currentIds.get(event.getIndex())));
        getArguments().putInt("index", event.getIndex());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNextStory(NextStoryReaderEvent event) {
        if (storiesViewPager.getCurrentItem() < storiesViewPager.getAdapter().getCount() - 1) {
            EventBus.getDefault().post(new ChangeStoryEvent(currentIds.get(storiesViewPager.getCurrentItem() + 1),
                    storiesViewPager.getCurrentItem() + 1));
            storiesViewPager.setCurrentItem(storiesViewPager.getCurrentItem() + 1);
        } else {
            if (!StoryDownloader.getInstance().findItemByStoryId(currentIds.get(storiesViewPager.getCurrentItem())).disableClose)
                EventBus.getDefault().post(new CloseStoryReaderEvent(false));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPrevStory(PrevStoryReaderEvent event) {
        if (storiesViewPager.getCurrentItem() > 0) {
            EventBus.getDefault().post(new ChangeStoryEvent(currentIds.get(storiesViewPager.getCurrentItem() - 1),
                    storiesViewPager.getCurrentItem() - 1));
            storiesViewPager.setCurrentItem(storiesViewPager.getCurrentItem() - 1);
        } else {
            CaseStoryService.getInstance().cubeAnimation = false;
            EventBus.getDefault().post(new PrevStoryFragmentEvent(CaseStoryService.getInstance().getCurrentId()));
        }
    }


    @Override
    public boolean webViewLoaded(int index) {
        return false;
    }
}
