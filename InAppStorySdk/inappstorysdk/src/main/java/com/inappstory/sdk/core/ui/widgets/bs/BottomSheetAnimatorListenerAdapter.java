package com.inappstory.sdk.core.ui.widgets.bs;

import android.animation.Animator;

public abstract class BottomSheetAnimatorListenerAdapter implements Animator.AnimatorListener {

    private boolean mIsCancelled;

    protected BottomSheetAnimatorListenerAdapter() {
        mIsCancelled = false;
    }

    @Override
    public final void onAnimationStart(Animator animation, boolean isReverse) {
        mIsCancelled = false;

        onAnimationStarted(animation);
    }

    @Override
    public final void onAnimationStart(Animator animation) {
        mIsCancelled = false;

        onAnimationStarted(animation);
    }

    public void onAnimationStarted(Animator animation) {

    }

    @Override
    public final void onAnimationEnd(Animator animation, boolean isReverse) {
        onAnimationEnded(animation);
    }

    @Override
    public final void onAnimationEnd(Animator animation) {
        onAnimationEnded(animation);
    }

    public void onAnimationEnded(Animator animation) {

    }

    @Override
    public final void onAnimationCancel(Animator animation) {
        mIsCancelled = true;

        onAnimationCancelled(animation);
    }

    public void onAnimationCancelled(Animator animation) {

    }

    @Override
    public final void onAnimationRepeat(Animator animation) {
        mIsCancelled = false;

        onAnimationRepeated(animation);
    }

    public void onAnimationRepeated(Animator animation) {

    }

    public final boolean isCancelled() {
        return mIsCancelled;
    }

}