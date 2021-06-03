package com.inappstory.sdk.stories.ui.reader;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.eventbus.CsThreadMode;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryLinkObject;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
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
import com.inappstory.sdk.stories.events.StorySwipeBackEvent;
import com.inappstory.sdk.stories.managers.OldStatisticManager;
import com.inappstory.sdk.stories.outerevents.CallToAction;
import com.inappstory.sdk.stories.outerevents.ClickOnButton;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.serviceevents.ChangeIndexEventInFragment;
import com.inappstory.sdk.stories.storieslistenerevents.OnNextEvent;
import com.inappstory.sdk.stories.storieslistenerevents.OnPrevEvent;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.StoriesReaderPager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.StoriesReaderPagerAdapter;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPagerAdapter;
import com.inappstory.sdk.stories.utils.BackPressHandler;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.stories.utils.StatusBarController;

import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_POSITION;
import static com.inappstory.sdk.AppearanceManager.CS_READER_SETTINGS;
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
        if (positionOffset == 0f) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    invMask.setVisibility(View.GONE);
                }
            }, 100);
        } else {
            if (invMask.getVisibility() != View.VISIBLE)
                invMask.setVisibility(View.VISIBLE);
        }
        storiesViewPager.pageScrolled(positionOffset);
    }

    List<Integer> currentIds = new ArrayList<>();


    ReaderPagerAdapter outerViewPagerAdapter;
    View invMask;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (InAppStoryService.isNull()) {
            if (getActivity() != null) getActivity().finish();
            return;
        }
        if (isDestroyed) return;
        CsEventBus.getDefault().register(this);
        configurationChanged = false;
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        storiesViewPager.setParameters(
                getArguments().getInt(CS_STORY_READER_ANIMATION, 0));
        int closePosition = getArguments().getInt(CS_CLOSE_POSITION, 1);
        currentIds = getArguments().getIntegerArrayList("stories_ids");
        if (currentIds == null) {
            if (getActivity() != null) getActivity().finish();
            return;
        }
        outerViewPagerAdapter =
                new ReaderPagerAdapter(
                        getChildFragmentManager(),
                        getArguments().getString(CS_READER_SETTINGS),
                        currentIds);
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
            if (InAppStoryService.isNotNull()) {
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


    ReaderPager storiesViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isDestroyed = getArguments().getBoolean("isDestroyed");
        created = true;
        RelativeLayout resView = new RelativeLayout(getContext());
        resView.setBackgroundColor(getResources().getColor(R.color.black));
        storiesViewPager = new ReaderPager(getContext());
        storiesViewPager.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        invMask = new View(getContext());
        invMask.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        invMask.setVisibility(View.GONE);
        storiesViewPager.setId(R.id.ias_stories_pager);
        invMask.setId(R.id.ias_inv_mask);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            storiesViewPager.setElevation(4);
            storiesViewPager.setElevation(5);
        }
        resView.addView(storiesViewPager);
        resView.addView(invMask);
        return resView;//inflater.inflate(R.layout.cs_fragment_stories, container, false);
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
            ScreensManager.getInstance().setTempShareStoryId(0);
            ScreensManager.getInstance().setTempShareId(null);
            if (ScreensManager.getInstance().getOldTempShareId() != null) {
                CsEventBus.getDefault().post(new ShareCompleteEvent(
                        ScreensManager.getInstance().getOldTempShareStoryId(),
                        ScreensManager.getInstance().getOldTempShareId(), true));
            }
            ScreensManager.getInstance().setOldTempShareStoryId(0);
            ScreensManager.getInstance().setOldTempShareId(null);
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
        InAppStoryService.getInstance().getDownloadManager().reloadPage(event.getStoryId(), event.getIndex(), adds);
    }

    int lastPos = -1;


    @Override
    public void onPageSelected(int pos0) {
        if (isDestroyed) return;
        final int position = pos0;
        if (lastPos < pos0 && lastPos > -1) {
            sendStatBlock(true, StatisticManager.NEXT, currentIds.get(pos0));
        } else if (lastPos > pos0 && lastPos > -1) {
            sendStatBlock(true, StatisticManager.PREV, currentIds.get(pos0));
        } else if (lastPos == -1) {
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
            sendStatBlock(false, whence, currentIds.get(pos0));
        }
        lastPos = pos0;
        if (getArguments() != null) {
            getArguments().putInt("index", position);
        }

        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(currentIds.get(position));
        if (story != null)
            CsEventBus.getDefault().post(new ShowStory(story.id, story.title, story.tags,
                    story.slidesCount, getArguments().getInt("source")));
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
                InAppStoryService.getInstance().getDownloadManager().addStoryTask(currentIds.get(position), adds);


                if (InAppStoryService.isNull()) return;
                if (currentIds != null && currentIds.size() > position) {
                    OldStatisticManager.getInstance().addStatisticBlock(currentIds.get(position),
                            InAppStoryService.getInstance().getDownloadManager().getStoryById(currentIds.get(position)).lastIndex);
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

    private void sendStatBlock(boolean hasCloseEvent, String whence, int id) {
        Story story2 = InAppStoryService.getInstance().getDownloadManager().getStoryById(id);
        StatisticManager.getInstance().sendCurrentState();
        if (hasCloseEvent) {
            Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(currentIds.get(lastPos));
            StatisticManager.getInstance().sendCloseStory(story.id, whence, story.lastIndex, story.slidesCount);
        }
        StatisticManager.getInstance().sendViewStory(id, whence);
        StatisticManager.getInstance().sendOpenStory(id, whence);
        StatisticManager.getInstance().createCurrentState(story2.id, story2.lastIndex);
    }


    private int getCurIndexById(int id) {
        if (InAppStoryService.getInstance().getDownloadManager() == null) return 0;
        Story st = InAppStoryService.getInstance().getDownloadManager().getStoryById(id);
        return st == null ? 0 : st.lastIndex;
    }

    int currentIndex = 0;


    @Override
    public void onPageScrollStateChanged(int state) {
        if (InAppStoryService.isNull()) return;
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
                int sz = (!Sizes.isTablet() ? Sizes.getScreenSize(getContext()).x : Sizes.dpToPxExt(400));
                if (real >= 0.3 * sz && !event.isForbidden()) {
                    CsEventBus.getDefault().post(new NextStoryPageEvent(currentIds.get(storiesViewPager.getCurrentItem())));
                } else if (real < 0.3 * sz) {
                    CsEventBus.getDefault().post(new PrevStoryPageEvent(currentIds.get(storiesViewPager.getCurrentItem())));
                }
            } else {
                tapOnLink(event.getLink());
            }
        }
    }


    private void tapOnLink(String link) {
        StoryLinkObject object = JsonParser.fromJson(link, StoryLinkObject.class);
        if (object != null) {
            switch (object.getLink().getType()) {
                case "url":
                    Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(
                            currentIds.get(storiesViewPager.getCurrentItem()));
                    CsEventBus.getDefault().post(new ClickOnButton(story.id, story.title,
                            story.tags, story.slidesCount, story.lastIndex,
                            object.getLink().getTarget()));
                    int cta = CallToAction.BUTTON;
                    if (object.getType() != null && !object.getType().isEmpty()) {
                        switch (object.getType()) {
                            case "swipeUpLink":
                                cta = CallToAction.SWIPE;
                                break;
                            default:
                                break;
                        }
                    }
                    CsEventBus.getDefault().post(new CallToAction(story.id, story.title,
                            story.tags, story.slidesCount, story.lastIndex,
                            object.getLink().getTarget(), cta));
                    OldStatisticManager.getInstance().addLinkOpenStatistic();
                    if (CallbackManager.getInstance().getUrlClickCallback() != null) {
                        CallbackManager.getInstance().getUrlClickCallback().onUrlClick(
                                object.getLink().getTarget()
                        );
                    } else {
                        if (!InAppStoryService.isConnected()) {
                            return;
                        }
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setData(Uri.parse(object.getLink().getTarget()));
                        startActivity(i);
                        getActivity().overridePendingTransition(R.anim.popup_show, R.anim.empty_animation);
                    }
                    break;
                default:
                    if (CallbackManager.getInstance().getAppClickCallback() != null) {
                        CallbackManager.getInstance().getAppClickCallback().onAppClick(
                                object.getLink().getType(),
                                object.getLink().getTarget()
                        );
                    }
                    break;
            }
        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void nextStoryPageEvent(OnNextEvent event) {
        if (isDestroyed) return;
        if (InAppStoryService.isNull()) return;
        Story st = InAppStoryService.getInstance().getDownloadManager()
                .getStoryById(InAppStoryService.getInstance().getCurrentId());
        if (st.durations != null && !st.durations.isEmpty()) st.slidesCount = st.durations.size();
        if (currentIndex < st.slidesCount - 1) {
            currentIndex++;
        }

        CsEventBus.getDefault().post(new StoryPageOpenEvent(
                InAppStoryService.getInstance().getCurrentId(),
                currentIndex, true, false
        ));

        if (st.lastIndex < st.slidesCount) {
            st.lastIndex++;
        }
    }




    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void setCurrentIndexEvent(ChangeIndexEvent event) {
        if (!isDestroyed) {
            int curItem = storiesViewPager.getCurrentItem();
            InAppStoryService.getInstance().getDownloadManager()
                    .getStoryById(currentIds.get(curItem)).lastIndex = event.getIndex();
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
        if (InAppStoryService.isNull()) return;

        Handler handler = new Handler(Looper.getMainLooper());
        if (InAppStoryService.getInstance().getDownloadManager()
                .getStoryById(InAppStoryService.getInstance().getCurrentId()).lastIndex > 0) {
            InAppStoryService.getInstance().getDownloadManager()
                    .getStoryById(InAppStoryService.getInstance().getCurrentId()).lastIndex--;
        }
        if (currentIndex > 0) {
            currentIndex--;
            CsEventBus.getDefault().post(new StoryPageOpenEvent(
                    InAppStoryService.getInstance().getCurrentId(),
                    currentIndex, false, true
            ));
        }

    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeStoryEvent(ChangeStoryEvent event) {
        if (isDestroyed) return;
        if (InAppStoryService.isNull()) return;
        InAppStoryService.getInstance().setCurrentId(currentIds.get(event.getIndex()));
        currentIndex = InAppStoryService.getInstance().getDownloadManager()
                .getStoryById(currentIds.get(event.getIndex())).lastIndex;
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
    public void onNextStory(NextStoryReaderEvent event) {
        if (isDestroyed) return;
        storiesViewPager.onNextStory();
    }



    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void onPrevStory(PrevStoryReaderEvent event) {
        if (isDestroyed) return;
        storiesViewPager.onPrevStory();
    }

}
