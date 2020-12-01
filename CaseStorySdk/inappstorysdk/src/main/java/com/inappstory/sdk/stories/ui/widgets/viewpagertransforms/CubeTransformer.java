package com.inappstory.sdk.stories.ui.widgets.viewpagertransforms;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by Paperrose on 13.07.2018.
 */

public class CubeTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View page, float position) {
     //   onPreTransform(page, position);
        onTransform(page, position);
     //   onPostTransform(page, position);
    }

    protected void onTransform(View view, float position) {
    /*    if (Math.abs(position) > 0.90 || Math.abs(position) < 0.09) {
            StoriesManager.getInstance().cubeAnimation = false;
        } else {
            StoriesManager.getInstance().cubeAnimation = true;
        }*/
        view.setPivotX(position < 0f ? view.getWidth() : 0f);
        view.setPivotY(view.getHeight() * 0.6f);
        view.setRotationY(60 * position);
    }

    protected void onPostTransform(View page, float position) {
    }

    protected boolean hideOffscreenPages() {
        return true;
    }

    protected boolean isPagingEnabled() {
        return false;
    }

    protected void onPreTransform(View page, float position) {
        final float width = page.getWidth();

        page.setRotationX(0);
        page.setRotationY(0);
        page.setRotation(0);
        page.setScaleX(1);
        page.setScaleY(1);
        page.setPivotX(0);
        page.setPivotY(0);
        page.setTranslationY(0);
        page.setTranslationX(isPagingEnabled() ? 0f : -width * position);

        if (hideOffscreenPages()) {
            page.setAlpha(position <= -1f || position >= 1f ? 0f : 1f);
            page.setEnabled(false);
        } else {
            page.setEnabled(true);
            page.setAlpha(1f);
        }
    }
}
