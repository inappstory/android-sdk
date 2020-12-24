package com.inappstory.sdk.stories.ui.widgets.readerscreen;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.inappstory.sdk.R;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.cache.StoryDownloader;
import com.inappstory.sdk.stories.events.PauseStoryReaderEvent;
import com.inappstory.sdk.stories.events.ResumeStoryReaderEvent;
import com.inappstory.sdk.stories.events.StorySwipeBackEvent;
import com.inappstory.sdk.stories.events.SwipeDownEvent;
import com.inappstory.sdk.stories.events.SwipeLeftEvent;
import com.inappstory.sdk.stories.events.SwipeRightEvent;
import com.inappstory.sdk.stories.events.WidgetTapEvent;
import com.inappstory.sdk.stories.ui.widgets.viewpagertransforms.CubeTransformer;
import com.inappstory.sdk.stories.ui.widgets.viewpagertransforms.DepthTransformer;

public class StoriesReaderPager extends ViewPager {

    public boolean canUseNotLoaded;

    public StoriesReaderPager(Context context) {
        super(context);
    }

    public StoriesReaderPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        switch (i) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
        }
        return super.getChildDrawingOrder(childCount, i);
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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (InAppStoryService.getInstance().cubeAnimation) {
            return false;
        }

        long pressEndTime;
        float pressedEndX = 0f;
        float pressedEndY = 0f;
        boolean distance = false;
        boolean distanceY = false;
        boolean time = false;
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
           // CsEventBus.getDefault().post(new PauseStoryReaderEvent(false));
            pressStartTime = System.currentTimeMillis();
            pressedX = motionEvent.getX();
            pressedY = motionEvent.getY();
            CsEventBus.getDefault().post(new WidgetTapEvent());
        } else if (!(motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL)) {
            pressEndTime = System.currentTimeMillis() - pressStartTime;
            pressedEndX = motionEvent.getX() - pressedX;
            pressedEndY = motionEvent.getY() - pressedY;
            distance = (float) Math.sqrt(pressedEndX * pressedEndX + pressedEndY * pressedEndY) > 20;
            time = pressEndTime > 100;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            pressedEndY = motionEvent.getY() - pressedY;
            pressedEndX = motionEvent.getX() - pressedX;
            distanceY = pressedEndY > 400;
            if (pressedEndY > 0) {
            }
          //  CsEventBus.getDefault().post(new ResumeStoryReaderEvent(false));
            if (distanceY) {
                if (!StoryDownloader.getInstance().getStoryById(InAppStoryService.getInstance().getCurrentId()).disableClose) {
                    CsEventBus.getDefault().post(new SwipeDownEvent());
                    return true;
                }
            }
            if (getCurrentItem() == 0 &&
                    pressedEndX * pressedEndX > pressedEndY * pressedEndY &&
                    pressedEndX > 300) {
                if (StoryDownloader.getInstance().getStoryById(InAppStoryService.getInstance().getCurrentId()) == null) return true;
                if (!StoryDownloader.getInstance().getStoryById(InAppStoryService.getInstance().getCurrentId()).disableClose) {
                    CsEventBus.getDefault().post(new SwipeRightEvent());
                    return true;
                }
            }

            if (getCurrentItem() == getAdapter().getCount() - 1 &&
                    pressedEndX * pressedEndX > pressedEndY * pressedEndY &&
                    pressedEndX < -300) {
                Story st = StoryDownloader.getInstance().getStoryById(InAppStoryService.getInstance().getCurrentId());
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


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            CsEventBus.getDefault().post(new PauseStoryReaderEvent(false));
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            CsEventBus.getDefault().post(new ResumeStoryReaderEvent(false));
            CsEventBus.getDefault().post(new StorySwipeBackEvent(InAppStoryService.getInstance().getCurrentId()));
        }
        return super.onTouchEvent(motionEvent);
    }
}