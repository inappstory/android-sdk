package com.inappstory.sdk.refactoring.stories.ui.reader;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.SizeF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.ui.widgets.customicons.CustomIconWithoutStates;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageLoaderState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageLoaderType;
import com.inappstory.sdk.stories.ui.widgets.TouchFrameLayout;
import com.inappstory.sdk.stories.utils.Sizes;

public class StoryReaderPageLoader extends RelativeLayout
        implements Observer<StoryReaderPageLoaderState> {
    private StoryReaderPageLoaderType lastType = StoryReaderPageLoaderType.HIDDEN;
    private TouchFrameLayout refresh;
    private FrameLayout loading;

    public StoryReaderPageLoader(@NonNull Context context) {
        super(context);
        init(context);
    }

    public StoryReaderPageLoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StoryReaderPageLoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setRefreshClickListener(OnClickListener clickListener) {
        if (refresh != null)
            refresh.setClickListener(clickListener);
    }

    private void clearListeners() {
        if (refresh != null)
            refresh.setClickListener(null);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void createRefreshButton(Context context) {
        refresh = new TouchFrameLayout(context);
        refresh.setId(R.id.ias_refresh_button);
        int maxSize = Sizes.dpToPxExt(40, getContext());
        RelativeLayout.LayoutParams refreshLp = new RelativeLayout.LayoutParams(
                maxSize,
                maxSize
        );
        refreshLp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        refresh.setElevation(18);
        refresh.setVisibility(View.GONE);
        final CustomIconWithoutStates customRefreshIconInterface = AppearanceManager.
                getCommonInstance().
                csCustomIcons().
                refreshIcon();
        final View customRefreshView = customRefreshIconInterface.
                createIconView(context, new SizeF(maxSize, maxSize));
        refresh.addView(customRefreshView);
        customRefreshView.setClickable(false);
        refresh.setTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                customRefreshIconInterface.touchEvent(customRefreshView, event);
                return false;
            }
        });
        refresh.setLayoutParams(refreshLp);
    }

    private void createLoading(Context context) {
        loading = new FrameLayout(context);
        loading.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT,
                MATCH_PARENT));
        loading.setElevation(8);
        ((ViewGroup) loading).addView(AppearanceManager.getLoader(context, Color.WHITE));
    }

    private void init(@NonNull Context context) {
        setAlpha(0.99f);
        createRefreshButton(context);
        createLoading(context);
        setLayoutParams(
                new RelativeLayout.LayoutParams(
                        MATCH_PARENT,
                        MATCH_PARENT
                )
        );
        setBackgroundColor(Color.BLACK);
        addView(refresh);
        addView(loading);
    }

    private void showLoading() {
        post(new Runnable() {
            @Override
            public void run() {
                if (loading != null) loading.setVisibility(VISIBLE);
            }
        });
    }

    private void showRefresh() {
        post(new Runnable() {
            @Override
            public void run() {
                if (refresh != null) refresh.setVisibility(VISIBLE);
            }
        });
    }

    private void hideLoading() {
        post(new Runnable() {
            @Override
            public void run() {
                if (loading != null) loading.setVisibility(GONE);
            }
        });
    }

    private void hideRefresh() {
        post(new Runnable() {
            @Override
            public void run() {
                if (refresh != null) refresh.setVisibility(GONE);
            }
        });
    }

    private void hide() {
        post(() -> {
            clearAnimation();
            animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setVisibility(GONE);
                        }
                    })
                    .start();
        });
    }

    private void show() {
        post(new Runnable() {
            @Override
            public void run() {
                clearAnimation();
                setVisibility(VISIBLE);
                animate().alpha(1f).setStartDelay(300).setDuration(300).start();
            }
        });
    }

    @Override
    public void onUpdate(StoryReaderPageLoaderState newValue) {
        switch (lastType) {
            case LOADING:
                hideLoading();
                break;
            case HIDDEN:
                show();
                break;
            case REFRESH:
                hideRefresh();
                break;
        }
        switch (newValue.loaderType()) {
            case HIDDEN:
                hide();
                break;
            case LOADING:
                showLoading();
                break;
            case REFRESH:
                showRefresh();
                break;
        }
        lastType = newValue.loaderType();
    }
}
