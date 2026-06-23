package com.inappstory.sdk.utils;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void onSwipeLeft() {
    }

    public void onSwipeRight() {
    }

    public void onSwipeDown() {
    }

    public void onSwipeUp() {
    }

    public void onDownTouch() {

    }

    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (Math.abs(totalXDrag) > Math.abs(totalYDrag)) {
                if (Math.abs(totalXDrag) > 200) {
                    if (totalXDrag > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                }
            } else if (Math.abs(totalYDrag) > 200) {
                if (totalYDrag > 0) {
                    onSwipeDown();
                } else {
                    onSwipeUp();
                }
            }
        }
        return gestureDetector.onTouchEvent(event);
    }

    private long lastXScrollEventTime = 0L;
    private long lastYScrollEventTime = 0L;
    private float totalYDrag = 0f;
    private float totalXDrag = 0f;


    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            lastXScrollEventTime = 0L;
            lastYScrollEventTime = 0L;
            totalXDrag = 0f;
            totalYDrag = 0f;
            onDownTouch();
            return false;
        }

        @Override
        public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (System.currentTimeMillis() - lastXScrollEventTime > 30) {
                totalXDrag = 0f;
            }
            if (System.currentTimeMillis() - lastYScrollEventTime > 30) {
                totalYDrag = 0f;
            }
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if ((diffX > 0 && totalXDrag < 0) || (diffX < 0 && totalXDrag > 0)) {
                    totalXDrag = 0f;
                }
                totalXDrag += diffX;
                lastXScrollEventTime = System.currentTimeMillis();
            } else {
                if ((diffY > 0 && totalYDrag < 0) || (diffY < 0 && totalYDrag > 0)) {
                    totalYDrag = 0f;
                }
                totalYDrag += diffY;
                lastYScrollEventTime = System.currentTimeMillis();
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

      /*  @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                Log.e("SWTListener", "onFling " + diffX + " " + diffY);
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                } else if (Math.abs(diffY) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeDown();
                    } else {
                        onSwipeUp();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }*/
    }
}