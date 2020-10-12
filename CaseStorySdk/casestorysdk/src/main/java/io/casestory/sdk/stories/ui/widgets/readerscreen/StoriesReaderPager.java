package io.casestory.sdk.stories.ui.widgets.readerscreen;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import io.casestory.casestorysdk.R;
import io.casestory.sdk.CaseStoryService;
import io.casestory.sdk.eventbus.EventBus;
import io.casestory.sdk.stories.api.models.Story;
import io.casestory.sdk.stories.cache.StoryDownloader;
import io.casestory.sdk.stories.events.ResumeStoryReaderEvent;
import io.casestory.sdk.stories.events.StorySwipeBackEvent;
import io.casestory.sdk.stories.events.SwipeDownEvent;
import io.casestory.sdk.stories.events.SwipeLeftEvent;
import io.casestory.sdk.stories.events.SwipeRightEvent;
import io.casestory.sdk.stories.events.WidgetTapEvent;
import io.casestory.sdk.stories.ui.widgets.viewpagertransforms.CubeTransformer;
import io.casestory.sdk.stories.ui.widgets.viewpagertransforms.DepthTransformer;

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
        if (CaseStoryService.getInstance().cubeAnimation) {
            return false;
        }

        if (System.currentTimeMillis() - CaseStoryService.getInstance().lastTapEventTime < 700) {
            Log.e("VPIntTouch_skip", motionEvent.toString());
            return false;
        }
        Log.e("VPIntTouch_done", motionEvent.toString());
        long pressEndTime;
        float pressedEndX = 0f;
        float pressedEndY = 0f;
        boolean distance = false;
        boolean distanceY = false;
        boolean time = false;
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            pressStartTime = System.currentTimeMillis();
            pressedX = motionEvent.getX();
            pressedY = motionEvent.getY();
            EventBus.getDefault().post(new WidgetTapEvent());
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
            Log.e("resumeTimer", "ReaderTouch");
            EventBus.getDefault().post(new ResumeStoryReaderEvent(false));
            EventBus.getDefault().post(new StorySwipeBackEvent(CaseStoryService.getInstance().getCurrentId()));
            if (distanceY) {
                if (!StoryDownloader.getInstance().getStoryById(CaseStoryService.getInstance().getCurrentId()).disableClose) {
                    EventBus.getDefault().post(new SwipeDownEvent());
                    return true;
                }
            }
            if (getCurrentItem() == 0 &&
                    pressedEndX * pressedEndX > pressedEndY * pressedEndY &&
                    pressedEndX > 300) {
                if (!StoryDownloader.getInstance().getStoryById(CaseStoryService.getInstance().getCurrentId()).disableClose) {
                    EventBus.getDefault().post(new SwipeRightEvent());
                    return true;
                }
            }

            if (getCurrentItem() == getAdapter().getCount() - 1 &&
                    pressedEndX * pressedEndX > pressedEndY * pressedEndY &&
                    pressedEndX < -300) {
                if (!StoryDownloader.getInstance().getStoryById(CaseStoryService.getInstance().getCurrentId()).disableClose) {
                    EventBus.getDefault().post(new SwipeLeftEvent());
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
}