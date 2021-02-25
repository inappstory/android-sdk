package com.inappstory.sdk.stories.ui.widgets.viewpagertransforms;

import android.util.Log;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by Paperrose on 13.07.2018.
 */

public class DepthTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View view, float position) {
       // Log.e("ViewTransform", view.toString() + " " + position);
        float MIN_SCALE = 0.85f;
        if (position <= -1.0) {
            view.setTranslationX(0f);
        } else if (position <= 0f) {

            final float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));

            view.setAlpha(1 + position);
            view.setPivotY(0.5f * view.getHeight());
            view.setTranslationX(view.getWidth() * -position);
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

        } else if (position <= 1f) {

          //  view.setAlpha(1f);


            view.setTranslationX(0f);
            view.setScaleX(1f);
            view.setScaleY(1f);

        } else if (position > 1) {
            view.setTranslationX(view.getWidth());
        }
    }
}
