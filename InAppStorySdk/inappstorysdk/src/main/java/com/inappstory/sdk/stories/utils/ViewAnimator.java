package com.inappstory.sdk.stories.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.inappstory.sdk.stories.ui.widgets.readerscreen.generated.GeneratedView;

public class ViewAnimator {
    public static final String FOCUS_IN = "focus-in";
    public static final String FADE_IN_UP = "fade-in-up";
    public static final String FADE_IN_DOWN = "fade-in-down";
    public static final String FADE_IN_LEFT = "fade-in-left";
    public static final String FADE_IN_RIGHT = "fade-in-right";
    public static final String ZOOM = "zoom";
    public static final String ZOOM_IN = "zoom-in";
    public static final String ZOOM_OUT = "zoom-out";
    public static final String SHAKE = "shake";
    public static final String SCROLL_UP = "scroll-up";
    public static final String SCROLL_DOWN = "scroll-down";
    public static final String SCROLL_LEFT = "scroll-left";
    public static final String SCROLL_RIGHT = "scroll-right";
    public static final String BLUR = "blur";

    public ValueAnimator focusIn(final View gv) {
        RelativeLayout parent = (RelativeLayout) gv.getParent();
        final BlurringView bv = new BlurringView(gv.getContext());
        bv.setLayoutParams(gv.getLayoutParams());
        parent.addView(bv);
        bv.setBlurredView(gv);
        ValueAnimator animator = ValueAnimator.ofFloat(12f, 0f);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int ds = Math.max(1, (int) ((float) animation.getAnimatedValue() / 3f));
                bv.setDownsampleFactor(ds);
                //bv.setBlurRadius(Math.max(0.01f, (float)animation.getAnimatedValue()));
                bv.setAlpha(1f / 12 * (float) animation.getAnimatedValue());
                if ((float) animation.getAnimatedValue() < 0.01f) {
                    bv.setVisibility(View.GONE);
                }
                bv.invalidate();
            }
        });
        bv.invalidate();
        //animator.setStartDelay(2000);
        return animator;
    }

    public ValueAnimator fadeIn(final View gv, String animation) {
        int xPlus = 0;
        int yPlus = 0;
        int duration = 1000;
        switch (animation) {
            case FADE_IN_UP:
                yPlus = -Sizes.getScreenSize().y;
                xPlus = 0;
                break;
            case FADE_IN_DOWN:
                yPlus = Sizes.getScreenSize().y;
                xPlus = 0;
                break;
            case FADE_IN_LEFT:
                yPlus = 0;
                xPlus = -Sizes.getScreenSize().x;
                break;
            case FADE_IN_RIGHT:
                yPlus = 0;
                xPlus = Sizes.getScreenSize().x;
                break;
        }

        ValueAnimator animator = ValueAnimator.ofFloat(1, 100);
        animator.setDuration(duration);
        final AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new AlphaAnimation(0f, 1f));
        animationSet.addAnimation(new TranslateAnimation(xPlus, 0f, yPlus, 0f));
        animationSet.setDuration(duration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                gv.startAnimation(animationSet);
            }
        });
        return animator;
    }

    public ValueAnimator zoom(final View gv, String animation) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) gv.getLayoutParams();
        int xSize = lp.width;
        int ySize = lp.height;
        if (xSize == -1) xSize = Sizes.getScreenSize().x;
        if (ySize == -1) ySize = Sizes.getScreenSize().y;

        float from = 1f;
        float to = 1f;
        switch (animation) {
            case ZOOM:
            case ZOOM_IN:
                from = 1f;
                to = 1.2f;
                break;
            case ZOOM_OUT:
                from = 1.2f;
                to = 1f;
        }
        if (from > 1) {
            gv.setPivotX(xSize / 2);
            gv.setPivotY(ySize / 2);
            gv.setScaleX(from);
            gv.setScaleY(from);
        }
        ValueAnimator animator = ValueAnimator.ofFloat(1, 100);
        int duration = 10000;
        final AnimationSet animationSet = new AnimationSet(true);
        animator.setDuration(duration);
        animationSet.setDuration(duration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                gv.startAnimation(animationSet);
            }
        });
        animationSet.setFillAfter(true);
        animationSet.setFillBefore(true);
        animationSet.addAnimation(new ScaleAnimation(from, to, from, to,
                Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f));

        return animator;
    }

    public ValueAnimator shake(final View gv) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) gv.getLayoutParams();
        int xSize = lp.width;
        int ySize = lp.height;
        if (ySize == -1) ySize = Sizes.getScreenSize().y;
        if (xSize == -1) {
            xSize = Sizes.getScreenSize().x;
            gv.setPivotX(xSize / 2f);
            gv.setPivotY(ySize / 2f);
            gv.setScaleX(1.1f);
            gv.setScaleY(1.1f);
        }

        final float[] toX = {1, -1, -3, 3, 1, -1, -3, -3, -1, 1, 1};
        final float[] toY = {1, -2, 0, 2, -1, 2, 1, 1, -1, 2, -2};
        final int[] toDeg = {0, -1, 1, 0, 1, -1, 0, 0, 1, 0, -1};
        ValueAnimator animator = ValueAnimator.ofFloat(0, 10);
        int duration = 1000;
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animStep = ((Float)animation.getAnimatedValue()).intValue();
                gv.setTranslationX(toX[animStep]);
                gv.setTranslationY(toY[animStep]);
                gv.setRotation(toDeg[animStep]);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                gv.setTranslationX(toX[10]);
                gv.setTranslationY(toY[10]);
                gv.setRotation(toDeg[10]);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                gv.setTranslationX(toX[0]);
                gv.setTranslationY(toY[0]);
                gv.setRotation(toDeg[0]);
            }
        });
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);

        return animator;
    }

    public ValueAnimator scroll(final View gv, String animation) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) gv.getLayoutParams();
        int xSize = lp.width;
        int ySize = lp.height;
        if (xSize == -1) xSize = Sizes.getScreenSize().x;
        if (ySize == -1) ySize = Sizes.getScreenSize().y;

        float xf = lp.topMargin;
        float xt = lp.topMargin;
        float yf = lp.leftMargin;
        float yt = lp.leftMargin;

        gv.setPivotX(xSize / 2);
        gv.setPivotY(ySize / 2);
        gv.setScaleX(1.2f);
        gv.setScaleY(1.2f);
        switch (animation) {
            case SCROLL_DOWN:
                yf -= 0.08f * ySize;
                yt += 0.08f * ySize;
                break;
            case SCROLL_UP:
                yf += 0.08f * ySize;
                yt -= 0.08f * ySize;
                break;
            case SCROLL_RIGHT:
                xf -= 0.08f * xSize;
                xt += 0.08f * xSize;
                break;
            case SCROLL_LEFT:
                xf += 0.08f * xSize;
                xt -= 0.08f * xSize;
                break;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(1, 100);
        int duration = 20000;
        final AnimationSet animationSet = new AnimationSet(true);
        animator.setDuration(duration);
        animationSet.setDuration(duration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                gv.startAnimation(animationSet);
            }
        });
        animationSet.setFillAfter(true);
        animationSet.setFillBefore(true);
        animationSet.addAnimation(new TranslateAnimation(xf, xt, yf, yt));

        return animator;
    }

    public ValueAnimator blur(View gv) {
        return null;
    }

    public ValueAnimator animate(View gv, String animation) {
        switch (animation) {
            case FOCUS_IN:
                return focusIn(gv);
            case FADE_IN_UP:
            case FADE_IN_DOWN:
            case FADE_IN_LEFT:
            case FADE_IN_RIGHT:
                return fadeIn(gv, animation);
            case ZOOM:
            case ZOOM_IN:
            case ZOOM_OUT:
                return zoom(gv, animation);
            case SHAKE:
                return shake(gv);
            case SCROLL_UP:
            case SCROLL_DOWN:
            case SCROLL_LEFT:
            case SCROLL_RIGHT:
                return scroll(gv, animation);
            case BLUR:
                return blur(gv);
        }
        return null;
    }
}
