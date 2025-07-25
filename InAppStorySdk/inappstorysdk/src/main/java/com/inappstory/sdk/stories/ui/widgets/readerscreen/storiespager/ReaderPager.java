package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.ui.reader.BothSideViewPager;
import com.inappstory.sdk.stories.ui.reader.StoriesContentFragment;
import com.inappstory.sdk.stories.ui.widgets.viewpagertransforms.CoverTransformer;
import com.inappstory.sdk.stories.ui.widgets.viewpagertransforms.CubeTransformer;
import com.inappstory.sdk.stories.ui.widgets.viewpagertransforms.DepthTransformer;

public class ReaderPager extends BothSideViewPager {
    public void setHost(StoriesContentFragment host) {
        this.host = host;
    }

    StoriesContentFragment host;

    public ReaderPager(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ReaderPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(@NonNull Context context) {
        setLayoutDirection(context.getResources().getConfiguration().getLayoutDirection());
    }

    public boolean canUseNotLoaded;

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int res = super.getChildDrawingOrder(childCount, i);
        return childCount - res - 1;
    }

    private float pressedX;
    private float pressedY;
    boolean startMove;

    public static PageTransformer cubeTransformer = new CubeTransformer();
    public static PageTransformer depthTransformer = new DepthTransformer();
    public static PageTransformer coverTransformer = new CoverTransformer();
    private boolean swipeVerticalEnabled = true;

    public void swipeVerticalEnabled(boolean swipeVerticalEnabled) {
        this.swipeVerticalEnabled = swipeVerticalEnabled;
    }

    public void setTransformAnimation(int transformAnimation) {
        this.transformAnimation = transformAnimation;
    }

    private int transformAnimation;
    boolean closeOnSwipe;

    public void setParameters(int transformAnimation) {
        this.transformAnimation = transformAnimation;
        init();
    }

    public void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ReaderPager);
        }
    }

    public void init() {
        switch (transformAnimation) {
            case AppearanceManager.ANIMATION_FLAT:
                break;
            case AppearanceManager.ANIMATION_DEPTH:
                setChildrenDrawingOrderEnabled(true);
                setPageTransformer(true, depthTransformer);
                break;
            case AppearanceManager.ANIMATION_COVER:
                setChildrenDrawingOrderEnabled(true);
                setPageTransformer(true, coverTransformer);
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

    public boolean cubeAnimation = false;


    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (getParent() != null)
            getParent().requestDisallowInterceptTouchEvent(!swipeVerticalEnabled);
        if (cubeAnimation) {
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
            if (swipeVerticalEnabled) {
                if (pressedEndY > 400) {
                    host.swipeDownEvent(getCurrentItem());
                    return true;
                }
                if (pressedEndY < -400) {
                    host.swipeUpEvent(getCurrentItem());
                    return true;
                }
            }
            if (swipeRightCondition &&
                    pressedEndX * pressedEndX > pressedEndY * pressedEndY &&
                    pressedEndX > 300) {
                host.swipeRightEvent(getCurrentItem());
                return true;
            }

            if (swipeLeftCondition &&
                    pressedEndX * pressedEndX > pressedEndY * pressedEndY &&
                    pressedEndX < -300) {
                host.swipeLeftEvent(getCurrentItem());
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

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        try {
            return super.onTouchEvent(motionEvent);
        } catch (Exception e) {
            return false;
        }
    }
}
