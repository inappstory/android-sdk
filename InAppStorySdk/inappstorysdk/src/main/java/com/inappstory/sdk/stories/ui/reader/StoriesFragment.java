package com.inappstory.sdk.stories.ui.reader;

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

import java.util.ArrayList;
import java.util.List;

import com.inappstory.sdk.R;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.eventbus.CsThreadMode;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.cache.StoryDownloader;
import com.inappstory.sdk.stories.events.AppKillEvent;
import com.inappstory.sdk.stories.events.ChangeIndexEvent;
import com.inappstory.sdk.stories.events.ChangeStoryEvent;
import com.inappstory.sdk.stories.events.ChangeUserIdEvent;
import com.inappstory.sdk.stories.events.CloseStoryReaderEvent;
import com.inappstory.sdk.stories.events.NextStoryPageEvent;
import com.inappstory.sdk.stories.events.NextStoryReaderEvent;
import com.inappstory.sdk.stories.events.PageByIdSelectedEvent;
import com.inappstory.sdk.stories.events.PageByIndexRefreshEvent;
import com.inappstory.sdk.stories.events.PauseStoryReaderEvent;
import com.inappstory.sdk.stories.events.PrevStoryPageEvent;
import com.inappstory.sdk.stories.events.PrevStoryReaderEvent;
import com.inappstory.sdk.stories.events.ResumeStoryReaderEvent;
import com.inappstory.sdk.stories.events.ShareCompleteEvent;
import com.inappstory.sdk.stories.events.StoryOpenEvent;
import com.inappstory.sdk.stories.events.StoryPageOpenEvent;
import com.inappstory.sdk.stories.events.StoryReaderTapEvent;
import com.inappstory.sdk.stories.events.StoryTimerReverseEvent;
import com.inappstory.sdk.stories.events.StorySwipeBackEvent;
import com.inappstory.sdk.stories.managers.OldStatisticManager;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.serviceevents.ChangeIndexEventInFragment;
import com.inappstory.sdk.stories.serviceevents.PrevStoryFragmentEvent;
import com.inappstory.sdk.stories.storieslistenerevents.OnNextEvent;
import com.inappstory.sdk.stories.storieslistenerevents.OnPrevEvent;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.StoriesProgressView;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.StoriesReaderPager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.StoriesReaderPagerAdapter;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPagerAdapter;
import com.inappstory.sdk.stories.utils.BackPressHandler;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.stories.utils.StatusBarController;

import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_SWIPE;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_POSITION;
import static com.inappstory.sdk.AppearanceManager.CS_STORY_READER_ANIMATION;

public class StoriesFragment extends Fragment implements BackPressHandler, ViewPager.OnPageChangeListener {

    public StoriesFragment() {
        super();
    }

    boolean isDestroyed = false;

    boolean created = false;

