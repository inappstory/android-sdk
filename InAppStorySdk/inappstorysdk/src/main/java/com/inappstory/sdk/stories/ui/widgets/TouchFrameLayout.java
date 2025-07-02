package com.inappstory.sdk.stories.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TouchFrameLayout extends FrameLayout {
    private OnTouchListener touchListener;
    private OnClickListener clickListener;
    private GestureDetector mGestureDetector;

    public void setTouchListener(OnTouchListener touchListener) {
        this.touchListener = touchListener;
    }

    public void setClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public TouchFrameLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public TouchFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TouchFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private boolean longPressed = false;

    private void init(Context context) {
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (clickListener != null)
                    clickListener.onClick(TouchFrameLayout.this);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                longPressed = true;
                super.onLongPress(e);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (touchListener != null) touchListener.onTouch(this, event);
                return true;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (touchListener != null) touchListener.onTouch(this, event);
                if (longPressed) {
                    clickListener.onClick(this);
                }
                longPressed = false;
                return true;
        }
        return super.onTouchEvent(event);
    }
}
