package com.inappstory.sdk.refactoring.stories.ui.reader.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels.StoryReaderViewModel;
import com.inappstory.sdk.stories.ui.reader.BothSideViewPager;
import com.inappstory.sdk.stories.ui.widgets.viewpagertransforms.CoverTransformer;
import com.inappstory.sdk.stories.ui.widgets.viewpagertransforms.CubeTransformer;
import com.inappstory.sdk.stories.ui.widgets.viewpagertransforms.DepthTransformer;

public class StoryReaderPager extends BothSideViewPager {
    public StoryReaderPager(@NonNull Context context) {
        super(context);
    }

    public StoryReaderPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void viewModel(StoryReaderViewModel readerViewModel) {
        this.readerViewModel = readerViewModel;
    }

    private StoryReaderViewModel readerViewModel;

    public void transformAnimation(int animation) {
        switch (animation) {
            case AppearanceManager.ANIMATION_FLAT:
                break;
            case AppearanceManager.ANIMATION_DEPTH:
                setChildrenDrawingOrderEnabled(true);
                setPageTransformer(true, new DepthTransformer());
                break;
            case AppearanceManager.ANIMATION_COVER:
                setChildrenDrawingOrderEnabled(true);
                setPageTransformer(true, new CoverTransformer());
                break;
            default:
                setChildrenDrawingOrderEnabled(false);
                setPageTransformer(true, new CubeTransformer());
                break;
        }
    }

    private boolean needToLockVerticalSwipe = true;

    public void needToLockVerticalSwipe(boolean readerHasVerticalSwipeGesture) {
        this.needToLockVerticalSwipe = readerHasVerticalSwipeGesture;
    }

    private boolean pageChangeInProgress = false;

    public void pageChangeInProgress(boolean inProgress) {
        pageChangeInProgress = inProgress;
        requestDisallowInterceptTouchEvent(inProgress);
    }


    private float pressedX;
    private float pressedY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (getParent() != null)
            getParent().requestDisallowInterceptTouchEvent(!needToLockVerticalSwipe);
        if (pageChangeInProgress) {
            return true;
        }
        float pressedEndX = 0f;
        float pressedEndY = 0f;
        boolean distance = false;
        boolean swipeLeftCondition = (getCurrentItem() ==
                ((getLayoutDirection() == LAYOUT_DIRECTION_RTL) ?
                        0 : (getAdapter().getCount() - 1)));
        boolean swipeRightCondition = (getCurrentItem() ==
                ((getLayoutDirection() == LAYOUT_DIRECTION_LTR) ?
                        0 : (getAdapter().getCount() - 1)));
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            pressedX = motionEvent.getX();
            pressedY = motionEvent.getY();
        } else if (!(motionEvent.getAction() == MotionEvent.ACTION_UP
                || motionEvent.getAction() == MotionEvent.ACTION_CANCEL)) {
            pressedEndX = motionEvent.getX() - pressedX;
            pressedEndY = motionEvent.getY() - pressedY;
            distance = (float) Math.sqrt(pressedEndX * pressedEndX + pressedEndY * pressedEndY) > 20;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP
                || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            pressedEndY = motionEvent.getY() - pressedY;
            pressedEndX = motionEvent.getX() - pressedX;
            if (needToLockVerticalSwipe) {
                if (pressedEndY > 400) {
                    readerViewModel.swipe(getCurrentItem(), SwipeDirection.DOWN);
                    return true;
                }
                if (pressedEndY < -400) {
                    readerViewModel.swipe(getCurrentItem(), SwipeDirection.UP);
                    return true;
                }
            }
            if (swipeRightCondition &&
                    pressedEndX * pressedEndX > pressedEndY * pressedEndY &&
                    pressedEndX > 300) {
                readerViewModel.swipe(getCurrentItem(), SwipeDirection.RIGHT);
                return true;
            }

            if (swipeLeftCondition &&
                    pressedEndX * pressedEndX > pressedEndY * pressedEndY &&
                    pressedEndX < -300) {
                readerViewModel.swipe(getCurrentItem(), SwipeDirection.LEFT);
                return true;
            }
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            if (pressedEndX * pressedEndX < pressedEndY * pressedEndY
                    && (float) Math.sqrt(pressedEndY * pressedEndY) > 20) {
                return false;
            }
            if (distance && !(
                    swipeRightCondition &&
                            pressedEndX * pressedEndX > pressedEndY * pressedEndY &&
                            pressedEndX > 0)
                    &&
                    !(swipeLeftCondition &&
                            pressedEndX * pressedEndX > pressedEndY * pressedEndY &&
                            pressedEndX < 0)) {
                return true;
            } else {
                return false;
            }
        }
        return super.onInterceptTouchEvent(motionEvent);
    }
}