    boolean backPaused = false;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (isDestroyed) return;
        if (InAppStoryService.getInstance() == null) return;
        if (positionOffset == 0f) {
            InAppStoryService.getInstance().cubeAnimation = false;
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
            InAppStoryService.getInstance().cubeAnimation = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                storiesViewPager.requestDisallowInterceptTouchEvent(true);
            }
        }
        InAppStoryService.getInstance().lastTapEventTime = System.currentTimeMillis() + 100;
    }

    List<Integer> currentIds = new ArrayList<>();


    StoriesReaderPager storiesViewPager;
    View invMask;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (InAppStoryService.getInstance() == null || InAppStoryManager.getInstance() == null) {
            if (getActivity() != null) getActivity().finish();
            return;
        }
        if (isDestroyed) return;
        CsEventBus.getDefault().register(this);
        configurationChanged = false;
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
            CsEventBus.getDefault().unregister(this);
            if (InAppStoryService.getInstance() != null) {
                OldStatisticManager.getInstance().currentEvent = null;
                if (!configurationChanged) {
                    // EventBus.getDefault().post(new DestroyStoriesFragmentEvent());
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
            CsEventBus.getDefault().post(new PauseStoryReaderEvent(true));
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
        if (!isDestroyed) {
            backPaused = false;
            if (!created)
                CsEventBus.getDefault().post(new ResumeStoryReaderEvent(true));
            if (!Sizes.isTablet())
                StatusBarController.hideStatusBar(getActivity(), true);
            created = false;
            InAppStoryManager.getInstance().setTempShareStoryId(0);
            InAppStoryManager.getInstance().setTempShareId(null);
            if (InAppStoryManager.getInstance().getOldTempShareId() != null) {
                CsEventBus.getDefault().post(new ShareCompleteEvent(
                        InAppStoryManager.getInstance().getOldTempShareStoryId(),
                        InAppStoryManager.getInstance().getOldTempShareId(), true));
            }
            InAppStoryManager.getInstance().setOldTempShareStoryId(0);
            InAppStoryManager.getInstance().setOldTempShareId(null);
        }
        super.onResume();
    }


    @CsSubscribe
    public void refreshPageEvent(PageByIndexRefreshEvent event) {
        if (isDestroyed) return;
        ArrayList<Integer> adds = new ArrayList<>();
        int position = currentIds.indexOf(event.getStoryId());
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
        StoryDownloader.getInstance().reloadPage(event.getStoryId(), event.getIndex(), adds);
    }

    int lastPos = -1;

    @Override
    public void onPageSelected(int pos0) {
        if (isDestroyed) return;
        final int position = pos0;
        if (lastPos < pos0 && lastPos > -1) {
            Story story = StoryDownloader.getInstance().getStoryById(currentIds.get(lastPos));
            Story story2 = StoryDownloader.getInstance().getStoryById(currentIds.get(pos0));
            StatisticManager.getInstance().sendCurrentState();
            StatisticManager.getInstance().sendCloseStory(story.id, StatisticManager.NEXT, story.lastIndex, story.slidesCount);
            StatisticManager.getInstance().sendViewStory(currentIds.get(pos0), StatisticManager.NEXT);
            StatisticManager.getInstance().sendOpenStory(currentIds.get(pos0), StatisticManager.NEXT);
            StatisticManager.getInstance().createCurrentState(story2.id, story2.lastIndex);
        } else if (lastPos > pos0 && lastPos > -1) {
            Story story = StoryDownloader.getInstance().getStoryById(currentIds.get(lastPos));
            Story story2 = StoryDownloader.getInstance().getStoryById(currentIds.get(pos0));
            StatisticManager.getInstance().sendCurrentState();
            StatisticManager.getInstance().sendCloseStory(story.id, StatisticManager.PREV, story.lastIndex, story.slidesCount);
            StatisticManager.getInstance().sendViewStory(currentIds.get(pos0), StatisticManager.PREV);
            StatisticManager.getInstance().sendOpenStory(currentIds.get(pos0), StatisticManager.PREV);
            StatisticManager.getInstance().createCurrentState(story2.id, story2.lastIndex);
        } else if (lastPos == -1) {
            Story story2 = StoryDownloader.getInstance().getStoryById(currentIds.get(pos0));
            StatisticManager.getInstance().sendCurrentState();
            String whence = StatisticManager.DIRECT;
            switch (getArguments().getInt("source", 0)) {
                case 1:
                    whence = StatisticManager.ONBOARDING;
                    break;
                case 2:
                    whence = StatisticManager.LIST;
                    break;
                case 3:
                    whence = StatisticManager.FAVORITE;
                    break;
                default:
                    break;
            }
            StatisticManager.getInstance().sendViewStory(currentIds.get(pos0), whence);
            StatisticManager.getInstance().sendOpenStory(currentIds.get(pos0), whence);
            StatisticManager.getInstance().createCurrentState(story2.id, story2.lastIndex);
        }
        lastPos = pos0;
        if (getArguments() != null) {
            getArguments().putInt("index", position);
        }

        Story story = StoryDownloader.getInstance().getStoryById(currentIds.get(position));
        if (story != null)
            CsEventBus.getDefault().post(new ShowStory(story.id, story.title, story.tags, story.slidesCount, getArguments().getInt("source")));
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                Log.e("PageTaskToLoadEvent", "addStoryTask " + currentIds.get(position));
                StoryDownloader.getInstance().addStoryTask(currentIds.get(position), adds);


                if (InAppStoryService.getInstance() == null) return;
                if (currentIds != null && currentIds.size() > position) {
                    OldStatisticManager.getInstance().addStatisticBlock(currentIds.get(position),
                            StoryDownloader.getInstance().findItemByStoryId(currentIds.get(position)).lastIndex);
                    CsEventBus.getDefault().post(new ChangeStoryEvent(currentIds.get(position), position));
                }
                final int pos = position;

                CsEventBus.getDefault().post(new PageByIdSelectedEvent(currentIds.get(pos), false));
                if (pos > 0) {

                    CsEventBus.getDefault().post(new PageByIdSelectedEvent(currentIds.get(pos - 1), true));
                }
                if (pos < currentIds.size() - 1) {
                    CsEventBus.getDefault().post(new PageByIdSelectedEvent(currentIds.get(pos + 1), true));
                }
            }
        }).start();

    }


    private int getCurIndexById(int id) {
        if (StoryDownloader.getInstance() == null) return 0;
        Story st = StoryDownloader.getInstance().findItemByStoryId(id);
        return st == null ? 0 : st.lastIndex;
    }

    int currentIndex = 0;


    @Override
    public void onPageScrollStateChanged(int state) {
        if (InAppStoryService.getInstance() == null) return;
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (getCurIndexById(InAppStoryService.getInstance().getCurrentId()) == currentIndex) {
                CsEventBus.getDefault().post(new ResumeStoryReaderEvent(false));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CsEventBus.getDefault().post(new StorySwipeBackEvent(InAppStoryService.getInstance().getCurrentId()));
                    }
                }, 50);
            }

        }
        currentIndex = getCurIndexById(InAppStoryService.getInstance().getCurrentId());

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeUserId(ChangeUserIdEvent event) {
        if (isDestroyed) return;
        CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.AUTO));
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void closeReaderEvent(CloseStoryReaderEvent event) {
        isDestroyed = true;
        CsEventBus.getDefault().unregister(this);
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void storyReaderTap(StoryReaderTapEvent event) {
        if (!isDestroyed) {

            Handler handler = new Handler(Looper.getMainLooper());
            invMask.setVisibility(View.VISIBLE);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    invMask.setVisibility(View.GONE);
                }
            }, 200);

            if (event.getLink() == null || event.getLink().isEmpty()) {
                int real = event.getCoordinate();
                int sz = (!Sizes.isTablet() ? Sizes.getScreenSize().x : Sizes.dpToPxExt(400));
                if (real >= 0.3 * sz && !event.isForbidden()) {
                    CsEventBus.getDefault().post(new NextStoryPageEvent(currentIds.get(storiesViewPager.getCurrentItem())));
                } else if (real < 0.3 * sz) {
                    CsEventBus.getDefault().post(new PrevStoryPageEvent(currentIds.get(storiesViewPager.getCurrentItem())));
                }
            } else {

            }
        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void nextStoryPageEvent(OnNextEvent event) {
        if (isDestroyed) return;
        if (InAppStoryService.getInstance() == null) return;
        Story st = StoryDownloader.getInstance().findItemByStoryId(InAppStoryService.getInstance().getCurrentId());
        if (st.durations != null && !st.durations.isEmpty()) st.slidesCount = st.durations.size();
        if (currentIndex < st.slidesCount - 1) {
            currentIndex++;
        }

        CsEventBus.getDefault().post(new StoryPageOpenEvent(
                InAppStoryService.getInstance().getCurrentId(),
                currentIndex
        ) {{
            isNext = true;
        }});

        if (st.lastIndex < st.slidesCount) {
            st.lastIndex++;
        }
    }


    /**
     * Открываем предыдущий слайд нарратива
     *
     * @param event
     */
    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void prevStoryPageEvent(StoryTimerReverseEvent event) {
        if (isDestroyed) return;
        CsEventBus.getDefault().post(new OnPrevEvent());
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void setCurrentIndexEvent(ChangeIndexEvent event) {
        if (!isDestroyed) {
            int curItem = storiesViewPager.getCurrentItem();
            StoryDownloader.getInstance().findItemByStoryId(currentIds.get(curItem)).lastIndex = event.getIndex();
            CsEventBus.getDefault().post(new ChangeIndexEventInFragment(event.getIndex(), currentIds.get(curItem)));
        }
    }

    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void onNext(OnNextEvent event) {

    }*/

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeIndexEvent(ChangeIndexEventInFragment event) {
        if (isDestroyed) return;
        currentIndex = event.getIndex();

    }


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void onPrev(OnPrevEvent event) {
        if (isDestroyed) return;
        if (InAppStoryService.getInstance() == null) return;

        Handler handler = new Handler(Looper.getMainLooper());
        if (StoryDownloader.getInstance().findItemByStoryId(InAppStoryService.getInstance().getCurrentId()).lastIndex > 0) {
            StoryDownloader.getInstance().findItemByStoryId(InAppStoryService.getInstance().getCurrentId()).lastIndex--;
        }
        if (currentIndex > 0) {
            currentIndex--;
            CsEventBus.getDefault().post(new StoryPageOpenEvent(
                    InAppStoryService.getInstance().getCurrentId(),
                    currentIndex
            ) {{
                isPrev = true;
            }});
        }

    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeStoryEvent(ChangeStoryEvent event) {
        if (isDestroyed) return;
        if (InAppStoryService.getInstance() == null) return;
        InAppStoryService.getInstance().setCurrentId(currentIds.get(event.getIndex()));
        currentIndex = StoryDownloader.getInstance().findItemByStoryId(currentIds.get(event.getIndex())).lastIndex;
        invMask.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                invMask.setVisibility(View.GONE);
            }
        }, 600);

        CsEventBus.getDefault().post(new StoryOpenEvent(currentIds.get(event.getIndex())));
        ArrayList<Integer> lst = new ArrayList<>();
        lst.add(currentIds.get(event.getIndex()));
        OldStatisticManager.getInstance().previewStatisticEvent(lst);
        getArguments().putInt("index", event.getIndex());
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void appKillEvent(AppKillEvent event) {
        Log.e("app_kill", "appKill");
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void onNextStory(NextStoryReaderEvent event) {
        if (isDestroyed) return;
        if (storiesViewPager.getCurrentItem() < storiesViewPager.getAdapter().getCount() - 1) {

            Story story = StoryDownloader.getInstance().getStoryById(currentIds.get(storiesViewPager.getCurrentItem()));
        /*    StatisticManager.getInstance().sendCloseStory(story.id, StatisticManager.PREV, story.lastIndex, story.slidesCount);
            StatisticManager.getInstance().sendViewStory(
                    currentIds.get(storiesViewPager.getCurrentItem() + 1), StatisticManager.NEXT);
            StatisticManager.getInstance().sendOpenStory(
                    currentIds.get(storiesViewPager.getCurrentItem() + 1), StatisticManager.NEXT);
*/
            CsEventBus.getDefault().post(new ChangeStoryEvent(currentIds.get(storiesViewPager.getCurrentItem() + 1),
                    storiesViewPager.getCurrentItem() + 1));
            storiesViewPager.setCurrentItem(storiesViewPager.getCurrentItem() + 1);
        } else {
            if (!StoryDownloader.getInstance().findItemByStoryId(currentIds.get(storiesViewPager.getCurrentItem())).disableClose)
                CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.AUTO));
        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void onPrevStory(PrevStoryReaderEvent event) {
        if (isDestroyed) return;
        if (InAppStoryService.getInstance() == null) return;
        if (storiesViewPager.getCurrentItem() > 0) {

            StatisticManager.getInstance().sendCurrentState();
            Story story = StoryDownloader.getInstance().getStoryById(currentIds.get(storiesViewPager.getCurrentItem()));
          /*  StatisticManager.getInstance().sendCloseStory(story.id, StatisticManager.PREV, story.lastIndex, story.slidesCount);
            StatisticManager.getInstance().sendViewStory(
                    currentIds.get(storiesViewPager.getCurrentItem() - 1), StatisticManager.PREV);
            StatisticManager.getInstance().sendOpenStory(
                    currentIds.get(storiesViewPager.getCurrentItem() - 1), StatisticManager.PREV);
*/
            CsEventBus.getDefault().post(new ChangeStoryEvent(currentIds.get(storiesViewPager.getCurrentItem() - 1),
                    storiesViewPager.getCurrentItem() - 1));
            storiesViewPager.setCurrentItem(storiesViewPager.getCurrentItem() - 1);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    InAppStoryService.getInstance().cubeAnimation = false;
                }
            }, 100);
            CsEventBus.getDefault().post(new PrevStoryFragmentEvent(InAppStoryService.getInstance().getCurrentId()));
        }
    }

}
