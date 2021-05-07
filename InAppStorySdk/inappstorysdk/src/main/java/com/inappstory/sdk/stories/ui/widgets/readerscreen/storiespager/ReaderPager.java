package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.cache.OldStoryDownloader;
import com.inappstory.sdk.stories.events.ChangeStoryEvent;
import com.inappstory.sdk.stories.events.CloseStoryReaderEvent;
import com.inappstory.sdk.stories.events.NextStoryReaderEvent;
import com.inappstory.sdk.stories.events.PrevStoryReaderEvent;
import com.inappstory.sdk.stories.events.SwipeDownEvent;
import com.inappstory.sdk.stories.events.SwipeLeftEvent;
import com.inappstory.sdk.stories.events.SwipeRightEvent;
import com.inappstory.sdk.stories.events.SwipeUpEvent;
import com.inappstory.sdk.stories.events.WidgetTapEvent;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.serviceevents.PrevStoryFragmentEvent;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.StoriesReaderPagerAdapter;
import com.inappstory.sdk.stories.ui.widgets.viewpagertransforms.CubeTransformer;
import com.inappstory.sdk.stories.ui.widgets.viewpagertransforms.DepthTransformer;

public class ReaderPager extends ViewPager {
    public ReaderPager(@NonNull Context context) {
        super(context);
        CsEventBus.getDefault().register(this);
    }

    public ReaderPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        CsEventBus.getDefault().register(this);
    }

    public boolean canUseNotLoaded;

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int res = super.getChildDrawingOrder(childCount, i);
        return childCount - res - 1;
    }

    private long pressStartTime;
    private float pressedX;
    private float pressedY;
    boolean startMove;

    public static PageTransformer cubeTransformer = new CubeTransformer();
    public static PageTransformer depthTransformer = new DepthTransformer();

    public void setTransformAnimation(int transformAnimation) {
        this.transformAnimation = transformAnimation;
    }

    private int transformAnimation;
    boolean closeOnSwipe;

    public void setParameters(int transformAnimation, boolean closeOnSwipe) {
        this.transformAnimation = transformAnimation;
        this.closeOnSwipe = closeOnSwipe;
        init();
    }

    public void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.StoriesReaderPager);
        }
    }

    public void init() {
        switch (transformAnimation) {
            case 0:
                setChildrenDrawingOrderEnabled(false);
                setPageTransformer(true, cubeTransformer);
                break;
            case 1:
                setChildrenDrawingOrderEnabled(true);
                setPageTransformer(true, depthTransformer);
                break;
            default:
                setChildrenDrawingOrderEnabled(false);
                setPageTransformer(true, cubeTransformer);
                break;

        }
    }

    public void pageScrolled(float positionOffset) {
        if (positionOffset == 0f) {
            cubeAnimation = false;
            requestDisallowInterceptTouchEvent(false);
        } else {
            cubeAnimation = true;
            requestDisallowInterceptTouchEvent(true);
        }
    }

    @CsSubscribe
    public void nextStoryEvent(NextStoryReaderEvent event) {
        cubeAnimation = true;
    }

    public boolean cubeAnimation = false;

    @CsSubscribe
    public void prevStoryEvent(PrevStoryReaderEvent event) {
        cubeAnimation = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (cubeAnimation) {
            return false;
        }
        Story st = InAppStoryService.getInstance().getDownloadManager().getStoryById(InAppStoryService.getInstance().getCurrentId());
        float pressedEndX = 0f;
        float pressedEndY = 0f;
        boolean distance = false;
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            pressStartTime = System.currentTimeMillis();
            pressedX = motionEvent.getX();
            pressedY = motionEvent.getY();
            CsEventBus.getDefault().post(new WidgetTapEvent());
        } else if (!(motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL)) {
            pressedEndX = motionEvent.getX() - pressedX;
            pressedEndY = motionEvent.getY() - pressedY;
            distance = (float) Math.sqrt(pressedEndX * pressedEndX + pressedEndY * pressedEndY) > 20;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            pressedEndY = motionEvent.getY() - pressedY;
            pressedEndX = motionEvent.getX() - pressedX;
            if (pressedEndY > 400) {
                if (st != null
                        && !st.disableClose) {
                    CsEventBus.getDefault().post(new SwipeDownEvent());
                    return true;
                }
            }
            if (pressedEndY < -400) {
                if (st != null) {
                    CsEventBus.getDefault().post(new SwipeUpEvent());
                    return true;
                }
            }
            if (getCurrentItem() == 0 &&
                    pressedEndX * pressedEndX > pressedEndY * pressedEndY &&
                    pressedEndX > 300) {
                if (st == null) return true;
                if (!st.disableClose) {
                    CsEventBus.getDefault().post(new SwipeRightEvent());
                    return true;
                }
            }

            if (getCurrentItem() == getAdapter().getCount() - 1 &&
                    pressedEndX * pressedEndX > pressedEndY * pressedEndY &&
                    pressedEndX < -300) {
                if (st == null) return true;
                if (!st.disableClose) {
                    CsEventBus.getDefault().post(new SwipeLeftEvent());
                    return true;
                }
            }
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            if (pressedEndX * pressedEndX < pressedEndY * pressedEndY && (float) Math.sqrt(pressedEndY * pressedEndY) > 20) {
                return false;
            }
            if (distance && !(
                    getCurrentItem() == 0 &&
                            pressedEndX * pressedEndX > pressedEndY * pressedEndY &&
                            pressedEndX > 0)
                    &&
                    !(getCurrentItem() == getAdapter().getCount() - 1 &&
                            pressedEndX * pressedEndX > pressedEndY * pressedEndY &&
                            pressedEndX < 0)) {
                return true;
            } else {
                return false;
            }
        }
        boolean c = super.onInterceptTouchEvent(motionEvent);
        return c;
    }


    public void onNextStory() {
        if (getCurrentItem() < getAdapter().getCount() - 1) {

            CsEventBus.getDefault().post(new ChangeStoryEvent(((ReaderPagerAdapter)getAdapter()).
                    getItemId(getCurrentItem() + 1),
                    getCurrentItem() + 1));
            setCurrentItem(getCurrentItem() + 1);
        } else {
            CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.AUTO));
        }
    }

    public void onPrevStory() {
        if (getCurrentItem() > 0) {

            StatisticManager.getInstance().sendCurrentState();
            CsEventBus.getDefault().post(new ChangeStoryEvent(((ReaderPagerAdapter)getAdapter()).
                    getItemId(getCurrentItem() - 1),
                    getCurrentItem() - 1));
            setCurrentItem(getCurrentItem() - 1);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    cubeAnimation = false;
                }
            }, 100);
            CsEventBus.getDefault().post(new PrevStoryFragmentEvent(InAppStoryService.getInstance().getCurrentId()));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        return super.onTouchEvent(motionEvent);
    }
}
